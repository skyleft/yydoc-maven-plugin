package me.skyleft;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import me.skyleft.utils.Consts;
import me.skyleft.utils.MavenScmCli;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * Goal which publish the doc to yy doc server
 */
@Mojo(name = "publish", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class PublishMojo
        extends AbstractMojo {

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
            e.printStackTrace();
        }
        if (mavenScmCli == null) return;
        File indexDir = new File(outputDirectory.getAbsolutePath() + File.separator + ".tmp.index");
        File workDir = new File(outputDirectory.getAbsolutePath() + File.separator + ".tmp.auto");
        if (indexDir.exists()) {
            getLog().error("首页工作目录已存在,请手动清除");
            return;
        } else {
            mavenScmCli.execute(Consts.BASE_SVN_PATH, "checkout", indexDir, false);
        }
        if (workDir.exists()) {
            getLog().error("AUTO工作目录已存在,请手动清除");
            return;
        } else {
            mavenScmCli.execute(Consts.WORK_SVN_PATH, "checkout", workDir);
        }

        File indexFile = new File(indexDir.getAbsolutePath() + File.separator + Consts.INDEX_FILE_NAME);
        if (indexFile == null || !indexFile.exists()) {
            getLog().error("文档库首页html不存在");
            clean(indexDir);
            clean(workDir);
            return;
        }

        Properties properties = project.getProperties();
        String projectName = properties.getProperty("yydoc.name");
        String ifIgnore = properties.getProperty("yydoc.ignore");
        if (ifIgnore != null && "true".equals(ifIgnore.toLowerCase())) {
            clean(indexDir);
            clean(workDir);
            return;
        }
        String projectNameAlias = properties.getProperty("yydoc.alias");
        String currentStatus = properties.getProperty("yydoc.status");
        String developers = properties.getProperty("yydoc.pics");
        if (projectName == null || "".equals(projectName.trim()) || projectNameAlias == null || "".equals(projectNameAlias.trim())) {
            getLog().error("yydoc项目名和别名不能为空");
            clean(indexDir);
            clean(workDir);
            return;
        }
        File currentDir = new File(workDir.getAbsolutePath() + File.separator + "docs" + File.separator + projectNameAlias);
        File docFile;
        if (!currentDir.exists()) {
            if (!currentDir.mkdirs()) {
                getLog().error("创建工作目录失败");
                clean(indexDir);
                clean(workDir);
                return;
            }
        }

        docFile = new File(outputDirectory.getAbsolutePath() + File.separator + Consts.DOC_FILE_NAME);
        if (docFile != null && docFile.exists()) {
            File destFile = new File(currentDir.getAbsolutePath() + File.separator + Consts.DOC_FILE_NAME);
            try {
                FileUtils.copyFile(docFile, destFile);
            } catch (IOException e) {
                getLog().error("拷贝文档失败");
                clean(indexDir);
                clean(workDir);
                return;
            }
        } else {
            getLog().error("项目文档不存在，请先运行 yydoc:gen-doc 生成文档");
        }

        Document doc = null;
        try {
            doc = Jsoup.parse(indexFile, "UTF-8", "");
        } catch (IOException e) {
            getLog().error("文档库首页格式错误");
        }
        Element div = doc.select("div#nav-hatch > div").last();
        Boolean menuOk = false;
        for (Element ele : div.getElementsByTag("li")) {
            String nid = ele.attr("nid");
            if (projectNameAlias.equals(nid)) {
                getLog().info("文档库导航已存在此菜单");
                menuOk = true;
                break;
            }
        }
        if (menuOk) {
            mavenScmCli.addFile(Consts.WORK_SVN_PATH, currentDir);
            mavenScmCli.addFile(Consts.WORK_SVN_PATH, new File(currentDir.getAbsolutePath() + File.separator + Consts.DOC_FILE_NAME));
            mavenScmCli.execute(Consts.WORK_SVN_PATH, "checkin", workDir);
        } else {
            StringBuilder appendNode = new StringBuilder();
            appendNode.append("<li nid='").append(projectNameAlias).append("'><a href='#' onclick=\"showContent('/posts/auto/docs/");
            appendNode.append(projectNameAlias);
            appendNode.append("/").append(Consts.DOC_FILE_NAME);
            appendNode.append("')\">");
            appendNode.append(projectName);
            appendNode.append("</a></li>");
            div.append(appendNode.toString());
            try {
                String newIndexHtml = doc.outerHtml();
                if (newIndexHtml != null && newIndexHtml.trim().length() > 0) {
                    //FileUtils.fileWrite(indexFile, newIndexHtml);
                    BufferedWriter buf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile), Charset.forName("UTF-8")));
                    buf.write(newIndexHtml);
                    buf.flush();
                    buf.close();
                } else {
                    getLog().error("首页文档处理失败");
                    clean(indexDir);
                    clean(workDir);
                    return;
                }

            } catch (IOException e) {
                e.printStackTrace();
                getLog().error("更新文档库首页出错" + e.getMessage());
                clean(indexDir);
                clean(workDir);
                return;
            }
            mavenScmCli.execute(Consts.BASE_SVN_PATH, "checkin", indexDir);
            mavenScmCli.addFile(Consts.WORK_SVN_PATH, currentDir);
            mavenScmCli.addFile(Consts.WORK_SVN_PATH, new File(currentDir.getAbsolutePath() + File.separator + Consts.DOC_FILE_NAME));
            mavenScmCli.execute(Consts.WORK_SVN_PATH, "checkin", workDir);
        }

        clean(indexDir);
        clean(workDir);


    }

    private void clean(File f) {
        try {
            for (File cf : f.listFiles()) {
                if (cf.isDirectory() && cf.listFiles() != null && cf.listFiles().length > 0) {
                    clean(cf);
                } else {
                    cf.delete();
                }
            }
            f.delete();
        } catch (Exception e) {
        }

    }


}
