/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import org.junit.Test;
import static org.junit.Assert.fail;


/**
 * Unit tests for {@code TextReportWriter}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class TextReportWriterTest extends OutputStreamReportWriterTestBase
{
    @Override
    protected OutputStreamReportWriter createReportWriter(OutputStream pOutputStream)
    {
        return new TextReportWriter(pOutputStream);
    }


    /**
     * Calling the two-args constructor with a null {@code OutputStream} argument should throw a
     * {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void twoArgsCtorThrowsForNullOutputStream()
    {
        // When
        new TextReportWriter(null, StandardCharsets.UTF_8);
    }


    /**
     * Calling the two-args constructor with a null {@code Charset} argument should throw a
     * {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void twoArgsCtorThrowsForNullCharset()
    {
        // When
        new TextReportWriter(new ByteArrayOutputStream(), null);
    }


    /**
     * The report should contain the date and time from the {@code LocalDateTime} passed to
     * {@code writeReportContents}.
     */
    @Test
    public void reportContainsTimestamp()
    {
        // Given
        LocalDateTime aTimeStamp = LocalDateTime.of(2018, 9, 3, 12, 37, 17);

        // When
        writeReport(aTimeStamp, null);

        // Then
        assertReportContents(
            "2018-09-03",
            "12:37:17"
        );
    }


    /**
     * The report should contain the version string passed to {@code writeReportContents}.
     */
    @Test
    public void reportContainsVersion()
    {
        // Given
        String aVersion = "v314159";

        // When
        writeReport(null, aVersion);

        // Then
        assertReportContents(aVersion);
    }


    /**
     * The report should contain the modular compilation unit metrics and module declaration
     * metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedModules()
    {
        // When
        collectAndWriteReport(
            "module x { exports org.myire.scent; }",
            "open module y { requires java.sql; requires java.xml;}");

        // Then
        assertReportContents(
            "2 modules",
            "module x",
            "1 exports",
            "open module y",
            "2 requires"
        );
    }


    /**
     * The report should contain the comments from the collected modular compilation unit metrics
     * and module declaration metrics.
     */
    @Test
    public void reportContainsModuleComments()
    {
        // When
        collectAndWriteReport(
            "/* Compilation unit comment */ \n\n // Module comment\n module x { exports org.myire.scent; }"
        );

        // Then
        assertReportContents(
            "1 block comment",
            "1 lines",
            "length 24",
            "1 line comment",
            "length 14"
        );
    }


    /**
     * The report should contain the package metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedPackages()
    {
        // When
        collectAndWriteReport("package x;", "package y;");

        // Then
        assertReportContents(
            "2 packages",
            "package x",
            "package y"
        );
    }


    /**
     * The report should contain the compilation unit metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedCompilationUnits()
    {
        // When
        collectAndWriteReport("package x;", "class X{}", "interface Z {}");

        // Then
        assertReportContents(
            "3 compilation units",
            "Test0.java",
            "Test1.java",
            "Test2.java"
        );
    }


    /**
     * The report should contain the type metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedTypes()
    {
        // When
        collectAndWriteReport("class Y{}", "interface Z{}", "enum W{}", "@interface Q{}");

        // Then
        assertReportContents(
            "4 types",
            "class Y",
            "interface Z",
            "enum W",
            "annotation Q"
        );
    }


    /**
     * The report should contain the field metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedFields()
    {
        // When
        collectAndWriteReport("class Y{int fField; static long cField;}");

        // Then
        assertReportContents(
            "2 fields",
            "instance_field fField",
            "static_field cField"
        );
    }


    /**
     * The report should contain the method metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedMethods()
    {
        // When
        collectAndWriteReport("class Y{ native char n(); void v(){} static void sv(){} }");

        // Then
        assertReportContents(
            "3 methods",
            "native_method char n()",
            "instance_method void v()",
            "static_method void sv()"
        );
    }


    /**
     * The report should contain the inner type metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedInnerTypes()
    {
       // When
        collectAndWriteReport("class Y{ interface InnerY{} }");

        // Then
        assertReportContents(
            "2 types",
            "interface InnerY"
        );
    }


    /**
     * The report should contain the local type metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedLocalTypes()
    {
        // When
        collectAndWriteReport("class X{ void m() { class LocalClass {} } }");

        // Then
        assertReportContents(
            "2 types",
            "class LocalClass"
        );
    }


    /**
     * The report should contain the statement metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedStatements()
    {
        // When
        collectAndWriteReport("class Y{ int f=2; int m(){return 4711;} }");

        // Then
        assertReportContents(
            "2 statements",
            "1 statement"
        );
    }


    /**
     * The report should contain the line comment metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedLineComments()
    {
        // When
        collectAndWriteReport(
            "// A class\n" +
            "class Y{\n" +
            "// One field\n" +
            "int f;\n" +
            "// A method\n" +
            "void m(){} }");

        // Then
        assertReportContents(
            "3 line comments",
            "length 24",
            "1 line comment",
            "length 7",
            "length 9",
            "length 8"
        );
    }


    /**
     * The report should contain the block comment metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedBlockComments()
    {
        // When
        collectAndWriteReport(
            "/* This class */\n" +
            "class Y{\n" +
            "/* has one\n" +
            "field */\n" +
            "int f;\n" +
            "/* and one method */\n" +
            "void m(){} }");

        // Then
        assertReportContents(
            "3 block comments",
            "4 lines",
            "length 36",
            "1 block comment",
            "1 line",
            "length 10",
            "2 lines",
            "length 12",
            "length 14"
        );
    }


    /**
     * The report should contain the JavaDoc comment metrics collected from the Java sources.
     */
    @Test
    public void reportContainsCollectedJavaDocComments()
    {
        // When
        collectAndWriteReport(
            "/** This class is documented. */\n" +
            "class Y{\n" +
            "/** This field has two\n" +
            "    JavaDoc lines. */\n" +
            "int f;\n" +
            "/** Inner type JavaDoc. */\n" +
            "interface Inner{} }");

        // Then
        assertReportContents(
            "3 JavaDoc comments",
            "4 lines",
            "length 76",
            "1 JavaDoc comment",
            "1 line",
            "length 25",
            "2 lines",
            "length 32",
            "length 19"
        );
    }


    /**
     * Assert that the report written to the {@code OutputStream} passed to
     * {@link #createReportWriter(OutputStream)} contains all of a sequence of strings.
     *
     * @param pContents The strings to assert are part of the report.
     *
     * @throws AssertionError   if one of the strings isn't present in the report text.
     */
    private void assertReportContents(String... pContents)
    {
        String aReport = new String(getReportContents(), StandardCharsets.UTF_8);
        for (String aString : pContents)
            if (!aReport.contains(aString))
                fail('\'' + aString + "' is missing in '" + aReport + '\'');
    }
}
