package me.skyleft.bean;

import java.util.List;

/**
 * Created by andy on 15/11/14.
 */
public class Module {

    /**
     * 模块名
     */
    private String name;

    /**
     * 模块svn地址
     */
    private String svn;

    /**
     * 模块域名
     */
    private String url;

    /**
     * 模块对应在潜龙的位置
     */
    private String dragon;

    /**
     * 模块部署的ip
     */
    private String ips;

    /**
     * 模块包含的接口
     */
    private List<Interface> interfaces;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSvn() {
        return svn;
    }

    public void setSvn(String svn) {
        this.svn = svn;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDragon() {
        return dragon;
    }

    public void setDragon(String dragon) {
        this.dragon = dragon;
    }

    public List<Interface> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<Interface> interfaces) {
        this.interfaces = interfaces;
    }

    public String getIps() {
        return ips;
    }

    public void setIps(String ips) {
        this.ips = ips;
    }

    @Override
    public String toString() {
        return "Module{" +
                "name='" + name + '\'' +
                ", svn='" + svn + '\'' +
                ", url='" + url + '\'' +
                ", dragon='" + dragon + '\'' +
                ", ips='" + ips + '\'' +
                ", interfaces=" + interfaces +
                '}';
    }
}
