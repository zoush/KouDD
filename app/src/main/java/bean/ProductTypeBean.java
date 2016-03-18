package bean;

import java.io.Serializable;

/**
 * Created by zoushaohua on 2016/3/11.
 * qq:756350775
 */
public class ProductTypeBean implements Serializable{
    private String merchant_class_id;
    private String class_name;
    private ChildbBean[] sort_list;

    public String getMerchant_class_id() {
        return merchant_class_id;
    }

    public void setMerchant_class_id(String merchant_class_id) {
        this.merchant_class_id = merchant_class_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public ChildbBean[] getSort_list() {
        return sort_list;
    }

    public void setSort_list(ChildbBean[] sort_list) {
        this.sort_list = sort_list;
    }
}
