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
import org.apache.maven.plugin.descriptor.PluginDescriptor;

import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class LoadPathHelper extends AbstractJarHelper {

    private static final String FILE_URI_PREFIX = "file:";
    private static final String JAR_ROOT_PREFIX = "!/";
    private static final String RB_SUFFIX = ".rb";
    private static final String LIB_DIR = "lib";


    public LoadPathHelper(AbstractCompassMojo mojo) {
        super(mojo);
    }


    public List<String> getLoadPaths() throws MojoFailureException {
        JarFile jarFile = getJar();
        String pathRoot = new StringBuilder(FILE_URI_PREFIX).append(jarFile.getName()).append(JAR_ROOT_PREFIX).toString();
        int jarRootIndex = pathRoot.length();

        Set<String> loadPaths = new TreeSet<String>(new EntryComparator(jarRootIndex));

        for(Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
            String entryName = entries.nextElement().getName();

            if(entryName.endsWith(RB_SUFFIX)) {
                String entryDirectory = new StringBuilder(pathRoot)
                        .append(entryName.substring(0, entryName.lastIndexOf(SEPARATOR))).toString();

                if(entryDirectory.endsWith(LIB_DIR) && !pathExists(entryDirectory, loadPaths, jarRootIndex)) loadPaths.add(entryDirectory);
            }
        }

        return new ArrayList<String>(loadPaths);
    }


    private boolean pathExists(String entryDirectory, Set<String> loadPaths, int jarRootIndex) {
        int index = entryDirectory.lastIndexOf('/');
        return index > jarRootIndex &&
                (loadPaths.contains(entryDirectory) ||
                        (!entryDirectory.endsWith(LIB_DIR) &&
                                pathExists(entryDirectory.substring(0, index), loadPaths, jarRootIndex)));
    }
}
