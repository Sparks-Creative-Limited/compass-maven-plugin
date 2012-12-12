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

import org.apache.maven.plugin.MojoFailureException;

import java.io.*;

public class CommandInjectionHelper {

    private static final String REPLACEMENT = "# command-injection";
    private static final String SCRIPT_GENERATION_ERROR = "Could not generate compass script.";


    private final String script;


    public CommandInjectionHelper(AbstractCompassMojo mojo, String scriptPath) throws MojoFailureException {
        BufferedReader in = new BufferedReader(new InputStreamReader(mojo.getResourceAsStream(scriptPath)));
        StringBuilder out = new StringBuilder();
        String line;

        try {
            while((line = in.readLine()) != null) {
                out.append(line.replaceFirst(REPLACEMENT, getCommmands(mojo))).append('\n');
            }
        } catch (IOException e) { throw new MojoFailureException(SCRIPT_GENERATION_ERROR); }

        script = out.toString();
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(script.getBytes());
    }


    private String getCommmands(AbstractCompassMojo mojo) throws MojoFailureException {
        StringBuilder commands = new StringBuilder();
        for(String command : mojo.getExtraCommands()) commands.append(command).append("\n");
        return commands.toString();
    }
}
