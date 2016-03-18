package bean;

import java.io.Serializable;

/**
 * Created by d on 2016/3/8.
 */
public class CollectShopBean implements Serializable{
    private String collect_id;
    private String merchant_id;
    private String taobaoid;
    private String address;
    private String item_num;
    private String taobao_level="0";

    public String getTaobao_level() {
        return taobao_level;
    }

    public void setTaobao_level(String taobao_level) {
        this.taobao_level = taobao_level;
    }

    public String getCollect_id() {
        return collect_id;
    }

    public void setCollect_id(String collect_id) {
        this.collect_id = collect_id;
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getTaobaoid() {
        return taobaoid;
    }

    public void setTaobaoid(String taobaoid) {
        this.taobaoid = taobaoid;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getItem_num() {
        return item_num;
    }

    public void setItem_num(String item_num) {
        this.item_num = item_num;
    }
}
