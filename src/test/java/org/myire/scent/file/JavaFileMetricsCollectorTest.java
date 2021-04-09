/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.myire.scent.collect.JavaMetricsCollector;


/**
 * Unit tests for {@code org.myire.scent.file.JavaFileMetricsCollector}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class JavaFileMetricsCollectorTest extends FileTestBase
{
    private final JavaMetricsCollector fDelegate = mock(JavaMetricsCollector.class);
    private final JavaFileMetricsCollector fCollector = new JavaFileMetricsCollector(fDelegate);


    /**
     * Calling the constructor with a null argument should throw a {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullArgument()
    {
        // When
        new JavaFileMetricsCollector(null);
    }


    /**
     * Calling {@code getDelegate()} should return the{@code JavaMetricsCollector} passed to the
     * constructor.
     */
    @Test
    public void getDelegateReturnsInstancePassedToCtor()
    {
        // Given
        JavaMetricsCollector aDelegate = new JavaMetricsCollector();

        // When
        JavaFileMetricsCollector aCollector = new JavaFileMetricsCollector(aDelegate);

        // Then
        assertSame(aDelegate, aCollector.getDelegate());
    }


    /**
     * Calling {@code visitFile()} with the path to an existing Java file should cause metrics for
     * that file to be collected by the delegate {@code JavaMetricsCollector}.
     */
    @Test
    public void visitFileCollectsMetricsForExistingJavaFile() throws IOException, ParseException
    {
        // Given
        String aFileName = "FullClass.java";
        Path aFile = createTestFileFromJavaResource(aFileName);

        // When
        fCollector.visitFile(aFile, null);

        // Then
        verify(fDelegate).collect(eq(aFileName), any(InputStream.class), any(Charset.class));
    }


    /**
     * Calling {@code visitFile()} with the path to an existing Java file should increment the file
     * count.
     */
    @Test
    public void visitFileIncrementsFileCountForExistingJavaFile() throws IOException
    {
        // Given
        String aFileName = "FullInterface.java";
        Path aFile = createTestFileFromJavaResource(aFileName);

        // When
        fCollector.visitFile(aFile, null);

        // Then
        assertEquals(1, fCollector.getNumFiles());
    }


    /**
     * Calling {@code visitFile()} with the path to a non-existing Java file should cause that file
     * to be ignored.
     */
    @Test
    public void visitFileIgnoresNonExistingJavaFile() throws IOException
    {
        // Given
        Path aPath = Paths.get("DoesNotExist.java");

        // When
        fCollector.visitFile(aPath, null);

        // Then
        verifyNoInteractions(fDelegate);
    }


    /**
     * Calling {@code visitFile()} with the path to a non-existing Java file should not increment
     * the file count.
     */
    @Test
    public void visitFileDoesNotIncrementFileCountForNonExistingJavaFile() throws IOException
    {
        // Given
        Path aPath = Paths.get("DoesNotExist.java");

        // When
        fCollector.visitFile(aPath, null);

        // Then
        assertEquals(0, fCollector.getNumFiles());
    }


    /**
     * Calling {@code visitFile()} with the path to an existing non-Java file should cause that file
     * to be ignored.
     */
    @Test
    public void visitFileIgnoresNonJavaFile() throws IOException
    {
        // Given
        Path aTestFile = createTestFile("NotAJavaFile.txt");

        // When
        fCollector.visitFile(aTestFile, null);

        // Then
        verifyNoInteractions(fDelegate);
    }


    /**
     * Calling {@code visitFile()} with an empty path should cause that path to be ignored.
     */
    @Test
    public void visitFileIgnoresEmptyPath() throws IOException
    {
        // When
        fCollector.visitFile(new File("").toPath(), null);

        // Then
        verifyNoInteractions(fDelegate);
    }


    /**
     * Calling {@code visitFile()} with the path to an existing non-Java file should not increment
     * the file count.
     */
    @Test
    public void visitFileDoesNotIncrementFileCountForNonJavaFile() throws IOException
    {
        // Given
        Path aTestFile = createTestFile("NotAJavaFile.txt");

        // When
        fCollector.visitFile(aTestFile, null);

        // Then
        assertEquals(0, fCollector.getNumFiles());
    }


    /**
     * Calling {@code visitFile()} with the path to a syntactically invalid Java file should cause
     * an {@code IOException} to be thrown.
     */
    @Test(expected = IOException.class)
    public void visitFileThrowsForInvalidJavaFile() throws IOException
    {
        // Given
        Path aFile = createTestFileFromJavaResource("Invalid.java");
        JavaFileMetricsCollector aVisitor = new JavaFileMetricsCollector(new JavaMetricsCollector());

        // When
        aVisitor.visitFile(aFile, null);
    }


    /**
     * Calling {@code visitFile()} with the path to a Java file with an encoding incompatible with
     * the one specified for the {@code JavaMetricsCollector} should cause an {@code IOException} to
     * be thrown.
     */
    @Test(expected = IOException.class)
    public void visitFileThrowsForJavaFileWithWrongEncoding() throws IOException
    {
        // Given
        Path aFile = createTestFileFromJavaResource("FullAnnotation.java");
        JavaFileMetricsCollector aVisitor =
                new JavaFileMetricsCollector(new JavaMetricsCollector(), StandardCharsets.UTF_16LE);

        // When
        aVisitor.visitFile(aFile, null);
    }
}
