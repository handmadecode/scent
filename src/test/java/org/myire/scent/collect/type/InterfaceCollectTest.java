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
 * Unit tests related to parsing and collecting metrics for interfaces.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class InterfaceCollectTest extends TypeCollectTestBase
{
    /**
     * An interface field should be collected as a {@code FieldMetrics} with the correct name.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void interfaceFieldIsCollected() throws ParseException
    {
        // Given
        String aName = "VALUE";
        String aSrc = "interface Miserable { char " + aName + " = 'c';}";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(aName, aFieldMetrics.getName());
    }


    /**
     * A field in an interface that is explicitly declared static should be collected as a
     * {@code FieldMetrics} with the kind {@code STATIC_FIELD}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void explicitStaticFieldIsOfStaticKind() throws ParseException
    {
        // Given
        String aSrc = "interface Miserable { static int X=1;}";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, getFirstField(aMetrics).getKind());
    }


    /**
     * A field in an interface that is not explicitly declared static should be collected as a
     * {@code FieldMetrics} and be identified to have the kind {@code STATIC_FIELD}, as interface
     * fields always are static.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void nonExplicitStaticFieldIsOfStaticKind() throws ParseException
    {
        // Given
        String aSrc = "interface Miserable { int field=2;}";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, getFirstField(aMetrics).getKind());
    }


    /**
     * An interface field should be be counted as a statement by the metrics collector since it must
     * have an initializer expression.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void interfaceFieldCountsAsStatement() throws ParseException
    {
        // Given
        String aSrc = "interface Miserable { char VALUE = 'c';}";

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(1, aFieldMetrics.getStatements().getNumStatements());
    }


    /**
     * An interface method should be collected as a {@code MethodMetrics} with the correct name and
     * kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void interfaceMethodHasTheCorrectKind() throws ParseException
    {
        // Given
        String aName = "void print()";
        String aSrc = createTypeDeclarationWithMembers(aName + ';');

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        MethodMetrics aMethodMetrics = getFirstMethod(aMetrics);
        assertEquals(aName, aMethodMetrics.getName());
        assertEquals(MethodMetrics.Kind.ABSTRACT_METHOD, aMethodMetrics.getKind());
    }


    /**
     * A default method should be collected as a {@code MethodMetrics} with the correct name and
     * kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void defaultMethodHasTheCorrectKind() throws ParseException
    {
        // Given
        String aName = "void print()";
        String aSrc = createTypeDeclarationWithMembers("default " + aName + "{System.out.println();}");

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        MethodMetrics aMethodMetrics = getFirstMethod(aMetrics);
        assertEquals(aName, aMethodMetrics.getName());
        assertEquals(MethodMetrics.Kind.DEFAULT_METHOD, aMethodMetrics.getKind());
    }


    /**
     * A private instance method in an interface (introduced in Java 9) should be collected as a
     * {@code MethodMetrics} with the correct name and kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void privateInstanceMethodHasTheCorrectKind() throws ParseException
    {
        // Given
        String aName = "void privateMethod()";
        String aSrc = createTypeDeclarationWithMembers("private " + aName + "{System.exit();}");

        // When
        JavaMetrics aMetrics = collect(aSrc);

        // Then
        MethodMetrics aMethodMetrics = getFirstMethod(aMetrics);
        assertEquals(aName, aMethodMetrics.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aMethodMetrics.getKind());
    }


    /**
     * An interface with all kinds of members should have the corresponding code element metrics
     * collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fullExampleCollectsTheExpectedMetrics() throws ParseException
    {
        // Given
        String aResourceName = "/FullInterface.java";

        // When
        JavaMetrics aMetrics = collectFromResource(aResourceName);

        // Assert package metrics name
        PackageMetrics aPackage = getFirstPackage(aMetrics);
        assertEquals("org.myire.scent", aPackage.getName());

        // Assert compilation unit name and file header line comment.
        CompilationUnitMetrics aCompilationUnit = getFirstCompilationUnit(aPackage);
        assertEquals(aResourceName, aCompilationUnit.getName());
        CommentMetrics aComments = aCompilationUnit.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(23, aComments.getLineCommentsLength());

        // Assert interface name and kind, and comments
        TypeMetrics aInterface = getFirstType(aCompilationUnit);
        assertEquals("FullInterface", aInterface.getName());
        assertEquals(TypeMetrics.Kind.INTERFACE, aInterface.getKind());
        aComments = aInterface.getComments();
        assertEquals(2, aComments.getNumBlockComments());
        assertEquals(2, aComments.getNumBlockCommentLines());
        assertEquals(58, aComments.getBlockCommentsLength());

        // Assert explicitly static field
        Iterator<FieldMetrics> aFields = aInterface.getFields().iterator();
        FieldMetrics aField = aFields.next();
        assertEquals("staticField1", aField.getName());
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, aField.getKind());
        assertEquals(1, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(38, aComments.getLineCommentsLength());

        // Assert implicitly static field
        aField = aFields.next();
        assertEquals("staticField2", aField.getName());
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, aField.getKind());
        assertEquals(1, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(3, aComments.getNumLineComments());
        assertEquals(55, aComments.getLineCommentsLength());

        // Assert interface method
        Iterator<MethodMetrics> aMethods = aInterface.getMethods().iterator();
        MethodMetrics aMethod = aMethods.next();
        assertEquals("int interfaceMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.ABSTRACT_METHOD, aMethod.getKind());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(5, aComments.getNumJavaDocLines());
        assertEquals(55, aComments.getJavaDocCommentsLength());

        // Assert default method
        aMethod = aMethods.next();
        assertEquals("int defaultMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.DEFAULT_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(2, aComments.getNumLineComments());
        assertEquals(40, aComments.getLineCommentsLength());

        // Assert class method
        aMethod = aMethods.next();
        assertEquals("int classMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.STATIC_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(4, aComments.getNumBlockComments());
        assertEquals(4, aComments.getNumBlockCommentLines());
        assertEquals(104, aComments.getBlockCommentsLength());

        // Assert private interface method
        aMethod = aMethods.next();
        assertEquals("long privateMethod()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.INSTANCE_METHOD, aMethod.getKind());
        assertEquals(1, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(62, aComments.getLineCommentsLength());

        // Assert inner interface
        Iterator<TypeMetrics> aInnerTypes = aInterface.getInnerTypes().iterator();
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
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(5, aComments.getNumJavaDocLines());
        assertEquals(38 , aComments.getJavaDocCommentsLength());
        aField = getFirstField(aInnerType);
        assertEquals("innerField", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        assertEquals(1, aField.getStatements().getNumStatements());
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
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(40, aComments.getLineCommentsLength());
    }


    @Override
    protected TypeMetrics.Kind getTypeKind()
    {
        return TypeMetrics.Kind.INTERFACE;
    }


    @Override
    protected String createTypeDeclarationStart(String pName)
    {
        return  "public interface " + pName + '{';
    }
}
