package me.skyleft;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.comments.Comment;
import me.skyleft.bean.Interface;
import me.skyleft.bean.Module;
import me.skyleft.bean.Project;
import me.skyleft.utils.Consts;
import me.skyleft.utils.TemplateRender;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Goal which generate markdown document for YY project
 *
 */
@Mojo(name = "gen-doc", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class GenDocMojo
        extends AbstractMojo {
    /**
     * 默认生成文档放在项目根目录
     */
    @Parameter(defaultValue = "${project.basedir}", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    public void execute()
            throws MojoExecutionException {


        Properties properties = project.getProperties();
        String projectName = properties.getProperty("yydoc.name");
        String description = properties.getProperty("yydoc.desc");
        String ifIgnore = properties.getProperty("yydoc.ignore");
        if (ifIgnore != null && "true".equals(ifIgnore.toLowerCase())) {
            return;
        }
        String projectNameAlias = properties.getProperty("yydoc.alias");
        String currentStatus = properties.getProperty("yydoc.status");
        String developers = properties.getProperty("yydoc.pics");


        List<Module> modules = new ArrayList<Module>();
        List<MavenProject> childProjects = project.getCollectedProjects();
        if (childProjects == null || childProjects.size() <= 0) {
            Module module = new Module();
            module.setName(projectName);
            module.setSvn(properties.getProperty("yydoc.svn"));
            module.setUrl(properties.getProperty("yydoc.domain"));
            module.setDragon(properties.getProperty("yydoc.dragon"));
            module.setIps(properties.getProperty("yydoc.ips"));
            List<Interface> interfaces = new ArrayList<Interface>();
            List<String> sourcePaths = project.getCompileSourceRoots();
            for (String sourcePath : sourcePaths) {
                List<File> javafiles = getAllJavaFiles(new File(sourcePath));
                for (File f : javafiles) {
                    CompilationUnit cu = null;
                    FileInputStream in = null;
                    try {
                        // parse the file
                        in = new FileInputStream(f);
                        cu = JavaParser.parse(in);
                        List<Comment> comments = cu.getComments();
                        for (Comment c : comments) {
                            String co = c.getContent();
                            Interface itl = parseInterface(co);
                            if (itl != null) {
                                interfaces.add(itl);
                            }
                        }
                    } catch (Throwable e) {
                        continue;
                    } finally {
                        if (in != null)
                            try {
                                in.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                    }
                }

            }
            module.setInterfaces(interfaces);
            modules.add(module);
        } else {
            for (MavenProject childProject : childProjects) {
                Module module = new Module();
                module.setName(childProject.getName());
                Properties properties1 = childProject.getProperties();
                module.setSvn(properties1.getProperty("yydoc.svn"));
                module.setUrl(properties1.getProperty("yydoc.domain"));
                module.setDragon(properties1.getProperty("yydoc.dragon"));
                module.setIps(properties.getProperty("yydoc.ips"));
                List<Interface> interfaces = new ArrayList<Interface>();
                List<String> sourcePaths = childProject.getCompileSourceRoots();
                for (String sourcePath : sourcePaths) {
                    List<File> javafiles = getAllJavaFiles(new File(sourcePath));
                    for (File f : javafiles) {
                        CompilationUnit cu = null;
                        FileInputStream in = null;
                        try {
                            // parse the file
                            in = new FileInputStream(f);
                            cu = JavaParser.parse(in);
                            List<Comment> comments = cu.getComments();
                            for (Comment c : comments) {
                                String co = c.getContent();
                                Interface itl = parseInterface(co);
                                if (itl != null) {
                                    interfaces.add(itl);
                                }
                            }
                        } catch (Throwable e) {
                            continue;
                        } finally {
                            if (in != null)
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    //e.printStackTrace();
                                }
                        }
                    }

                }
                module.setInterfaces(interfaces);
                modules.add(module);

            }
        }


        Project project = new Project();
        project.setProjectName(projectName);
        project.setProjectNameAlias(projectNameAlias);
        project.setPics(developers);
        project.setCurrentStatus(currentStatus);
        project.setDescription(description);
        project.setModules(modules);

        File f = outputDirectory;

        if (!f.exists()) {
            f.mkdirs();
        }

        File docFile = new File(f, Consts.DOC_FILE_NAME);

        OutputStreamWriter w = null;


        try {
            w = new OutputStreamWriter(
                    new FileOutputStream(docFile), "UTF-8"
            );
            w.write(TemplateRender.render(project));
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + docFile, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

    public List<File> getAllJavaFiles(File file) {
        if (file.isDirectory()) {
            List<File> javafiles = new LinkedList<File>(Arrays.asList(file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(".java");
                }
            })));
            for (File directory : file.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            })) {
                javafiles.addAll(getAllJavaFiles(directory));
            }
            return javafiles;
        }
        return new ArrayList<File>();
    }

    public Interface parseInterface(String comments) {
        if (comments.matches("[\\w\\W]*@yyport\\s+[\\w\\u4E00-\\u9FA5]+[\\w\\W]*")) {

            Pattern pattern = Pattern.compile("(@yy(port|param|return|desc|url))\\s+([\\w/\\.\\u4E00-\\u9FA5]+)");
            Matcher matcher = pattern.matcher(comments);
            Interface itl = new Interface();
            StringBuilder inparam = new StringBuilder("");
            StringBuilder outparam = new StringBuilder("");
            String tagName, tagValue;
            while (matcher.find()) {
                tagName = matcher.group(1).trim();
                tagValue = matcher.group(3).trim();
                if (StringUtils.isNotEmpty(tagName) && StringUtils.isNotEmpty(tagValue)) {
                    if ("@yyport".equalsIgnoreCase(tagName)) {
                        itl.setName(tagValue);
                    }
                    if ("@yyparam".equalsIgnoreCase(tagName)) {
                        inparam.append(tagValue).append(" \\| ");
                    }
                    if ("@yyreturn".equalsIgnoreCase(tagName)) {
                        outparam.append(tagValue).append(" \\| ");
                    }
                    if ("@yydesc".equalsIgnoreCase(tagName)) {
                        itl.setDesc(tagValue);
                    }
                    if ("@yyurl".equalsIgnoreCase(tagName)) {
                        itl.setUrl(tagValue);
                    }
                }

            }
            if (itl.getName() != null) {
                itl.setInparam(inparam.toString());
                itl.setOutparam(outparam.toString());
            }

            return itl;
        }
        return null;
    }
}
