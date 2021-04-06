/*
 * Copyright 2016, 2018-2020 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.List;
import static java.util.Objects.requireNonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParseStart;
import com.github.javaparser.Problem;
import com.github.javaparser.Provider;
import com.github.javaparser.Providers;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;

import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.PackageMetrics;


/**
 * A collector of Java source code metrics. Metrics are collected and stored in an instance of this
 * class with repeated calls to {@link #collect(String, InputStream, Charset)}. The collected
 * metrics can then be retrieved by calling {@link #getCollectedMetrics()}.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class JavaMetricsCollector
{
    private final JavaParser fJavaParser;
    private final JavaMetrics fCollectedMetrics = new JavaMetrics();


    /**
     * Create a new {@code JavaMetricsCollector} for the default language level as specified by
     * {@link LanguageLevel#getDefault()}.
     */
    public JavaMetricsCollector()
    {
        this(LanguageLevel.getDefault());
    }


    /**
     * Create a new {@code JavaMetricsCollector} for a specific language level.
     *
     * @param pLanguageLevel    The language level.
     *
     * @throws NullPointerException if {@code pLanguageLevel} is null.
     */
    public JavaMetricsCollector(@Nonnull LanguageLevel pLanguageLevel)
    {
        fJavaParser = new JavaParser(createParserConfiguration(pLanguageLevel));
    }


    /**
     * Parse an {@code InputStream} containing Java source code and collect metrics from it. The
     * resulting metrics will be stored in this instance and returned when
     * {@link #getCollectedMetrics()} is called.
     *
     * @param pName         The name of the stream's origin, will be used as the name of the
     *                      {@code CompilationUnitMetrics} where the collected metrics are put.
     * @param pJavaSource   The input stream containing the source code to parse and collect metrics
     *                      for.
     * @param pEncoding     The encoding of the stream's bytes.
     *
     * @throws ParseException   if the stream's source code is lexically or syntactically invalid.
     * @throws NullPointerException if any of the parameters is null.
     */
    public void collect(
            @Nonnull String pName,
            @Nonnull InputStream pJavaSource,
            @Nonnull Charset pEncoding) throws ParseException
    {
        collect(pName, Providers.provider(requireNonNull(pJavaSource), requireNonNull(pEncoding)));
    }


    /**
     * Parse a string containing Java source code and collect metrics from it. The resulting metrics
     * will be stored in this instance and returned when {@link #getCollectedMetrics()} is called.
     *
     * @param pName         The name of the string's origin, will be used as the name of the
     *                      {@code CompilationUnitMetrics} where the collected metrics are put.
     * @param pJavaSource   The string containing the source code to parse and collect metrics for.
     *
     * @throws ParseException   if the string's source code is lexically or syntactically invalid.
     * @throws NullPointerException if any of the parameters is null.
     */
    public void collect(@Nonnull String pName, @Nonnull String pJavaSource) throws ParseException
    {
        collect(pName, Providers.provider(requireNonNull(pJavaSource)));
    }


    /**
     * Get the metrics collected in all calls to {@code collect}.
     *
     * @return  A {@code JavaMetrics} containing the collected metrics, never null.
     */
    @Nonnull
    public JavaMetrics getCollectedMetrics()
    {
        return fCollectedMetrics;
    }


    /**
     * Parse the contents of {@code Provider} containing Java source code and collect metrics from
     * it. The resulting metrics will be stored in this instance and returned when
     * {@link #getCollectedMetrics()} is called.
     *
     * @param pName         The name of the source code's origin, will be used as the name of the
     *                      {@code CompilationUnitMetrics} where the collected metrics are put.
     * @param pJavaSource   A provider with the source code to parse and collect metrics for.
     *
     * @throws ParseException   if the source code is lexically or syntactically invalid.
     * @throws NullPointerException if any of the parameters is null.
     */
    private void collect(
            @Nonnull String pName,
            @Nonnull Provider pJavaSource) throws ParseException
    {
        ParseResult<CompilationUnit> aResult =
                fJavaParser.parse(ParseStart.COMPILATION_UNIT, pJavaSource);
        if (aResult.isSuccessful())
            aResult.getResult().ifPresent(_cu -> collectMetrics(_cu, pName));
        else
            throw createParseException(aResult);
    }


    /**
     * Collect metrics from a {@code CompilationUnit}.
     *
     * @param pCompilationUnit  The instance to collect metrics from.
     * @param pName             The name of the compilation unit.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    private void collectMetrics(@Nonnull CompilationUnit pCompilationUnit, @Nonnull String pName)
    {
        if (pCompilationUnit.getModule().isPresent())
        {
            // A modular compilation unit, collect metrics for it and add to the overall results.
            ModularCompilationUnitMetricsCollector aCollector =
                new ModularCompilationUnitMetricsCollector(pCompilationUnit, pName);
            fCollectedMetrics.add(aCollector.collect());
        }
        else
        {
            // An ordinary compilation unit, collect its metrics into the appropriate package's
            // metrics.
            CompilationUnitMetricsCollector aCollector =
                new CompilationUnitMetricsCollector(pCompilationUnit, pName);
            aCollector.collect(this::getPackageMetrics);
        }
    }


    /**
     * Get, possibly first creating, the {@code PackageMetrics} for a {@code PackageDeclaration}.
     *
     * @param pPackage  The package declaration to get the metrics for. A null instance represents
     *                  the default package.
     *
     * @return  The {@code PackageMetrics} instance for the specified package, never null.
     */
    @Nonnull
    private PackageMetrics getPackageMetrics(@CheckForNull PackageDeclaration pPackage)
    {
        String aPackageName = pPackage != null ? pPackage.getName().asString() : "";
        return fCollectedMetrics.maybeCreate(aPackageName);
    }


    /**
     * Create a {@code ParserConfiguration} for a specific language level.
     *
     * @param pLanguageLevel    The language level.
     *
     * @return  A new  {@code ParserConfiguration}, never null.
     *
     * @throws NullPointerException if {@code pLanguageLevel} is null.
     */
    @Nonnull
    static private ParserConfiguration createParserConfiguration(@Nonnull LanguageLevel pLanguageLevel)
    {
        ParserConfiguration aConfiguration = new ParserConfiguration();

        switch (pLanguageLevel)
        {
            case JAVA_8:
                aConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_8);
                break;

            case JAVA_9:
                aConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_9);
                break;

            case JAVA_10:
                aConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_10);
                break;

            case JAVA_11:
                aConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_11);
                break;

            case JAVA_12:
                aConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_12);
                break;

            case JAVA_13:
                aConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_13);
                break;

            case JAVA_14:
                aConfiguration.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_14);
                break;

            default:
                break;
        }

        return aConfiguration;
    }


    /**
     * Create a {@code ParseException} from the problems in a {@code ParseResult}.
     *
     * @param pResult   An unsuccessful parse result.
     *
     * @return  A new {@code ParseException} with message and position taken from the parse result's
     *          problems. Null is never returned.
     *
     * @throws NullPointerException if {@code pResult} is null.
     */
    @Nonnull
    static private ParseException createParseException(@Nonnull ParseResult<?> pResult)
    {
        List<Problem> aProblems = pResult.getProblems();
        ParseProblemException aCause = new ParseProblemException(aProblems);
        int aPosition = 0;
        for (int i=0; i<aProblems.size() && aPosition == 0; i++)
        {
            // Tell me again how Optional makes code more readable.
            aPosition =
                    aProblems.get(i)
                            .getLocation()
                            .map(_l -> _l.getBegin().getRange().map(_r -> _r.begin.line).orElse(0))
                            .orElse(0);
        }

        ParseException aParseException = new ParseException(aCause.getMessage(), aPosition);
        aParseException.initCause(aCause);
        return aParseException;
    }


    /**
     * The supported Java language levels.
     */
    public enum LanguageLevel
    {
        /** Java 8 (introducing lambdas and type annotations). */
        JAVA_8,

        /** Java 9 (introducing modules and private interface methods). */
        JAVA_9,

        /** Java 10 (introducing local variable type inference). */
        JAVA_10,

        /** Java 11 (introducing local variable syntax for lambda parameters (JEP 323)). */
        JAVA_11,

        /** Java 12 (introducing switch expressions preview (JEP 325)). */
        JAVA_12,

        /**
         * Java 13.
         *<ul>
         * <li>text blocks preview (JEP 355)</li>
         * <li>switch expressions second preview (JEP 354))</li>
         *</ul>
         */
        JAVA_13,

        /**
         * Java 14.
         *<ul>
         * <li>switch expressions (JEP 361)</li>
         * <li>pattern matching for instanceof preview (JEP 305)</li>
         * <li>records preview (JEP 359)</li>
         * <li>text blocks second preview (JEP 368)</li>
         *</ul>
         */
        JAVA_14
        ;

        /**
         * Get the default language level for Java metrics collecting.
         *
         * @return  The default language level, never null.
         */
        @Nonnull
        static public LanguageLevel getDefault()
        {
            return JAVA_14;
        }
    }
}
