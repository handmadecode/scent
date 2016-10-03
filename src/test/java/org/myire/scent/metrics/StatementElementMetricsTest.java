/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Abstract base class with common unit tests for subclasses of {@code StatementElementMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class StatementElementMetricsTest extends CodeElementMetricsTest
{
    /**
     * A newly created {@code StatementElementMetrics} should have zero statements.
     */
    @Test
    public void newInstanceHasZeroComments()
    {
        // When
        StatementElementMetrics aMetrics = createInstance("x");

        // Then
        assertEquals(0, aMetrics.getStatements().getNumStatements());
    }


    /**
     * Create an instance of the {@code StatementElementMetrics} subclass being tested.
     *
     * @param pName The name to pass to the constructor.
     *
     * @return  A new instance of the {@code StatementElementMetrics} subclass under test.
     */
    abstract protected StatementElementMetrics createInstance(String pName);
}
