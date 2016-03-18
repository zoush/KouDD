package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/29.
 * qq:756350775
 */
public class ProperBean implements Serializable{
    private String item_sku_price_id;
    private String sku_price;
    private String sku_stock;
    private String property_value1;
    private String property_value2;
    private String property_name1;
    private String property_name2;

    public String getItem_sku_price_id() {
        return item_sku_price_id;
    }

    public void setItem_sku_price_id(String item_sku_price_id) {
        this.item_sku_price_id = item_sku_price_id;
    }

    public String getSku_price() {
        return sku_price;
    }

    public void setSku_price(String sku_price) {
        this.sku_price = sku_price;
    }

    public String getSku_stock() {
        return sku_stock;
    }

    public void setSku_stock(String sku_stock) {
        this.sku_stock = sku_stock;
    }

    public String getProperty_value1() {
        return property_value1;
    }

    public void setProperty_value1(String property_value1) {
        this.property_value1 = property_value1;
    }

    public String getProperty_value2() {
        return property_value2;
    }

    public void setProperty_value2(String property_value2) {
        this.property_value2 = property_value2;
    }

    public String getProperty_name1() {
        return property_name1;
    }

    public void setProperty_name1(String property_name1) {
        this.property_name1 = property_name1;
    }

    public String getProperty_name2() {
        return property_name2;
    }

    public void setProperty_name2(String property_name2) {
        this.property_name2 = property_name2;
    }
}
