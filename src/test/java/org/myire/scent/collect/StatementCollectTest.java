/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.collect;

import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import static org.myire.scent.collect.CollectTestUtil.collectFromResource;
import org.myire.scent.metrics.PackageMetrics;
import org.myire.scent.metrics.StatementMetrics;

import static org.myire.scent.collect.CollectTestUtil.collect;
import static org.myire.scent.collect.CollectTestUtil.getFirstMethod;


/**
 * Unit tests related to parsing and collecting metrics for statements.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class StatementCollectTest
{
    static private final String CONSTRUCTOR_PREFIX ="class X { X() {";
    static private final String VOID_METHOD_PREFIX ="class X { void someMethod() {";
    static private final String VOID_METHOD_PARAM_PREFIX ="class X { void someMethod(Object pParam) {";
    static private final String RETURN_TYPE_METHOD_PREFIX ="class X { Object someMethod() {";
    static private final String ANY_METHOD_SUFFIX ="}}";


    /**
     * An {@code assert} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void assertStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "assert pParam != null;",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A {@code break} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void breakStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PREFIX,
                "while(true)",
                "if (Thread.currentThread().isInterrupted())",
                "break;",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(3, aStatements.getNumStatements());
    }


    /**
     * A {@code continue} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void continueStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "while(true) {",
                "if (Thread.currentThread().isInterrupted())",
                "continue;",
                "System.out.println();",
                "}",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(4, aStatements.getNumStatements());
    }


    /**
     * A {@code do} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void doStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "do {",
                "} while(true);",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * An explicit invocation of a constructor should be collected in the method's
     * {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void constructorInvocationStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                CONSTRUCTOR_PREFIX,
                "super();",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A expression statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void expressionStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PREFIX,
                "System.out.println();",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A {@code foreach} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void forEachStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PREFIX,
                "for (Object aKey : System.getProperties().keySet())",
                ";",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A {@code for} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void forStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PREFIX,
                "for (int i=0; i<10; i++)",
                ";",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * An {@code if} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void ifStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "if (pParam != null)",
                "System.out.println(pParam);",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(2, aStatements.getNumStatements());
    }


    /**
     * An {@code if-else} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void ifElseStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "if (pParam != null)",
                "System.out.println(pParam);",
                "else",
                "System.out.println(null);",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(3, aStatements.getNumStatements());
    }


    /**
     * A {@code return} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void returnStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                RETURN_TYPE_METHOD_PREFIX,
                "return null;",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A {@code switch} statement and its cases should be collected in the method's
     * {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void switchStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "switch (pParam.hashCode()) {",
                "case 1: System.out.println(false);",
                "default: System.out.println();",
                "}",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(5, aStatements.getNumStatements());
    }


    /**
     * A {@code synchronized} statement should be collected in the method's
     * {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void synchronizedStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "synchronized (pParam) {",
                "System.out.println();",
                "}",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(2, aStatements.getNumStatements());
    }


    /**
     * A {@code throws} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void throwsStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PREFIX,
                "throw new RuntimeException();",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A {@code try-with} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void tryWithStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PREFIX,
                "try (java.io.InputStream aStream = getClass().getResourceAsStream()) {",
                "}",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(2, aStatements.getNumStatements());
    }


    /**
     * A {@code try-catch} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void tryCatchStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PREFIX,
                "try {",
                "}",
                "catch (Throwable t) {",
                "}",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A {@code try-catch-finally} statement should be collected in the method's
     * {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void tryCatchFinallyStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PREFIX,
                "try {",
                "}",
                "catch (Throwable t) {",
                "}",
                "finally {",
                "}",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A {@code while} statement should be collected in the method's {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void whileStatementIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "while (true);",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
    }


    /**
     * A local variable declaration with initialization should be collected in the method's
     * {@code StatementMetrics}.
     *
     * @throws ParseException   if the test fails unexpectedly.
     */
    @Test
    public void localVariableDeclarationWithInitializationIsCollected() throws ParseException
    {
        // Given
        String[] aSourceLines = {
                VOID_METHOD_PARAM_PREFIX,
                "int x = 1;",
                ANY_METHOD_SUFFIX
        };

        // When
        StatementMetrics aStatements = collectStatementMetrics(aSourceLines);

        // Then
        assertEquals(1, aStatements.getNumStatements());
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
        String aResourceName = "/Statements.java";

        // When
        Iterable<PackageMetrics> aMetrics = collectFromResource(aResourceName);

        // Then
        StatementMetrics aStatements = getFirstMethod(aMetrics).getStatements();
        assertEquals(38, aStatements.getNumStatements());
    }


    static private StatementMetrics collectStatementMetrics(String[] pSourceLines) throws ParseException
    {
        Iterable<PackageMetrics> aMetrics = collect(pSourceLines);
        return getFirstMethod(aMetrics).getStatements();
    }
}
