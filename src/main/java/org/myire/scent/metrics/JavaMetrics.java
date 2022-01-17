/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * Source code metrics for a Java code base. The metrics are grouped by Java package, and each
 * package's metrics contain the metrics for the individual compilation units declaring that they
 * belong to the package in question.
 *<p>
 * In addition to the metrics grouped by package, an instance also contains metrics for any modular
 * compilation units in the code base.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class JavaMetrics
{
    // Use a linked hash map to iterate over the package metrics in the same order as they were
    // added.
    private final Map<String, PackageMetrics> fPackages = new LinkedHashMap<>();

    private final Collection<ModularCompilationUnitMetrics> fModules = new ArrayList<>();


    /**
     * Check if this instance is empty.
     *
     * @return  True iff no package metrics or modular compilation unit metrics have been added to
     *          this instance.
     */
    public boolean isEmpty()
    {
        return fPackages.isEmpty() && fModules.isEmpty();
    }


    /**
     * Get the number of packages for which metrics have been collected in this instance.
     *
     * @return  The number of packages.
     */
    public int getNumPackages()
    {
        return fPackages.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the package metrics that have been collected in
     * this instance.
     *
     * @return  An {@code Iterable} for the package metrics, never null.
     */
    @Nonnull
    public Iterable<PackageMetrics> getPackages()
    {
        return fPackages.values();
    }


    /**
     * Get the number of modular compilation units for which metrics have been collected in this
     * instance.
     *
     * @return  The number of modular compilation units.
     */
    public int getNumModularCompilationUnits()
    {
        return fModules.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the modular compilation unit metrics that have
     * been collected in this instance.
     *
     * @return  An {@code Iterable} for the modular compilation unit metrics, never null.
     */
    @Nonnull
    public Iterable<ModularCompilationUnitMetrics> getModularCompilationUnits()
    {
        return fModules;
    }


    /**
     * Get the {@code PackageMetrics} for a package with a specific name. If no package metrics with
     * the specified name exists in this instance, a new {@code PackageMetrics} will be created and
     * returned.
     *<p>
     * This method returns the same {@code PackageMetrics} instance when called repeatedly with the
     * same package name.
     *
     * @param pPackageName  The name of the package to get the {@code PackageMetrics} for. The
     *                      default package is represented by an empty string.
     *
     * @return  The {@code PackageMetrics} instance for the specified package name, never null.
     */
    @Nonnull
    public PackageMetrics maybeCreate(@Nonnull String pPackageName)
    {
        return fPackages.computeIfAbsent(pPackageName, PackageMetrics::new);
    }


    /**
     * Add metrics for a modular compilation unit to this instance.
     *
     * @param pMetrics  The  modular compilation unit metrics.
     *
     * @throws NullPointerException if {@code pMetrics} is null.
     */
    public void add(@Nonnull ModularCompilationUnitMetrics pMetrics)
    {
        fModules.add(requireNonNull(pMetrics));
    }
}
