/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.myire.scent.collect.JavaMetricsCollector;
import org.myire.scent.metrics.JavaMetrics;
import org.myire.scent.report.MetricsReportMetaData;
import org.myire.scent.report.MetricsReportWriter;
import org.myire.scent.report.TextReportWriter;
import org.myire.scent.report.XmlReportWriter;
import org.myire.scent.report.XslReportWriter;


/**
 * Main entry point for the scent jar.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public final class Main
{
    /**
     * Private constructor to disallow instantiations of utility method class.
     */
    private Main()
    {
        // Empty default ctor, defined to override access scope.
    }


    /**
     * Collect metrics from all files ending in &quot;.java&quot; found in the path(s) specified as
     * argument(s) to this method, recursively descending into subdirectories.
     *<p>
     * The synopsis for the arguments to this method are:
     *<pre>
     *  [-text] [-xml] [-html] [-xsl xsl-file] [-o output-file] path ...
     *</pre>
     * where the options are
     *<ul>
     * <li>{@code -text}: report the collected metrics in plain text format</li>
     * <li>{@code -xml}: report the collected metrics in xml format</li>
     * <li>{@code -html}: report the collected metrics in html format</li>
     * <li>{@code -xsl xsl-file}: report the collected metrics by applying the specified XSL file to
     *      an intermediate xml report</li>
     * <li>{@code -o output-file}: write the report to the specified file</li>
     *</ul>
     * If no format is specified, the text format will be used. If multiple formats are specified,
     * the last will take precedence. If no output file is specified, the report will be written to
     * {@code System.out}.
     *
     * @param pArgs Any options followed by the Java source path(s) to collect metrics from. The
     *              files in the paths are assumed to be encoded in UTF-8.
     *
     * @throws NullPointerException if {@code pArgs} is null.
     */
    static public void main(@Nonnull String... pArgs)
    {
        MainOptions aMainOptions = new MainOptions(System.out);
        int aFirstPathIndex = aMainOptions.extract(pArgs);

        JavaMetricsCollector aCollector = new JavaMetricsCollector();
        collectMetrics(aCollector, pArgs, aFirstPathIndex);

        JavaMetrics aMetrics = aCollector.getCollectedMetrics();
        if (!aMetrics.isEmpty())
           writeReport(aMetrics, aMainOptions::createOutputStream, aMainOptions::createReportWriter);
    }


    /**
     * Visit all files in zero or more paths, recursively descending into subdirectories, and
     * collect metrics for those ending in &quot;.java&quot;.
     *
     * @param pCollector    The collector to collect metrics with.
     * @param pPaths        The Java source file path(s) to collect metrics from. The files are
     *                      assumed to be encoded in UTF-8.
     * @param pFromIndex    The first valid index in {@code pPaths}.
     *
     * @throws NullPointerException if any of the reference parameters is null.
     */
    static private void collectMetrics(
        @Nonnull JavaMetricsCollector pCollector,
        @Nonnull String[] pPaths,
        int pFromIndex)
    {
        PrintingCollector aCollector = new PrintingCollector(pCollector, System.out, System.err);
        for (int i=pFromIndex; i<pPaths.length; i++)
        {
            try
            {
                Files.walkFileTree(Paths.get(pPaths[i]), aCollector);
            }
            catch (IOException e)
            {
                printError("Error when collecting metrics from " + pPaths[i] + ": "+ e.getMessage());
            }
        }

        printInfo("Collected metrics from " + aCollector.getNumFiles() + " files");
    }


    /**
     * Write a report with the values in a {@code JavaMetrics} to an {@code OutputStream} using a
     * {@code MetricsReportWriter}.
     *
     * @param pMetrics              The metrics to create the report from.
     * @param pOutputStreamSupplier A supplier of the output stream to write the report to.
     * @param pReportWriterCreator  A function that creates a {@code MetricsReportWriter} given an
     *                              {@code OutputStream}.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    static private void writeReport(
        @Nonnull JavaMetrics pMetrics,
        @Nonnull Supplier<OutputStream> pOutputStreamSupplier,
        @Nonnull Function<OutputStream, MetricsReportWriter> pReportWriterCreator)
    {
        try (OutputStream aOutputStream = pOutputStreamSupplier.get())
        {
            MetricsReportWriter aWriter = pReportWriterCreator.apply(aOutputStream);
            String aVersion = Main.class.getPackage().getImplementationVersion();
            LocalDateTime aTimestamp = LocalDateTime.now().withNano(0);
            aWriter.writeReport(pMetrics, new MetricsReportMetaData(aTimestamp, aVersion));
        }
        catch (IOException ioe)
        {
            printError("Failed to write report: " + ioe.getMessage());
        }
    }


    /**
     * Print an information message to {@code System.out}.
     *
     * @param pMessage  The message to print.
     */
    static private void printInfo(@Nullable String pMessage)
    {
        printMessage(System.out, pMessage);
    }


    /**
     * Print an error message to {@code System.err}.
     *
     * @param pMessage  The message to print.
     */
    static private void printError(@Nullable String pMessage)
    {
        printMessage(System.err, pMessage);
    }


    /**
     * Print a message to a {@code PrintStream}.
     *
     * @param pStream   The stream to print to.
     * @param pMessage  The message to print.
     *
     * @throws NullPointerException if {@code pStream} is null.
     */
    static private void printMessage(@Nonnull PrintStream pStream, @Nullable String pMessage)
    {
        pStream.println(pMessage);
    }


    /**
     * Class holding the option arguments passed to {@code main()}.
     */
    static private class MainOptions
    {
        private final PrintStream fErrStream;

        private String fOutputFilePath;
        private String fXslFilePath;
        private Function<OutputStream, MetricsReportWriter> fReportWriterCreator = TextReportWriter::new;

        /**
         * Create a new {@code MainOptions}.
         *
         * @param pErrStream    The stream to print errors and warnings to.
         *
         * @throws NullPointerException if {@code pErrStream} is null.
         */
        MainOptions(@Nonnull PrintStream pErrStream)
        {
            fErrStream = requireNonNull(pErrStream);
        }

        /**
         * Extract the options from the arguments passed to {@code main}.
         *
         * @param pArgs The {@code main} arguments.
         *
         * @return  The index of the first non-option argument in {@code pArgs}.
         *
         * @throws NullPointerException if {@code pArgs} is null.
         */
        int extract(@Nonnull String[] pArgs)
        {
            int i=0;
            while (i<pArgs.length)
            {
                switch (pArgs[i])
                {
                    case "-text":
                        fReportWriterCreator = TextReportWriter::new;
                        break;
                    case "-xml":
                        fReportWriterCreator = XmlReportWriter::new;
                        break;
                    case "-html":
                        fReportWriterCreator = this::createXslReportWriter;
                        break;
                    case "-xsl":
                        if (i < pArgs.length - 1)
                        {
                            fXslFilePath = pArgs[++i];
                            fReportWriterCreator = this::createXslReportWriter;
                        }
                        else
                            fErrStream.println("Warning: missing xsl file path, ignoring xsl format");
                        break;
                    case "-o":
                        if (i < pArgs.length - 1)
                            fOutputFilePath = pArgs[++i];
                        else
                            fErrStream.println("Warning: missing output file path, writing to System.out");
                        break;
                    default:
                        if (pArgs[i].charAt(0) == '-')
                            fErrStream.println("Warning: ignoring unknown option '" + pArgs[i] + '\'');
                        else
                            return i;
                }

                i++;
            }

            return pArgs.length;
        }

        /**
         * Create an {@code OutputStream} for the output file specified in the options passed to
         * {@link #extract(String[])}.
         *
         * @return  A new {@code OutputStream}, or null if no output file was specified. Null is
         *          also returned if the specified output file is invalid.
         */
        @CheckForNull
        OutputStream createOutputStream()
        {
            if (fOutputFilePath == null)
                return null;

            try
            {
                return new FileOutputStream(fOutputFilePath);
            }
            catch (FileNotFoundException fnfe)
            {
                fErrStream.println(
                    "Error: cannot write report to '" +
                        fOutputFilePath +
                        "': " +
                        fnfe.getMessage());

                return null;
            }
        }

        /**
         * Create the {@code MetricsReportWriter} specified in the options passed to
         * {@link #extract(String[])}.
         *
         * @param pOutputStream The output stream the report should be written to. If this parameter
         *                      is null, {@code System.out} will be used.
         *
         * @return  A new {@code MetricsReportWriter}, never null.
         */
        @Nonnull
        MetricsReportWriter createReportWriter(@Nullable OutputStream pOutputStream)
        {
            if (pOutputStream != null)
                return fReportWriterCreator.apply(pOutputStream);
            else
                return fReportWriterCreator.apply(System.out);
        }

        /**
         * Create an {@code XslReportWriter} that writes to the specified {@code OutputStream}. The
         * XSL will be taken from the file specified as argument to the {@code -xsl} option or, if
         * that option wasn't specified, from the built-in XSL resource for creating an HTML report.
         *
         * @param pOutputStream The stream to write the report to.
         *
         * @return  A new {@code MetricsReportWriter}, never null.
         *
         * @throws NullPointerException if {@code pOutputStream} is null.
         */
        @Nonnull
        private MetricsReportWriter createXslReportWriter(@Nonnull OutputStream pOutputStream)
        {
            if (fXslFilePath != null)
                return new XslReportWriter(pOutputStream, new File(fXslFilePath));
            else
                return new XslReportWriter(pOutputStream, "/html-report.xsl");
        }
    }
}
