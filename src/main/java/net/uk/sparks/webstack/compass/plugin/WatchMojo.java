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


/**
 * Watches .sass and .scss files for automatic compilation into .css
 *
 * @goal watch
 */
public class WatchMojo extends AbstractCompassMojo {

    private static final String WATCH_COMMAND = "watch";

    private static final String COMPILING_COMPASS_MESSAGE = "Watching compass resources.";


    public void execute() throws MojoExecutionException, MojoFailureException {
        runCompass(COMPILING_COMPASS_MESSAGE);
    }


    protected String getCommand() {
        return WATCH_COMMAND;
    }
}
