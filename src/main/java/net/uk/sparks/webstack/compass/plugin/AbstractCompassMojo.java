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

import net.uk.sparks.webstack.compass.utils.AbstractMojoHelper;
import net.uk.sparks.webstack.compass.utils.GemJarMojoHelper;
import net.uk.sparks.webstack.compass.utils.LocalMojoHelper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.Properties;

public abstract class AbstractCompassMojo extends AbstractMojo {

    private static final String PLUGIN_PROPERTIES_PATH = "/META-INF/plugin.properties";
    private static final String PLUGIN_PROPERTIES_ERROR = "Could not locate plugin properties";
    private static final String PROPERTY_JRUBY_GEMS_JAR = "jruby.gems.jar";


    /** @parameter default-value="${project}" */
    private org.apache.maven.project.MavenProject mavenProject;


    public MavenProject getMavenProject() {
        return mavenProject;
    }


    protected AbstractMojoHelper getMojoHelper() throws MojoFailureException {
        Properties pluginProperties = new Properties();

        try {
            pluginProperties.load(AbstractCompassMojo.class.getResourceAsStream(PLUGIN_PROPERTIES_PATH));
            return Boolean.valueOf(pluginProperties.getProperty(PROPERTY_JRUBY_GEMS_JAR))
                    ? new GemJarMojoHelper() : new LocalMojoHelper();
        } catch(IOException e) {
            throw new MojoFailureException(PLUGIN_PROPERTIES_ERROR);
        }

    }
}
