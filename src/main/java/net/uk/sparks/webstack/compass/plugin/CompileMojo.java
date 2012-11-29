package net.uk.sparks.webstack.compass.plugin;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;


/**
 * Compiles .sass and .scss files into .css
 * @goal compile
 */
public class CompileMojo extends AbstractMojo {

    /**
     * Source directory for .sass and .scss files. Expressed relative to the project base directory.
     * @parameter expression="${compass.source}" default-value="src/main/resources/scss"
     */
    private Object source;

    /**
     * Target directory for .css resources. Expressed relative to the base url of the intended service.
     * @parameter expression="${compass.target}" default-value="css"
     */
    private Object target;

    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Compiling css resources");
    }
}
