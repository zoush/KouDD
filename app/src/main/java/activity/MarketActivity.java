package activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import com.pulltorefresh.PullToRefreshLayout;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapter.MyFragmentPagerAdapter;
import bean.MerchantBean;
import bean.ShareBean;
import de.greenrobot.event.EventBus;
import fragments.market.NewFragment;
import fragments.market.PriceFragment;
import fragments.market.SaleFragment;
import fragments.market.TuiFragment;
import utils.MD5;
import utils.PreferenceUtil;
import utils.ToastUtil;
import widgh.AutoHeightViewPager;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.MainActivity;
import yd.koudd.MyApplication;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/2/25.
 * qq:756350775
 * 店铺
 */
public class MarketActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {
    @ViewInject(R.id.tvRecommond)
    private TextView tvRecommond;
    @ViewInject(R.id.tvSale)
    private TextView tvSale;
    @ViewInject(R.id.tvNew)
    private TextView tvNew;
    @ViewInject(R.id.tvPrice)
    private TextView tvPrice;
    @ViewInject(R.id.viewpager)
    private AutoHeightViewPager viewpager;
    private ArrayList<Fragment> _listFragemt;
    private TuiFragment tuiFragment;
    private SaleFragment saleFragment;
    private NewFragment newFragment;
    private PriceFragment priceFragment;
    @ViewInject(R.id.tvName)
    private TextView tvName;
    @ViewInject(R.id.tvAddress)
    private TextView tvAddress;
    public static String merchant_id = "";
    PreferenceUtil _pref;

    @ViewInject(R.id.ivMessage)
    private ImageView ivMessage;
    private String[] str = {"分类", "店铺动态", "联系客服", "首页", "分享"};

    MerchantBean mMerchantBean;

    ShareBean mShareBean;
    private int tag = 1;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;
    @ViewInject(R.id.etSearch)
    public static EditText etSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markey);

        ViewUtils.inject(this);
        _pref = PreferenceUtil.getInstance(this);
        initData();
        initViewPager();
        MyApplication.activityList.add(this);

        getMarketDetails();

        getShareParams();
        refresh_view.setOnRefreshListener(this);
        EventBus.getDefault().register(this);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                EventBus.getDefault().post("search");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        merchant_id = getIntent().getStringExtra("merchant_id");
        tvName.setText(getIntent().getStringExtra("name"));
        if (getIntent().getStringExtra("address").length() != 0)
            tvAddress.setText(getIntent().getStringExtra("address"));
        else
            tvAddress.setVisibility(View.GONE);
    }

    @OnClick({R.id.common_title_bar_btn_left, R.id.ivMessage, R.id.tvName})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.common_title_bar_btn_left:
                finish();
                break;
            case R.id.ivMessage:
                showPopu(ivMessage);
                break;
            case R.id.tvName:
                Intent intent = new Intent(MarketActivity.this, MarketDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("merchant", mMerchantBean);
                intent.putExtras(bundle);
                intent.putExtra("merchant_id", merchant_id);
                startActivity(intent);
                break;
        }
    }

    private void showPopu(ImageView layout) {
        View view = LayoutInflater.from(MarketActivity.this).inflate(R.layout.search_popu, null);
        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ListView listview = (ListView) view.findViewById(R.id.popuListview);
        ArrayAdapter adapter = new ArrayAdapter<String>(
                getApplicationContext(), R.layout.item_search,
                R.id.type_item, str);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(MarketActivity.this, ShopTypeActivity.class);
                    intent.putExtra("merchant_id", merchant_id);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(MarketActivity.this, ActiveActivity.class);
                    intent.putExtra("merchant_id", merchant_id);
                    startActivity(intent);
                } else if (position == 2) {
                    if (mMerchantBean.getTel() != null && mMerchantBean.getTel().length() != 0)
                        new MaterialDialog.Builder(MarketActivity.this)
                                .positiveText("拨打")
                                .content(mMerchantBean.getTel())
                                .contentColor(getResources().getColor(R.color.rule_color))
                                .negativeText("取消")
                                .backgroundColor(getResources().getColor(R.color.white))
                                .onAny(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        if (which.name().equals("POSITIVE")) {
                                            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mMerchantBean.getTel()));
                                            startActivity(intent);
                                        }
                                    }
                                })
                                .show();
                } else if (position == 3) {
                    Intent intent = new Intent(MarketActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    for (Activity activity : MyApplication.activityList)
                        activity.finish();
                } else if (position == 4) {
                    showDialog();
                }
                popupWindow.dismiss();
            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(layout);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

    }

    private void showDialog() {
        final SHARE_MEDIA[] displaylist = new SHARE_MEDIA[]
                {
                        SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,
                        SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE
                };
        new ShareAction(this).setDisplayList(displaylist)
                .withTitle(mShareBean.getTitle())
                .withText(mShareBean.getTitle())
                .withMedia(new UMImage(MarketActivity.this, R.mipmap.ic_icon))
                .withTargetUrl("http://www.taobao.com")
                .open();

    }

    public void onEventMainThread(String msg) {

    }

    private void initViewPager() {
        tvRecommond.setSelected(true);
        _listFragemt = new ArrayList<Fragment>();
        tuiFragment = new TuiFragment();
        saleFragment = new SaleFragment();
        newFragment = new NewFragment();
        priceFragment = new PriceFragment();
        _listFragemt.add(tuiFragment);
        _listFragemt.add(saleFragment);
        _listFragemt.add(newFragment);
        _listFragemt.add(priceFragment);

        viewpager.setOffscreenPageLimit(_listFragemt.size());
        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), _listFragemt));
        viewpager.setCurrentItem(0);
        viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                if (tag == 1) {
                    EventBus.getDefault().post("1refresh");
                } else if (tag == 2) {
                    EventBus.getDefault().post("2refresh");
                } else if (tag == 3) {
                    EventBus.getDefault().post("3refresh");
                } else if (tag == 4) {
                    EventBus.getDefault().post("4refresh");
                }
            }
        }, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                if (tag == 1) {
                    EventBus.getDefault().post("1load");
                } else if (tag == 2) {
                    EventBus.getDefault().post("2load");
                } else if (tag == 3) {
                    EventBus.getDefault().post("3load");
                } else if (tag == 4) {
                    EventBus.getDefault().post("4load");
                }
            }
        }, 2000);
    }


    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    tag = 1;
                    tvRecommond.setSelected(true);
                    tvSale.setSelected(false);
                    tvNew.setSelected(false);
                    tvPrice.setSelected(false);
                    break;
                case 1:
                    tag = 2;
                    tvRecommond.setSelected(false);
                    tvSale.setSelected(true);
                    tvNew.setSelected(false);
                    tvPrice.setSelected(false);
                    break;
                case 2:
                    tag = 3;
                    tvRecommond.setSelected(false);
                    tvSale.setSelected(false);
                    tvNew.setSelected(true);
                    tvPrice.setSelected(false);
                    break;
                case 3:
                    tag = 4;
                    tvRecommond.setSelected(false);
                    tvSale.setSelected(false);
                    tvNew.setSelected(false);
                    tvPrice.setSelected(true);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

    }

    @OnClick({R.id.tvRecommond, R.id.tvSale, R.id.tvNew, R.id.tvPrice})
    private void viewPagerClickEvent(View v) {
        tvRecommond.setSelected(false);
        tvSale.setSelected(false);
        tvNew.setSelected(false);
        tvPrice.setSelected(true);
        ((TextView) v).setSelected(false);
        switch (v.getId()) {
            case R.id.tvRecommond:
                tag = 1;
                viewpager.setCurrentItem(0);
                tvRecommond.setSelected(true);
                break;
            case R.id.tvSale:
                tag = 2;
                viewpager.setCurrentItem(1);
                tvSale.setSelected(true);
                break;
            case R.id.tvNew:
                tag = 3;
                viewpager.setCurrentItem(2);
                tvNew.setSelected(true);
                break;
            case R.id.tvPrice:
                tag = 4;
                viewpager.setCurrentItem(3);
                tvPrice.setSelected(true);
                break;
        }
    }

    @OnClick({R.id.btnFav})
    private void favClickEvent(View v) {
        if (v.getId() == R.id.btnFav) {
            collectMerchant();
        }
    }

    private void collectMerchant() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.collect.collectShop");
        _params.addBodyParameter("merchant_id", merchant_id);
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.collect.collectShop&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        ToastUtil.showToast(MarketActivity.this, jsonObject.getString("data"));
                    } else {
                        ToastUtil.showToast(MarketActivity.this, jsonObject.getString("error_msg"));
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

    private void getMarketDetails() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.merchant.getMerchantInfo");
        _params.addBodyParameter("merchant_id", merchant_id);
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.merchant.getMerchantInfo&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        mMerchantBean = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), MerchantBean.class);
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

    private void getShareParams() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.merchant.getShareParams");
        _params.addBodyParameter("merchant_id", merchant_id);
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.merchant.getShareParams&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        mShareBean = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), ShareBean.class);
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
