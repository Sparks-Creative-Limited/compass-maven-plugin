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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ResourceHelper {

    private static final ClassLoader classloader = ResourceHelper.class.getClassLoader();


    private final AbstractCompassMojo mojo;


    public ResourceHelper(AbstractCompassMojo mojo) {
        this.mojo = mojo;
    }


    public void writeResource(String resource, File target) throws MojoFailureException {

        InputStream input = null;
        FileOutputStream output = null;

        try {
            input = classloader.getResourceAsStream(resource);

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
