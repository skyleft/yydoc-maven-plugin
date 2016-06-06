# yydoc-maven-plugin 用于文档自动化的maven插件

## 插件简介
    
插件执行prefix为yydoc，有两个maven goal, gen-doc 和 publish

* 执行gen-doc 可以扫描项目的pom配置文件以及java代码，渲染内置的markdown模板，生成一份项目文档
* 执行publish 可以将markdown项目文档 上传到项目文档的svn中，并自动修改http://dev.yypm.com/开发指引的菜单，添加菜单指向当前项目的文档。

很大程度上解决了项目文档格式杂乱（统一的内置模板），上传到开发指引网站操作繁琐（publish一个maven goal搞定）的问题

## 使用方法

### 添加pom依赖

    <plugin>
        <groupId>com.yy.expe</groupId>
        <artifactId>yydoc-maven-plugin</artifactId>
        <version>1.0-SNAPSHOT</version>
    </plugin>

### 在pom中配置项目信息

下面是一份完整的配置，有些条目不需要可以不配置

    <properties>
       <yydoc.name>微房话神挑战赛后台</yydoc.name>
       <yydoc.desc>用于支持本次微房话神挑战赛的活动，活动预期人数xxx，营收xxx</yydoc.desc>
       <yydoc.alias>wordsgod</yydoc.alias>
       <yydoc.status>开发完成，维护中</yydoc.status>
       <yydoc.pics>张宗超</yydoc.pics>
       <yydoc.dragon>重大活动运营->微房话神挑战赛活动</yydoc.dragon>
       <yydoc.ips>183.60.218.141, 183.60.218.142, 183.60.218.143</yydoc.ips>
       <yydoc.domain>wordsgod.yy.com</yydoc.domain>
       <yydoc.svn>https://svn.yy.com/web/gh/newactivity/trunk/wordsgod</yydoc.svn>
    </properties>
                
*若项目组织形式为一个父pom下边有多个模块的形式，由于执行maven的时候，往往将目标在子模块中也去执行，
为避免子模块重复生成子文档，需要在子模块的pom中，加属性
    
    <yydoc.ignore>true</yydoc.ignore>

### 配置对外接口

使用类似javadoc的文档注解进行配置，例如
    
    /**
    	 *
    	 * @yyport 接口名字
    	 * @yyurl /record.do
    	 * @yyparam uid
    	 * @yyreturn 中奖记录
    	 * @yydesc 返回指定用户的中奖纪录
    */
    
对于param和return可以不配置，默认可以读取javadoc的@param,@return注释

### 生成文档

*执行mvn yydoc:gen-doc*

### 上传并配置文档

*执行 mvn yydoc:publish*

## 存在的问题
* 插件首先checkout下开发指引的index.html，若在插件的执行过程中，有别的用户，update了index.html，会导致插件后面提交失败。需要重新运行。
