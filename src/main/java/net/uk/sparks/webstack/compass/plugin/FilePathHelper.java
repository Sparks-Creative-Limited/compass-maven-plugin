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

import java.io.File;

public class FilePathHelper {

    private static final String CURRENT_DIR = ".";
    private static final String PARENT_DIR = "..";


    public String getRelativePath(File source, File target) {
        StringBuilder path = buildPath(new StringBuilder(), 0,
                source.getAbsolutePath().split(File.separator),
                target.getAbsolutePath().split(File.separator));
        return path.length() == 0 ? CURRENT_DIR : path.substring(0, path.length() - 1);
    }

    private StringBuilder buildPath(StringBuilder path, int i, String[] source, String[] target) {
        return i < source.length && i < target.length && source[i].equals(target[i])
                ? buildPath(path, i + 1, source, target)
                : buildTargetPath(buildSourcePath(path, i, source), i, target);
    }

    private StringBuilder buildSourcePath(StringBuilder path, int s, String[] source) {
        return s < source.length
                ? buildSourcePath(path.append(PARENT_DIR).append(File.separator), s + 1, source)
                : path;
    }

    private StringBuilder buildTargetPath(StringBuilder path, int t, String[] target) {
        return t < target.length
                ? buildTargetPath(path.append(target[t]).append(File.separator), t + 1, target)
                : path;
    }
}
