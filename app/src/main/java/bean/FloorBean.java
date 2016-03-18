package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/26.
 * qq:756350775
 */
public class FloorBean implements Serializable {
    private String floor_id;
    private String floor_name;

    public String getFloor_id() {
        return floor_id;
    }

    public void setFloor_id(String floor_id) {
        this.floor_id = floor_id;
    }

    public String getFloor_name() {
        return floor_name;
    }

    public void setFloor_name(String floor_name) {
        this.floor_name = floor_name;
    }
}
