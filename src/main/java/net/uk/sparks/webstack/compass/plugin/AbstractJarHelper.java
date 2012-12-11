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
import java.util.Comparator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class AbstractJarHelper {

    public static final char SEPARATOR = '/';


    private static final ClassLoader classloader = AbstractJarHelper.class.getClassLoader();
    private static final String PLUGIN_DESCRIPTOR_KEY = "pluginDescriptor";

    private static final String JAR_FILE_ERROR = "Could not resolve jar file: ";


    public class EntryComparator implements Comparator<String> {
        private final int jarRootIndex;

        public EntryComparator(int jarRootIndex) {
            this.jarRootIndex = jarRootIndex;
        }

        public int compare(String o1, String o2) {
            int comparison = countSeparators(o1) - countSeparators(o2);
            return comparison == 0 ? o1.compareTo(o2) : comparison;
        }

        private int countSeparators(String s) {
            int separatorCount = 0;
            CharSequence chars = s.subSequence(jarRootIndex, s.length());
            for(int i = 0; i < chars.length(); separatorCount += SEPARATOR == chars.charAt(i++) ? 1 : 0);
            return separatorCount;
        }
    }

    public class JarEntryComparator implements Comparator<JarEntry> {
        private final EntryComparator delegate;

        public JarEntryComparator(int jarRootIndex) {
            delegate = new EntryComparator(jarRootIndex);
        }

        public int compare(JarEntry o1, JarEntry o2) {
            return delegate.compare(o1.getName(), o2.getName());
        }
    }


    private final AbstractCompassMojo mojo;


    public AbstractJarHelper(AbstractCompassMojo mojo) {
        this.mojo = mojo;
    }

    public JarFile getJar() throws MojoFailureException {
        return getJar(((PluginDescriptor) mojo.getPluginContext().get(PLUGIN_DESCRIPTOR_KEY)).getSource());
    }

    public JarFile getJar(String jarPath) throws MojoFailureException {
        try {
            return new JarFile(jarPath);
        } catch (IOException e) {
            throw new MojoFailureException(JAR_FILE_ERROR + jarPath);
        }
    }


    protected ClassLoader getClassloader() {
        return classloader;
    }
}
