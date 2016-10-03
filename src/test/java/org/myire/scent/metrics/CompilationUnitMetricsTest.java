/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;


/**
 * Unit tests for {@code CompilationUnitMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class CompilationUnitMetricsTest extends CodeElementMetricsTest
{
    /**
     * A newly created {@code CompilationUnitMetrics} should have zero types.
     */
    @Test
    public void newInstanceHasZeroTypes()
    {
        // When
        CompilationUnitMetrics aMetrics = new CompilationUnitMetrics("cu");

        // Then
        assertEquals(0, aMetrics.getNumTypes());
        assertFalse(aMetrics.getTypes().iterator().hasNext());
    }


    /**
     * Adding a null {@code TypeMetrics} instance with a call to {@code add} should throw a
     * {@code NullPointerException}.
     */
    @Test(expected=NullPointerException.class)
    public void addTypeThrowsForNullArgument()
    {
        // Given
        TypeMetrics aType = null;

        // When
        new CompilationUnitMetrics("cu").add(aType);
    }


    /**
     * Adding a {@code TypeMetrics} instance with a call to {@code add} should increment the value
     * returned by {@code getNumTypes}.
     */
    @Test
    public void addTypeIncrementsTypeCount()
    {
        // Given
        CompilationUnitMetrics aMetrics = new CompilationUnitMetrics("cu");
        int aNumTypes = aMetrics.getNumTypes();

        // When
        aMetrics.add(new TypeMetrics("a", TypeMetrics.Kind.ANNOTATION));

        // Then
        assertEquals(++aNumTypes, aMetrics.getNumTypes());

        // When
        aMetrics.add(new TypeMetrics("i", TypeMetrics.Kind.INTERFACE));

        // Then
        assertEquals(++aNumTypes, aMetrics.getNumTypes());
    }


    /**
     * A {@code TypeMetrics} instance passed to {@code add} should be returned by {@code getTypes}.
     */
    @Test
    public void typeMetricsShouldBeReturnedByGetter()
    {
        // Given
        TypeMetrics aType = new TypeMetrics("a", TypeMetrics.Kind.ANNOTATION);
        CompilationUnitMetrics aMetrics = new CompilationUnitMetrics("cu");

        // When
        aMetrics.add(aType);

        // Then
        assertSame(aType, aMetrics.getTypes().iterator().next());
    }


    @Override
    protected CompilationUnitMetrics createInstance(String pName)
    {
        return new CompilationUnitMetrics(pName);
    }
}
