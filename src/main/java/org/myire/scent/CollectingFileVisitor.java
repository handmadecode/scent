/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import static java.util.Objects.requireNonNull;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;

import org.myire.scent.collect.JavaMetricsCollector;


/**
 * A {@code FileVisitor} that calls
 * {@link JavaMetricsCollector#collect(String, InputStream, Charset)} for each visited Java file.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
class CollectingFileVisitor implements FileVisitor<Path>
{
    static private final String ERROR_TEMPLATE = "Failed to collect metrics from %s: %s";

    private final JavaMetricsCollector fCollector;
    private final PrintStream fOutStream;
    private final PrintStream fErrStream;
    private final Charset fFileEncoding;
    private int fNumFiles;


    /**
     * Create a new {@code CollectingFileVisitor} that operates on files encoded with UTF-8.
     *
     * @param pCollector    The instance to collect metrics with.
     * @param pOutStream    The print stream to print progress to.
     * @param pErrStream    The print stream to print any errors to.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    CollectingFileVisitor(
            @Nonnull JavaMetricsCollector pCollector,
            @Nonnull PrintStream pOutStream,
            @Nonnull PrintStream pErrStream)
    {
        this(pCollector, pOutStream, pErrStream, StandardCharsets.UTF_8);
    }


    /**
     * Create a new {@code CollectingFileVisitor}.
     *
     * @param pCollector    The instance to collect metrics with.
     * @param pOutStream    The print stream to print progress to.
     * @param pErrStream    The print stream to print any errors to.
     * @param pFileEncoding The charset the files are encoded with.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    CollectingFileVisitor(
            @Nonnull JavaMetricsCollector pCollector,
            @Nonnull PrintStream pOutStream,
            @Nonnull PrintStream pErrStream,
            @Nonnull Charset pFileEncoding)
    {
        fCollector = requireNonNull(pCollector);
        fOutStream = requireNonNull(pOutStream);
        fErrStream = requireNonNull(pErrStream);
        fFileEncoding = requireNonNull(pFileEncoding);
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
        if (!isJavaFile(pFile))
            return FileVisitResult.CONTINUE;

        try (FileInputStream aStream = new FileInputStream(pFile.toFile()))
        {
            // Use only the file name part of the path as name for the compilation unit.
            Path aFileName = pFile.getFileName();
            if (aFileName == null)
                aFileName = pFile;

            fCollector.collect(aFileName.toString(), aStream, fFileEncoding);
            fNumFiles++;
        }
        catch (IOException | ParseException e)
        {
            fErrStream.println(String.format(ERROR_TEMPLATE, pFile, e.getMessage()));
        }

        return FileVisitResult.CONTINUE;
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


    /**
     * Get the number of files that metrics have been collected for.
     *
     * @return  The number of files.
     */
    int getNumFiles()
    {
        return fNumFiles;
    }


    /**
     * Check if a file system item is a Java file.
     *
     * @param pPath The path to the item to check.
     *
     * @return  True if {@code pPath} is a regular file with a name that ends in &quot;.java&quot;,
     *          false otherwise.
     *
     * @throws NullPointerException if {@code pPath} is null.
     */
    static private boolean isJavaFile(@Nonnull Path pPath)
    {
        return Files.isRegularFile(pPath) && pPath.toString().endsWith(".java");
    }
}
