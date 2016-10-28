/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.annotation.Nonnull;

import org.myire.scent.collect.JavaMetricsCollector;
import org.myire.scent.metrics.AggregatedMetrics;
import org.myire.scent.metrics.PackageMetrics;


/**
 * Main entry point for the scent jar.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public final class Main
{
    static private final PrintStream OUT = System.out;
    static private final PrintStream ERR = System.err;


    /**
     * Private constructor to disallow instantiations of utility method class.
     */
    private Main()
    {
        // Empty default ctor, defined to override access scope.
    }


    /**
     * Collect metrics from all files ending in &quot;.java&quot; found in the path(s) specified as
     * argument(s) to this method, recursively descending into subdirectories.
     *
     * @param pArgs The Java source file path(s) to collect metrics from. The files are assumed to
     *              be encoded in UTF-8.
     */
    static public void main(@Nonnull String... pArgs)
    {
        JavaMetricsCollector aCollector = new JavaMetricsCollector();
        collectMetrics(aCollector, pArgs);

        if (aCollector.getNumCollectedPackages() > 0)
           printMetrics(aCollector.getCollectedMetrics());
    }


    /**
     * Visit all files in zero or more paths, recursively descending into subdirectories, and
     * collect metrics for those ending in &quot;.java&quot;.
     *
     * @param pCollector    The collector to collect metrics with.
     * @param pArgs         The Java source file path(s) to collect metrics from. The files are
     *                      assumed to be encoded in UTF-8.
     *
     *@throws NullPointerException if any of the parameters is null.
     */
    static private void collectMetrics(@Nonnull JavaMetricsCollector pCollector, @Nonnull String... pArgs)
    {
        PrintingCollector aCollector = new PrintingCollector(pCollector, OUT, ERR);
        for (String aArg : pArgs)
        {
            try
            {
                Files.walkFileTree(Paths.get(aArg), aCollector);
            }
            catch (IOException e)
            {
                ERR.println("Error when collecting metrics from " + aArg + ": "+ e.getMessage());
            }
        }

        OUT.println("Collected metrics from " + aCollector.getNumFiles() + " files");
    }


    /**
     * Print collected metrics to the {@code OUT} print stream.
     *
     * @param pMetrics  The metrics to print.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    static private void printMetrics(@Nonnull Iterable<PackageMetrics> pMetrics)
    {
        MetricsPrinter aPrinter = new MetricsPrinter(OUT);

        OUT.println();
        OUT.println("Summary:");
        aPrinter.print(AggregatedMetrics.of(pMetrics));

        OUT.println();
        OUT.println("Details:");
        aPrinter.print(pMetrics);
    }
}
