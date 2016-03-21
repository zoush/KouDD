package activity;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bean.KeyBean;
import bean.MarketBean;
import utils.MD5;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.MyApplication;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/2/3.
 * qq:756350775
 */
public class SearchActivity extends BaseActivity {
    @ViewInject(R.id.hot_flowlayout)
    private TagFlowLayout mHotFlowLayout;
    @ViewInject(R.id.history_flowlayout)
    private TagFlowLayout mHistoryFlowLayout;
    private KeyBean[] mVals;
    private ArrayList<String> mValsProduct;

    LayoutInflater mInflater;
    @ViewInject(R.id.etSearch)
    private EditText etSearch;
    @ViewInject(R.id.tvType)
    private TextView tvType;
    @ViewInject(R.id.llType)
    private LinearLayout llType;
    private String[] str = {"商品", "店铺", "市场"};

    private KeyBean[] historyVals;
    private ArrayList<String> mValsHistory;
    private int type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ViewUtils.inject(this);
        mInflater = LayoutInflater.from(this);

        getHotKeywords();

        getSearchHistoryList();

        mHotFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (type == 1) {
                    Intent intent = new Intent(SearchActivity.this, ProductListActivity.class);
                    intent.putExtra("item_name", mValsProduct.get(position));
                    intent.putExtra("title", mValsProduct.get(position));
                    startActivity(intent);
                } else if (type == 2) {
                    getRankList(mValsProduct.get(position));
                } else if (type == 3) {
                    getEnterpriseList(mValsProduct.get(position));
                }
                return false;
            }
        });
        mHistoryFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (type == 1) {
                    Intent intent = new Intent(SearchActivity.this, ProductListActivity.class);
                    intent.putExtra("item_name", mValsHistory.get(position));
                    intent.putExtra("title", mValsHistory.get(position));
                    startActivity(intent);
                } else if (type == 2) {
                    getRankList(mValsHistory.get(position));
                } else if (type == 3) {
                    getEnterpriseList(mValsHistory.get(position));
                }
                return false;
            }
        });
    }

    @OnClick({R.id.tvSearch, R.id.llType, R.id.common_title_bar_btn_left})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.tvSearch:
                if (etSearch.getText().toString().length() != 0) {
                    Intent intent = new Intent(SearchActivity.this, ProductListActivity.class);
                    intent.putExtra("title", "");
                    intent.putExtra("item_name", etSearch.getText().toString());
                    startActivity(intent);
                }
                break;
            case R.id.llType:
                showPopu(llType);
                break;
            case R.id.common_title_bar_btn_left:
                finish();
                break;
        }
    }

    private void showPopu(LinearLayout layout) {
        View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_popu, null);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ListView listview = (ListView) view.findViewById(R.id.popuListview);
        ArrayAdapter adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.item_search,
                R.id.type_item, str);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tvType.setText(str[position]);
                type = position + 1;
                initKeyData(position + 1 + "");
                initHistoryData(position + 1 + "");
                popupWindow.dismiss();
            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(layout);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
    }

    private void initKeyData(String type) {
        mValsProduct = new ArrayList<String>();
        if (mVals.length != 0) {
            for (int i = 0; i < mVals.length; i++) {
                if (mVals[i].getSearch_type().equals(type))
                    mValsProduct.add(mVals[i].getKeywords());
            }
            initKeyWords();
        }
    }

    private void initHistoryData(String type) {
        mValsHistory = new ArrayList<String>();
        if (historyVals.length != 0) {
            for (int i = 0; i < historyVals.length; i++) {
                if (historyVals[i].getSearch_type().equals(type))
                    mValsHistory.add(historyVals[i].getKeywords());
            }
            initHistory();
        }
    }

    private void getHotKeywords() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.mall.getHotKeywords");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.mall.getHotKeywords&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        HttpUtils _http = new HttpUtils();
        _http.configCurrentHttpCacheExpiry(0 * 1000);
        _http.configDefaultHttpCacheExpiry(0);
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        //search_type为搜索类型（1为商品，2为店铺，3为市场）
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        mVals = new KeyBean[jsonArray.length()];
                        mValsProduct = new ArrayList<String>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            KeyBean keyBean = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), KeyBean.class);
                            mVals[i] = keyBean;
                            if (keyBean.getSearch_type().equals("1"))
                                mValsProduct.add(keyBean.getKeywords());
                        }
                        initKeyWords();
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

    private void getSearchHistoryList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.mall.getSearchHistory");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.mall.getSearchHistory&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        HttpUtils _http = new HttpUtils();
        _http.configCurrentHttpCacheExpiry(0 * 1000);
        _http.configDefaultHttpCacheExpiry(0);
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        //search_type为搜索类型（1为商品，2为店铺，3为市场）
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        if (jsonArray != null && jsonArray.length() != 0) {
                            historyVals = new KeyBean[jsonArray.length()];
                            mValsHistory = new ArrayList<String>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                KeyBean keyBean = new Gson().fromJson(jsonArray.getJSONObject(i).toString(), KeyBean.class);
                                historyVals[i] = keyBean;
                                if (keyBean.getSearch_type().equals("1"))
                                    mValsHistory.add(keyBean.getKeywords());
                            }
                            initHistory();
                        }
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

    private void initKeyWords() {
        if (mValsProduct.size() != 0) {
            String array[] = (String[]) mValsProduct.toArray(new String[mValsProduct.size()]);
            mHotFlowLayout.setAdapter(new TagAdapter<String>(array) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.label_tv,
                            mHotFlowLayout, false);
                    tv.setText(s);
                    return tv;
                }
            });
        }
    }

    private void initHistory() {
        if (mValsHistory.size() != 0) {
            String array[] = (String[]) mValsHistory.toArray(new String[mValsHistory.size()]);
            mHistoryFlowLayout.setAdapter(new TagAdapter<String>(array) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.label_tv,
                            mHistoryFlowLayout, false);
                    tv.setText(s);
                    return tv;
                }
            });
        }
    }


    //获取商家
    private void getRankList(String shop_name) {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.merchant.getMerchantList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("shop_name", shop_name);
        _params.addBodyParameter("orderby", "RANK");
        String signStr = "api_name=koudai.merchant.getMerchantList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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

    //搜市场

    private void getEnterpriseList(String market_name) {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.market.getMarketList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("lon", MyApplication.mLng + "");
        _params.addBodyParameter("lat", MyApplication.mLat + "");
        _params.addBodyParameter("market_name", market_name);
        String signStr = "api_name=koudai.market.getMarketList&appid=" + Constants.APPID + "&lon=" + MyApplication.mLng + "&lat=" + MyApplication.mLat + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        HttpUtils _http = new HttpUtils();
        _http.configCurrentHttpCacheExpiry(0 * 1000);
        _http.configDefaultHttpCacheExpiry(0);
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("market_list");
                        if (jsonArray != null && jsonArray.length() != 0) {
                            MarketBean marketBean = new Gson().fromJson(jsonArray.getJSONObject(0).toString(), MarketBean.class);
                            Intent intent = new Intent(SearchActivity.this, DiectDetailsActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("market", marketBean);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
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
