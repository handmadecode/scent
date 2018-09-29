/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.time.LocalDateTime;

import org.junit.Test;

import org.myire.scent.collect.JavaMetricsCollector;
import org.myire.scent.metrics.JavaMetrics;


/**
 * Abstract base class for unit testing of {@code OutputStreamReportWriter} subclasses.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
abstract public class OutputStreamReportWriterTestBase
{
    static private final MetricsReportMetaData EMPTY_META_DATA = new MetricsReportMetaData(null, null);


    private final JavaMetricsCollector fCollector = new JavaMetricsCollector();
    private final ByteArrayOutputStream fByteOutputStream = new ByteArrayOutputStream(8192);


    /**
     * Creating an instance of the {@code OutputStreamReportWriter} subclass being tested with a
     * null {@code OutputStream} should throw a {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void ctorThrowsForNullOutputStream()
    {
        // When
        createReportWriter(null);
    }


    /**
     * Create an instance of the {@code OutputStreamReportWriter} subclass being tested.
     *
     * @param pOutputStream The stream that the created instance should write its report to.
     *
     * @return  A new instance of the class to test, never null.
     */
    abstract protected OutputStreamReportWriter createReportWriter(OutputStream pOutputStream);


    /**
     * Call {@code writeReportContents} on an {@code OutputStreamReportWriter} returned by
     * {@link #createReportWriter(OutputStream)} with a specific timestamp and version string in the
     * report meta data. The {@code JavaMetrics} instance passed to the method will be empty.
     *
     * @param pTimestamp    The timestamp to put into the report meta data.
     * @param pVersion      The version string to put into the report meta data.
     */
    protected void writeReport(LocalDateTime pTimestamp, String pVersion)
    {
        OutputStreamReportWriter aWriter = createReportWriter(fByteOutputStream);
        aWriter.writeReportContents(new JavaMetrics(), new MetricsReportMetaData(pTimestamp, pVersion));
        aWriter.flush();
    }


    /**
     * Collect metrics from one or more Java source strings and write a report using an
     * {@code OutputStreamReportWriter} returned by {@link #createReportWriter(OutputStream)}.
     *<p>
     * Each source will be associated with a compilation unit named &quot;Test<i>n</i>.java&quot;,
     * where <i>n</i> is the source's index in the parameter array. So the first name will be
     * &quot;Test0.java&quot;, the second &quot;Test1.java&quot;, etc.
     *<p>
     * The timestamp and version string in the report meta data passed to
     * {@code writeReportContents} will be null.
     *
     * @param pSources  The sources to collect metrics from.
     *
     * @throws IllegalArgumentException if any of the sources has syntax errors.
     */
    protected void collectAndWriteReport(String... pSources)
    {
        try
        {
            for (int i=0; i<pSources.length; i++)
                fCollector.collect("Test" + i + ".java", pSources[i]);

            OutputStreamReportWriter aWriter = createReportWriter(fByteOutputStream);
            aWriter.writeReportContents(fCollector.getCollectedMetrics(), EMPTY_META_DATA);
            aWriter.flush();
        }
        catch (ParseException pe)
        {
            throw new IllegalArgumentException(pe);
        }
    }


    /**
     * Get all bytes written to the {@code OutputStream} during the call to
     * {@link #writeReport(LocalDateTime, String)} or {@link #collectAndWriteReport(String...)}.
     *
     * @return  An array with all bytes written to the report output stream.
     */
    protected byte[] getReportContents()
    {
        return fByteOutputStream.toByteArray();
    }
}
