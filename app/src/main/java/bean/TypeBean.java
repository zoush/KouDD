package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/22.
 * qq:756350775
 */
public class TypeBean implements Serializable {
    private int id;
    private String class_id;
    private String class_name;
    private SortBean[] sort_list;

    public String getClass_id() {
        return class_id;
    }

    public void setClass_id(String class_id) {
        this.class_id = class_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public SortBean[] getSort_list() {
        return sort_list;
    }

    public void setSort_list(SortBean[] sort_list) {
        this.sort_list = sort_list;
    }
}
