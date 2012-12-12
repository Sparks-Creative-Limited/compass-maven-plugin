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

    private static final String CREATE_COMMAND = "create";
    private static final String USING_ARGUMENT = "--using";
    private static final String frameworks_DIR = "frameworks";

    private static final String CREATING_COMPASS_MESSAGE = "Creating Compass resources.";
    private static final String UNDEFINED_DIRECTORY_ERROR = "Library install directory is undefined.";
    private static final String Framework_INSTALL_ERROR = "Could not install frameworks.";


    private final ResourceHelper resourceHelper = new ResourceHelper(this);


    private List<String> arguments;


    public void execute() throws MojoExecutionException, MojoFailureException {
        File directory = getInstallDir();
        if(directory != null && (directory.exists() || directory.mkdirs())) {
            installExtensions();
            runCompass(CREATING_COMPASS_MESSAGE);
        } else throw new MojoFailureException(UNDEFINED_DIRECTORY_ERROR);
    }


    @Override
    protected String getCommand() {
        return CREATE_COMMAND;
    }

    @Override
    protected List<String> getArguments() throws MojoFailureException {
        if(null == arguments) parseExtensions();
        return arguments;
    }


    private void installExtensions() throws MojoFailureException {
        File frameworks = new File(getInstallDir(), frameworks_DIR);
        if(frameworks.exists() || frameworks.mkdirs()) {
            JarFile plugin = resourceHelper.getJar();
            resourceHelper.writeResource(plugin, Framework.COMPASS.getPath(getVersions()), frameworks);
            String[] extensions = getExtensions();

            if(extensions.length > 0) {
                Framework framework;

                for(String extension : extensions) {
                    switch(framework = Framework.get(extension)) {
                        case EXTERNAL:
                            break;
                        default:
                            resourceHelper.writeResource(plugin, framework.getPath(getVersions()), frameworks);
                            break;
                    }
                }
            }
        } else throw new MojoFailureException(Framework_INSTALL_ERROR);
    }

    @Override
    protected void parseExtensions() throws MojoFailureException {
        super.parseExtensions();
        arguments = new LinkedList<String>();
        Framework framework;

        String[] extensions = getExtensions();
        boolean usingArgFlag = true;

        for(String extension : extensions) {
            if((framework = Framework.get(extension)).usingArg) {
                if(usingArgFlag) {
                    arguments.add(USING_ARGUMENT);
                    arguments.add(framework.name);
                    usingArgFlag = false;
                }
            }
        }
    }
}
