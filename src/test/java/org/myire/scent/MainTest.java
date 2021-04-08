/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.myire.scent.file.FileTestBase;


/**
 * Unit tests for the {@code Main} class.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class MainTest extends FileTestBase
{
    private final PrintStream fMockedSystemOut = mock(PrintStream.class);
    private final PrintStream fMockedSystemErr = mock(PrintStream.class);

    private final ByteArrayOutputStream fByteArrayOutputStream = new ByteArrayOutputStream();
    private final PrintStream fByteArraySystemOut = new PrintStream(fByteArrayOutputStream);

    private PrintStream fOriginalSystemOut;
    private PrintStream fOriginalSystemErr;


    /**
     * Restore the system streams to the values that were replaced in {@link #mockSystemStreams()}
     * or {@link #captureSystemOut()}.
     */
    @After
    public void restoreSystemStreams()
    {
        if (fOriginalSystemOut != null)
            System.setOut(fOriginalSystemOut);
        if (fOriginalSystemErr != null)
            System.setErr(fOriginalSystemErr);
    }


    /**
     * Passing the path of an existing Java file to {@code main} should cause the file to be parsed
     * and its metrics to be printed to {@code System.out}.
     *
     * @throws IOException  if creating the test Java file fails.
     */
    @Test
    public void metricsForExistingJavaFileArePrinted() throws IOException
    {
        // Given
        mockSystemStreams();
        Path aJavaFile = createTestFileFromJavaResource("FullClass.java");

        // When
        Main.main(aJavaFile.toString());

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut, atLeastOnce()).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains("1"));
        verifyNoInteractions(fMockedSystemErr);
    }


    /**
     * Passing the path of a directory containing an existing Java file to {@code main} should cause
     * the directory to be processed and its path to be printed to {@code System.out}.
     *
     * @throws IOException  if creating the test Java file fails.
     */
    @Test
    public void fileInDirectoryIsCollected() throws IOException
    {
        // Given (a Java file in the test directory.)
        mockSystemStreams();
        createTestFileFromJavaResource("FullInterface.java");
        String aDirectory = getTestDirectory().toString();

        // When (collect all files in the test directory)
        Main.main(aDirectory);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut, atLeastOnce()).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getAllValues().get(0).contains(aDirectory));
        verifyNoInteractions(fMockedSystemErr);
    }


    /**
     * Passing the path to a non-Java file to {@code main} should cause the file to be ignored.
     *
     * @throws IOException  if creating the test file fails.
     */
    @Test
    public void nonJavaFileIsIgnored() throws IOException
    {
        // Given
        mockSystemStreams();
        Path aFile = createTestFile("NotJava.txt");

        // When
        Main.main(aFile.toString());

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains("0"));
        verifyNoInteractions(fMockedSystemErr);
    }


    /**
     * Passing the path to a non-existing Java file to {@code main} should cause the file to be
     * ignored and an error to be printed to {@code System.err}.
     */
    @Test
    public void nonExistingJavaFileIsIgnored()
    {
        // Given
        mockSystemStreams();
        String aFileName = "DoesNotExist.java";

        // When
        Main.main(aFileName);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains("0"));
        verify(fMockedSystemErr).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
    }


    /**
     * Passing the path to a syntactically invalid Java file to {@code main} should cause an error
     * to be printed.
     *
     * @throws IOException  if creating the test Java file fails.
     */
    @Test
    public void errorIsPrintedForInvalidJavaFile() throws IOException
    {
        // Given
        mockSystemStreams();
        String aFileName = "Invalid.java";
        Path aJavaFile = createTestFileFromJavaResource(aFileName);

        // When
        Main.main(aJavaFile.toString());

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains("0"));
        verify(fMockedSystemErr).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
    }


    /**
     * Specifying the {@code -text} option should result in a plain text report.
     *
     * @throws IOException  if creating a test Java file fails.
     */
    @Test
    public void textOptionCreatesTextReport() throws IOException
    {
        // Given
        captureSystemOut();
        Path aJavaFile = createTestFileFromJavaResource("FullClass.java");

        // When
        Main.main("-text", aJavaFile.toString());

        // Then
        String aReport = new String(fByteArrayOutputStream.toByteArray());
        assertTrue(aReport.contains("Scent report created on"));
    }


    /**
     * Specifying the {@code -xml} option should result in an XML report.
     *
     * @throws IOException  if creating a test Java file fails.
     */
    @Test
    public void xmlOptionCreatesXmlReport() throws IOException
    {
        // Given
        captureSystemOut();
        Path aJavaFile = createTestFileFromJavaResource("FullInterface.java");

        // When
        Main.main("-xml", aJavaFile.toString());

        // Then
        String aReport = new String(fByteArrayOutputStream.toByteArray());
        assertTrue(aReport.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
    }


    /**
     * Specifying the {@code -html} option should result in an HTML report.
     *
     * @throws IOException  if creating a test Java file fails.
     */
    @Test
    public void htmlOptionCreatesHtmlReport() throws IOException
    {
        // Given
        captureSystemOut();
        Path aJavaFile = createTestFileFromJavaResource("FullEnum.java");

        // When
        Main.main("-html", aJavaFile.toString());

        // Then
        String aReport = new String(fByteArrayOutputStream.toByteArray());
        assertTrue(aReport.contains("<html>"));
    }


    /**
     * Specifying the {@code -xsl} option should result in a report transformed by the specified
     * XSL file.
     *
     * @throws IOException  if creating a test file fails.
     */
    @Test
    public void xslOptionCreatesXslReport() throws IOException
    {
        // Given
        captureSystemOut();
        Path aXslFile = createTestFileFromXslResource("valid.xsl");
        Path aJavaFile = createTestFileFromJavaResource("FullAnnotation.java");

        // When
        Main.main("-xsl", aXslFile.toString(), aJavaFile.toString());

        // Then
        String aReport = new String(fByteArrayOutputStream.toByteArray());
        assertTrue(aReport.contains("XSL transformation report"));
    }


    /**
     * Specifying the {@code -xsl} option without an XSL file should result in a warning being
     * printed.
     */
    @Test
    public void xslOptionWithoutXslFilePrintsWarning()
    {
        // Given
        mockSystemStreams();

        // When
        Main.main("-xsl");

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut, atLeastOnce()).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getAllValues().get(0).contains("Warning: missing xsl file"));
        verifyNoInteractions(fMockedSystemErr);
    }


    /**
     * Specifying the {@code -o} option should result in the report being written to file.
     *
     * @throws IOException  if creating a test Java file fails.
     */
    @Test
    public void outputFileOptionWritesToFile() throws IOException
    {
        // Given
        captureSystemOut();
        Path aOutFile = Paths.get("report.out");
        markForDeletion(aOutFile);
        Path aJavaFile = createTestFileFromJavaResource("FullClass.java");

        // When
        Main.main("-o", aOutFile.toString(), aJavaFile.toString());

        // Then
        String aReport = new String(Files.readAllBytes(aOutFile));
        assertTrue(aReport.contains("Scent report created on"));
    }


    /**
     * Specifying the {@code -o} option without an output file should result in a warning being
     * printed.
     */
    @Test
    public void outputFileOptionWithoutOutputFilePrintsWarning()
    {
        // Given
        mockSystemStreams();

        // When
        Main.main("-o");

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut, atLeastOnce()).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getAllValues().get(0).contains("Warning: missing output file"));
        verifyNoInteractions(fMockedSystemErr);
    }


    /**
     * Specifying an invalid path for the {@code -o} option should result in an error being
     * printed.
     */
    @Test
    public void invalidOutputFilePathPrintsError() throws IOException
    {
        // Given
        mockSystemStreams();
        Path aJavaFile = createTestFileFromJavaResource("FullEnum.java");

        // When
        Main.main("-o", "/no/such/file", aJavaFile.toString());

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut, atLeastOnce()).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getAllValues().get(1).contains("Error: cannot write report to"));
    }


    /**
     * Specifying an unknown option should result in a warning being printed.
     */
    @Test
    public void unknownOptionPrintsWarning() throws IOException
    {
        // Given
        mockSystemStreams();
        Path aJavaFile = createTestFileFromJavaResource("FullEnum.java");

        // When
        Main.main("-xml", "-fast", aJavaFile.toString());

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut, atLeastOnce()).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getAllValues().get(0).contains("unknown option"));
    }


    /**
     * An error that occurs when writing the report should be printed.
     */
    @Test
    public void reportErrorIsPrinted() throws IOException
    {
        // Given
        mockSystemStreams();
        Path aXslFile = createTestFileFromXslResource("invalid.xsl");
        Path aJavaFile = createTestFileFromJavaResource("FullEnum.java");

        // When
        Main.main("-xsl", aXslFile.toString(), aJavaFile.toString());

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemErr).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains("Failed to write report"));
    }


    /**
     * Replace the system streams with mocks.
     */
    private void mockSystemStreams()
    {
        fOriginalSystemOut = System.out;
        fOriginalSystemErr = System.err;
        System.setOut(fMockedSystemOut);
        System.setErr(fMockedSystemErr);
    }


    /**
     * Replace {@code System.out} with {@code fByteArraySystemOut}.
     */
    private void captureSystemOut()
    {
        fOriginalSystemOut = System.out;
        System.setOut(fByteArraySystemOut);
    }
}
