package me.skyleft;

import me.skyleft.utils.MavenScmCli;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;

import java.io.File;

/**
 * Goal which remove the doc from yy doc server
 */
@Mojo(name = "un-publish", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class UnPublishMojo
        extends AbstractMojo {

    @Component
    private Prompter prompter;

    @Parameter(defaultValue = "${project.basedir}", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute()
            throws MojoExecutionException {
        MavenScmCli mavenScmCli = null;
        try {
            mavenScmCli = new MavenScmCli();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        if (mavenScmCli == null) return;
        String ifSure = "N";
        try {
            ifSure = prompter.prompt("是否确定要从文档库中清除此文档? Y表示是，N表示否");
        } catch (PrompterException e) {
            e.printStackTrace();
        }
        if ("Y".equalsIgnoreCase(ifSure)){
            getLog().info("准备执行删除");
        }

    }


}
