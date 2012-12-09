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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;

/**
 * Creates Compass base layout resources into a project.
 * By default, these will be installed in under the <code>{project.basedir}/src/main/resources/compass</code> directory.
 * Optional Compass extensions may also be installed and configured under this layout.
 *
 * @goal create
 */
public class CreateMojo extends AbstractCompassMojo {

    private static final String CREATE_COMMAND = "create";
    private static final String CURRENT_DIRECTORY = ".";
    private static final String QUIET_FLAG = "-q";

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
     * @parameter expression="${compass.installDir}" default-value="${project.basedir}/src/main/resources/compass"
     */
    private File installDir;


    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info(CREATING_COMPASS_MESSAGE);
        File directory = getInstallDir();

        if(directory != null) {
            if(directory.exists() || directory.mkdirs()) {
                runCompass(newScriptingContainer(directory), CREATE_COMMAND, CURRENT_DIRECTORY, QUIET_FLAG);
            } else throw new MojoFailureException(INVALID_DIRECTORY_ERROR);
        } else throw new MojoFailureException(UNDEFINED_DIRECTORY_ERROR);
    }

    public void setExtensions(String[] extensions) { this.extensions = extensions; }

    public String[] getExtensions() {
        return extensions;
    }

    public File getInstallDir() {
        return installDir;
    }
}
