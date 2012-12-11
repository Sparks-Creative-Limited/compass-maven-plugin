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
        COMPASS("compass", "compass-%s/frameworks/compass/"),
        BLUEPRINT("blueprint", "compass-%s/frameworks/blueprint/"),
        BLUEPRINT_BASIC("blueprint/basic", "compass-%s/frameworks/blueprint/"),
        BLUEPRINT_SEMANTIC("blueprint/semantic", "compass-%s/frameworks/blueprint/"),
        ZEN_GRIDS("zen-grids", "zen-grids-1.2/"),
        EXTERNAL("", "");

        private static final String GEMS_ROOT = "META-INF/ruby.gems/gems/";

        public static FRAMEWORK get(String name) {
            for(FRAMEWORK f : FRAMEWORK.values()) if(f.name.equals(name)) return f;
            return EXTERNAL;
        }


        public String getPath(String version) {
            return String.format(path, version);
        }


        final String name;
        final String path;


        private FRAMEWORK(String name, String path) {
            this.name = name;
            this.path = GEMS_ROOT + path;
        }
    }

    private static final String CREATE_COMMAND = "create";
    private static final String USING_ARGUMENT = "--using";
    private static final String FRAMEWORKS_DIR = "frameworks";
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
        FRAMEWORK framework;

        for(String extension : extensions) {
            switch(framework = FRAMEWORK.get(extension)) {
                case BLUEPRINT:
                case BLUEPRINT_BASIC:
                case BLUEPRINT_SEMANTIC:
                    if(!blueprintAdded) {
                        arguments.add(USING_ARGUMENT);
                        arguments.add(framework.name);
                        blueprintAdded = true;
                    }
                    break;
                default: // Do nothing.
                    break;
            }
        }

        return arguments;
    }

    private void installExtensions() throws MojoFailureException {
        File frameworks = new File(getInstallDir(), FRAMEWORKS_DIR);
        if(frameworks.exists() || frameworks.mkdirs()) {
            JarFile plugin = resourceHelper.getJar();
            resourceHelper.writeResource(plugin, FRAMEWORK.COMPASS.getPath(COMPASS_VERSION), frameworks);

            if(extensions.length > 0) {
                FRAMEWORK framework;

                for(String extension : extensions) {
                    switch(framework = FRAMEWORK.get(extension)) {
                        case EXTERNAL:
                            break;
                        default:
                            resourceHelper.writeResource(plugin, framework.getPath(COMPASS_VERSION), frameworks);
                            break;
                    }
                }
            }
        } else throw new MojoFailureException(FRAMEWORK_INSTALL_ERROR);
    }
}
