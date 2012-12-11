/*
 * Copyright 2012 Sparks Creative Limited
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package net.uk.sparks.webstack.compass.plugin;

import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourceHelper extends AbstractJarHelper {


    public ResourceHelper(AbstractCompassMojo mojo) {
        super(mojo);
    }


    public void writeResource(JarFile jar, String resourceRoot, File targetDir) throws MojoFailureException {
        JarEntry rootResource = jar.getJarEntry(resourceRoot);

        if(rootResource != null) {
            int fileIndex = resourceRoot.lastIndexOf(SEPARATOR);

            if(rootResource.isDirectory()) {
                if(fileIndex == resourceRoot.length() - 1) {
                    fileIndex = resourceRoot.substring(0, fileIndex).lastIndexOf(SEPARATOR);
                }

                File rootDir = new File(targetDir, resourceRoot.substring(fileIndex));

                if(rootDir.exists() || rootDir.mkdirs()) {
                    int jarRootIndex = resourceRoot.length();
                    Set<JarEntry> resources = new TreeSet<JarEntry>(new JarEntryComparator(jarRootIndex));
                    JarEntry entry = null;

                    for(Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements(); entry = entries.nextElement()) {
                        if(entry != null && !entry.isDirectory() && entry.getName().startsWith(resourceRoot)) resources.add(entry);
                    }

                    for(JarEntry resource : resources) writeResource(jar, resource, jarRootIndex, rootDir);
                }
            } else {
                writeResource(jar, rootResource, fileIndex, targetDir);
            }
        }
    }


    private void writeResource(JarFile jar, JarEntry resource, int jarRootIndex, File parentDir) throws MojoFailureException {
        String resourceName = resource.getName().substring(jarRootIndex);
        int fileIndex = resourceName.lastIndexOf(SEPARATOR);
        File targetDir = fileIndex < 0
                ? parentDir : new File(parentDir, resourceName.substring(0, fileIndex));

        if(targetDir.exists() || targetDir.mkdirs()) {
            InputStream input = null;
            FileOutputStream output = null;

            try {
                input = jar.getInputStream(resource);
                output = new FileOutputStream(new File(targetDir, fileIndex < 0
                        ? resourceName : resourceName.substring(fileIndex)));
                writeResource(input, output);
            } catch (IOException e) {
                throw new MojoFailureException(e.getMessage());
            } finally {
                try {
                    if(output != null) output.close();
                    if(input != null) input.close();
                } catch (IOException e) {
                    throw new MojoFailureException(e.getMessage());
                }
            }
        }
    }

    private void writeResource(InputStream input, FileOutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) output.write(buffer, 0, bytesRead);
    }
}
