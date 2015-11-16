package me.skyleft.utils;

import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.Version;
import me.skyleft.bean.Project;
import org.codehaus.plexus.util.StringOutputStream;

import java.io.*;

import static freemarker.template.Configuration.*;

/**
 * Created by andy on 15/11/14.
 */
public class TemplateRender {
    private static Configuration cfg = new Configuration(VERSION_2_3_0);
    private static Template defaultMdTpl = null;
    private static String defaultMdTplStr = "# ${projectName!'项目名'}(${projectNameAlias!'项目别名'})\n" +
            "\n" +
            "---\n" +
            "## 概要\n" +
            "\n" +
            "> | *项*              | *详细*          |\n" +
            "> | ----------------- | ----------------|\n" +
            "> | 状态              | ${currentStatus!'目前状态'}          |\n" +
            "> | 负责人            | <#if pic??><#list pics as pic>${pic}</#list></#if>|\n" +
            "\n" +
            "---\n" +
            "## 说明\n" +
            "<pre>\n" +
            "${description!'描述信息'}\n" +
            "</pre>\n" +
            "\n" +
            "---\n" +
            "## svn地址\n" +
            "<#if modules??>\n"+
            "<#list modules as module>\n" +
            "    ${module_index + 1}. ${module.name}:${module.svn}\n" +
            "</#list>\n" +
            "</#if>\n"+
            "\n" +
            "\n" +
            "---\n" +
            "## URL\n" +
            "<#if modules??>\n"+
            "<#list modules as module>\n" +
            "    ${module_index + 1}. ${module.name}:${module.url}\n" +
            "</#list>\n" +
            "</#if>\n"+
            "\n" +
            "---\n" +
            "## 部署\n" +
            "<pre>\n" +
            "<#if modules??>\n"+
            "<#list modules as module>\n" +
            "${module.name}\n" +
            "    ip1\n" +
            "    ip2\n" +
            "    ...\n" +
            "</#list>\n" +
            "</#if>\n"+
            "\n" +
            "潜龙位置:\n" +
            "<#if modules??>\n"+
            "<#list modules as module>\n" +
            "${module.name}:此处填写模块在潜龙中的位置\n" +
            "</#list>\n" +
            "</#if>\n"+
            "</pre>\n" +
            "\n" +
            "\n" +
            "---\n" +
            "## 接口描述\n" +
            "<#if modules??>\n"+
            "<#list modules as module>\n" +
            "###${module.name}\n" +
            "地址 | 入参 | 出参\n" +
            "----|------|----\n" +
            "<#list module.interfaces as interface>\n" +
            "${interface.url}|${interface.inparam}|${interface.outparam}\n" +
            "</#list>\n" +
            "</#list>\n" +
            "</#if>\n"+
            "\n";
    static {
        try {
            defaultMdTpl = new Template("defaultMdTpl", new StringReader(defaultMdTplStr), cfg);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String render(Project project){
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Writer writer = new OutputStreamWriter(bout);
        try {
            defaultMdTpl.process(project,writer);
            writer.flush();
            writer.close();
        } catch (TemplateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bout.toString();
    }

}
