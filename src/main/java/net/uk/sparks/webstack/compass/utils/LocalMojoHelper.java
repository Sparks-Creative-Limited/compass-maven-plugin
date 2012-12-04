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

public class LocalMojoHelper implements MojoHelper {

    public void create(CreateMojo mojo) throws MojoExecutionException, MojoFailureException {
        mojo.getLog().info("Creating Local based layout.");
    }

    public void compile(CompileMojo mojo) throws MojoExecutionException, MojoFailureException {
        mojo.getLog().info("Compiling with Local libraries.");
    }
}
