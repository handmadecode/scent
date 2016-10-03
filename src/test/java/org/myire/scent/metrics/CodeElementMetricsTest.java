/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Abstract base class with common unit tests for subclasses of {@code CodeElementMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class CodeElementMetricsTest
{
    /**
     * The constructor should throw a {@code NullPointerException} when passed a null name.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullName()
    {
        createInstance(null);
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
        CodeElementMetrics aMetrics = createInstance(aName);

        // Then
        assertEquals(aName, aMetrics.getName());
    }


    /**
     * A newly created {@code CodeElementMetrics} should have zero comments.
     */
    @Test
    public void newInstanceHasZeroComments()
    {
        // When
        CodeElementMetrics aMetrics = createInstance("x");

        // Then
        assertEquals(0, aMetrics.getComments().getNumLineComments());
        assertEquals(0, aMetrics.getComments().getNumBlockComments());
        assertEquals(0, aMetrics.getComments().getNumBlockCommentLines());
        assertEquals(0, aMetrics.getComments().getNumJavaDocComments());
        assertEquals(0, aMetrics.getComments().getNumJavaDocLines());
    }


    /**
     * Create an instance of the {@code CodeElementMetrics} subclass being tested.
     *
     * @param pName The name to pass to the constructor.
     *
     * @return  A new instance of the {@code CodeElementMetrics} subclass under test.
     */
    abstract protected CodeElementMetrics createInstance(String pName);
}
