/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
    private final Map<String, PackageMetrics> fPackages = new LinkedHashMap<>();


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
     * Get the number of packages for which metrics have been collected in all calls to
     * {@code collect}.
     *
     * @return  The number of collected package metrics.
     */
    public int getNumCollectedPackages()
    {
        return fPackages.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the metrics collected in all calls to
     * {@code collect}.
     *
     * @return  An {@code Iterable} for the collected package metrics, never null.
     */
    @Nonnull
    public Iterable<PackageMetrics> getCollectedMetrics()
    {
        return fPackages.values();
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
            aResult.getResult().ifPresent(cu -> collectMetrics(cu, pName));
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
        if (pCompilationUnit.getModule().isPresent() && pCompilationUnit.getTypes().isEmpty())
            // Don't collect compilation units containing only a module declaration.
            return;

        CompilationUnitMetricsCollector aCollector =
            new CompilationUnitMetricsCollector(pCompilationUnit, pName);
        aCollector.collect(this::getPackageMetrics);
    }


    /**
     * Get the {@code PackageMetrics} for a package from the internal map. If the specified package
     * doesn't have an entry in the internal map a new {@code PackageMetrics} will be created an put
     * into the map before it is returned.
     *
     * @param pPackage  The package to get the {@code PackageMetrics} for, or null to get the
     *                  metrics for the default package.
     *
     * @return  The {@code PackageMetrics} instance for the specified package, never null.
     */
    @Nonnull
    private PackageMetrics getPackageMetrics(@CheckForNull PackageDeclaration pPackage)
    {
        String aPackageName = pPackage != null ? pPackage.getName().asString() : "";
        PackageMetrics aPackageMetrics = fPackages.get(aPackageName);
        if (aPackageMetrics == null)
        {
            aPackageMetrics = new PackageMetrics(aPackageName);
            fPackages.put(aPackageName, aPackageMetrics);
        }

        return aPackageMetrics;
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
                            .map(l -> l.getBegin().getRange().map(r -> r.begin.line).orElse(0))
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
        JAVA_10
        ;

        /**
         * Get the default language level for Java metrics collecting.
         *
         * @return  The default language level, never null.
         */
        @Nonnull
        static public LanguageLevel getDefault()
        {
            return JAVA_10;
        }
    }
}
