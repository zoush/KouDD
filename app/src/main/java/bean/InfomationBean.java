package bean;

import java.io.Serializable;

/**
 * Created by d on 2016/3/9.
 */
public class InfomationBean implements Serializable{
    private String infomation_id;
    private String title;
    private String description;
    private String addtime;
    private String img_path;

    public String getInfomation_id() {
        return infomation_id;
    }

    public void setInfomation_id(String infomation_id) {
        this.infomation_id = infomation_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public String getImg_path() {
        return img_path;
    }

    public void setImg_path(String img_path) {
        this.img_path = img_path;
    }
}
