package bean;

import java.io.Serializable;

/**
 * Created by d on 2016/3/3.
 */
public class KeyBean implements Serializable{
    private int id;
    private String keywords;
    private String search_type;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSearch_type() {
        return search_type;
    }

    public void setSearch_type(String search_type) {
        this.search_type = search_type;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }
}
