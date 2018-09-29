/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;

import org.myire.scent.metrics.JavaMetrics;


/**
 * Abstract base class for writing metrics reports to an {@code OutputStream}.
 *<p>
 * Note that the protected {@code write*} methods throw an {@code UncheckedIOException} rather than
 * an {@code IOException}. This is to allow these methods to be called in methods intended to
 * implement functional interfaces that don't throw {@code IOException}. The main entry point for
 * this class, {@link #writeReport(JavaMetrics, MetricsReportMetaData)}, catches any thrown
 * {@code UncheckedIOException} and throws its {@code IOException} cause.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class OutputStreamReportWriter implements MetricsReportWriter
{
    static private final char[] LINE_BREAK = System.getProperty("line.separator").toCharArray();
    static private final char[] INDENTATION = {' ', ' '};


    private final Writer fWriter;
    private int fIndentationLevel;


    /**
     * Create a new {@code OutputStreamReportWriter}.
     *
     * @param pOutputStream The stream to write the report to.
     * @param pCharset      The character set to encode the report with.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    protected OutputStreamReportWriter(
        @Nonnull OutputStream pOutputStream,
        @Nonnull Charset pCharset)
    {
        fWriter = new OutputStreamWriter(pOutputStream, pCharset);
    }


    /**
     * Write a report for the source code metrics in a {@code JavaMetrics} instance.
     *
     * @param pMetrics  The metrics to get the report data from.
     * @param pMetaData The report's meta data.
     *
     * @throws IOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    @Override
    public void writeReport(
        @Nonnull JavaMetrics pMetrics,
        @Nonnull MetricsReportMetaData pMetaData) throws IOException
    {
        try
        {
            writeReportContents(pMetrics, pMetaData);
            flush();
        }
        catch (UncheckedIOException uioe)
        {
            // Rethrow the checked cause.
            throw uioe.getCause();
        }
    }


    /**
     * Write the report's contents to the underlying stream.
     *
     * @param pMetrics  The metrics to get the report data from.
     * @param pMetaData The report's meta data.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if any of the parameters is null.
     */
    abstract protected void writeReportContents(
        @Nonnull JavaMetrics pMetrics,
        @Nonnull MetricsReportMetaData pMetaData);


    /**
     * Increase the indentation level used by {@link #writeIndentation()}.
     */
    protected void increaseIndentationLevel()
    {
        fIndentationLevel++;
    }


    /**
     * Decrease the indentation level used by {@link #writeIndentation()}.
     */
    protected void decreaseIndentationLevel()
    {
        fIndentationLevel--;
    }


    /**
     * Write the current indentation to the underlying stream. Each indentation level is represented
     * by two spaces.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     */
    protected void writeIndentation()
    {
        for (int i=0; i<fIndentationLevel; i++)
            write(INDENTATION);
    }


    /**
     * Write the platform's line separator to the underlying stream.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     */
    protected void writeLineBreak()
    {
        write(LINE_BREAK);
    }


    /**
     * Write a single character to the underlying stream.
     *
     * @param pChar The character to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     */
    protected void write(char pChar)
    {
        try
        {
            fWriter.write(pChar);
        }
        catch (IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }


    /**
     * Write all characters in an array to the underlying stream.
     *
     * @param pChars    The characters to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pChars} is null.
     */
    protected void write(@Nonnull char[] pChars)
    {
        try
        {
            fWriter.write(pChars);
        }
        catch (IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }


    /**
     * Write all characters in a string to the underlying stream.
     *
     * @param pChars    The characters to write.
     *
     * @throws UncheckedIOException if writing to the underlying stream fails.
     * @throws NullPointerException if {@code pChars} is null.
     */
    protected void write(@Nonnull String pChars)
    {
        try
        {
            fWriter.write(pChars);
        }
        catch (IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }


    /**
     * Flush any buffered writes to the underlying stream.
     *
     * @throws UncheckedIOException if flushing to the underlying stream fails.
     */
    protected void flush()
    {
        try
        {
            fWriter.flush();
        }
        catch (IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }
}
