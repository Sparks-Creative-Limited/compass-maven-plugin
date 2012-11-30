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

import java.util.LinkedList;
import java.util.List;

public class LoadPathsHelper {

    public static List<String> getLoadPaths() {
        List<String> loadPaths = new LinkedList<String>();
        loadPaths.add("classpath:/gems/sass-3.2.3/lib/");
        loadPaths.add("classpath:/gems/compass-0.12.2/lib/");
        loadPaths.add("classpath:/gems/fssm-0.2.9/lib/");
        loadPaths.add("classpath:/gems/chunky_png-1.2.6/lib/");
        loadPaths.add("classpath:/gems/zen-grids-1.2/lib/");
        return loadPaths;
    }
}
