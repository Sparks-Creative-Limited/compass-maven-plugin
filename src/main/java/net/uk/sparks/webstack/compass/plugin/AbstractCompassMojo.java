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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.jruby.CompatVersion;
import org.jruby.Ruby;
import org.jruby.RubyBoolean;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;


public abstract class AbstractCompassMojo extends AbstractMojo {

    private final LoadPathHelper loadPathHelper = new LoadPathHelper(this);

    private final ResourceHelper resourceHelper = new ResourceHelper(this);

    /** @parameter default-value="${project}" */
    private org.apache.maven.project.MavenProject mavenProject;


    public MavenProject getMavenProject() {
        return mavenProject;
    }


    protected ScriptingContainer newScriptingContainer(String currentDirectory) throws MojoFailureException {
        ScriptingContainer container = new ScriptingContainer(LocalContextScope.CONCURRENT);
        container.setCompatVersion(CompatVersion.RUBY1_9);
        container.setCurrentDirectory(currentDirectory);
        container.setLoadPaths(loadPathHelper.getLoadPaths());
        if(getLog().isDebugEnabled()) setRubyDebug(container);
        return container;
    }


    private void setRubyDebug(ScriptingContainer container) {
        Ruby runtime = container.getProvider().getRuntime();
        RubyBoolean rubyTrue = RubyBoolean.newBoolean(runtime, true);
        runtime.setDebug(rubyTrue);
        runtime.setVerbose(rubyTrue);
    }
}
