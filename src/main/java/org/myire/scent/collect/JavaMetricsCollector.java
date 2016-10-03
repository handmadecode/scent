/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.QualifiedNameExpr;

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
    private final Map<String, PackageMetrics> fPackages = new LinkedHashMap<>();


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
        try
        {
            collectMetrics(JavaParser.parse(pJavaSource, pEncoding.name()), pName);
        }
        catch (com.github.javaparser.ParseException | com.github.javaparser.TokenMgrError e)
        {
            ParseException aWrapper = new ParseException(e.getMessage(), 0);
            aWrapper.initCause(e);
            throw aWrapper;
        }
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
     * Collect metrics from a {@code CompilationUnit}.
     *
     * @param pCompilationUnit  The instance to collect metrics from.
     * @param pName             The name of the compilation unit.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    private void collectMetrics(@Nonnull CompilationUnit pCompilationUnit, @Nonnull String pName)
    {
        CompilationUnitMetricsCollector aCollector = new CompilationUnitMetricsCollector(pCompilationUnit, pName);
        PackageMetrics aPackageMetrics = getPackageMetrics(pCompilationUnit.getPackage());
        aPackageMetrics.add(aCollector.collect());
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
        String aPackageName = pPackage != null ? getQualifiedName(pPackage.getName()) : "";
        PackageMetrics aPackageMetrics = fPackages.get(aPackageName);
        if (aPackageMetrics == null)
        {
            aPackageMetrics = new PackageMetrics(aPackageName);
            fPackages.put(aPackageName, aPackageMetrics);
        }

        return aPackageMetrics;
    }


    /**
     * Get the qualified name of a {@code NameExpr}. Since {@code QualifiedNameExpr} doesn't return
     * the qualifier part in {@code getName}, that part must be prepended to its name explicitly.
     *
     * @param pNameExpression   The instance to get the qualified name of.
     *
     * @return  The qualified name of the specified name expression.
     *
     * @throws NullPointerException if {@code pNameExpression} is null.
     */
    static private String getQualifiedName(@Nonnull NameExpr pNameExpression)
    {
        if (pNameExpression instanceof QualifiedNameExpr)
            return getQualifiedName(((QualifiedNameExpr) pNameExpression).getQualifier())
                    + '.'
                    + pNameExpression.getName();
        else
            return pNameExpression.getName();
    }
}
