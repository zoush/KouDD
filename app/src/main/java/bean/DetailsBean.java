package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/29.
 * qq:756350775
 */
public class DetailsBean implements Serializable{
    private String base_pic;
    private String item_name;
    private String mall_price;
    private String wholesale_price;
    private String addtime;
    private String merchant_id;
    private int is_collected;
    private SkusBean skus;

    public SkusBean getSkus() {
        return skus;
    }

    public void setSkus(SkusBean skus) {
        this.skus = skus;
    }

    public int getIs_collected() {
        return is_collected;
    }

    public void setIs_collected(int is_collected) {
        this.is_collected = is_collected;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getBase_pic() {
        return base_pic;
    }

    public void setBase_pic(String base_pic) {
        this.base_pic = base_pic;
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

    public String getWholesale_price() {
        return wholesale_price;
    }

    public void setWholesale_price(String wholesale_price) {
        this.wholesale_price = wholesale_price;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

}
