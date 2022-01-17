/*
 * Copyright 2016, 2018, 2022 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.type;

import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.MethodMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.getFirstField;
import static org.myire.scent.util.CollectTestUtil.getFirstMethod;


/**
 * Abstract base class with unit tests related to parsing and collecting metrics for normal classes
 * and enum classes.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class ClassAndEnumCollectTestBase extends ClassTypeCollectTestBase
{
    /**
     * An instance field should be collected as a {@code FieldMetrics} with the correct name and
     * kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void instanceFieldIsCollected() throws ParseException
    {
        // Given
        String aName = "fField";
        String aSrc = createTypeDeclarationWithMembers("int " + aName + ";");

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        FieldMetrics aField = getFirstField(aMetrics);
        assertEquals(aName, aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
    }


    /**
     * An instance field declaration without an initializer should not be counted as a statement by
     * the metrics collector.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void instanceFieldWithoutInitializerDoesNotCountAsStatement() throws ParseException
    {
        // Given
        String aSrc = createTypeDeclarationWithMembers("int fField;");

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(0, aFieldMetrics.getStatements().getNumStatements());
    }


    /**
     * An instance field declaration with an initializer should be counted as a statement by the
     * metrics collector.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void instanceFieldWithInitializerCountsAsStatement() throws ParseException
    {
        // Given
        String aSrc = createTypeDeclarationWithMembers("int fField = 4711;");

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(1, aFieldMetrics.getStatements().getNumStatements());
    }


    /**
     * An instance initializer should be collected as a {@code MethodMetrics} with the correct
     * kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void instanceInitializerIsCollected() throws ParseException
    {
        // Given
        String aSrc = createTypeDeclarationWithMembers("{System.out.println();}");

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        assertEquals(MethodMetrics.Kind.INSTANCE_INITIALIZER, getFirstMethod(aMetrics).getKind());
    }
}
