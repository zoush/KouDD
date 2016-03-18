package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/29.
 * qq:756350775
 */
public class SkusBean implements Serializable{
    private ProperBean[] sku_info;
    private String sku_name1;
    private String sku_name2;

    public ProperBean[] getSku_info() {
        return sku_info;
    }

    public void setSku_info(ProperBean[] sku_info) {
        this.sku_info = sku_info;
    }

    public String getSku_name1() {
        return sku_name1;
    }

    public void setSku_name1(String sku_name1) {
        this.sku_name1 = sku_name1;
    }

    public String getSku_name2() {
        return sku_name2;
    }

    public void setSku_name2(String sku_name2) {
        this.sku_name2 = sku_name2;
    }
}
