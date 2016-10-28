/*
 * Copyright 2016 Peter Franzen. All rights reserved.
 *
 * Licensed under the Apache License v2.0: http://www.apache.org/licenses/LICENSE-2.0
 */
package org.myire.scent.file;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.After;


/**
 * Base class for tests creating a file that should be deleted when the test finishes.
 *
 * @author <a href="mailto:peter@myire.org">Peter Franzen</a>
 */
public class FileTestBase
{
    protected Path fTestFile;


    /**
     * Delete the test file if created by the test.
     *
     * @throws IOException  if deleting the file fails.
     */
    @After
    public void deleteTestFile() throws IOException
    {
        if (fTestFile != null)
            Files.deleteIfExists(fTestFile);
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
            Files.copy(aStream, pFilePath);
            return pFilePath;
        }
    }
}
