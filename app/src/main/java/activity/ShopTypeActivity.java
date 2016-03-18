package activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bean.ProductTypeBean;
import utils.MD5;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/3/11.
 * qq:756350775
 */
public class ShopTypeActivity extends BaseActivity {
    @ViewInject(R.id.expandableListView)
    private ExpandableListView expandableListView;
    private FollowListViewAdapter treeViewAdapter;
    public static List<Integer> gcategory = new ArrayList<Integer>();

    List<Map<String, Object>> groups = new ArrayList<Map<String, Object>>();
    Map<String, List<Map<String, Object>>> childs = new HashMap<String, List<Map<String, Object>>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_type);

        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("店铺分类");
        treeViewAdapter = new FollowListViewAdapter(this);
        expandableListView = (ExpandableListView) this.findViewById(R.id.expandableListView);

        getMerchantClassList();
    }

    private void initData() {

        //封装所有分类////////////////////////////

        //groups = new Data().initGroups();
        //childs = new Data().initChilds();

        List<FollowListViewAdapter.TreeNode> treeNode = treeViewAdapter.GetTreeNode();
        System.out.println("treeNode是什么：" + treeNode);
        for (int i = 0; i < groups.size(); i++) {
            FollowListViewAdapter.TreeNode node = new FollowListViewAdapter.TreeNode();
            node.parent = groups.get(i).get("name");
            node.id = groups.get(i).get("class_id");
            System.out.println("获得到的名字：" + groups.get(i).get("name"));
            node.childs = childs.get(groups.get(i).get("id").toString());
            System.out.println("获得的id：" + childs.get(groups.get(i).get("id").toString()));
            treeNode.add(node);
        }
        treeViewAdapter.UpdateTreeNode(treeNode);
        expandableListView.setAdapter(treeViewAdapter);
        expandableListView.expandGroup(0);
    }

    private void getMerchantClassList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.merchant.getMerchantClassList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("merchant_id", getIntent().getStringExtra("merchant_id"));
        String signStr = "api_name=koudai.merchant.getMerchantClassList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        final HttpUtils _http = new HttpUtils();
        _http.configCurrentHttpCacheExpiry(0 * 1000);
        _http.configDefaultHttpCacheExpiry(0);
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        groups.clear();
                        childs.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            ProductTypeBean productTypeBean = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), ProductTypeBean.class);
                            Map<String, Object> group = new HashMap<String, Object>();
                            group.put("name", productTypeBean.getClass_name());
                            group.put("id", i);
                            group.put("class_id",productTypeBean.getMerchant_class_id());
                            groups.add(group);
                            List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();

                            for (int j = 0; j < productTypeBean.getSort_list().length; j++) {
                                Map<String, Object> map = new HashMap<String, Object>();
                                map.put("id", productTypeBean.getSort_list()[j].getMerchant_sort_id());
                                map.put("name", productTypeBean.getSort_list()[j].getSort_name());
                                child.add(map);
                                childs.put("" + i, child);
                            }
                        }
                        if (groups.size() != 0)
                            initData();
                    } else {

                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("sa", s);
            }
        });
    }
}
