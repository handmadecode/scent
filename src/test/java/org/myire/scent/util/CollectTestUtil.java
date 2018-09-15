/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

import org.myire.scent.collect.JavaMetricsCollector;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.MethodMetrics;
import org.myire.scent.metrics.ModularCompilationUnitMetrics;
import org.myire.scent.metrics.ModuleDeclarationMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.TypeMetrics;


/**
 * Utility methods for unit tests related to collecting metrics.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
final public class CollectTestUtil
{
    static private final String LINE_FEED = System.getProperty("line.separator");


    /**
     * Append an array of source code lines into a single source code string.
     *
     * @param pSourceLines  The source code lines.
     *
     * @return  All strings in {@code pSourceLines} appended together with a line separator between
     *          them.
     */
    static public String toSourceString(String[] pSourceLines)
    {
        StringBuilder aBuilder = new StringBuilder(512);
        for (String aSourceLine : pSourceLines)
            aBuilder.append(aSourceLine).append(LINE_FEED);

        return aBuilder.toString();
    }


    /**
     * Parse a string containing Java source and return the collected source code metrics.
     *
     * @param pSource   The Java source to collect metrics from.
     *
     * @return  A {@code JavaMetrics} with the collected source code metrics.
     *
     * @throws ParseException   if the specified source code is lexically or syntactically invalid.
     */
    static public JavaMetrics collect(String pSource) throws ParseException
    {
        return collect("inMemory", pSource);
    }


    /**
     * Append an array of source code lines into a single source code string, parse the string and
     * return the collected source code metrics.
     *
     * @param pSourceLines  The Java source code lines to collect metrics from.
     *
     * @return  A {@code JavaMetrics} with the collected source code metrics.
     *
     * @throws ParseException   if the specified source code is lexically or syntactically invalid.
     */
    static public JavaMetrics collect(String[] pSourceLines) throws ParseException
    {
        return collect(toSourceString(pSourceLines));
    }


    /**
     * Parse a string containing Java source and return the collected source code metrics.
     *
     * @param pName     The name to give the {@code CompilationUnitMetrics}.
     * @param pSource   The Java source to collect metrics from.
     *
     * @return  A {@code JavaMetrics} with the collected source code metrics.
     *
     * @throws ParseException   if the specified source code is lexically or syntactically invalid.
     */
    static public JavaMetrics collect(String pName, String pSource) throws ParseException
    {
        JavaMetricsCollector aCollector = new JavaMetricsCollector();
        aCollector.collect(pName, pSource);
        return aCollector.getCollectedMetrics();
    }


    /**
     * Parse a resource on the classpath containing Java source code and return the collected code
     * metrics.
     *
     * @param pResourceName The name of the resource to collect metrics from.
     *
     * @return  A {@code JavaMetrics} with the collected source code metrics.
     *
     * @throws ParseException   if the specified source code is lexically or syntactically invalid.
     * @throws RuntimeException if the resource doesn't exist.
     */
    static public JavaMetrics collectFromResource(String pResourceName) throws ParseException
    {
        try (InputStream aStream = CollectTestUtil.class.getResourceAsStream(pResourceName))
        {
            JavaMetricsCollector aCollector = new JavaMetricsCollector();
            aCollector.collect(pResourceName, aStream, StandardCharsets.UTF_8);
            return aCollector.getCollectedMetrics();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get the metrics for the first modular compilation unit in a {@code JavaMetrics}.
     *
     * @param pMetrics  The {@code JavaMetrics} to get the first modular compilation unit metrics
     *                  from.
     *
     * @return  A {@code ModularCompilationUnitMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no modular compilation
     *                                          units.
     */
    static public ModularCompilationUnitMetrics getFirstModularCompilationUnit(JavaMetrics pMetrics)
    {
        return pMetrics.getModularCompilationUnits().iterator().next();
    }


    /**
     * Get the metrics for the module declaration of the first modular compilation unit in a
     * {@code JavaMetrics}.
     *
     * @param pMetrics  The {@code JavaMetrics} to get the first module declaration metrics from.
     *
     * @return  A {@code ModuleDeclarationMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no modular compilation
     *                                          units.
     */
    static public ModuleDeclarationMetrics getFirstModuleDeclaration(JavaMetrics pMetrics)
    {
        return getFirstModularCompilationUnit(pMetrics).getModule();
    }


    /**
     * Get the metrics for the first package in a {@code JavaMetrics}.
     *
     * @param pMetrics  The {@code JavaMetrics} to get the first package metrics from.
     *
     * @return  A {@code PackageMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no packages.
     */
    static public PackageMetrics getFirstPackage(JavaMetrics pMetrics)
    {
        return pMetrics.getPackages().iterator().next();
    }


    /**
     * Get the metrics for the first compilation unit in a {@code PackageMetrics}.
     *
     * @param pPackageMetrics   The instance to get the first compilation unit metrics from.
     *
     * @return  A {@code CompilationUnitMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pPackageMetrics} has no compilation units.
     */
    static public CompilationUnitMetrics getFirstCompilationUnit(PackageMetrics pPackageMetrics)
    {
        return pPackageMetrics.getCompilationUnits().iterator().next();
    }


    /**
     * Get the metrics for the first compilation unit in the first package in a {@code JavaMetrics}.
     *
     * @param pMetrics  The instance to get the first compilation unit metrics from.
     *
     * @return  A {@code CompilationUnitMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no packages, or if the first
     *                                          package has no compilation units.
     */
    static public CompilationUnitMetrics getFirstCompilationUnit(JavaMetrics pMetrics)
    {
        return getFirstCompilationUnit(getFirstPackage(pMetrics));
    }


    /**
     * Get the metrics for the first type in a {@code CompilationUnitMetrics}.
     *
     * @param pCompilationUnitMetrics   The instance to get the first type metrics from.
     *
     * @return  A {@code TypeMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pCompilationUnitMetrics} has no types.
     */
    static public TypeMetrics getFirstType(CompilationUnitMetrics pCompilationUnitMetrics)
    {
        return pCompilationUnitMetrics.getTypes().iterator().next();
    }


    /**
     * Get the metrics for the first type in the first compilation unit in the first package in a
     * {@code JavaMetrics}.
     *
     * @param pMetrics  The instance to get the first type metrics from.
     *
     * @return  A {@code TypeMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no packages, or if the first
     *                                          package has no compilation units, or if the first
     *                                          compilation unit has no types.
     */
    static public TypeMetrics getFirstType(JavaMetrics pMetrics)
    {
        return getFirstType(getFirstCompilationUnit(pMetrics));
    }


    /**
     * Get the metrics for the first field in a {@code TypeMetrics}.
     *
     * @param pTypeMetrics  The instance to get the first field metrics from.
     *
     * @return  A {@code FieldMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pTypeMetrics} has no fields.
     */
    static public FieldMetrics getFirstField(TypeMetrics pTypeMetrics)
    {
        return pTypeMetrics.getFields().iterator().next();
    }


    /**
     * Get the metrics for the first field in the first type in the first compilation unit in the
     * first package in a {@code JavaMetrics}.
     *
     * @param pMetrics  The instance to get the first field metrics from.
     *
     * @return  A {@code FieldMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no packages, or if the first
     *                                          package has no compilation units, or if the first
     *                                          compilation unit has no types, or if the first type
     *                                          has no fields.
     */
    static public FieldMetrics getFirstField(JavaMetrics pMetrics)
    {
        return getFirstField(getFirstType(pMetrics));
    }


    /**
     * Get the metrics for the first method in a {@code TypeMetrics}.
     *
     * @param pTypeMetrics  The instance to get the first method metrics from.
     *
     * @return  A {@code MethodMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pTypeMetrics} has no methods.
     */
    static public MethodMetrics getFirstMethod(TypeMetrics pTypeMetrics)
    {
        return pTypeMetrics.getMethods().iterator().next();
    }


    /**
     * Get the metrics for the first method in the first type in the first compilation unit in the
     * first package in a {@code JavaMetrics}.
     *
     * @param pMetrics  The instance to get the first method metrics from.
     *
     * @return  A {@code MethodMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no packages, or if the first
     *                                          package has no compilation units, or if the first
     *                                          compilation unit has no types, or if the first type
     *                                          has no methods.
     */
    static public MethodMetrics getFirstMethod(JavaMetrics pMetrics)
    {
        return getFirstMethod(getFirstType(pMetrics));
    }


    /**
     * Get the metrics for the first inner type in a {@code TypeMetrics}.
     *
     * @param pTypeMetrics  The instance to get the first inner type metrics from.
     *
     * @return  A {@code TypeMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pTypeMetrics} has no inner types.
     */
    static public TypeMetrics getFirstInnerType(TypeMetrics pTypeMetrics)
    {
        return pTypeMetrics.getInnerTypes().iterator().next();
    }


    /**
     * Get the metrics for the first inner type in the first type in the first compilation unit in
     * the first package in a {@code JavaMetrics}.
     *
     * @param pMetrics  The instance to get the first inner type metrics from.
     *
     * @return  A {@code TypeMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no packages, or if the first
     *                                          package has no compilation units, or if the first
     *                                          compilation unit has no types, or if the first type
     *                                          has no inner types.
     */
    static public TypeMetrics getFirstInnerType(JavaMetrics pMetrics)
    {
        return getFirstInnerType(getFirstType(pMetrics));
    }


    /**
     * Get the metrics for the first local type in a {@code MethodMetrics}.
     *
     * @param pMethodMetrics  The instance to get the first local type metrics from.
     *
     * @return  A {@code TypeMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMethodMetrics} has no local types.
     */
    static public TypeMetrics getFirstLocalType(MethodMetrics pMethodMetrics)
    {
        return pMethodMetrics.getLocalTypes().iterator().next();
    }


    /**
     * Get the metrics for the first local type in the first method in the first type in the
     * first compilation unit in the first package in a {@code JavaMetrics}.
     *
     * @param pMetrics  The instance to get the first local type metrics from.
     *
     * @return  A {@code TypeMetrics} instance.
     *
     * @throws java.util.NoSuchElementException if {@code pMetrics} has no packages, or if the first
     *                                          package has no compilation units, or if the first
     *                                          compilation unit has no types, or if the first type
     *                                          has methods, or if the first method has no local
     *                                          types.
     */
    static public TypeMetrics getFirstLocalType(JavaMetrics pMetrics)
    {
        return getFirstLocalType(getFirstMethod(pMetrics));
    }
}
