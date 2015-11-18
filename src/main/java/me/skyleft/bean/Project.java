package me.skyleft.bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by andy on 15/11/14.
 */
public class Project {

    private String projectName;
    private String projectNameAlias;
    private String currentStatus;
    private String pics;
    private String description;
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
}
