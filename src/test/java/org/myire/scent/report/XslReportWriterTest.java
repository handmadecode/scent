/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;

import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.util.CollectTestUtil;


/**
 * Unit tests for {@code XslReportWriter}.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class XslReportWriterTest
{
    static private final JavaMetrics EMPTY_METRICS = new JavaMetrics();
    static private final MetricsReportMetaData EMPTY_META_DATA = new MetricsReportMetaData(null, null);

    private PrintStream fOriginalSystemErr;


    /**
     * Reassign {@code System.err} to prevent the test output from being cluttered.
     */
    @Before
    public void captureSystemErr()
    {
        fOriginalSystemErr = System.err;
        System.setErr(new PrintStream(new ByteArrayOutputStream()));
    }


    /**
     * Restore {@code System.err} to the stream saved in {@link #captureSystemErr()}.
     */
    @After
    public void restoreSystemErr()
    {
        if (fOriginalSystemErr != null)
            System.setErr(fOriginalSystemErr);
    }


    /**
     * Calling the constructor(OutputStream, File) with a null {@code OutputStream} argument should
     * throw a {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void fileCtorThrowsForNullOutputStream()
    {
        // When
        new XslReportWriter(null, new File(""));
    }


    /**
     * Calling the constructor(OutputStream, File) with a null {@code File} argument should throw a
     * {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void fileCtorThrowsForNullFile()
    {
        // Given
        File aFile = null;

        // When
        new XslReportWriter(new ByteArrayOutputStream(), aFile);
    }


    /**
     * Calling the constructor(OutputStream, String) with a null {@code OutputStream} argument
     * should throw a {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void resourceCtorThrowsForNullOutputStream()
    {
        // When
        new XslReportWriter(null, "");
    }


    /**
     * Calling the constructor(OutputStream, String) with a null {@code String} argument should
     * throw a {@code NullPointerException}.
     */
    @Test(expected = NullPointerException.class)
    public void resourceCtorThrowsForNullResourceName()
    {
        // Given
        String aResource = null;

        // When
        new XslReportWriter(new ByteArrayOutputStream(), aResource);
    }


    /**
     * Calling {@code writeReport} on an instance that was created with an XSL file that does not
     * exist should throw an {@code IOException}.
     *
     * @throws IOException  always.
     */
    @Test(expected = IOException.class)
    public void writeReportThrowsForNonExistingXslFile() throws IOException
    {
        // Given
        XslReportWriter aReportWriter =
            new XslReportWriter(new ByteArrayOutputStream(), new File("/does/not/exist"));

        // When
        aReportWriter.writeReport(EMPTY_METRICS, EMPTY_META_DATA);
    }


    /**
     * Calling {@code writeReport} on an instance that was created with an XSL file with invalid
     * contents should throw an {@code IOException}.
     *
     * @throws IOException  always.
     */
    @Test(expected = IOException.class)
    public void writeReportThrowsForInvalidXslFile() throws IOException
    {
        // Given
        Path aPath = createFileFromResource("writeReportThrowsForInvalidXslFile.xsl", "/xsl/invalid.xsl");
        XslReportWriter aReportWriter =
            new XslReportWriter(new ByteArrayOutputStream(), aPath.toFile());

        try
        {
            // When
            aReportWriter.writeReport(EMPTY_METRICS, EMPTY_META_DATA);
        }
        finally
        {
            Files.deleteIfExists(aPath);
        }
    }


    /**
     * Calling {@code writeReport} on an instance that was created with an XSL resource that does
     * not exist should throw an {@code IOException}.
     *
     * @throws IOException  always.
     */
    @Test(expected = IOException.class)
    public void writeReportThrowsForNonExistingXslResource() throws IOException
    {
        // Given
        XslReportWriter aReportWriter =
            new XslReportWriter(new ByteArrayOutputStream(), "/does/not/exist");

        // When
        aReportWriter.writeReport(EMPTY_METRICS, EMPTY_META_DATA);
    }


    /**
     * Calling {@code writeReport} on an instance that was created with an XSL resource with invalid
     * contents should throw an {@code IOException}.
     *
     * @throws IOException  always.
     */
    @Test(expected = IOException.class)
    public void writeReportThrowsThrowsForInvalidXslResource() throws IOException
    {
        // Given
        XslReportWriter aReportWriter =
            new XslReportWriter(new ByteArrayOutputStream(), "/xsl/invalid.xsl");

        // When
        aReportWriter.writeReport(EMPTY_METRICS, EMPTY_META_DATA);
    }


    /**
     * The report should contain the result of the XSL transformation.
     *
     * @throws ParseException   if the test fails unexpectedly
     * @throws IOException  if the test fails unexpectedly
     */
    @Test
    public void reportContainsTransformationResult() throws ParseException, IOException
    {
        // Given
        ByteArrayOutputStream aOutputStream = new ByteArrayOutputStream();
        XslReportWriter aReportWriter =
            new XslReportWriter(aOutputStream, "/xsl/valid.xsl");
        JavaMetrics aMetrics = CollectTestUtil.collect("package x.y.z; class C{}");
        LocalDateTime aTimestamp = LocalDateTime.of(2018, 9, 26, 13, 0, 55);
        String aVersion = "v17.47.11";

        // When
        aReportWriter.writeReport(aMetrics, new MetricsReportMetaData(aTimestamp, aVersion));

        // Then
        String aReport = new String(aOutputStream.toByteArray(), StandardCharsets.UTF_8);
        assertTrue(aReport.contains("Pkg: x.y.z"));
        assertTrue(aReport.contains("Type: C"));
        assertTrue(aReport.contains("Date: 2018-09-26"));
        assertTrue(aReport.contains("Time: 13:00:55"));
        assertTrue(aReport.contains("Version: " + aVersion));
    }


    /**
     * The {@code writeReport} method should throw an {@code IOException} if the XSL transformation
     * encounters a processing error.
     *
     * @throws IOException  always
     * @throws ParseException   if the test fails unexpectedly
     */
    @Test(expected = IOException.class)
    public void writeReportThrowsOnProcessingError() throws IOException, ParseException
    {
        // Given
        XslReportWriter aReportWriter =
            new XslReportWriter(new ByteArrayOutputStream(), "/xsl/processing_error.xsl");

        // When
        aReportWriter.writeReport(CollectTestUtil.collect(""), EMPTY_META_DATA);
    }


    static private Path createFileFromResource(String pFileName, String pResourceName)
    {
        try (InputStream aStream =
                 XslReportWriterTest.class.getResourceAsStream(pResourceName))
        {
            Path aPath = Paths.get(pFileName);
            Files.copy(aStream, aPath);
            return aPath;
        }
        catch (IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }
}
