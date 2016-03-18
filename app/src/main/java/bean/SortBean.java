package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/22.
 * qq:756350775
 */
public class SortBean implements Serializable{
    private String sort_id;
    private String sort_name;
    private String sort_icon;

    public String getSort_id() {
        return sort_id;
    }

    public void setSort_id(String sort_id) {
        this.sort_id = sort_id;
    }

    public String getSort_name() {
        return sort_name;
    }

    public void setSort_name(String sort_name) {
        this.sort_name = sort_name;
    }

    public String getSort_icon() {
        return sort_icon;
    }

    public void setSort_icon(String sort_icon) {
        this.sort_icon = sort_icon;
    }
}
