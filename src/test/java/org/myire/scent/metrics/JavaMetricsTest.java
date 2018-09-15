/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;


/**
 * Unit tests for {@code JavaMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class JavaMetricsTest
{
    /**
     * A newly created {@code JavaMetrics} should have zero packages.
     */
    @Test
    public void newInstanceHasZeroPackages()
    {
        // When
        JavaMetrics aMetrics = new JavaMetrics();

        // Then
        assertEquals(0, aMetrics.getNumPackages());
        assertFalse(aMetrics.getPackages().iterator().hasNext());
    }


    /**
     * {@code maybeCreate()} should throw a {@code NullPointerException} when passed a null
     * argument.
     */
    @Test(expected=NullPointerException.class)
    public void maybeCreateThrowsForNullArgument()
    {
        // When
        new JavaMetrics().maybeCreate(null);
    }


    /**
     * {@code maybeCreate()} should return an empty {@code PackageMetrics} instance on the first
     * call with a package name.
     */
    @Test
    public void maybeCreateReturnsEmptyPackageMetricsOnFirstCall()
    {
        // Given
        JavaMetrics aJavaMetrics = new JavaMetrics();

        // When
        PackageMetrics aPackageMetrics = aJavaMetrics.maybeCreate("x");

        // Then
        assertEquals(0, aPackageMetrics.getNumCompilationUnits());
        assertFalse(aPackageMetrics.getCompilationUnits().iterator().hasNext());
        assertTrue(aPackageMetrics.getComments().isEmpty());

        // When
        aPackageMetrics = aJavaMetrics.maybeCreate("y");

        // Then
        assertEquals(0, aPackageMetrics.getNumCompilationUnits());
        assertFalse(aPackageMetrics.getCompilationUnits().iterator().hasNext());
        assertTrue(aPackageMetrics.getComments().isEmpty());
    }


    /**
     * {@code maybeCreate()} should return the same {@code PackageMetrics} instance for all calls
     * with the same package name.
     */
    @Test
    public void maybeCreateReturnsSamePackageMetricsInstance()
    {
        // Given
        String aPackageName = "z.x.c";
        JavaMetrics aJavaMetrics = new JavaMetrics();

        // When
        PackageMetrics aPackageMetrics1 = aJavaMetrics.maybeCreate(aPackageName);
        PackageMetrics aPackageMetrics2 = aJavaMetrics.maybeCreate(aPackageName);

        // Then
        assertSame(aPackageMetrics1, aPackageMetrics2);
    }


    /**
     * {@code maybeCreate()} should return different {@code PackageMetrics} instances for different
     * package names.
     */
    @Test
    public void maybeCreateReturnsDifferentPackageMetricsInstances()
    {
        // Given
        JavaMetrics aJavaMetrics = new JavaMetrics();

        // When
        PackageMetrics aPackageMetrics1 = aJavaMetrics.maybeCreate("pkg1");
        PackageMetrics aPackageMetrics2 = aJavaMetrics.maybeCreate("pkg2");

        // Then
        assertNotSame(aPackageMetrics1, aPackageMetrics2);
    }
}
