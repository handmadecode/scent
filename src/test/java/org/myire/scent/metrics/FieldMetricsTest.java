/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.metrics;

import org.junit.Test;
import static org.junit.Assert.assertEquals;


/**
 * Unit tests for {@code FieldMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class FieldMetricsTest extends StatementElementMetricsTest
{
    /**
     * The constructor should throw a {@code NullPointerException} when passed a null kind.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullKind()
    {
        new FieldMetrics("x", null);
    }


    /**
     * The {@code getKind} method should return the value passed to the constructor.
     */
    @Test
    public void getKindReturnsValuePassedToCtor()
    {
        // Given
        FieldMetrics.Kind aKind = FieldMetrics.Kind.ENUM_CONSTANT;

        // When
        FieldMetrics aMetrics = new FieldMetrics("f", aKind);

        // Then
        assertEquals(aKind, aMetrics.getKind());
    }


    @Override
    protected FieldMetrics createInstance(String pName)
    {
        return new FieldMetrics(pName, FieldMetrics.Kind.ENUM_CONSTANT);
    }
}
