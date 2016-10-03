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
 * Unit tests for {@code TypeMetrics}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class TypeMetricsTest extends CodeElementMetricsTest
{
    /**
     * The constructor should throw a {@code NullPointerException} when passed a null kind.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullKind()
    {
        new TypeMetrics("x", null);
    }


    /**
     * The {@code getKind} method should return the value passed to the constructor.
     */
    @Test
    public void getKindReturnsValuePassedToCtor()
    {
        // Given
        TypeMetrics.Kind aKind = TypeMetrics.Kind.ANNOTATION;

        // When
        TypeMetrics aMetrics = new TypeMetrics("a", aKind);

        // Then
        assertEquals(aKind, aMetrics.getKind());
    }


    /**
     * A newly created {@code TypeMetrics} should have zero fields.
     */
    @Test
    public void newInstanceHasZeroFields()
    {
        // When
        TypeMetrics aMetrics = new TypeMetrics("c", TypeMetrics.Kind.CLASS);

        // Then
        assertEquals(0, aMetrics.getNumFields());
        assertFalse(aMetrics.getFields().iterator().hasNext());
    }


    /**
     * A newly created {@code TypeMetrics} should have zero methods.
     */
    @Test
    public void newInstanceHasZeroMethods()
    {
        // When
        TypeMetrics aMetrics = new TypeMetrics("c", TypeMetrics.Kind.CLASS);

        // Then
        assertEquals(0, aMetrics.getNumMethods());
        assertFalse(aMetrics.getMethods().iterator().hasNext());
    }


    /**
     * A newly created {@code TypeMetrics} should have zero inner types.
     */
    @Test
    public void newInstanceHasZeroInnerTypes()
    {
        // When
        TypeMetrics aMetrics = new TypeMetrics("i", TypeMetrics.Kind.INTERFACE);

        // Then
        assertEquals(0, aMetrics.getNumInnerTypes());
        assertFalse(aMetrics.getInnerTypes().iterator().hasNext());
    }


    /**
     * {@code add(FieldMetrics)} should throw a {@code NullPointerException} when passed a null
     * argument.
     */
    @Test(expected=NullPointerException.class)
    public void addFieldThrowsForNullArgument()
    {
        // Given
        FieldMetrics aField = null;

        // When
        new TypeMetrics("e", TypeMetrics.Kind.ENUM).add(aField);
    }


    /**
     * {@code add(MethodMetrics)} should throw a {@code NullPointerException} when passed a null
     * argument.
     */
    @Test(expected=NullPointerException.class)
    public void addMethodThrowsForNullArgument()
    {
        // Given
        MethodMetrics aMethod = null;

        // When
        new TypeMetrics("e", TypeMetrics.Kind.ENUM).add(aMethod);
    }


    /**
     * {@code add(TypeMetrics)} should throw a {@code NullPointerException} when passed a null
     * argument.
     */
    @Test(expected=NullPointerException.class)
    public void addInnerTypeThrowsForNullArgument()
    {
        // Given
        TypeMetrics aInnerType = null;

        // When
        new TypeMetrics("a", TypeMetrics.Kind.ANNOTATION).add(aInnerType);
    }


    /**
     * Adding a {@code FieldMetrics} instance with a call to {@code add} should increment the value
     * returned by {@code getNumFields}.
     */
    @Test
    public void addFieldIncrementsFieldCount()
    {
        // Given
        TypeMetrics aMetrics = new TypeMetrics("e", TypeMetrics.Kind.ENUM_CONSTANT);
        int aNumFields = aMetrics.getNumFields();

        // When
        aMetrics.add(new FieldMetrics("f1", FieldMetrics.Kind.STATIC_FIELD));

        // Then
        assertEquals(++aNumFields, aMetrics.getNumFields());

        // When
        aMetrics.add(new FieldMetrics("f2", FieldMetrics.Kind.INSTANCE_FIELD));

        // Then
        assertEquals(++aNumFields, aMetrics.getNumFields());
    }


    /**
     * Adding a {@code MethodMetrics} instance with a call to {@code add} should increment the value
     * returned by {@code getNumMethods}.
     */
    @Test
    public void addMethodIncrementsMethodCount()
    {
        // Given
        TypeMetrics aMetrics = new TypeMetrics("c", TypeMetrics.Kind.CLASS);
        int aNumMethods = aMetrics.getNumMethods();

        // When
        aMetrics.add(new MethodMetrics("m1", MethodMetrics.Kind.STATIC_METHOD));

        // Then
        assertEquals(++aNumMethods, aMetrics.getNumMethods());

        // When
        aMetrics.add(new MethodMetrics("m2", MethodMetrics.Kind.DEFAULT_METHOD));

        // Then
        assertEquals(++aNumMethods, aMetrics.getNumMethods());
    }


    /**
     * Adding a {@code TypeMetrics} instance with a call to {@code add} should increment the value
     * returned by {@code getNumInnerTypes}.
     */
    @Test
    public void addInnerTypeIncrementsInnerTypeCount()
    {
        // Given
        TypeMetrics aMetrics = new TypeMetrics("i", TypeMetrics.Kind.INTERFACE);
        int aNumInnerTypes = aMetrics.getNumInnerTypes();

        // When
        aMetrics.add(new TypeMetrics("e", TypeMetrics.Kind.ENUM));

        // Then
        assertEquals(++aNumInnerTypes, aMetrics.getNumInnerTypes());

        // When
        aMetrics.add(new TypeMetrics("c", TypeMetrics.Kind.CLASS));

        // Then
        assertEquals(++aNumInnerTypes, aMetrics.getNumInnerTypes());
    }


    /**
     * A {@code FieldMetrics} instance passed to {@code add} should be returned by
     * {@code getFields}.
     */
    @Test
    public void fieldMetricsIsReturnedByGetter()
    {
        // Given
        FieldMetrics aFieldMetrics = new FieldMetrics("f", FieldMetrics.Kind.INSTANCE_FIELD);
        TypeMetrics aTypeMetrics = new TypeMetrics("c", TypeMetrics.Kind.CLASS);

        // When
        aTypeMetrics.add(aFieldMetrics);

        // Then
        assertSame(aFieldMetrics, aTypeMetrics.getFields().iterator().next());
    }


    /**
     * A {@code MethodMetrics} instance passed to {@code add} should be returned by
     * {@code getMethods}.
     */
    @Test
    public void methodMetricsIsReturnedByGetter()
    {
        // Given
        MethodMetrics aMethodMetrics = new MethodMetrics("m", MethodMetrics.Kind.INSTANCE_METHOD);
        TypeMetrics aTypeMetrics = new TypeMetrics("c", TypeMetrics.Kind.CLASS);

        // When
        aTypeMetrics.add(aMethodMetrics);

        // Then
        assertSame(aMethodMetrics, aTypeMetrics.getMethods().iterator().next());
    }


    /**
     * A {@code TypeMetrics} instance passed to {@code add} should be returned by
     * {@code getInnerTypes}.
     */
    @Test
    public void innerTypeMetricsIsReturnedByGetter()
    {
        // Given
        TypeMetrics aInnerType = new TypeMetrics("i", TypeMetrics.Kind.INTERFACE);
        TypeMetrics aTypeMetrics = new TypeMetrics("ec", TypeMetrics.Kind.ENUM_CONSTANT);

        // When
        aTypeMetrics.add(aInnerType);

        // Then
        assertSame(aInnerType, aTypeMetrics.getInnerTypes().iterator().next());
    }


    @Override
    protected TypeMetrics createInstance(String pName)
    {
        return new TypeMetrics(pName, TypeMetrics.Kind.ANNOTATION);
    }
}
