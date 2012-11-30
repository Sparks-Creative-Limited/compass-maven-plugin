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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import java.io.File;

import static net.uk.sparks.webstack.compass.utils.LoadPathsHelper.getLoadPaths;

/**
 * Creates Compass base layout resources into a project.
 * By default, these will be installed in under the <code>{project.basedir}/src/main/resources/compass</code> directory.
 * Optional Compass extensions may also be installed and configured under this layout.
 *
 * @goal create
 */
public class CreateMojo extends AbstractMojo {

    private static final String CREATING_COMPASS_MESSAGE = "Creating Compass resources.";
    private static final String INVALID_DIRECTORY_ERROR = "Library install directory is invalid.";
    private static final String UNDEFINED_DIRECTORY_ERROR = "Library install directory is undefined.";

    /**
     * Compass extensions to include with installation of the base libraries.
     * @parameter expression="${compass.extensions}"
     */
    private String[] extensions;

    /**
     * Source directory for .sass and .scss files. Expressed relative to the project base directory.
     * @parameter expression="${compass.directory}" default-value="${project.basedir}/src/main/resources/compass"
     */
    private File directory;


    public void setExtensions(String[] extensions) { this.extensions = extensions; }


    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info(CREATING_COMPASS_MESSAGE);

        if(directory != null) {
            if(directory.exists() || directory.mkdirs()) {
                ScriptingContainer container = new ScriptingContainer(LocalContextScope.CONCURRENT);
                container.setCurrentDirectory(directory.getAbsolutePath());
                container.setLoadPaths(getLoadPaths());
                //container.setClassLoader(getClass().getClassLoader());
                container.runScriptlet("exit Compass::Exec:SubCommandUI.new([\"create\", \"compass\", \"-q\"]).run!");
            } else throw new MojoFailureException(INVALID_DIRECTORY_ERROR);
        } else throw new MojoFailureException(UNDEFINED_DIRECTORY_ERROR);
    }
}
