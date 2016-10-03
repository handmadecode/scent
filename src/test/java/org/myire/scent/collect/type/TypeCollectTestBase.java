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
import static org.junit.Assert.assertFalse;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.CompilationUnitMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.TypeMetrics;

import static org.myire.scent.collect.CollectTestUtil.collect;
import static org.myire.scent.collect.CollectTestUtil.getFirstCompilationUnit;
import static org.myire.scent.collect.CollectTestUtil.getFirstInnerType;
import static org.myire.scent.collect.CollectTestUtil.getFirstType;


/**
 * Abstract base class with unit tests related to parsing and collecting metrics for types of any
 * kind (classes, interface, enums, and annotations).
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class TypeCollectTestBase
{
    /**
     * A type declaration should be collected as a {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void typeDeclarationIsCollected() throws ParseException
    {
        // Given
        String aName = "TheType";

        // When
        Iterable<PackageMetrics> aMetrics = collect(createEmptyTypeDeclaration(aName));

        // Then
        CompilationUnitMetrics aUnit = getFirstCompilationUnit(aMetrics);
        assertEquals(1, aUnit.getNumTypes());
        assertEquals(aName, getFirstType(aUnit).getName());
    }


    /**
     * A type declaration should be collected as a {@code TypeMetrics} with the correct kind.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void typeMetricsHasTheCorrectKind() throws ParseException
    {
        // When
        Iterable<PackageMetrics> aMetrics = collect(createEmptyTypeDeclaration("AnyType"));

        // Then
        assertEquals(getTypeKind(), getFirstType(aMetrics).getKind());
    }


    /**
     * An inner class should be collected as a {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void innerClassIsCollected() throws ParseException
    {
        // Given
        String aName = "InnerClass";
        String aSrc = createTypeDeclarationWithMembers("class " + aName + "{}");

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        TypeMetrics aType = getFirstType(aMetrics);
        assertEquals(1, aType.getNumInnerTypes());
        TypeMetrics aInnerType = getFirstInnerType(aType);
        assertEquals(aName, aInnerType.getName());
        assertEquals(TypeMetrics.Kind.CLASS, aInnerType.getKind());
    }


    /**
     * An inner interface should be collected as a {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void innerInterfaceIsCollected() throws ParseException
    {
        // Given
        String aName = "InnerInterface";
        String aSrc = createTypeDeclarationWithMembers("interface " + aName + "{}");

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        TypeMetrics aType = getFirstType(aMetrics);
        assertEquals(1, aType.getNumInnerTypes());
        TypeMetrics aInnerType = getFirstInnerType(aType);
        assertEquals(aName, aInnerType.getName());
        assertEquals(TypeMetrics.Kind.INTERFACE, aInnerType.getKind());
    }


    /**
     * An inner enum should be collected as a {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void innerEnumIsCollected() throws ParseException
    {
        // Given
        String aName = "InnerEnum";
        String aSrc = createTypeDeclarationWithMembers("enum " + aName + "{}");

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        TypeMetrics aType = getFirstType(aMetrics);
        assertEquals(1, aType.getNumInnerTypes());
        TypeMetrics aInnerType = getFirstInnerType(aType);
        assertEquals(aName, aInnerType.getName());
        assertEquals(TypeMetrics.Kind.ENUM, aInnerType.getKind());
    }


    /**
     * An inner annotation should be collected as a {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void innerAnnotationIsCollected() throws ParseException
    {
        // Given
        String aName = "InnerAnnotation";
        String aSrc = createTypeDeclarationWithMembers("@interface " + aName + "{}");

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        TypeMetrics aType = getFirstType(aMetrics);
        assertEquals(1, aType.getNumInnerTypes());
        TypeMetrics aInnerType = getFirstInnerType(aType);
        assertEquals(aName, aInnerType.getName());
        assertEquals(TypeMetrics.Kind.ANNOTATION, aInnerType.getKind());
    }


    /**
     * Parsing multiple inner types should result in the same number of {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void innerTypesAreCollectedInSeparateMetrics() throws ParseException
    {
        // Given
        String aClassName = "InnerClass";
        String aInterfaceName = "InnerInterface";
        String aEnumName = "InnerEnum";
        String aAnnotationName = "InnerAnnotation";
        String aSrc =
                createTypeDeclarationWithMembers(
                        "class " + aClassName + "{}",
                        "interface " + aInterfaceName + "{}",
                        "enum " + aEnumName + "{}",
                        "@interface " + aAnnotationName + "{}");

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSrc);

        // Then
        TypeMetrics aType = getFirstType(aMetrics);
        assertEquals(4, aType.getNumInnerTypes());
        Iterator<TypeMetrics> aIterator = aType.getInnerTypes().iterator();

        TypeMetrics aInnerType = aIterator.next();
        assertEquals(aClassName, aInnerType.getName());
        assertEquals(TypeMetrics.Kind.CLASS, aInnerType.getKind());

        aInnerType = aIterator.next();
        assertEquals(aInterfaceName, aInnerType.getName());
        assertEquals(TypeMetrics.Kind.INTERFACE, aInnerType.getKind());

        aInnerType = aIterator.next();
        assertEquals(aEnumName, aInnerType.getName());
        assertEquals(TypeMetrics.Kind.ENUM, aInnerType.getKind());

        aInnerType = aIterator.next();
        assertEquals(aAnnotationName, aInnerType.getName());
        assertEquals(TypeMetrics.Kind.ANNOTATION, aInnerType.getKind());

        assertFalse(aIterator.hasNext());
    }


    /**
     * A block comment for a type should be collected in the {@code CommentMetrics} of the type's
     * {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleBlockCommentForTypeIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "/* Comment about the type */",
                createEmptyTypeDeclaration("X")
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstType(aMetrics).getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());
    }


    /**
     * Multiple block comments for a type should be collected in the {@code CommentMetrics} of the
     * type's {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiBlockCommentForTypeIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "/* Comment about the type */",
                "/* Another comment about the type */",
                "/* A third comment about the type */",
                createEmptyTypeDeclaration("X")
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstType(aMetrics).getComments();
        assertEquals(3, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
    }


    /**
     * A single line comment for a type should be collected in the {@code CommentMetrics} of the
     * type's {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void singleLineCommentForTypeIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "// Comment about the type",
                createEmptyTypeDeclaration("X")
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstType(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * Multiple single line comment for a type should be collected in the {@code CommentMetrics} of
     * the type's {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void multiLineCommentForTypeIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "// Comment about the type",
                "// spans two lines",
                createEmptyTypeDeclaration("X")
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstType(aMetrics).getComments();
        assertEquals(2, aComments.getNumLineComments());
    }


    /**
     * A trailing line comment for a type should be collected in the {@code CommentMetrics} of the
     * type's {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void trailingLineCommentForTypeIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createEmptyTypeDeclaration("X") + "// Comment about the type"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstType(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * A JavaDoc comment for a type should be collected in the {@code CommentMetrics} of the type's
     * {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void javadocForTypeIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                "/**",
                " * A type.",
                " */",
                createEmptyTypeDeclaration("X")
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstType(aMetrics).getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());
    }


    /**
     * Get the {@code TypeMetrics.Kind} for the type being tested by this class.
     *
     * @return  The type's kind.
     */
    abstract protected TypeMetrics.Kind getTypeKind();

    /**
     * Create the start of a type declaration up to the point where the first member should be
     * placed.
     *
     * @param pName The name of the type.
     *
     * @return  The start of a type declaration with the specified name.
     */
    abstract protected String createTypeDeclarationStart(String pName);


    /**
     * Create an empty declaration of the type being tested by this class.
     *
     * @param pName The name of the type.
     *
     * @return  An empty type declaration with the specified name.
     */
    protected String createEmptyTypeDeclaration(String pName)
    {
        return createTypeDeclarationStart(pName) + '}';
    }


    /**
     * Create a type declaration with members of the type being tested by this class.
     *
     * @param pMembers      The member declarations to put inside the type declaration.
     *
     * @return  A type declaration with the specified members.
     */
    protected String createTypeDeclarationWithMembers(String... pMembers)
    {
        StringBuilder aBuilder = new StringBuilder(createTypeDeclarationStart("X"));
        for (String aMember : pMembers)
            aBuilder.append(aMember);

        return aBuilder.append('}').toString();
    }
}
