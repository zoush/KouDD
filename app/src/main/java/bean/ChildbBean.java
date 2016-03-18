package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/3/11.
 * qq:756350775
 */
public class ChildbBean implements Serializable{
    private String merchant_sort_id;
    private String sort_name;

    public String getMerchant_sort_id() {
        return merchant_sort_id;
    }

    public void setMerchant_sort_id(String merchant_sort_id) {
        this.merchant_sort_id = merchant_sort_id;
    }

    public String getSort_name() {
        return sort_name;
    }

    public void setSort_name(String sort_name) {
        this.sort_name = sort_name;
    }
}
