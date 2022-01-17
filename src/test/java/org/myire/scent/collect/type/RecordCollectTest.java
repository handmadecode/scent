/*
 * Copyright 2022 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.type;

import java.text.ParseException;
import java.util.Iterator;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.metrics.MethodMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.TypeMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.collectFromResource;
import static org.myire.scent.util.CollectTestUtil.getFirstCompilationUnit;
import static org.myire.scent.util.CollectTestUtil.getFirstField;
import static org.myire.scent.util.CollectTestUtil.getFirstMethod;
import static org.myire.scent.util.CollectTestUtil.getFirstPackage;
import static org.myire.scent.util.CollectTestUtil.getFirstType;


/**
 * Unit tests related to parsing and collecting metrics for record classes.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class RecordCollectTest extends ClassTypeCollectTestBase
{
    @Test
    @Ignore("https://github.com/javaparser/javaparser/issues/3260")
    public void innerRecordIsCollected() throws ParseException
    {
        super.innerRecordIsCollected();
    }


    /**
     * A record's compact canonical constructor should be collected a method of the constructor
     * kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void compactCanonicalConstructorIsCollected() throws ParseException
    {
        // Given
        String aSrc = "record X(int f) { X {f++;} }";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        assertEquals(MethodMetrics.Kind.CONSTRUCTOR, getFirstMethod(aMetrics).getKind());
    }


    /**
     * A record field should be collected as a {@code FieldMetrics} with the correct name.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void recordFieldIsCollected() throws ParseException
    {
        // Given
        String aName = "f";
        String aSrc = "record Rec(int " + aName + ") {}";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(aName, aFieldMetrics.getName());
    }


    /**
     * All record fields should be collected as {@code FieldMetrics} with the correct names.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void recordFieldsAreCollected() throws ParseException
    {
        // Given
        String[] aFieldNames = {"fldA", "fldB", "fldC"};
        String aSrc =
            "record Rec(int " + aFieldNames[0] +
                ", String " + aFieldNames[1] +
                ", float "+ aFieldNames[2] +
                ") {}";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        TypeMetrics aTypeMetrics = getFirstType(aMetrics);
        assertEquals(3, aTypeMetrics.getNumFields());
        int i = 0;
        for (FieldMetrics aFieldMetrics : aTypeMetrics.getFields())
            assertEquals(aFieldNames[i++], aFieldMetrics.getName());
    }


    /**
     * An explicitly declared getter for a record field should be collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void explicitlyDeclaredGetterIsCollected() throws ParseException
    {
        // Given
        String aGetter = "int getF()";
        String aSrc = "record Rec(int f) {" + aGetter + " {return f;}}";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        MethodMetrics aMethodMetrics = getFirstMethod(aMetrics);
        assertEquals(aGetter, aMethodMetrics.getName());
    }


    /**
     * A record class with all kinds of members should have the corresponding code element metrics
     * collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fullExampleCollectsTheExpectedMetrics() throws ParseException
    {
        // Given
        String aResourceName = "/FullRecord.java";
        String aClassName = "FullRecord";

        // When
        JavaMetrics aMetrics = collectFromResource(aResourceName);

        // Assert package metrics name
        PackageMetrics aPackage = getFirstPackage(aMetrics);
        assertEquals("org.myire.scent", aPackage.getName());

        // Assert compilation unit name and file header block comment.
        CompilationUnitMetrics aCompilationUnit = getFirstCompilationUnit(aPackage);
        assertEquals(aResourceName, aCompilationUnit.getName());
        CommentMetrics aComments = aCompilationUnit.getComments();
        assertEquals(1, aComments.getNumLineComments());

        // Assert record class name and kind, and class JavaDoc
        TypeMetrics aClass = getFirstType(aCompilationUnit);
        assertEquals(aClassName, aClass.getName());
        assertEquals(TypeMetrics.Kind.RECORD, aClass.getKind());
        aComments = aClass.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
        assertEquals(21, aComments.getJavaDocCommentsLength());

        // Assert first instance field
        Iterator<FieldMetrics> aFields = aClass.getFields().iterator();
        FieldMetrics aField = aFields.next();
        assertEquals("instanceField1", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        assertEquals(0, aField.getStatements().getNumStatements());

        // Assert second instance field
        aField = aFields.next();
        assertEquals("instanceField2", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        assertEquals(0, aField.getStatements().getNumStatements());

        // Assert class field
        aField = aFields.next();
        assertEquals("staticField", aField.getName());
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, aField.getKind());
        assertEquals(0, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(38, aComments.getLineCommentsLength());

        // Assert class initializer
        Iterator<MethodMetrics> aMethods = aClass.getMethods().iterator();
        MethodMetrics aMethod = aMethods.next();
        assertEquals("clinit", aMethod.getName());
        assertEquals(MethodMetrics.Kind.STATIC_INITIALIZER, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());

        // Assert compact canonical constructor
        aMethod = aMethods.next();
        assertEquals(aClassName + "()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.CONSTRUCTOR, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(29, aComments.getLineCommentsLength());

        // Assert constructor
        aMethod = aMethods.next();
        assertEquals(aClassName + "()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.CONSTRUCTOR, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());

        // Assert overridden getter method
        aMethod = aMethods.next();
        assertEquals("String f2()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aMethod.getKind());
        assertEquals(2, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(4, aComments.getNumJavaDocLines());
        assertEquals(39, aComments.getJavaDocCommentsLength());

        // Assert instance method
        aMethod = aMethods.next();
        assertEquals("int instanceMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(5, aComments.getNumJavaDocLines());
        assertEquals(47, aComments.getJavaDocCommentsLength());

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
        Iterator<TypeMetrics> aInnerTypes = aClass.getInnerTypes().iterator();
        TypeMetrics aInnerType = aInnerTypes.next();
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
        aFields = aInnerType.getFields().iterator();
        aField = aFields.next();
        assertEquals("ENUM_CONSTANT_1", aField.getName());
        assertEquals(FieldMetrics.Kind.ENUM_CONSTANT, aField.getKind());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(37, aComments.getLineCommentsLength());
        aField = aFields.next();
        assertEquals("ENUM_CONSTANT_2", aField.getName());
        assertEquals(FieldMetrics.Kind.ENUM_CONSTANT, aField.getKind());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(1, aComments.getNumJavaDocLines());
        assertEquals(26, aComments.getJavaDocCommentsLength());

        // Assert inner annotation
        aInnerType = aInnerTypes.next();
        assertEquals("InnerAnnotation", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.ANNOTATION, aInnerType.getKind());
        aField = getFirstField(aInnerType);
        assertEquals("name", aField.getName());
        assertEquals(FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT, aField.getKind());

        // Assert inner record
        // Pending "https://github.com/javaparser/javaparser/issues/3260"
        /*
        aInnerType = aInnerTypes.next();
        assertEquals("InnerRecord", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.RECORD, aInnerType.getKind());
        aFields = aInnerType.getFields().iterator();
        aField = aFields.next();
        assertEquals("recordField", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        */

        // Assert no more inner types
        assertFalse(aInnerTypes.hasNext());
    }


    @Override
    protected TypeMetrics.Kind getTypeKind()
    {
        return TypeMetrics.Kind.RECORD;
    }


    @Override
    protected String createTypeDeclarationStart(String pName)
    {
        return  "public record " + pName + "(){";
    }
}
