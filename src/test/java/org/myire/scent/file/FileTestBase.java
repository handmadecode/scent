/*
 * Copyright 2016, 2018 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;


/**
 * Base class for tests that create one or more files that should be deleted when the test finishes.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class FileTestBase
{
    static private final String JAVA_FILE_RESOURCE_ROOT = "/";
    static private final String XSL_FILE_RESOURCE_ROOT = "/xsl/";

    private final List<Path> fTestFiles = new ArrayList<>();
    private Path fTestDirectory;


    /**
     * Create the temporary directory where all test files will be created.
     *
     * @throws IOException  if creating the directory fails.
     */
    @Before
    public void createTestDirectory() throws IOException
    {
        fTestDirectory = Files.createTempDirectory(getClass().getName());
    }


    /**
     * Delete the test files and directory created by the test.
     *
     * @throws IOException  if deleting a file or the directory fails.
     */
    @After
    public void deleteTestFiles() throws IOException
    {
        for (Path aTestFile : fTestFiles)
            Files.deleteIfExists(aTestFile);

        if (fTestDirectory != null)
            Files.deleteIfExists(fTestDirectory);
    }


    /**
     * Get the test directory where the test files by default are created.
     *
     * @return  The test directory.
     */
    protected Path getTestDirectory()
    {
        return fTestDirectory;
    }


    /**
     * Mark a file to be deleted after the test has run.
     *
     * @param pTestFile The file to delete after the test.
     */
    protected void markForDeletion(Path pTestFile)
    {
        fTestFiles.add(pTestFile);
    }


    /**
     * Create a file in the temporary test directory and mark the file for deletion after the test
     * has finished.
     *
     * @param pFileName The name of the file to create.
     *
     * @return  The path to the created file.
     *
     * @throws IOException  if creating the file fails.
     */
    protected Path createTestFile(String pFileName) throws IOException
    {
        Path aPath = Files.createFile(fTestDirectory.resolve(pFileName));
        markForDeletion(aPath);
        return aPath;
    }


    /**
     * Copy a classpath resource to a file in the temporary test directory and mark the file for
     * deletion after the test has finished.
     *
     * @param pResourceName The name of the resource relative to the test Java resource root.
     *
     * @return  The path to the created file.
     *
     * @throws IOException  if accessing the resource or writing to the file fails.
     */
    protected Path createTestFileFromJavaResource(String pResourceName) throws IOException
    {
        return createTestFileFromResource(JAVA_FILE_RESOURCE_ROOT, pResourceName);
    }


    /**
     * Copy a classpath resource to a file in the temporary test directory and mark the file for
     * deletion after the test has finished.
     *
     * @param pResourceName The name of the resource relative to the test XSL resource root.
     *
     * @return  The path to the created file.
     *
     * @throws IOException  if accessing the resource or writing to the file fails.
     */
    protected Path createTestFileFromXslResource(String pResourceName) throws IOException
    {
        return createTestFileFromResource(XSL_FILE_RESOURCE_ROOT, pResourceName);
    }


    /**
     * Copy a classpath resource to a file in the temporary test directory and mark the file for
     * deletion after the test has finished.
     *
     * @param pResourcePrefix   The prefix that should be prepended to the file name to get the
     *                          resource path.
     * @param pFileName         The name of the file to create.
     *
     * @return  The path to the created file.
     *
     * @throws IOException  if accessing the resource or writing to the file fails.
     */
    protected Path createTestFileFromResource(String pResourcePrefix, String pFileName) throws IOException
    {
        Path aPath = copyResourceToFile(pResourcePrefix + pFileName, fTestDirectory.resolve(pFileName));
        markForDeletion(aPath);
        return aPath;
    }


    /**
     * Copy a resource to a file.
     *
     * @param pResource The name of the resource.
     * @param pFilePath The path of the file to copy the resource's contents to.
     *
     * @return  {@code pFilePath}.
     *
     * @throws IOException  if accessing the resource or writing to the file fails.
     */
    static protected Path copyResourceToFile(String pResource, Path pFilePath) throws IOException
    {
        try (InputStream aStream = FileTestBase.class.getResourceAsStream(pResource))
        {
            Files.copy(aStream, pFilePath, StandardCopyOption.REPLACE_EXISTING);
            return pFilePath;
        }
    }
}
