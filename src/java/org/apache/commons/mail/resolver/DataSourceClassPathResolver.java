/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.mail.resolver;

import org.apache.commons.mail.ByteArrayDataSource;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import java.io.IOException;
import java.io.InputStream;

/**
 * Creates a <code>DataSource</code> based on an class path.
 *
 * @since 1.3
 */
public class DataSourceClassPathResolver extends DataSourceBaseResolver
{
    /** the base string of the resource relative to the classpath when resolving relative paths */
    private final String classPathBase;

    public DataSourceClassPathResolver()
    {
        this.classPathBase = "/";
    }

    public DataSourceClassPathResolver(final String classPathBase)
    {
        this.classPathBase = classPathBase.endsWith("/") ? classPathBase : classPathBase + "/";
    }

    public DataSourceClassPathResolver(final String classPathBase, final boolean lenient)
    {
        super(lenient);
        this.classPathBase = classPathBase.endsWith("/") ? classPathBase : classPathBase + "/";
    }

    /**
     * @return the classPathBase
     */
    public String getClassPathBase()
    {
        return classPathBase;
    }

    public DataSource resolve(String resourceLocation) throws IOException
    {
        return resolve(resourceLocation, isLenient());
    }

    public DataSource resolve(final String resourceLocation, final boolean isLenient) throws IOException
    {
        DataSource result = null;

        try
        {
            if (!isCid(resourceLocation) && !isHttpUrl(resourceLocation))
            {
                String mimeType = FileTypeMap.getDefaultFileTypeMap().getContentType(resourceLocation);
                String resourceName = getResourceName(resourceLocation);
                InputStream is = DataSourceClassPathResolver.class.getResourceAsStream(resourceName);

                if(is != null)
                {
                    result = new ByteArrayDataSource(is, mimeType);
                }
                else
                {
                    if (isLenient)
                    {
                        return null;
                    }
                    else
                    {
                        throw new IOException("The following class path resource was not found : " + resourceLocation);
                    }
                }
            }


            return result;
        }
        catch (IOException e)
        {
            if (isLenient)
            {
                return null;
            }
            else
            {
                throw e;
            }
        }
    }

    private String getResourceName(final String resourceLocation)
    {
        return (getClassPathBase() + resourceLocation).replaceAll("//", "/");
    }
}
