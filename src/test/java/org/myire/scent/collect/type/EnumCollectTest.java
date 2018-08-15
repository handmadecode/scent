/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.type;

import java.text.ParseException;
import java.util.Iterator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.MethodMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.TypeMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.collectFromResource;
import static org.myire.scent.util.CollectTestUtil.getFirstCompilationUnit;
import static org.myire.scent.util.CollectTestUtil.getFirstField;
import static org.myire.scent.util.CollectTestUtil.getFirstInnerType;
import static org.myire.scent.util.CollectTestUtil.getFirstMethod;
import static org.myire.scent.util.CollectTestUtil.getFirstPackage;
import static org.myire.scent.util.CollectTestUtil.getFirstType;


/**
 * Unit tests related to parsing and collecting metrics for enums.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class EnumCollectTest extends ClassAndEnumCollectTestBase
{
    /**
     * A constructor should be collected as a {@code MethodMetrics} with the correct kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void constructorHasTheCorrectKind() throws ParseException
    {
        // Given
        String aSrc = "enum X {; private X() {} }";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        assertEquals(MethodMetrics.Kind.CONSTRUCTOR, getFirstMethod(aMetrics).getKind());
    }


    /**
     * An enum constant without a class body should be collected as a {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void enumConstantWithoutBodyIsCollected() throws ParseException
    {
        // Given
        String aName = "THINGY";
        String aSrc = "enum X { " + aName + '}';

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        TypeMetrics aType = getFirstType(aMetrics);
        assertEquals(1, aType.getNumFields());
        assertEquals(aName, getFirstField(aType).getName());
    }


    /**
     * An enum constant without a class body should be collected as a {@code FieldMetrics} with the
     * correct kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void enumConstantWithoutBodyHasTheCorrectKind() throws ParseException
    {
        // Given
        String aSrc = "enum X {THINGY;}";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        assertEquals(FieldMetrics.Kind.ENUM_CONSTANT, getFirstField(aMetrics).getKind());
    }


    /**
     * An enum constant with a class body should be collected as a {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void enumConstantWithBodyIsCollected() throws ParseException
    {
        // Given
        String aName = "THINGY";
        String aSrc = "public enum Things { " + aName + " {int bodyField; }; }";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        TypeMetrics aEnumMetrics = getFirstType(aMetrics);
        assertEquals(1, aEnumMetrics.getNumInnerTypes());
        TypeMetrics aEnumConstantMetrics = getFirstInnerType(aEnumMetrics);
        assertEquals(aName, aEnumConstantMetrics.getName());
    }


    /**
     * An enum constant with a class body should be collected as a {@code TypeMetrics} with the
     * correct kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void enumConstantWithBodyHasTheCorrectKind() throws ParseException
    {
        // Given
        String aSrc = "public enum Things { THANGY {int fField; } ; }";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        assertEquals(TypeMetrics.Kind.ENUM_CONSTANT, getFirstInnerType(aMetrics).getKind());
    }


    /**
     * An enum with all kinds of members should have the corresponding code element metrics
     * collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fullExampleCollectsTheExpectedMetrics() throws ParseException
    {
        // Given
        String aResourceName = "/FullEnum.java";
        String aEnumName = "FullEnum";

        // When
        Iterable<PackageMetrics> aMetrics = collectFromResource(aResourceName);

        // Assert package metrics name
        PackageMetrics aPackage = getFirstPackage(aMetrics);
        assertEquals("org.myire.scent", aPackage.getName());

        // Assert compilation unit name and file header block comments.
        CompilationUnitMetrics aCompilationUnit = getFirstCompilationUnit(aPackage);
        assertEquals(aResourceName, aCompilationUnit.getName());
        CommentMetrics aComments = aCompilationUnit.getComments();
        assertEquals(2, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
        assertEquals(54, aComments.getBlockCommentsLength());

        // Assert enum name and kind, and comment
        TypeMetrics aClass = getFirstType(aCompilationUnit);
        assertEquals(aEnumName, aClass.getName());
        assertEquals(TypeMetrics.Kind.ENUM, aClass.getKind());
        aComments = aClass.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(32, aComments.getLineCommentsLength());

        // Assert enum constant without body
        Iterator<FieldMetrics> aFields = aClass.getFields().iterator();
        FieldMetrics aField = aFields.next();
        assertEquals("ENUM_CONSTANT_1", aField.getName());
        assertEquals(FieldMetrics.Kind.ENUM_CONSTANT, aField.getKind());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(1, aComments.getNumJavaDocLines());
        assertEquals(23, aComments.getJavaDocCommentsLength());

        // Assert enum constant with body
        Iterator<TypeMetrics> aInnerTypes = aClass.getInnerTypes().iterator();
        TypeMetrics aInnerType = aInnerTypes.next();
        assertEquals("ENUM_CONSTANT_2", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.ENUM_CONSTANT, aInnerType.getKind());
        aComments = aInnerType.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
        assertEquals(56, aComments.getJavaDocCommentsLength());
        MethodMetrics aMethod = getFirstMethod(aInnerType);
        assertEquals("int instanceMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aMethod.getKind());

        // Assert class field
        aField = aFields.next();
        assertEquals("staticField", aField.getName());
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, aField.getKind());
        assertEquals(0, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(38, aComments.getLineCommentsLength());

        // Assert instance field
        aField = aFields.next();
        assertEquals("instanceField", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        assertEquals(0, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());
        assertEquals(42, aComments.getBlockCommentsLength());

        // Assert class initializer
        Iterator<MethodMetrics> aMethods = aClass.getMethods().iterator();
        aMethod = aMethods.next();
        assertEquals("clinit", aMethod.getName());
        assertEquals(MethodMetrics.Kind.STATIC_INITIALIZER, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());

        // Assert instance initializer
        aMethod = aMethods.next();
        assertEquals("init", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_INITIALIZER, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());

        // Assert constructor
        aMethod = aMethods.next();
        assertEquals(aEnumName + "()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.CONSTRUCTOR, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(39, aComments.getLineCommentsLength());

        // Assert instance method
        aMethod = aMethods.next();
        assertEquals("int instanceMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(8, aComments.getNumJavaDocLines());
        assertEquals(64, aComments.getJavaDocCommentsLength());

        // Assert class method
        aMethod = aMethods.next();
        assertEquals("int classMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.STATIC_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(2, aComments.getNumJavaDocLines());
        assertEquals(47, aComments.getJavaDocCommentsLength());

        // Assert inner interface
        aInnerType = aInnerTypes.next();
        assertEquals("InnerInterface", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.INTERFACE, aInnerType.getKind());
        aComments = aInnerType.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
        assertEquals(49, aComments.getBlockCommentsLength());
        aMethod = getFirstMethod(aInnerType);
        assertEquals("int get()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.ABSTRACT_METHOD, aMethod.getKind());

        // Assert inner class
        aInnerType = aInnerTypes.next();
        assertEquals("InnerClass", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.CLASS, aInnerType.getKind());
        aComments = aInnerType.getComments();
        assertEquals(2, aComments.getNumLineComments());
        assertEquals(43, aComments.getLineCommentsLength());
        aField = getFirstField(aInnerType);
        assertEquals("innerField", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        aMethods = aInnerType.getMethods().iterator();
        aMethod = aMethods.next();
        assertEquals(aInnerType.getName() + "(int)", aMethod.getName());
        assertEquals(MethodMetrics.Kind.CONSTRUCTOR, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aMethod = aMethods.next();
        assertEquals("int get()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());

        // Assert inner enum
        aInnerType = aInnerTypes.next();
        assertEquals("InnerEnum", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.ENUM, aInnerType.getKind());
        aComments = aInnerType.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(7, aComments.getNumJavaDocLines());
        assertEquals(52, aComments.getJavaDocCommentsLength());
        aFields = aInnerType.getFields().iterator();
        aField = aFields.next();
        assertEquals("INNER_CONSTANT_1", aField.getName());
        assertEquals(FieldMetrics.Kind.ENUM_CONSTANT, aField.getKind());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(37, aComments.getLineCommentsLength());
        aField = aFields.next();
        assertEquals("INNER_CONSTANT_2", aField.getName());
        assertEquals(FieldMetrics.Kind.ENUM_CONSTANT, aField.getKind());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());
        assertEquals(32, aComments.getBlockCommentsLength());

        // Assert inner annotation
        aInnerType = aInnerTypes.next();
        assertEquals("InnerAnnotation", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.ANNOTATION, aInnerType.getKind());
        aField = getFirstField(aInnerType);
        assertEquals("factor", aField.getName());
        assertEquals(FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT, aField.getKind());
    }


    @Override
    protected TypeMetrics.Kind getTypeKind()
    {
        return TypeMetrics.Kind.ENUM;
    }


    @Override
    protected String createTypeDeclarationStart(String pName)
    {
        return  "public enum " + pName + "{;";
    }
}
