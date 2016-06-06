package me.skyleft.utils;

/**
 * Created by zhangzongchao on 2015/11/17.
 */
public interface Consts {

    //文档资源svn根目录
    String BASE_SVN_PATH = "scm:svn:https://svn.yy.com/web/gh/dev-res-guide";

    //当前项目文档资源markdown根目录
    String WORK_SVN_PATH = "scm:svn:https://svn.yy.com/web/gh/dev-res-guide/posts/auto";

    //文档名称
    String DOC_FILE_NAME = "Readme.md";

    //文档资源库首页文件名
    String INDEX_FILE_NAME = "index.html";

    //SVN命令
    String CHECKIN = "checkin";
    String CHECKOUT= "checkout";

    //接口doc注解名
    String YYDOC_PORT_NAME = "@yyport";
    String YYDOC_PARAM_NAME = "@yyparam";
    String YYDOC_RETURN_NAME = "@yyreturn";
    String YYDOC_DESC = "@yydesc";


}
