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

import java.util.Properties;

public enum Framework {
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

    static Framework get(String name) {
        for(Framework f : Framework.values()) if(f.name.equals(name)) return f;
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


    private Framework(String name, String dir, String versionKey, boolean usingArg) {
        this.name = name;
        this.dir = dir;
        this.versionKey = versionKey;
        this.usingArg = usingArg;
    }
}
