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
import org.jruby.CompatVersion;
import org.jruby.Ruby;
import org.jruby.RubyBoolean;
import org.jruby.embed.LocalContextScope;
import org.jruby.embed.ScriptingContainer;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public abstract class AbstractCompassMojo extends AbstractMojo {

    private static final ClassLoader classloader = AbstractCompassMojo.class.getClassLoader();
    private static final String COMPASS_RB_PATH = "META-INF/scripts/compass.rb";
    private static final String COMPASS_RB_FILE = "compass.rb";

    private static final String CURRENT_DIRECTORY = ".";
    private static final String CSS_DIR_FLAG = "--css-dir";
    private static final String NO_LINE_COMMENTS_FLAG = "--no-line-comments";
    private static final String QUIET_FLAG = "-q";
    private static final String TRACE_FLAG = "--trace";

    private static final String INVALID_DIRECTORY_ERROR = "Library install directory is invalid.";


    private final LoadPathHelper loadPathHelper = new LoadPathHelper(this);
    private final FilePathHelper filePathHelper = new FilePathHelper();


    /**
     * Source directory for the installed compass project.
     * @parameter expression="${compass.installDir}" default-value="${project.basedir}/src/main/resources/compass"
     */
    private File installDir;


    /**
     * Target directory for the compiled css files.
     * @parameter expression="${compass.cssDir}" default-value="${project.basedir}/src/main/resources/compass/stylesheets"
     */
    private File cssDir;


    protected void runCompass(String infoMessage) throws MojoFailureException {
        getLog().info(infoMessage);

        if(installDir != null && installDir.exists()) {
            ScriptingContainer container = newScriptingContainer(installDir);
            container.setArgv(compileArgs());
            container.runScriptlet(classloader.getResourceAsStream(COMPASS_RB_PATH), COMPASS_RB_FILE);
        } else throw new MojoFailureException(INVALID_DIRECTORY_ERROR);
    }

    protected abstract String getCommand();

    protected List<String> getArguments() {
        return Collections.emptyList();
    }

    protected File getInstallDir() {
        return installDir;
    }


    private ScriptingContainer newScriptingContainer(File currentDirectory) throws MojoFailureException {
        ScriptingContainer container = new ScriptingContainer(LocalContextScope.CONCURRENT);
        container.setCompatVersion(CompatVersion.RUBY1_8);
        container.setCurrentDirectory(currentDirectory.getAbsolutePath());
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

    private String[] compileArgs() {
        List<String> args = new LinkedList<String>();
        args.add(getCommand());
        args.add(CURRENT_DIRECTORY);
        args.add(CSS_DIR_FLAG);
        args.add(filePathHelper.getRelativePath(installDir, cssDir));
        args.add(NO_LINE_COMMENTS_FLAG);
        args.add(QUIET_FLAG);
        args.addAll(getArguments());
        if(getLog().isDebugEnabled()) args.add(TRACE_FLAG);
        return args.toArray(new String[args.size()]);
    }
}
