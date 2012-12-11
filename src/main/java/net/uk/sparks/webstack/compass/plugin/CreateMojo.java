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
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Creates Compass base layout resources into a project.
 * By default, these will be installed in under the <code>{project.basedir}/src/main/resources/compass</code> directory.
 * Optional Compass extensions may also be installed and configured under this layout.
 *
 * @goal create
 */
public class CreateMojo extends AbstractCompassMojo {

    private enum FRAMEWORK {
        BLUEPRINT("blueprint"),
        BLUEPRINT_BASIC("blueprint/basic"),
        BLUEPRINT_SEMANTIC("blueprint/semantic"),
        EXTERNAL("");

        public static FRAMEWORK get(String name) {
            for(FRAMEWORK f : FRAMEWORK.values()) if(f.name.equals(name)) return f;
            return EXTERNAL;
        }

        private final String name;

        private FRAMEWORK(String name) {
            this.name = name;
        }
    }

    private static final String CREATE_COMMAND = "create";
    private static final String USING_ARGUMENT = "--using";
    private static final String FRAMEWORKS_DIR = "frameworks";
    private static final String BLUEPRINT_FRAMEWORK = "META-INF/ruby.gems/gems/compass-%s/frameworks/blueprint/";
    private static final String COMPASS_FRAMEWORK = "META-INF/ruby.gems/gems/compass-%s/frameworks/compass/";
    private static final String COMPASS_VERSION = "0.12.2";

    private static final String CREATING_COMPASS_MESSAGE = "Creating Compass resources.";
    private static final String UNDEFINED_DIRECTORY_ERROR = "Library install directory is undefined.";
    private static final String FRAMEWORK_INSTALL_ERROR = "Could not install frameworks.";


    private final ResourceHelper resourceHelper = new ResourceHelper(this);


    /**
     * Compass extensions to include with installation of the base libraries.
     * @parameter expression="${compass.extensions}"
     */
    private String[] extensions;


    public void execute() throws MojoExecutionException, MojoFailureException {
        File directory = getInstallDir();
        if(directory != null && (directory.exists() || directory.mkdirs())) {
            installExtensions();
            runCompass(CREATING_COMPASS_MESSAGE);
        } else throw new MojoFailureException(UNDEFINED_DIRECTORY_ERROR);
    }

    public void setExtensions(String[] extensions) { this.extensions = extensions; }


    @Override
    protected String getCommand() {
        return CREATE_COMMAND;
    }

    @Override
    protected List<String> getArguments() {
        List<String> arguments = new LinkedList<String>();
        boolean blueprintAdded = false;

        for(String extension : extensions) {
            switch(FRAMEWORK.get(extension)) {
                case BLUEPRINT:
                case BLUEPRINT_BASIC:
                    if(!blueprintAdded) {
                        arguments.add(USING_ARGUMENT);
                        arguments.add(FRAMEWORK.BLUEPRINT_BASIC.name);
                        blueprintAdded = true;
                    }
                    break;
                case BLUEPRINT_SEMANTIC:
                    if(!blueprintAdded) {
                        arguments.add(USING_ARGUMENT);
                        arguments.add(FRAMEWORK.BLUEPRINT_SEMANTIC.name);
                        blueprintAdded = true;
                    }
                    break;
                default:
                    // Do nothing.
                    break;
            }
        }

        return arguments;
    }

    private void installExtensions() throws MojoFailureException {
        File frameworks = new File(getInstallDir(), FRAMEWORKS_DIR);
        if(frameworks.exists() || frameworks.mkdirs()) {
            JarFile plugin = resourceHelper.getJar();
            resourceHelper.writeResource(plugin, String.format(COMPASS_FRAMEWORK, COMPASS_VERSION), frameworks);

            if(extensions.length > 0) {

                for(String extension : extensions) {
                    switch(FRAMEWORK.get(extension)) {
                        case BLUEPRINT:
                        case BLUEPRINT_BASIC:
                        case BLUEPRINT_SEMANTIC:
                            resourceHelper.writeResource(plugin, String.format(BLUEPRINT_FRAMEWORK, COMPASS_VERSION), frameworks);
                            break;
                        case EXTERNAL:
                        default:
                            // Do nothing.
                            break;
                    }
                }
            }
        } else throw new MojoFailureException(FRAMEWORK_INSTALL_ERROR);
    }
}
