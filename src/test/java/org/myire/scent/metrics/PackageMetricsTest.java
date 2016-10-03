/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;


/**
 * Unit tests for {@code PackageMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class PackageMetricsTest
{
    /**
     * The constructor should throw a {@code NullPointerException} when passed a null name.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullName()
    {
        new PackageMetrics(null);
    }


    /**
     * The {@code getName} method should return the value passed to the constructor.
     */
    @Test
    public void getNameReturnsValuePassedToCtor()
    {
        // Given
        String aName = "x";

        // When
        PackageMetrics aMetrics = new PackageMetrics(aName);

        // Then
        assertEquals(aName, aMetrics.getName());
    }


    /**
     * A newly created {@code PackageMetrics} should have zero compilation units.
     */
    @Test
    public void newInstanceHasZeroCompilationUnits()
    {
        // When
        PackageMetrics aMetrics = new PackageMetrics("pkg");

        // Then
        assertEquals(0, aMetrics.getNumCompilationUnits());
    }


    /**
     * Adding a null {@code CompilationUnitMetrics} should throw a {@code NullPointerException}.
     */
    @Test(expected=NullPointerException.class)
    public void addingNullCompilationUnitMetricsThrows()
    {
        // Given
        CompilationUnitMetrics aMetrics = null;

        // When
        new PackageMetrics("pkg").add(aMetrics);
    }


    /**
     * Adding a {@code CompilationUnit} instance with a call to {@code add} should increment the
     * value returned by {@code getNumCompilationUnits}.
     */
    @Test
    public void addingCompilationUnitIncrementsCompilationUnitCount()
    {
        // Given
        PackageMetrics aMetrics = new PackageMetrics("pkg");
        int aNumCompilationUnits = aMetrics.getNumCompilationUnits();

        // When
        aMetrics.add(new CompilationUnitMetrics("cu1"));

        // Then
        assertEquals(++aNumCompilationUnits, aMetrics.getNumCompilationUnits());

        // When
        aMetrics.add(new CompilationUnitMetrics("cu2"));

        // Then
        assertEquals(++aNumCompilationUnits, aMetrics.getNumCompilationUnits());
    }


    /**
     * A {@code CompilationUnit} instance passed to {@code add} should be returned by
     * {@code getCompilationUnits}.
     */
    @Test
    public void compilationUnitMetricsShouldBeReturnedByGetter()
    {
        // Given
        CompilationUnitMetrics aCompilationUnit = new CompilationUnitMetrics("cu");
        PackageMetrics aMetrics = new PackageMetrics("pkg");

        // When
        aMetrics.add(aCompilationUnit);

        // Then
        assertSame(aCompilationUnit, aMetrics.getCompilationUnits().iterator().next());
    }
}
