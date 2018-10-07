/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
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
 * Unit tests related to parsing and collecting metrics for classes.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class ClassCollectTest extends ClassAndEnumCollectTestBase
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
        String aSrc = "class X { X() {} }";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        assertEquals(MethodMetrics.Kind.CONSTRUCTOR, getFirstMethod(aMetrics).getKind());
    }


    /**
     * A class with all kinds of members should have the corresponding code element metrics
     * collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fullExampleCollectsTheExpectedMetrics() throws ParseException
    {
        // Given
        String aResourceName = "/FullClass.java";
        String aClassName = "FullClass";

        // When
        JavaMetrics aMetrics = collectFromResource(aResourceName);

        // Assert package metrics name
        PackageMetrics aPackage = getFirstPackage(aMetrics);
        assertEquals("org.myire.scent", aPackage.getName());

        // Assert compilation unit name and file header block comment.
        CompilationUnitMetrics aCompilationUnit = getFirstCompilationUnit(aPackage);
        assertEquals(aResourceName, aCompilationUnit.getName());
        CommentMetrics aComments = aCompilationUnit.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
        assertEquals(26, aComments.getBlockCommentsLength());

        // Assert class name and kind, and class JavaDoc
        TypeMetrics aClass = getFirstType(aCompilationUnit);
        assertEquals(aClassName, aClass.getName());
        assertEquals(TypeMetrics.Kind.CLASS, aClass.getKind());
        aComments = aClass.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(4, aComments.getNumJavaDocLines());
        assertEquals(34, aComments.getJavaDocCommentsLength());

        // Assert class field
        Iterator<FieldMetrics> aFields = aClass.getFields().iterator();
        FieldMetrics aField = aFields.next();
        assertEquals("staticField", aField.getName());
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, aField.getKind());
        assertEquals(0, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(38, aComments.getLineCommentsLength());

        // Assert first instance field
        aField = aFields.next();
        assertEquals("instanceField1", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        assertEquals(0, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
        assertEquals(43, aComments.getBlockCommentsLength());

        // Assert second instance field
        aField = aFields.next();
        assertEquals("instanceField2", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        assertEquals(1, aField.getStatements().getNumStatements());

        // Assert class initializer
        Iterator<MethodMetrics> aMethods = aClass.getMethods().iterator();
        MethodMetrics aMethod = aMethods.next();
        assertEquals("clinit", aMethod.getName());
        assertEquals(MethodMetrics.Kind.STATIC_INITIALIZER, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());

        // Assert instance initializer
        aMethod = aMethods.next();
        assertEquals("init", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_INITIALIZER, aMethod.getKind());
        aComments = aMethod.getComments();
        assertEquals(3, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
        assertEquals(46, aComments.getBlockCommentsLength());
        assertEquals(1, aMethod.getStatements().getNumStatements());

        // Assert constructor
        aMethod = aMethods.next();
        assertEquals(aClassName + "()", aMethod.getName());
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

        // Assert method with local class
        aMethod = aMethods.next();
        assertEquals("void methodWithLocalClass()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        TypeMetrics aLocalType = aMethod.getLocalTypes().iterator().next();
        assertEquals("LocalClass", aLocalType.getName());
        assertEquals(TypeMetrics.Kind.CLASS, aLocalType.getKind());
        aComments = aLocalType.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());
        assertEquals(29, aComments.getBlockCommentsLength());
        aFields = aLocalType.getFields().iterator();
        aField = aFields.next();
        assertEquals("fLocalClassField", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        assertEquals(1, aField.getStatements().getNumStatements());

        // Assert method with anonymous class
        aMethod = aMethods.next();
        assertEquals("Object methodWithAnonymousClass()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.STATIC_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aLocalType = aMethod.getLocalTypes().iterator().next();
        assertEquals("Anonymous$AutoCloseable", aLocalType.getName());
        assertEquals(TypeMetrics.Kind.ANONYMOUS_CLASS, aLocalType.getKind());
        Iterator<MethodMetrics> aAnonymousMethods = aLocalType.getMethods().iterator();
        MethodMetrics aAnonymousMethod = aAnonymousMethods.next();
        assertEquals("void close()", aAnonymousMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aAnonymousMethod.getKind());
        assertEquals(1, aAnonymousMethod.getStatements().getNumStatements());
        aComments = aAnonymousMethod.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(18, aComments.getLineCommentsLength());

        // Assert abstract method
        aMethod = aMethods.next();
        assertEquals("String abstractMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.ABSTRACT_METHOD, aMethod.getKind());
        assertEquals(0, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(18, aComments.getLineCommentsLength());

        // Assert native method
        aMethod = aMethods.next();
        assertEquals("double nativeMethod(int)", aMethod.getName());
        assertEquals(MethodMetrics.Kind.NATIVE_METHOD, aMethod.getKind());
        assertEquals(0, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());
        assertEquals(15, aComments.getBlockCommentsLength());

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
        aComments = aInnerType.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(4, aComments.getNumBlockCommentLines());
        assertEquals(57, aComments.getBlockCommentsLength());
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
        aComments = aInnerType.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
        assertEquals(29, aComments.getJavaDocCommentsLength());
        aField = getFirstField(aInnerType);
        assertEquals("value", aField.getName());
        assertEquals(FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT, aField.getKind());
    }


    @Override
    protected TypeMetrics.Kind getTypeKind()
    {
        return TypeMetrics.Kind.CLASS;
    }


    @Override
    protected String createTypeDeclarationStart(String pName)
    {
        return "public class " + pName + " {";
    }
}
