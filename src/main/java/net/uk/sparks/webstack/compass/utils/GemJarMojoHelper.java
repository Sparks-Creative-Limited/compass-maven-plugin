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

import net.uk.sparks.webstack.compass.plugin.CompileMojo;
import net.uk.sparks.webstack.compass.plugin.CreateMojo;
import net.uk.sparks.webstack.compass.plugin.MojoHelper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import java.io.File;

import static net.uk.sparks.webstack.compass.utils.LoadPathsHelper.getLoadPaths;

public class GemJarMojoHelper implements MojoHelper {

    private static final String CREATING_COMPASS_MESSAGE = "Creating Compass resources.";
    private static final String INVALID_DIRECTORY_ERROR = "Library install directory is invalid.";
    private static final String UNDEFINED_DIRECTORY_ERROR = "Library install directory is undefined.";

    public void create(CreateMojo mojo) throws MojoExecutionException, MojoFailureException {
        mojo.getLog().info(CREATING_COMPASS_MESSAGE);
        File directory = mojo.getDirectory();

        if(directory != null) {
            if(directory.exists() || directory.mkdirs()) {
                ScriptingContainer container = new ScriptingContainer(LocalContextScope.CONCURRENT);
                container.setCurrentDirectory(directory.getAbsolutePath());
                container.setLoadPaths(getLoadPaths());
                container.runScriptlet(createCompassScript("targetdir"));
            } else throw new MojoFailureException(INVALID_DIRECTORY_ERROR);
        } else throw new MojoFailureException(UNDEFINED_DIRECTORY_ERROR);
    }

    public void compile(CompileMojo mojo) throws MojoExecutionException, MojoFailureException {
        mojo.getLog().info("Compiling with Gem Jar libraries.");
    }

    private String createCompassScript(String... args) {
        return new StringBuilder()
                .append("require 'rubygems'\n")
                .append("require 'compass'\n")
                .append("require 'compass/exec'\n")
                .append("exit Compass::Exec::SubCommandUI.new([\"create\", \"").append(args[0]).append("\", \"--trace\"]).run!")
                .toString();
    }
}
