package bean;

import java.io.Serializable;

/**
 * Created by d on 2016/3/4.
 */
public class ClassBean implements Serializable{
    private String class_id;
    private String class_name;
    private String class_tag;
    private String serial;
    private String isuse;
    private String is_index;
    private String class_icon;

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

    public String getClass_tag() {
        return class_tag;
    }

    public void setClass_tag(String class_tag) {
        this.class_tag = class_tag;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getIsuse() {
        return isuse;
    }

    public void setIsuse(String isuse) {
        this.isuse = isuse;
    }

    public String getIs_index() {
        return is_index;
    }

    public void setIs_index(String is_index) {
        this.is_index = is_index;
    }

    public String getClass_icon() {
        return class_icon;
    }

    public void setClass_icon(String class_icon) {
        this.class_icon = class_icon;
    }
}
