package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/2/24.
 * qq:756350775
 */
public class RankMerchantBean implements Serializable{
    private String merchant_id;
    private String taobaoid;
    private String user_rank_id;
    private String address;
    private String item_num;
    private String taobao_level;

    public String getTaobao_level() {
        return taobao_level;
    }

    public void setTaobao_level(String taobao_level) {
        this.taobao_level = taobao_level;
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

    public String getUser_rank_id() {
        return user_rank_id;
    }

    public void setUser_rank_id(String user_rank_id) {
        this.user_rank_id = user_rank_id;
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
