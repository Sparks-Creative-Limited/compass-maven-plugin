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
import java.util.Properties;
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
        COMPASS("compass", "compass-%s/frameworks/compass/", "gem.compass.version", false),
        BLUEPRINT("blueprint", "compass-%s/frameworks/blueprint/", "gem.compass.version", true),
        BLUEPRINT_BASIC("blueprint/basic", "compass-%s/frameworks/blueprint/", "gem.compass.version", true),
        BLUEPRINT_SEMANTIC("blueprint/semantic", "compass-%s/frameworks/blueprint/", "gem.compass.version", true),
        COMPASS_960("960","compass-960-plugin-%s/","gem.960.version", true),
        COMPASS_YUI("yui","yui-compass-plugin-%s/","gem.yui.version", true),
        ZEN_GRIDS("zen-grids", "zen-grids-%s/", "gem.zen-grids.version", true),
        ZURB_FOUNDATION("zurb-foundation", "foundation-sass-%s/", "gem.zurb-foundation.version", true),
        EXTERNAL("", "", "", false);

        private static final String GEMS_ROOT = "META-INF/ruby.gems/gems/";
        private static final String GEM_VERSION_ERROR = "Could not determine gem version for ";

        static FRAMEWORK get(String name) {
            for(FRAMEWORK f : FRAMEWORK.values()) if(f.name.equals(name)) return f;
            return EXTERNAL;
        }


        String getDir(Properties versions) throws MojoFailureException {
            String version = versions.getProperty(versionKey);
            if(null != version) return String.format(dir, version);
            else throw new MojoFailureException(GEM_VERSION_ERROR + name);
        }

        String getPath(Properties versions) throws MojoFailureException {
            return GEMS_ROOT + getDir(versions);
        }


        final String name;
        final String dir;
        final String versionKey;
        final boolean usingArg;


        private FRAMEWORK(String name, String dir, String versionKey, boolean usingArg) {
            this.name = name;
            this.dir = dir;
            this.versionKey = versionKey;
            this.usingArg = usingArg;
        }
    }

    private static final String CREATE_COMMAND = "create";
    private static final String USING_ARGUMENT = "--using";
    private static final String FRAMEWORKS_DIR = "frameworks";

    private static final String CREATING_COMPASS_MESSAGE = "Creating Compass resources.";
    private static final String UNDEFINED_DIRECTORY_ERROR = "Library install directory is undefined.";
    private static final String FRAMEWORK_INSTALL_ERROR = "Could not install frameworks.";


    private final ResourceHelper resourceHelper = new ResourceHelper(this);


    /**
     * Compass extensions to include with installation of the base libraries.
     * @parameter expression="${compass.extensions}"
     */
    private String[] extensions;

    private List<String> arguments;
    private List<String> extraCommands;


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
    protected List<String> getArguments() throws MojoFailureException {
        if(null == arguments) parseExtensions();
        return arguments;
    }

    @Override
    protected List<String> getExtraCommands() throws MojoFailureException {
        if(null == extraCommands) parseExtensions();
        return extraCommands;
    }

    private void installExtensions() throws MojoFailureException {
        File frameworks = new File(getInstallDir(), FRAMEWORKS_DIR);
        if(frameworks.exists() || frameworks.mkdirs()) {
            JarFile plugin = resourceHelper.getJar();
            resourceHelper.writeResource(plugin, FRAMEWORK.COMPASS.getPath(getVersions()), frameworks);

            if(extensions.length > 0) {
                FRAMEWORK framework;

                for(String extension : extensions) {
                    switch(framework = FRAMEWORK.get(extension)) {
                        case EXTERNAL:
                            break;
                        default:
                            resourceHelper.writeResource(plugin, framework.getPath(getVersions()), frameworks);
                            break;
                    }
                }
            }
        } else throw new MojoFailureException(FRAMEWORK_INSTALL_ERROR);
    }

    private void parseExtensions() throws MojoFailureException {
        arguments = new LinkedList<String>();
        extraCommands = new LinkedList<String>();

        boolean usingArgFlag = true;
        FRAMEWORK framework;

        for(String extension : extensions) {
            if((framework = FRAMEWORK.get(extension)).usingArg) {
                if(usingArgFlag) {
                    arguments.add(USING_ARGUMENT);
                    arguments.add(framework.name);
                    usingArgFlag = false;
                }

                extraCommands.add(new StringBuilder("Compass::Frameworks.register(\"").append(framework.name)
                        .append("\", :path => \"frameworks/").append(framework.getDir(getVersions())).append("\")")
                        .toString());
            }
        }
    }
}
