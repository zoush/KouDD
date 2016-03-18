package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/22.
 * qq:756350775
 */
public class ImageBean implements Serializable{
    private int id;
    private String title;
    private String link;
    private String pic;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
