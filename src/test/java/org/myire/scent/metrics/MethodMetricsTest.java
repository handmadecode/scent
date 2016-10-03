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
 * Unit tests for {@code MethodMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class MethodMetricsTest extends StatementElementMetricsTest
{
    /**
     * The constructor should throw a {@code NullPointerException} when passed a null kind.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullKind()
    {
        new MethodMetrics("x", null);
    }


    /**
     * The {@code getKind} method should return the value passed to the constructor.
     */
    @Test
    public void getKindReturnsValuePassedToCtor()
    {
        // Given
        MethodMetrics.Kind aKind = MethodMetrics.Kind.CONSTRUCTOR;

        // When
        MethodMetrics aMetrics = new MethodMetrics("m", aKind);

        // Then
        assertEquals(aKind, aMetrics.getKind());
    }


    /**
     * A newly created {@code MethodMetrics} should have zero local types.
     */
    @Test
    public void newInstanceHasZeroLocalTypes()
    {
        // When
        MethodMetrics aMetrics = new MethodMetrics("m", MethodMetrics.Kind.CONSTRUCTOR);

        // Then
        assertEquals(0, aMetrics.getNumLocalTypes());
        assertFalse(aMetrics.getLocalTypes().iterator().hasNext());
    }


    /**
     * {@code add(TypeMetrics)} should throw a {@code NullPointerException} when passed a null
     * argument.
     */
    @Test(expected=NullPointerException.class)
    public void addLocalTypeThrowsForNullArgument()
    {
        // Given
        TypeMetrics aLocalType = null;

        // When
        new MethodMetrics("m", MethodMetrics.Kind.INSTANCE_METHOD).add(aLocalType);
    }


    /**
     * Adding a {@code TypeMetrics} instance with a call to {@code add} should increment the value
     * returned by {@code getNumLocalTypes}.
     */
    @Test
    public void addLocalTypeIncrementsLocalTypeCount()
    {
        // Given
        MethodMetrics aMetrics = new MethodMetrics("m", MethodMetrics.Kind.STATIC_METHOD);
        int aNumLocalTypes = aMetrics.getNumLocalTypes();

        // When
        aMetrics.add(new TypeMetrics("a", TypeMetrics.Kind.ANONYMOUS_CLASS));

        // Then
        assertEquals(++aNumLocalTypes, aMetrics.getNumLocalTypes());

        // When
        aMetrics.add(new TypeMetrics("c", TypeMetrics.Kind.CLASS));

        // Then
        assertEquals(++aNumLocalTypes, aMetrics.getNumLocalTypes());
    }


    /**
     * A {@code TypeMetrics} instance passed to {@code add} should be returned by
     * {@code getLocalTypes}.
     */
    @Test
    public void localTypeMetricsIsReturnedByGetter()
    {
        // Given
        TypeMetrics aLocalType = new TypeMetrics("a", TypeMetrics.Kind.ANONYMOUS_CLASS);
        MethodMetrics aMetrics = new MethodMetrics("m", MethodMetrics.Kind.INSTANCE_METHOD);

        // When
        aMetrics.add(aLocalType);

        // Then
        assertSame(aLocalType, aMetrics.getLocalTypes().iterator().next());
    }


    @Override
    protected MethodMetrics createInstance(String pName)
    {
        return new MethodMetrics(pName, MethodMetrics.Kind.INSTANCE_INITIALIZER);
    }
}
