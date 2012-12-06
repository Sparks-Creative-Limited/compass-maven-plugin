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
import org.jruby.Main;

import java.io.File;

public class LocalMojoHelper implements MojoHelper {

    private static final String DEFAULT_GEMS_DIR = "src/main/rubygems";
    private static final String[] GEMS = {"sass -v 3.2.3", "compass -v 0.12.2", "zen-grids -v 1.2"};
    private static final String SPACE = " ";

    private static final String CREATE_LOCAL_MESSAGE = "Creating Local based layout.";
    private static final String COMPILE_LOCAL_MESSAGE = "Compiling with Local libraries.";

    private static final String GEMS_DIRECTORY_ERROR = "Failed to create gems directory ";
    private static final String GEM_INSTALL_ERROR = "Failed to install gem: ";
    private static final String COMPASS_CREATE_ERROR = "Failed to create compass resources";


    public void create(CreateMojo mojo) throws MojoExecutionException, MojoFailureException {
        mojo.getLog().info(CREATE_LOCAL_MESSAGE);
        File gemDir = new File(mojo.getMavenProject().getBasedir(), DEFAULT_GEMS_DIR);

        if(gemDir.exists() || gemDir.mkdirs()) {
            for(int i = 0; i < GEMS.length; i++) {
                if(new Main().run(new StringBuilder("-S gem install ").append(GEMS[i])
                        .append(" -i ").append(gemDir.getAbsolutePath())
                        .append(" --no-rdoc --no-ri").toString().split(SPACE)).getStatus() > 0) {
                    throw new MojoExecutionException(GEM_INSTALL_ERROR + GEMS[i]);
                }
            }

            File compassDir = mojo.getDirectory();

            if(compassDir.exists() || compassDir.mkdirs()) {
                if(new Main().run(new StringBuilder("-v -C ").append(gemDir.getAbsolutePath())
                        .append(" -I ").append(gemDir.getAbsolutePath())
                        .append(" -S bin/compass create ").append(compassDir.getAbsolutePath())
                        .toString().split(SPACE)).getStatus() > 0) {
                    throw new MojoExecutionException(COMPASS_CREATE_ERROR);
                }
            }
        } else throw new MojoExecutionException(GEMS_DIRECTORY_ERROR + gemDir.getAbsolutePath());
    }

    public void compile(CompileMojo mojo) throws MojoExecutionException, MojoFailureException {
        mojo.getLog().info(COMPILE_LOCAL_MESSAGE);
    }
}
