package me.skyleft.bean;

/**
 * Created by andy on 15/11/14.
 */
public class Interface {

    /**
     * 接口名
     */
    private String name;

    /**
     * 接口地址
     */
    private String url;

    /**
     * 入参（暂时用字符串简化表示）
     */
    private String inparam;

    /**
     * 出参数（暂时用字符串简化表示）
     */
    private String outparam;

    /**
     * 描述
     */
    private String desc;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getInparam() {
        return inparam;
    }

    public void setInparam(String inparam) {
        this.inparam = inparam;
    }

    public String getOutparam() {
        return outparam;
    }

    public void setOutparam(String outparam) {
        this.outparam = outparam;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "Interface{" +
                "name='" + name + '\'' +
                ", url='" + url + '\'' +
                ", inparam='" + inparam + '\'' +
                ", outparam='" + outparam + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
