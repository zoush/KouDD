package bean;

import java.io.Serializable;

/**
 * Created by d on 2016/3/2.
 */
public class MerchantBean implements Serializable {
    private String address;
    private String mobile;
    private String qq;
    private String reg_time;
    private String tel;
    private String service_type;
    private String taobao_level;
    private String item_num;
    private String is_collected;
    private String class_name;
    private String wangwangid;

    public String getWangwangid() {
        return wangwangid;
    }

    public void setWangwangid(String wangwangid) {
        this.wangwangid = wangwangid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public String getTaobao_level() {
        return taobao_level;
    }

    public void setTaobao_level(String taobao_level) {
        this.taobao_level = taobao_level;
    }

    public String getItem_num() {
        return item_num;
    }

    public void setItem_num(String item_num) {
        this.item_num = item_num;
    }

    public String getIs_collected() {
        return is_collected;
    }

    public void setIs_collected(String is_collected) {
        this.is_collected = is_collected;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
}
