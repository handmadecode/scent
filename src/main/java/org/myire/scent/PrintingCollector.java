/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import static java.util.Objects.requireNonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.myire.scent.collect.JavaMetricsCollector;
import org.myire.scent.file.JavaFileMetricsCollector;


/**
 * Extension of {@code JavaFileMetricsCollector} that prints progress to a {@code PrintStream} and
 * any errors to another {@code PrintStream}.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
class PrintingCollector extends JavaFileMetricsCollector
{
    static private final String ERROR_TEMPLATE = "Failed to collect metrics from %s: %s";

    private final PrintStream fOutStream;
    private final PrintStream fErrStream;


    /**
     * Create a new {@code PrintingCollector} that operates on files encoded with UTF-8.
     *
     * @param pDelegate     The instance to collect metrics with.
     * @param pOutStream    The print stream to print progress to.
     * @param pErrStream    The print stream to print any errors to.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    PrintingCollector(
            @Nonnull JavaMetricsCollector pDelegate,
            @Nonnull PrintStream pOutStream,
            @Nonnull PrintStream pErrStream)
    {
        super(pDelegate);
        fOutStream = requireNonNull(pOutStream);
        fErrStream = requireNonNull(pErrStream);
    }


    /**
     * Create a new {@code PrintingCollector}.
     *
     * @param pDelegate     The instance to collect metrics with.
     * @param pFileEncoding The charset the files are encoded with.
     * @param pOutStream    The print stream to print progress to.
     * @param pErrStream    The print stream to print any errors to.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    PrintingCollector(
            @Nonnull JavaMetricsCollector pDelegate,
            @Nonnull Charset pFileEncoding,
            @Nonnull PrintStream pOutStream,
            @Nonnull PrintStream pErrStream)
    {
        super(pDelegate, pFileEncoding);
        fOutStream = requireNonNull(pOutStream);
        fErrStream = requireNonNull(pErrStream);
    }


    @Override
    public FileVisitResult preVisitDirectory(@Nonnull Path pDirectory, @Nonnull BasicFileAttributes pAttributes)
    {
        fOutStream.println("Processing " + pDirectory);
        return FileVisitResult.CONTINUE;
    }


    @Override
    public FileVisitResult visitFile(@Nonnull Path pFile, @Nonnull BasicFileAttributes pAttributes)
    {
        try
        {
            return super.visitFile(pFile, pAttributes);
        }
        catch (IOException e)
        {
            fErrStream.println(String.format(ERROR_TEMPLATE, pFile, e.getMessage()));
            return FileVisitResult.CONTINUE;
        }
    }


    @Override
    public FileVisitResult visitFileFailed(@Nonnull Path pFile, @Nonnull IOException pException)
    {
        fErrStream.println(String.format(ERROR_TEMPLATE, pFile, pException.getMessage()));
        return FileVisitResult.CONTINUE;
    }


    @Override
    public FileVisitResult postVisitDirectory(@Nonnull Path pDirectory, @CheckForNull IOException pException)
    {
        if (pException != null)
            fErrStream.println(String.format(ERROR_TEMPLATE, pDirectory, pException.getMessage()));

        return FileVisitResult.CONTINUE;
    }
}
