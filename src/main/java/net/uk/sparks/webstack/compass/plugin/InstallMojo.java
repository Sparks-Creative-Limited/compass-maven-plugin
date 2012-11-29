package net.uk.sparks.webstack.compass.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Installs .scss library dependencies into a project.
 * @goal install
 */
public class InstallMojo extends AbstractMojo {


    /**
     * Base libraries to install. Typically will left as the default sass and compass pair.
     * @parameter expression="${compass.libraries}" default-value="sass compass"
     */
    private Object libraries;

    /**
     * Compass extensions to include with installation of the base libraries.
     * @parameter expression="${compass.extensions}"
     */
    private Object extensions;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Installing libraries");
    }
}
