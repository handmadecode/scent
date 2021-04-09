/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.myire.scent.collect.JavaMetricsCollector;
import org.myire.scent.file.FileTestBase;


/**
 * Unit tests for {@code org.myire.scent.PrintingCollector}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class PrintingCollectorTest extends FileTestBase
{
    private final PrintStream fOutStream = mock(PrintStream.class);
    private final PrintStream fErrStream = mock(PrintStream.class);

    private final PrintingCollector fCollector =
             new PrintingCollector(new JavaMetricsCollector(), fOutStream, fErrStream);


    /**
     * Calling {@code preVisitDirectory()} should print the name of the directory to the output
     * stream.
     */
    @Test
    public void preVisitDirectoryPrintsDirectoryName()
    {
        // Given
        String aDirectoryName = "theDirectory";

        // When
        fCollector.preVisitDirectory(Paths.get(aDirectoryName), null);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fOutStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aDirectoryName));
        verifyNoInteractions(fErrStream);
    }


    /**
     * Calling {@code visitFile()} with the path to a syntactically invalid Java file should cause
     * an error to be printed.
     */
    @Test
    public void visitFilePrintsErrorForInvalidJavaFile() throws IOException
    {
        // Given
        String aFileName = "Invalid.java";
        Path aInvalidPath = createTestFileFromJavaResource(aFileName);

        // When
        fCollector.visitFile(aInvalidPath, null);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fErrStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
        verifyNoInteractions(fOutStream);
    }


    /**
     * Calling {@code visitFile()} with the path to a Java file with an encoding incompatible with
     * the one specified for the {@code JavaMetricsCollector} should cause an error to be printed.
     */
    @Test
    public void visitFilePrintsErrorForJavaFileWithWrongEncoding() throws IOException
    {
        // Given
        String aFileName = "FullAnnotation.java";
        Path aTestFile = createTestFileFromJavaResource(aFileName);
        PrintingCollector aCollector = new PrintingCollector(
                new JavaMetricsCollector(),
                StandardCharsets.UTF_16LE,
                fOutStream,
                fErrStream);

        // When
        aCollector.visitFile(aTestFile, null);

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fErrStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
        verifyNoInteractions(fOutStream);
    }


    /**
     * Calling {@code visitFileFailed()} should print the name of the file to the error stream.
     */
    @Test
    public void visitFileFailedPrintsFileName()
    {
        // Given
        String aFileName = "badFile";

        // When
        fCollector.visitFileFailed(Paths.get(aFileName), mock(IOException.class));

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fErrStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aFileName));
        verifyNoInteractions(fOutStream);
    }


    /**
     * Calling {@code postVisitDirectory()} should print the name of the directory to the error
     * stream if the exception argument is non-null.
     */
    @Test
    public void postVisitDirectoryPrintsDirectoryNameOnException()
    {
        // Given
        String aDirectoryName = "theDirectory";

        // When
        fCollector.postVisitDirectory(Paths.get(aDirectoryName), mock(IOException.class));

        // Then
        ArgumentCaptor<String> aPrintlnArg = ArgumentCaptor.forClass(String.class);
        verify(fErrStream).println(aPrintlnArg.capture());
        assertTrue(aPrintlnArg.getValue().contains(aDirectoryName));
        verifyNoInteractions(fOutStream);
    }


    /**
     * Calling {@code postVisitDirectory()} should not print anything to the error stream if the
     * exception argument is null.
     */
    @Test
    public void postVisitDirectoryPrintsNothingOnNullException()
    {
        // When
        fCollector.postVisitDirectory(Paths.get("anyDirectory"), null);

        // Then
        verifyNoInteractions(fOutStream);
        verifyNoInteractions(fErrStream);
    }
}
