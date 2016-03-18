package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/24.
 * qq:756350775
 */
public class ProductBean implements Serializable {
    private String item_id;
    private String item_name;
    private String mall_price;
    private String small_img;
    private String address;
    private String market_name;

    public String getMarket_name() {
        return market_name;
    }

    public void setMarket_name(String market_name) {
        this.market_name = market_name;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getMall_price() {
        return mall_price;
    }

    public void setMall_price(String mall_price) {
        this.mall_price = mall_price;
    }

    public String getSmall_img() {
        return small_img;
    }

    public void setSmall_img(String small_img) {
        this.small_img = small_img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
