/*
 *
 * Copyright 2015 Netflix, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package com.netflix.imflibrary.st0429_9;

import com.netflix.imflibrary.IMFErrorLogger;
import com.netflix.imflibrary.exceptions.IMFException;
import org.xml.sax.SAXException;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * This class is an immutable implementation of the Mapped File Set concept defined in Section A.1 in Annex A of st0429-9:2014.
 * A MappedFileSet object can only be constructed if the constraints specified in Section A.1 in Annex A of st0429-9:2014 are
 * satisfied
 */
@Immutable
public final class MappedFileSet
{

    private static final String ASSETMAP_FILE_NAME = "ASSETMAP.xml";
    private final AssetMap assetMap;
    private final URI assetMapURI;

    /**
     * Constructor for a MappedFileSet object from a file representing the root of a directory tree
     * @param rootFile the directory which serves as the tree root of the Mapped File Set
     * @param imfErrorLogger an error logger for recording any errors - can be null
     * @throws SAXException - forwarded from {@link AssetMap#AssetMap(java.io.File, com.netflix.imflibrary.IMFErrorLogger) AssetMap} constructor
     * @throws IOException - forwarded from {@link AssetMap#AssetMap(java.io.File, com.netflix.imflibrary.IMFErrorLogger) AssetMap} constructor
     * @throws JAXBException - forwarded from {@link AssetMap#AssetMap(java.io.File, com.netflix.imflibrary.IMFErrorLogger) AssetMap} constructor
     * @throws URISyntaxException - forwarded from {@link AssetMap#AssetMap(java.io.File, com.netflix.imflibrary.IMFErrorLogger) AssetMap} constructor
     */
    public MappedFileSet(File rootFile, @Nullable IMFErrorLogger imfErrorLogger) throws IOException, SAXException, JAXBException, URISyntaxException
    {
        if (!rootFile.isDirectory())
        {
            throw new IMFException(String.format("Root file %s corresponding to the mapped file set is not a directory", rootFile.getAbsolutePath()));
        }

        FilenameFilter filenameFilter = new FilenameFilter()
        {
            @Override
            public boolean accept(File rootFile, String name)
            {
                return name.equals(MappedFileSet.ASSETMAP_FILE_NAME);
            }
        };

        File[] files = rootFile.listFiles(filenameFilter);
        if ((files == null) || (files.length != 1))
        {
            throw new IMFException(String.format("Found %d files with name %s in mapped file set rooted at %s, " +
                    "exactly 1 is allowed", (files == null) ? 0 : files.length, MappedFileSet.ASSETMAP_FILE_NAME, rootFile.getAbsolutePath()));
        }

        this.assetMap = new AssetMap(files[0], imfErrorLogger);
        this.assetMapURI = files[0].toURI();
    }

    /**
     * Getter for the {@link com.netflix.imflibrary.st0429_9.AssetMap AssetMap} object that represents the single AssetMap document
     * corresponding to this Mapped File Set
     * @return the AssetMap object
     */
    public AssetMap getAssetMap()
    {
        return this.assetMap;
    }

    /**
     * Getter for the file-based URI corresponding to the {@link com.netflix.imflibrary.st0429_9.AssetMap AssetMap} object associated with
     * this Mapped File Set
     * @return file-based URI for the AssetMap object
     */
    public URI getAssetMapURI()
    {
        return this.assetMapURI;
    }
}
