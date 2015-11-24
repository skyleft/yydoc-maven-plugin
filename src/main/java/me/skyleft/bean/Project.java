package me.skyleft.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by andy on 15/11/14.
 */
public class Project {

    /**
     * 项目名
     */
    private String projectName;

    /**
     * 项目别名
     */
    private String projectNameAlias;

    /**
     * 当前状态
     */
    private String currentStatus;

    /**
     * 负责人
     */
    private String pics;

    /**
     * 描述
     */
    private String description;

    /**
     * 包含的模块
     */
    private List<Module> modules;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getPics() {
        return pics;
    }

    public void setPics(String pics) {
        this.pics = pics;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public String getProjectNameAlias() {
        return projectNameAlias;
    }

    public void setProjectNameAlias(String projectNameAlias) {
        this.projectNameAlias = projectNameAlias;
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectName='" + projectName + '\'' +
                ", projectNameAlias='" + projectNameAlias + '\'' +
                ", currentStatus='" + currentStatus + '\'' +
                ", pics='" + pics + '\'' +
                ", description='" + description + '\'' +
                ", modules=" + modules +
                '}';
    }
}
