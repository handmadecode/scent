/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import java.util.ArrayList;
import java.util.Collection;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;


/**
 * Source code metrics for a java package. A package's metrics contain the metrics of all
 * compilation units that have the same package declaration. It may also contain comments, e.g.
 * JavaDocs, from a package declaration in a <i>package-info.java</i> file. Comments adjacent to
 * the package declaration in any other compilation unit are not associated with the package but
 * with the compilation unit.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class PackageMetrics extends CodeElementMetrics
{
    private final Collection<CompilationUnitMetrics> fCompilationUnits = new ArrayList<>();


    /**
     * Create a new {@code PackageMetrics}.
     *
     * @param pName The name of the package.
     *
     * @throws NullPointerException if {@code pName} is null.
     */
    public PackageMetrics(@Nonnull String pName)
    {
        super(pName);
    }


    /**
     * Get the number of compilation units that declare this package.
     *
     * @return  The number of compilation units.
     */
    public int getNumCompilationUnits()
    {
        return fCompilationUnits.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the metrics for each compilation unit belonging to
     * the package.
     *
     * @return  An {@code Iterable} for the compilation units' metrics, never null.
     */
    @Nonnull
    public Iterable<CompilationUnitMetrics> getCompilationUnits()
    {
        return fCompilationUnits;
    }


    /**
     * Add metrics for a compilation unit to this instance.
     *
     * @param pCompilationUnit  The compilation unit metrics.
     *
     * @throws NullPointerException if {@code pCompilationUnit} is null.
     */
    public void add(@Nonnull CompilationUnitMetrics pCompilationUnit)
    {
        fCompilationUnits.add(requireNonNull(pCompilationUnit));
    }
}
