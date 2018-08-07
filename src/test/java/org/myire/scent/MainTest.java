/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

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
    private PrintStream fOriginalSystemOut;
    private PrintStream fOriginalSystemErr;
    private Path fTestDirectory;


    /**
     * Replace the system streams with mocks.
     */
    @Before
    public void mockSystemStreams()
    {
        fOriginalSystemOut = System.out;
        fOriginalSystemErr = System.err;
        System.setOut(fMockedSystemOut);
        System.setErr(fMockedSystemErr);
    }


    /**
     * Restore the system streams to the values that were replaced in {@code mockSystemStreams()}.
     */
    @After
    public void restoreSystemStreams()
    {
        System.setOut(fOriginalSystemOut);
        System.setErr(fOriginalSystemErr);
    }


    /**
     * Delete the test directory after the test file has been deleted.
     *
     * @throws IOException  if deleting the file or the directory fails.
     */
    @Override
    public void deleteTestFile() throws IOException
    {
        super.deleteTestFile();
        if (fTestDirectory != null)
            Files.deleteIfExists(fTestDirectory);
    }


    /**
     * Passing the path to an existing Java file to {@code main} should cause the file to be parsed
     * and its metrics to be printed to {@code System.out}.
     */
    @Test
    public void metricsForExistingJavaFileArePrinted() throws IOException
    {
        // Given
        String aFileName = "FullClass.java";
        fTestFile = copyResourceToFile('/' + aFileName, Paths.get(aFileName));

        // When
        Main.main(aFileName);

        // Then
        verify(fMockedSystemOut, atLeastOnce()).println(anyString());
        verifyZeroInteractions(fMockedSystemErr);
    }


    /**
     * Passing the path to a directory containing an existing Java file to {@code main} should cause
     * the file to be parsed and its metrics to be printed to {@code System.out}.
     */
    @Test
    public void fileInDirectoryIsCollected() throws IOException
    {
        // Given
        fTestDirectory = Files.createTempDirectory(null);
        String aFileName = "FullInterface.java";
        fTestFile = copyResourceToFile('/' + aFileName, fTestDirectory.resolve(aFileName));

        // When
        Main.main(fTestDirectory.toString());

        // Then
        verify(fMockedSystemOut, atLeastOnce()).println(anyString());
        verifyZeroInteractions(fMockedSystemErr);
    }


    /**
     * Passing the path to a non-Java file to {@code main} should cause the file to be ignored.
     */
    @Test
    public void nonJavaFileIsIgnored() throws IOException
    {
        // Given
        fTestFile = Files.createTempFile(null, ".txt");

        // When
        Main.main(fTestFile.toString());

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains("0"));
        verifyZeroInteractions(fMockedSystemErr);
    }


    /**
     * Passing the path to a non-existing Java file to {@code main} should cause the file to be
     * ignored and an error to be printed to {@code System.err}.
     */
    @Test
    public void nonExistingJavaFileIsIgnored()
    {
        // Given
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
     */
    @Test
    public void errorIsPrintedForInvalidJavaFile() throws IOException
    {
        // Given
        String aFileName = "Invalid.java";
        fTestFile = copyResourceToFile('/' + aFileName, Paths.get(aFileName));

        // When
        Main.main(aFileName);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fMockedSystemOut).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains("0"));
        verify(fMockedSystemErr).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
    }
}
