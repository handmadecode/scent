/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect.method;

import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import org.myire.scent.metrics.CommentMetrics;
import org.myire.scent.metrics.FieldMetrics;
import org.myire.scent.metrics.MethodMetrics;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.TypeMetrics;

import static org.myire.scent.util.CollectTestUtil.collect;
import static org.myire.scent.util.CollectTestUtil.getFirstField;
import static org.myire.scent.util.CollectTestUtil.getFirstLocalType;
import static org.myire.scent.util.CollectTestUtil.getFirstMethod;


/**
 * Abstract base class with unit tests related to parsing and collecting metrics for methods that
 * have a body.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class MethodWithBodyCollectTestBase extends MethodCollectTestBase
{
    /**
     * A local class declared inside a method should be collected as a {@code TypeMetrics} in the
     * method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void localClassIsCollected() throws ParseException
    {
        // Given
        String aName = "LocalClass";
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodDeclarationStart(createMethodName()),
                "class " + aName + " {}",
                "}",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        MethodMetrics aMethod = getFirstMethod(aMetrics);
        assertEquals(1, aMethod.getNumLocalTypes());
        TypeMetrics aLocalType = getFirstLocalType(aMethod);
        assertEquals(aName, aLocalType.getName());
        assertEquals(TypeMetrics.Kind.CLASS, aLocalType.getKind());
    }


    /**
     * A local class declared inside a method should have its fields, methods, statements and
     * comments collected in the {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void localClassChildrenCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodDeclarationStart(createMethodName()),
                "/**",
                " * JavaDoc for local class",
                " */",
                "class LocalClass {",
                "// A field",
                "int f1 = 1;",
                "",
                "/* A method */",
                "void someMethod() {",
                "// This method prints",
                "System.out.println();",
                "System.err.println();",
                "}",
                "}",
                "}",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        MethodMetrics aMethod = getFirstMethod(aMetrics);
        CommentMetrics aComments = aMethod.getComments();
        assertEquals(0, aMethod.getStatements().getNumStatements());
        assertEquals(0, aComments.getNumBlockComments());
        assertEquals(0, aComments.getNumBlockCommentLines());
        assertEquals(0, aComments.getNumLineComments());
        assertEquals(0, aComments.getNumJavaDocComments());
        assertEquals(0, aComments.getNumJavaDocLines());

        TypeMetrics aLocalType = getFirstLocalType(aMethod);
        aComments = aLocalType.getComments();
        assertEquals(1, aComments.getNumJavaDocComments());
        assertEquals(3, aComments.getNumJavaDocLines());

        FieldMetrics aField = getFirstField(aLocalType);
        assertEquals(1, aField.getStatements().getNumStatements());
        assertEquals(1, aField.getComments().getNumLineComments());

        aMethod = getFirstMethod(aLocalType);
        assertEquals(2, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * An anonymous class declared inside a method should be collected as a {@code TypeMetrics} in
     * the method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void anonymousClassIsCollected() throws ParseException
    {
        // Given
        String aName = "Anonymous$AutoCloseable";
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodDeclarationStart(createMethodName()),
                "AutoCloseable ac = new AutoCloseable() {",
                "@Override",
                "public void close() {}" +
                "};",
                "}",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        MethodMetrics aMethod = getFirstMethod(aMetrics);
        assertEquals(1, aMethod.getNumLocalTypes());
        TypeMetrics aLocalType = getFirstLocalType(aMethod);
        assertEquals(aName, aLocalType.getName());
        assertEquals(TypeMetrics.Kind.ANONYMOUS_CLASS, aLocalType.getKind());
    }


    /**
     * An anonymous class declared inside a method should have its fields, methods, statements and
     * comments collected in the {@code TypeMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void anonymousClassChildrenCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodDeclarationStart(createMethodName()),
                "/*",
                " * Block comment for anonymous class belongs to assignment statement",
                " */",
                "AutoCloseable ac = new AutoCloseable() {",
                "/* A field */",
                "int f1 = 1;",
                "@Override",
                "public void close() {" +
                "/* This method prints */",
                "System.out.println();",
                "System.err.println();",
                "}",
                "};",
                "}",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        MethodMetrics aMethod = getFirstMethod(aMetrics);
        CommentMetrics aComments = aMethod.getComments();
        assertEquals(1, aMethod.getStatements().getNumStatements());
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(3, aComments.getNumBlockCommentLines());
        assertEquals(0, aComments.getNumLineComments());
        assertEquals(0, aComments.getNumJavaDocComments());
        assertEquals(0, aComments.getNumJavaDocLines());

        TypeMetrics aLocalType = getFirstLocalType(aMethod);
        aComments = aLocalType.getComments();
        assertEquals(0, aComments.getNumBlockComments());
        assertEquals(0, aComments.getNumBlockCommentLines());

        FieldMetrics aField = getFirstField(aLocalType);
        assertEquals(1, aField.getStatements().getNumStatements());
        aComments = aField.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());

        aMethod = getFirstMethod(aLocalType);
        assertEquals(2, aMethod.getStatements().getNumStatements());
        aComments = aMethod.getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());
    }


    /**
     * An object creation inside a method that doesn't declare an anonymous class should <b>not</b>
     * be collected as a {@code TypeMetrics} in the method's {@code MethodMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void simpleObjectCreationIsNotCollectedAsAnonymousClass() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodDeclarationStart(createMethodName()),
                "Object o = new Object();",
                "}",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        MethodMetrics aMethod = getFirstMethod(aMetrics);
        assertEquals(0, aMethod.getNumLocalTypes());
    }


    /**
     * All line comments inside a method should be collected in the method's {@code CommentMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void lineCommentsInMethodAreCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodDeclarationStart(createMethodName()) + "// Comment on the same line as opening brace",
                "// Comment for variable declarations",
                "int f, g;",
                "// This comment",
                "// has two lines",
                "f=1;",
                "// This comment does not belong to a statement",
                "",
                "// Comment for assignment statement",
                "g=2;",
                "} // Comment after method's closing brace",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(7, aComments.getNumLineComments());
    }


    /**
     * A line comment on the same line as the method's signature should be collected in the method's
     * {@code CommentMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void lineCommentOnSameLineAsMethodSignatureIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodSignature(createMethodName()) + "// Comment on the same line as signature",
                "{",
                "}",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(1, aComments.getNumLineComments());
    }


    /**
     * A block comment on the same line as the method's signature should be collected in the
     * method's {@code CommentMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void blockCommentOnSameLineAsMethodSignatureIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodSignature(createMethodName()) + "/* Comment on the same line as signature */",
                "{",
                "}",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(1, aComments.getNumBlockComments());
        assertEquals(1, aComments.getNumBlockCommentLines());
    }


    /**
     * All block comments inside a method should be collected in the method's
     * {@code CommentMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void blockCommentsInMethodAreCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                createTypeDeclarationStart(),
                createMethodDeclarationStart(createMethodName()),
                "/* Comment 1 */",
                "int f, g;",
                "/* Comment 2",
                " spans two lines */",
                "f=1;",
                "/* Comment 3 */",
                "",
                "/* The above comment ",
                "   does not belong to ",
                "   a statement */",
                "g=2;",
                "}",
                "}"
        };

        // When
        Iterable<PackageMetrics> aMetrics = collect(aSourceLines);

        // Then
        CommentMetrics aComments = getFirstMethod(aMetrics).getComments();
        assertEquals(4, aComments.getNumBlockComments());
        assertEquals(7, aComments.getNumBlockCommentLines());
    }


    /**
     * Create the signature of the method being tested by this class.
     *
     * @param pName The name of the method on the form it is expected to be collected.
     *
     * @return  A method signature with the specified name.
     */
    protected String createMethodSignature(String pName)
    {
        return pName;
    }


    /**
     * Create start of a declaration of the method being tested by this class. The start of the
     * method declaration ends at the point where the first statement should  be placed.
     *
     * @param pName The name of the method on the form it is expected to be collected.
     *
     * @return  The start of a method declaration with the specified name.
     */
    protected String createMethodDeclarationStart(String pName)
    {
        return createMethodSignature(pName) + '{';
    }


    /**
     * Create a complete, empty declaration of the method being tested by this class.
     *
     * @param pName The name of the method on the form it is expected to be collected.
     *
     * @return  An empty method declaration.
     */
    @Override
    protected String createMethodDeclaration(String pName)
    {
        return createMethodDeclarationStart(pName) + '}';
    }
}
