/*
 * Copyright 2016 Peter Franzen. All rights reserved.
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
 * Source code metrics for an ordinary compilation unit. These metrics contain the type(s) declared
 * within the compilation unit and any comments associated with the unit itself, such as a file
 * header.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class CompilationUnitMetrics extends CodeElementMetrics
{
    private final Collection<TypeMetrics> fTypes = new ArrayList<>();


    /**
     * Create a new {@code CompilationUnitMetrics}.
     *
     * @param pName The name of the compilation unit.
     *
     * @throws NullPointerException if {@code pName} is null.
     */
    public CompilationUnitMetrics(@Nonnull String pName)
    {
        super(pName);
    }


    /**
     * Get the number of types declared within the compilation unit.
     *
     * @return  The number of types.
     */
    public int getNumTypes()
    {
        return fTypes.size();
    }


    /**
     * Get an {@code Iterable} that iterates over the metrics for each type contained within the
     * compilation unit.
     *
     * @return  An {@code Iterable} for the types' metrics, never null.
     */
    @Nonnull
    public Iterable<TypeMetrics> getTypes()
    {
        return fTypes;
    }


    /**
     * Add metrics for a type to this instance.
     *
     * @param pType    The type metrics.
     *
     * @throws NullPointerException if {@code pType} is null.
     */
    public void add(@Nonnull TypeMetrics pType)
    {
        fTypes.add(requireNonNull(pType));
    }
}
