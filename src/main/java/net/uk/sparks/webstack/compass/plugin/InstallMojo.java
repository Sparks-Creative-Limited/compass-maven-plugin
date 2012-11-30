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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Installs .scss library dependencies into a project.
 *
 * @goal install
 */
public class InstallMojo extends AbstractMojo {

    private static final String INSTALLING_LIBRARIES_MESSAGE = "Installing libraries";
    private static final String INVALID_DIRECTORY_ERROR = "Library install directory is invalid.";
    private static final String UNDEFINED_DIRECTORY_ERROR = "Library install directory is undefined.";


    /**
     * Base libraries to install. Typically will left as the default sass and compass pair.
     * @parameter expression="${compass.libraries}" default-value="sass compass"
     */
    private String libraries;

    /**
     * Compass extensions to include with installation of the base libraries.
     * @parameter expression="${compass.extensions}"
     */
    private String[] extensions;


    /**
     * Source directory for .sass and .scss files. Expressed relative to the project base directory.
     * @parameter expression="${compass.directory}" default-value="${project.basedir}/src/main/resources/scss"
     */
    private File directory;


    public void setExtensions(String[] extensions) { this.extensions = extensions; }


    public void execute() throws MojoExecutionException, MojoFailureException {

        getLog().info(INSTALLING_LIBRARIES_MESSAGE);

        if(directory != null) {
            if(directory.exists() || directory.mkdirs()) {
                writeResource("bin/sass", new File(directory, "test"));

            } else throw new MojoFailureException(INVALID_DIRECTORY_ERROR);
        } else throw new MojoFailureException(UNDEFINED_DIRECTORY_ERROR);
    }


    private void writeResource(String resource, File target) throws MojoFailureException {

        InputStream input = null;
        FileOutputStream output = null;
        try {

            input = getClass().getClassLoader().getResourceAsStream(resource);

            if(!target.exists()) target.createNewFile();
            output = new FileOutputStream(target);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) output.write(buffer, 0, bytesRead);

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
