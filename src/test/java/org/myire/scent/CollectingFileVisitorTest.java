/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.mockito.ArgumentCaptor;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import org.myire.scent.collect.JavaMetricsCollector;


/**
 * Unit tests for {@code org.myire.scent.CollectingFileVisitor}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class CollectingFileVisitorTest extends FileTestBase
{
    private final JavaMetricsCollector fCollector = mock(JavaMetricsCollector.class);
    private final PrintStream fOutStream = mock(PrintStream.class);
    private final PrintStream fErrStream = mock(PrintStream.class);


    /**
     * Calling {@code preVisitDirectory()} should print the name of the directory to the output
     * stream.
     */
    @Test
    public void preVisitDirectoryPrintsDirectoryName()
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        String aDirectoryName = "theDirectory";

        // When
        aVisitor.preVisitDirectory(Paths.get(aDirectoryName), null);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fOutStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aDirectoryName));
        verifyZeroInteractions(fErrStream);
    }


    /**
     * Calling {@code visitFile()} with the path to an existing Java file should cause metrics for
     * that file to be collected by the {@code JavaMetricsCollector}.
     */
    @Test
    public void visitFileCollectsMetricsForExistingJavaFile() throws IOException, ParseException
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        String aFileName = "FullClass.java";
        fTestFile = copyResourceToFile('/' + aFileName, Paths.get(aFileName));

        // When
        aVisitor.visitFile(fTestFile, null);

        // Then
        verify(fCollector).collect(eq(aFileName), any(InputStream.class), any(Charset.class));
        verifyZeroInteractions(fOutStream);
        verifyZeroInteractions(fErrStream);
    }


    /**
     * Calling {@code visitFile()} with the path to an existing Java file should increment the file
     * count.
     */
    @Test
    public void visitFileIncrementsFileCountForExistingJavaFile() throws IOException, ParseException
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        String aFileName = "FullInterface.java";
        fTestFile = copyResourceToFile('/' + aFileName, Paths.get(aFileName));

        // When
        aVisitor.visitFile(fTestFile, null);

        // Then
        assertEquals(1, aVisitor.getNumFiles());
    }


    /**
     * Calling {@code visitFile()} with the path to a non-existing Java file should cause that file
     * to be ignored.
     */
    @Test
    public void visitFileIgnoresNonExistingJavaFile() throws IOException
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        Path aPath = Paths.get("DoesNotExist.java");

        // When
        aVisitor.visitFile(aPath, null);

        // Then
        verifyZeroInteractions(fCollector);
        verifyZeroInteractions(fOutStream);
        verifyZeroInteractions(fErrStream);
    }


    /**
     * Calling {@code visitFile()} with the path to a non-existing Java file should not increment
     * the file count.
     */
    @Test
    public void visitFileDoesNotIncrementFileCountForNonExistingJavaFile() throws IOException, ParseException
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        Path aPath = Paths.get("DoesNotExist.java");

        // When
        aVisitor.visitFile(aPath, null);

        // Then
        assertEquals(0, aVisitor.getNumFiles());
    }


    /**
     * Calling {@code visitFile()} with the path to an existing non-Java file should cause that file
     * to be ignored.
     */
    @Test
    public void visitFileIgnoresNonJavaFile() throws IOException
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        fTestFile = Files.createFile(Paths.get("NotAJavaFile.txt"));

        // When
        aVisitor.visitFile(fTestFile, null);

        // Then
        verifyZeroInteractions(fCollector);
        verifyZeroInteractions(fOutStream);
        verifyZeroInteractions(fErrStream);
    }


    /**
     * Calling {@code visitFile()} with the path to an existing non-Java file should not increment
     * the file count.
     */
    @Test
    public void visitFileDoesNotIncrementFileCountForNonJavaFile() throws IOException, ParseException
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        fTestFile = Files.createFile(Paths.get("NotAJavaFile.txt"));

        // When
        aVisitor.visitFile(fTestFile, null);

        // Then
        assertEquals(0, aVisitor.getNumFiles());
    }


    /**
     * Calling {@code visitFile()} with the path to a syntactically invalid Java file should cause
     * an error to be printed.
     */
    @Test
    public void visitFilePrintsErrorForInvalidJavaFile() throws IOException
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(new JavaMetricsCollector(), fOutStream, fErrStream);
        String aFileName = "Invalid.java";
        fTestFile = copyResourceToFile('/' + aFileName, Paths.get(aFileName));

        // When
        aVisitor.visitFile(fTestFile, null);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fErrStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
        verifyZeroInteractions(fOutStream);
    }


    /**
     * Calling {@code visitFile()} with the path to a Java file with an encoding incompatible with
     * the one specified for the {@code JavaMetricsCollector} should cause an error to be printed.
     */
    @Test
    public void visitFilePrintsErrorForJavaFileWithWrongEncoding() throws IOException
    {
        // Given
        CollectingFileVisitor aVisitor =
                new CollectingFileVisitor(new JavaMetricsCollector(), fOutStream, fErrStream, StandardCharsets.UTF_16LE);
        String aFileName = "FullAnnotation.java";
        fTestFile = copyResourceToFile('/' + aFileName, Paths.get(aFileName));

        // When
        aVisitor.visitFile(fTestFile, null);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fErrStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
        verifyZeroInteractions(fOutStream);
    }


    /**
     * Calling {@code visitFileFailed()} should print the name of the file to the error stream.
     */
    @Test
    public void visitFileFailedPrintsFileName()
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        String aFileName = "badFile";

        // When
        aVisitor.visitFileFailed(Paths.get(aFileName), mock(IOException.class));

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fErrStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
        verifyZeroInteractions(fOutStream);
    }


    /**
     * Calling {@code postVisitDirectory()} should print the name of the directory to the error
     * stream if the exception argument is non-null.
     */
    @Test
    public void postVisitDirectoryPrintsDirectoryNameOnException()
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);
        String aDirectoryName = "theDirectory";

        // When
        aVisitor.postVisitDirectory(Paths.get(aDirectoryName), mock(IOException.class));

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fErrStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aDirectoryName));
        verifyZeroInteractions(fOutStream);
    }


    /**
     * Calling {@code postVisitDirectory()} should not print anything to the error stream if the
     * exception argument is null.
     */
    @Test
    public void postVisitDirectoryPrintsNothingOnNullException()
    {
        // Given
        CollectingFileVisitor aVisitor = new CollectingFileVisitor(fCollector, fOutStream, fErrStream);

        // When
        aVisitor.postVisitDirectory(Paths.get("anyDirectory"), null);

        // Then
        verifyZeroInteractions(fOutStream);
        verifyZeroInteractions(fErrStream);
    }
}
