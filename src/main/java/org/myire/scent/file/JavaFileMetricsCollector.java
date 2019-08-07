/*
 * Copyright 2016, 2019 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import static java.util.Objects.requireNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.myire.scent.collect.JavaMetricsCollector;


/**
 * A {@code FileVisitor} that collects source code metrics for each visited Java file. The metrics
 * are collected by calling {@link JavaMetricsCollector#collect(String, InputStream, Charset)}.
 *<p>
 * Instances of this class are <b>not</b> safe for use by multiple threads without external
 * synchronization.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
@NotThreadSafe
public class JavaFileMetricsCollector extends SimpleFileVisitor<Path>
{
    private final JavaMetricsCollector fDelegate;
    private final Charset fFileEncoding;
    private int fNumFiles;


    /**
     * Create a new {@code JavaFileMetricsCollector} that operates on files encoded with UTF-8.
     *
     * @param pDelegate The instance to collect metrics with.
     *
     * @throws NullPointerException if {@code pDelegate} is null.
     */
    public JavaFileMetricsCollector(@Nonnull JavaMetricsCollector pDelegate)
    {
        this(pDelegate, StandardCharsets.UTF_8);
    }


    /**
     * Create a new {@code JavaFileMetricsCollector}.
     *
     * @param pDelegate     The instance to collect metrics with.
     * @param pFileEncoding The charset the files are encoded with.
     *
     * @throws NullPointerException if any of the parameters is null.
     */
    public JavaFileMetricsCollector(@Nonnull JavaMetricsCollector pDelegate, @Nonnull Charset pFileEncoding)
    {
        fDelegate = requireNonNull(pDelegate);
        fFileEncoding = requireNonNull(pFileEncoding);
    }


    @Override
    public FileVisitResult visitFile(@Nonnull Path pFile, @Nullable BasicFileAttributes pAttributes)
            throws IOException
    {
        if (!isJavaFile(pFile))
            return FileVisitResult.CONTINUE;

        try (InputStream aStream = Files.newInputStream(pFile))
        {
            // Use only the file name part of the path as name for the compilation unit.
            Path aFileName = pFile.getFileName();
            if (aFileName == null)
                aFileName = pFile;

            fDelegate.collect(aFileName.toString(), aStream, fFileEncoding);
            fNumFiles++;

            return FileVisitResult.CONTINUE;
        }
        catch (ParseException pe)
        {
            throw new IOException(pe);
        }
    }


    /**
     * Get the number of files that metrics have been collected for.
     *
     * @return  The number of files.
     */
    public int getNumFiles()
    {
        return fNumFiles;
    }


    /**
     * Get the {@code JavaMetricsCollector} passed to the constructor. The Java file parsing and
     * metrics collection is delegated to that instance.
     *
     * @return  The delegate {@code JavaMetricsCollector}, never null.
     */
    @Nonnull
    protected JavaMetricsCollector getDelegate()
    {
        return fDelegate;
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
