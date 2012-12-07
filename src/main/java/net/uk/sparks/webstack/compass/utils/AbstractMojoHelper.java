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

package net.uk.sparks.webstack.compass.utils;

import net.uk.sparks.webstack.compass.plugin.AbstractCompassMojo;
import net.uk.sparks.webstack.compass.plugin.MojoHelper;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;

import java.io.IOException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class AbstractMojoHelper implements MojoHelper {

    private static final String FILE_URI_PREFIX = "file:";
    private static final String JAR_ROOT_PREFIX = "!/";
    private static final char SEPARATOR = '/';
    private static final String RB_SUFFIX = ".rb";
    private static final String LIB_DIR = "lib";

    private static final String SOURCE_FILE_ERROR = "Could not resolve source file for plugin jar.";


    protected List<String> getLoadPaths(AbstractCompassMojo mojo) throws MojoFailureException {
        try {
            JarFile jarFile = new JarFile(((PluginDescriptor) mojo.getPluginContext().get("pluginDescriptor")).getSource());
            String pathRoot = new StringBuilder(FILE_URI_PREFIX).append(jarFile.getName()).append(JAR_ROOT_PREFIX).toString();
            int jarRootIndex = pathRoot.length();
            Set<String> loadPaths = newLoadPathSet(jarRootIndex);

            for(Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements();) {
                String entryName = entries.nextElement().getName();

                if(entryName.endsWith(RB_SUFFIX)) {
                    String entryDirectory = new StringBuilder(pathRoot)
                            .append(entryName.substring(0, entryName.lastIndexOf(SEPARATOR))).toString();

                    if(entryDirectory.endsWith(LIB_DIR) && !pathExists(entryDirectory, loadPaths, jarRootIndex)) loadPaths.add(entryDirectory);
                }
            }

            return new ArrayList<String>(loadPaths);
        } catch (IOException e) { throw new MojoFailureException(SOURCE_FILE_ERROR); }
    }

    private Set<String> newLoadPathSet(final int jarRootIndex) {
        return new TreeSet<String>(new Comparator<String>() {
            public int compare(String o1, String o2) {
                int comparison = countSeparators(o1) - countSeparators(o2);
                return comparison == 0 ? o1.compareTo(o2) : comparison;
            }

            private int countSeparators(String s) {
                int separatorCount = 0;
                CharSequence chars = s.subSequence(jarRootIndex, s.length());
                for (int i = 0; i < chars.length(); separatorCount += SEPARATOR == chars.charAt(i++) ? 1 : 0) ;
                return separatorCount;
            }
        });
    }

    private boolean pathExists(String entryDirectory, Set<String> loadPaths, int jarRootIndex) {
        int index = entryDirectory.lastIndexOf('/');
        return index > jarRootIndex &&
                (loadPaths.contains(entryDirectory) ||
                        (!entryDirectory.endsWith(LIB_DIR) &&
                                pathExists(entryDirectory.substring(0, index), loadPaths, jarRootIndex)));
    }
}
