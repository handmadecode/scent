/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.withSettings;

import org.myire.scent.metrics.JavaMetrics;


/**
 * Unit tests for {@code OutputStreamReportWriter}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class OutputStreamReportWriterTest
{
    static private final int BUFFER_SIZE = 32768;


    private final TestOutputStream fOutputStream = new TestOutputStream();

    private final OutputStreamReportWriter fWriter =
        mock(OutputStreamReportWriter.class,
             withSettings()
                 .useConstructor(fOutputStream, StandardCharsets.UTF_8)
                 .defaultAnswer(Mockito.CALLS_REAL_METHODS));


    /**
     * A call to {@code writeReport} should trigger a call to {@code writeReportContents} with the
     * same arguments as passed to the former method.
     *
     * @throws IOException  if the test fails unexpectedly.
     */
    @Test
    public void writeReportDelegatesToWriteReportContents() throws IOException
    {
        // Given
        JavaMetrics aMetrics = new JavaMetrics();
        MetricsReportMetaData aMetaData = new MetricsReportMetaData(null, null);

        // When
        fWriter.writeReport(aMetrics, aMetaData);

        // Then
        verify(fWriter).writeReportContents(aMetrics, aMetaData);
    }


    /**
     * A call to {@code writeReport} should throw an {@code IOException} if the call to
     * {@code writeReportContents} throws an {@code UncheckedIOException}.
     */
    @Test
    public void writeReportThrowsWhenWriteReportContentsThrowsUncheckedException()
    {
        // Given
        IOException aIOException = new IOException();
        doThrow(new UncheckedIOException(aIOException))
            .when(fWriter).writeReportContents(any(), any());

        // When
        try
        {
            fWriter.writeReport(new JavaMetrics(), new MetricsReportMetaData(null, null));
            fail("IOException not thrown");
        }
        catch (IOException ioe)
        {
            assertSame(aIOException, ioe);
        }
    }


    /**
     * The {@code writeIndentation} method should write nothing to the underlying stream when the
     * indentation level never has been increased on the {@code OutputStreamReportWriter}.
     */
    @Test
    public void writeIndentationWritesNothingWhenLevelHasNotBeenIncreased()
    {
        // When
        fWriter.writeIndentation();
        fWriter.flush();

        // Then
        assertEquals(0, fOutputStream.getNumBytes());
    }


    /**
     * The {@code writeIndentation} method should write to the underlying stream when the level has
     * been increased on the {@code OutputStreamReportWriter}.
     */
    @Test
    public void writeIndentationWritesWhenLevelHasBeenIncreased()
    {
        // Given
        fWriter.increaseIndentationLevel();

        // When
        fWriter.writeIndentation();
        fWriter.flush();

        // Then
        assertTrue(fOutputStream.getNumBytes() > 0);
    }


    /**
     * The {@code writeIndentation} method should write nothing to the underlying stream when the
     * indentation level has been increased and then decreased on the
     * {@code OutputStreamReportWriter}.
     */
    @Test
    public void writeIndentationWritesNothingWhenLevelHasIncreasedAndThenDecreased()
    {
        // Given
        fWriter.increaseIndentationLevel();
        fWriter.decreaseIndentationLevel();

        // When
        fWriter.writeIndentation();
        fWriter.flush();

        // Then
        assertEquals(0, fOutputStream.getNumBytes());
    }


    /**
     * The {@code writeIndentation} method should throw an {@code UncheckedIOException} when the
     * underlying stream throws an {@code IOException}.
     */
    @Test
    public void writeIndentationThrowsWhenUnderlyingStreamThrows()
    {
        // Given
        fWriter.increaseIndentationLevel();

        // When
        writeThrowsWhenUnderlyingStreamThrows(
            fWriter::writeIndentation
        );
    }


    /**
     * The {@code writeLineBreak} method should write to the underlying stream.
     */
    @Test
    public void writeLineBreakWritesToUnderlyingStream()
    {
        // When
        fWriter.writeLineBreak();
        fWriter.flush();

        // Then
        assertTrue(fOutputStream.getNumBytes() > 0);
    }


    /**
     * The {@code writeLineBreak} method should throw an {@code UncheckedIOException} when the
     * underlying stream throws an {@code IOException}.
     */
    @Test
    public void writeLineBreakThrowsWhenUnderlyingStreamThrows()
    {
        writeThrowsWhenUnderlyingStreamThrows(
            fWriter::writeLineBreak
        );
    }


    /**
     * The {@code write(char)} method should write to the underlying stream.
     */
    @Test
    public void writeCharWritesToUnderlyingStream()
    {
        // Given
        char aChar = 'x';

        // When
        fWriter.write(aChar);
        fWriter.flush();

        // Then
        assertEquals(1, fOutputStream.getNumBytes());
        assertEquals(aChar, fOutputStream.getBytesAsString().charAt(0));
    }


    /**
     * The {@code write(char)} method should throw an {@code UncheckedIOException} when the
     * underlying stream throws an {@code IOException}.
     */
    @Test
    public void writeCharThrowsWhenUnderlyingStreamThrows()
    {
        writeThrowsWhenUnderlyingStreamThrows(
            () -> fWriter.write('z')
        );
    }


    /**
     * The {@code write(char[])} method should write to the underlying stream.
     */
    @Test
    public void writeCharsWritesToUnderlyingStream()
    {
        // Given
        char[] aChars = {'x', 'y', 'z'};

        // When
        fWriter.write(aChars);
        fWriter.flush();

        // Then
        assertArrayEquals(aChars, fOutputStream.getBytesAsString().toCharArray());
    }


    /**
     * The {@code write(char[])} method should throw an {@code UncheckedIOException} when the
     * underlying stream throws an {@code IOException}.
     */
    @Test
    public void writeCharsThrowsWhenUnderlyingStreamThrows()
    {
        writeThrowsWhenUnderlyingStreamThrows(
            () -> fWriter.write(new char[100])
        );
    }


    /**
     * The {@code write(String)} method should write to the underlying stream.
     */
    @Test
    public void writeStringWritesToUnderlyingStream()
    {
        // Given
        String aString = "a string";

        // When
        fWriter.write(aString);
        fWriter.flush();

        // Then
        assertEquals(aString, fOutputStream.getBytesAsString());
    }


    /**
     * The {@code write(String)} method should throw an {@code UncheckedIOException} when the
     * underlying stream throws an {@code IOException}.
     */
    @Test
    public void writeStringThrowsWhenUnderlyingStreamThrows()
    {
        writeThrowsWhenUnderlyingStreamThrows(
            () -> fWriter.write("throw me")
        );
    }


    /**
     * The {@code flush()} method should flush the underlying stream.
     */
    @Test
    public void flushFlushesUnderlyingStream()
    {
        // When
        fWriter.flush();

        // Then
        assertTrue(fOutputStream.wasFlushed());
    }


    /**
     * The {@code flush} method should throw an {@code UncheckedIOException} when the underlying
     * stream throws an {@code IOException}.
     */
    @Test
    public void flushThrowsWhenUnderlyingStreamThrows()
    {
        // Given
        IOException aIOException = new IOException();
        fOutputStream.setFlushException(aIOException);

        // When
        try
        {
            fWriter.flush();
            fail("UncheckedIOException not thrown");
        }
        catch (UncheckedIOException uioe)
        {
            // Then
            assertSame(aIOException, uioe.getCause());
        }
    }


    /**
     * Test that a write operation on the {@code OutputStreamReportWriter} throws an
     * {@code UncheckedIOException} when the underlying stream throws an {@code IOException}, and
     * that the former's cause is the latter.
     *
     * @param pWriteOperation   The operation to invoke.
     */
    private void writeThrowsWhenUnderlyingStreamThrows(Runnable pWriteOperation)
    {
        // Given
        IOException aIOException = new IOException();
        fOutputStream.setWriteException(aIOException);

        // When
        try
        {
            // Perform the write enough times to fill the underlying stream encoder's buffer.
            for (int i=0; i<BUFFER_SIZE; i++)
                pWriteOperation.run();

            fail("UncheckedIOException not thrown");
        }
        catch (UncheckedIOException uioe)
        {
            // Then
            assertSame(aIOException, uioe.getCause());
        }
    }


    /**
     * {@code OutputStream} implementation for the test scenarios.
     */
    static private class TestOutputStream extends OutputStream
    {
        private StringBuilder fBytes = new StringBuilder();
        private boolean fFlushCalled;
        private IOException fWriteException;
        private IOException fFlushException;

        @Override
        public void write(int pByte) throws IOException
        {
            if (fWriteException != null)
                throw fWriteException;
            fBytes.append((char) pByte);
        }

        @Override
        public void flush() throws IOException
        {
            if (fFlushException != null)
                throw fFlushException;
            super.flush();
            fFlushCalled = true;
        }

        int getNumBytes()
        {
            return fBytes.length();
        }

        String getBytesAsString()
        {
            return fBytes.toString();
        }

        boolean wasFlushed()
        {
            return fFlushCalled;
        }

        void setWriteException(IOException pException)
        {
            fWriteException = pException;
        }

        void setFlushException(IOException pException)
        {
            fFlushException = pException;
        }
    }
}
