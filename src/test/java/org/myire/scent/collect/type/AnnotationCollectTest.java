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
import static org.myire.scent.util.CollectTestUtil.getFirstMethod;
import static org.myire.scent.util.CollectTestUtil.getFirstPackage;
import static org.myire.scent.util.CollectTestUtil.getFirstType;


/**
 * Unit tests related to parsing and collecting metrics for annotations.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class AnnotationCollectTest extends TypeCollectTestBase
{
    /**
     * An annotation field should be collected as a {@code FieldMetrics} with the correct name.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fieldIsCollected() throws ParseException
    {
        // Given
        String aName = "cField";
        String aSrc = "@interface TheAnnotation { char " + aName + " = 'c';}";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(aName, aFieldMetrics.getName());
    }


    /**
     * A field in an annotation that is explicitly declared static should be collected as a
     * {@code FieldMetrics} with the kind {@code STATIC_FIELD}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void explicitStaticFieldIsOfStaticKind() throws ParseException
    {
        // Given
        String aSrc = "@interface TheAnnotation { static int field=1;}";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, getFirstField(aMetrics).getKind());
    }


    /**
     * A field in an annotation that is not explicitly declared static should be collected as a
     * {@code FieldMetrics} and be identified to have the kind {@code STATIC_FIELD}, as annotation
     * fields always are static.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void nonExplicitStaticFieldIsOfStaticKind() throws ParseException
    {
        // Given
        String aSrc = "@interface TheAnnotation { int field=2;}";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, getFirstField(aMetrics).getKind());
    }


    /**
     * The initializer in an annotation field is mandatory and should be counted as a statement in
     * the collected {@code FieldMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fieldInitializerCountsAsStatement() throws ParseException
    {
        // Given
        String aSrc = "@interface TheAnnotation { int field=2;}";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        assertEquals(1, getFirstField(aMetrics).getStatements().getNumStatements());
    }


    /**
     * An annotation type element should be collected as a {@code FieldMetrics} with the correct
     * name and kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void annotationTypeElementIsCollected() throws ParseException
    {
        // Given
        String aName = "annotationTypeElement";
        String aSrc = "@interface TheAnnotation { int " + aName + "();}";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(aName, aFieldMetrics.getName());
        assertEquals(FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT, aFieldMetrics.getKind());
    }


    /**
     * An annotation type element without a default value should not be counted as a statement by
     * the metrics collector.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void annotationTypeElementWithoutDefaultDoesNotCountAsStatement() throws ParseException
    {
        // Given
        String aSrc = "public @interface TheAnnotation { int annotationMember();}";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(0, aFieldMetrics.getStatements().getNumStatements());
    }


    /**
     * An annotation type element with a default value should be be counted as a statement by the
     * metrics collector.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void annotationTypeElementWithDefaultCountsAsStatement() throws ParseException
    {
        // Given
        String aSrc = "public @interface TheAnnotation { int annotationMember() default 5;}";

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        FieldMetrics aFieldMetrics = getFirstField(aMetrics);
        assertEquals(1, aFieldMetrics.getStatements().getNumStatements());
    }


    /**
     * An annotation with all kinds of members should have the corresponding code element metrics
     * collected.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void fullExampleCollectsTheExpectedMetrics() throws ParseException
    {
        // Given
        String aResourceName = "/FullAnnotation.java";

        // When
        Iterable<PackageMetrics> aMetrics = collectFromResource(aResourceName);

        // Assert package metrics name
        PackageMetrics aPackage = getFirstPackage(aMetrics);
        assertEquals("org.myire.scent", aPackage.getName());

        // Assert compilation unit name and file header line comment.
        CompilationUnitMetrics aCompilationUnit = getFirstCompilationUnit(aPackage);
        assertEquals(aResourceName, aCompilationUnit.getName());
        CommentMetrics aComments = aCompilationUnit.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(5, aComments.getNumBlockCommentLines());
        assertEquals(132, aComments.getBlockCommentsLength());

        // Assert interface name and kind, and comments
        TypeMetrics aInterface = getFirstType(aCompilationUnit);
        assertEquals("FullAnnotation", aInterface.getName());
        assertEquals(TypeMetrics.Kind.ANNOTATION, aInterface.getKind());

        // Assert explicitly static field
        Iterator<FieldMetrics> aFields = aInterface.getFields().iterator();
        FieldMetrics aField = aFields.next();
        assertEquals("staticField1", aField.getName());
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, aField.getKind());
        assertEquals(1, aField.getStatements().getNumStatements());

        // Assert implicitly static field
        aField = aFields.next();
        assertEquals("staticField2", aField.getName());
        assertEquals(FieldMetrics.Kind.STATIC_FIELD, aField.getKind());
        assertEquals(1, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumLineComments());
        assertEquals(45, aComments.getLineCommentsLength());

        // Assert annotation element without default value
        aField = aFields.next();
        assertEquals("annotationElement", aField.getName());
        assertEquals(FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT, aField.getKind());
        assertEquals(0, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(6, aComments.getNumJavaDocLines());
        assertEquals(68, aComments.getJavaDocCommentsLength());

        // Assert annotation element with default value
        aField = aFields.next();
        assertEquals("annotationElementWithDefault", aField.getName());
        assertEquals(FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT, aField.getKind());
        assertEquals(1, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(5, aComments.getNumJavaDocLines());
        assertEquals(54, aComments.getJavaDocCommentsLength());

        // Assert inner interface
        Iterator<TypeMetrics> aInnerTypes = aInterface.getInnerTypes().iterator();
        TypeMetrics aInnerType = aInnerTypes.next();
        assertEquals("InnerInterface", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.INTERFACE, aInnerType.getKind());
        MethodMetrics aMethod = getFirstMethod(aInnerType);
        assertEquals("int get()", aMethod.getName());
        assertEquals(MethodMetrics.Kind.ABSTRACT_METHOD, aMethod.getKind());

        // Assert inner class
        aInnerType = aInnerTypes.next();
        assertEquals("InnerClass", aInnerType.getName());
        assertEquals(TypeMetrics.Kind.CLASS, aInnerType.getKind());
        aField = getFirstField(aInnerType);
        assertEquals("innerField", aField.getName());
        assertEquals(FieldMetrics.Kind.INSTANCE_FIELD, aField.getKind());
        Iterator<MethodMetrics> aMethods = aInnerType.getMethods().iterator();
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
        assertEquals(4, aComments.getNumJavaDocLines());
        assertEquals(37, aComments.getJavaDocCommentsLength());
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
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
        assertEquals(39, aComments.getBlockCommentsLength());
        aField = getFirstField(aInnerType);
        assertEquals("flag", aField.getName());
        assertEquals(FieldMetrics.Kind.ANNOTATION_TYPE_ELEMENT, aField.getKind());
    }


    @Override
    protected TypeMetrics.Kind getTypeKind()
    {
        return TypeMetrics.Kind.ANNOTATION;
    }


    @Override
    protected String createTypeDeclarationStart(String pName)
    {
        return  "public @interface " + pName + '{';
    }
}
