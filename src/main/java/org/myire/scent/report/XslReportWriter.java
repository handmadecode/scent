/*
 * Copyright 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.myire.scent.metrics.JavaMetrics;


/**
 * An {@code XslReportWriter} creates metrics reports by applying an XSL transformation to an
 * XML report produced by an {@link XmlReportWriter}. The report is written to an
 * {@code OutputStream}.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class XslReportWriter implements MetricsReportWriter
{
    static private final TransformerFactory cFactory = TransformerFactory.newInstance();

    private final OutputStream fOutputStream;
    private final File fXslFile;
    private final String fXslResource;


    /**
     * Create a new {@code XslReportWriter} that gets its XSL style sheet from a file.
     *
     * @param pOutputStream The stream to write the report to.
     * @param pXslFile      A file containing the XSL style sheet to create the report with.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    public XslReportWriter(@Nonnull OutputStream pOutputStream, @Nonnull File pXslFile)
    {
        fOutputStream = requireNonNull(pOutputStream);
        fXslFile = requireNonNull(pXslFile);
        fXslResource = null;
    }


    /**
     * Create a new {@code XslReportWriter} that gets its XSL style sheet from a classpath resource.
     *
     * @param pOutputStream The stream to write the report to.
     * @param pXslResource  The name of the resource containing the XSL style sheet.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    public XslReportWriter(@Nonnull OutputStream pOutputStream, @Nonnull String pXslResource)
    {
        fOutputStream = requireNonNull(pOutputStream);
        fXslResource = requireNonNull(pXslResource);
        fXslFile = null;
    }


    @Override
    public void writeReport(
        @Nonnull JavaMetrics pMetrics,
        @Nonnull MetricsReportMetaData pMetaData) throws IOException
    {
        try
        {
            // Create the transformer from the file or resource specified in the constructor.
            Transformer aTransformer =
                fXslFile != null ? createTransformer(fXslFile) :createTransformer(fXslResource);

            // Write the XML report to a byte array.
            ByteArrayOutputStream aXmlStream = new ByteArrayOutputStream(1 << 16);
            new XmlReportWriter(aXmlStream).writeReport(pMetrics, pMetaData);

            // Transform the XML report in the byte array and write the result to the output stream
            // specified in the constructor.
            ByteArrayInputStream aXmlInputStream =
                new ByteArrayInputStream(aXmlStream.toByteArray());
            aTransformer.transform(
                new StreamSource(aXmlInputStream),
                new StreamResult(fOutputStream));
        }
        catch (TransformerException te)
        {
            throw new IOException(te);
        }
    }


    /**
     * Create a {@code Transformer} from an XSL file.
     *
     * @param pXslFile  A file containing the XSL style sheet to create the {@code Transformer} from.
     *
     * @return  A new {@code Transformer}, never null.
     *
     * @throws TransformerConfigurationException
     *                              if the XSL file doesn't exist, cannot be accessed, or has
     *                              invalid contents.
     * @throws NullPointerException if {@code pXslFile} is null.
     */
    @Nonnull
    static private Transformer createTransformer(@Nonnull File pXslFile)
        throws TransformerConfigurationException
    {
        return cFactory.newTransformer(new StreamSource(pXslFile));
    }


    /**
     * Create a {@code Transformer} from an XSL resource on the classpath. The resource will be
     * accessed using the class loader of this class.
     *
     * @param pXslResource  The name of the XSL resource.
     *
     * @return  A new {@code Transformer}, never null.
     *
     * @throws IOException  if the XSL resource doesn't exist or cannot be accessed.
     * @throws TransformerConfigurationException
     *                              if the XSL resource has invalid contents.
     * @throws NullPointerException if {@code pXslResource} is null.
     */
    @Nonnull
    static private Transformer createTransformer(@Nonnull String pXslResource)
        throws IOException, TransformerConfigurationException
    {
        try (InputStream aResourceStream = getResourceAsStream(pXslResource))
        {
            return cFactory.newTransformer(new StreamSource(aResourceStream));
        }
    }


    /**
     * Get an {@code InputStream} for a classpath resource.
     *
     * @param pResource The name of the resource.
     *
     * @return  An open {@code InputStream}, it is the caller's responsibility to close it.
     *
     * @throws IOException  if the resource does not exist.
     */
    @Nonnull
    static private InputStream getResourceAsStream(@Nonnull String pResource) throws IOException
    {
        InputStream aResourceStream = XslReportWriter.class.getResourceAsStream(pResource);
        if (aResourceStream != null)
            return aResourceStream;
        else
            throw new IOException("Resource not found: " + pResource);
    }
}
