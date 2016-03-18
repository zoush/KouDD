package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.CallbackContext;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.session.model.Session;
import com.facebook.drawee.view.SimpleDraweeView;
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
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import bean.DetailsBean;
import bean.MerchantBean;
import bean.ProductBean;
import de.greenrobot.event.EventBus;
import utils.DensityUtil;
import utils.MD5;
import utils.PreferenceUtil;
import utils.ToastUtil;
import widgh.FullyLinearLayoutManager;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.MainActivity;
import yd.koudd.MyApplication;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/2/25.
 * qq:756350775
 */
public class ProductDetailActivity extends BaseActivity {
    @ViewInject(R.id.recyclerview)
    private RecyclerView recyclerView;
    private List<ProductBean> newList = new ArrayList<ProductBean>();
    @ViewInject(R.id.btnFav)
    private Button btnFav;
    PreferenceUtil _pref;

    private DetailsBean mDetailsBean;

    @ViewInject(R.id.ivLogo)
    private SimpleDraweeView ivLogo;
    @ViewInject(R.id.tvName)
    private TextView tvName;
    @ViewInject(R.id.tvWholePrice)
    private TextView tvWholePrice;
    @ViewInject(R.id.tvSalePrice)
    private TextView tvSalePrice;

    public MerchantBean mMerchantBean;

    @ViewInject(R.id.tvAddress)
    private TextView tvAddress;
    @ViewInject(R.id.tvSevice)
    private TextView tvSevice;
    @ViewInject(R.id.tvTaobao)
    private TextView tvTaobao;
    @ViewInject(R.id.tvQQ)
    private TextView tvQQ;
    @ViewInject(R.id.tvTime)
    private TextView tvTime;

    @ViewInject(R.id.ysnowswebview)
    private WebView ysnowswebview;
    @ViewInject(R.id.ivPopu)
    private ImageView ivPopu;
    private String[] str = {"首页", "分享"};

    @ViewInject(R.id.llColor)
    private LinearLayout llColor;
    @ViewInject(R.id.tvColor)
    private TextView tvColor;
    @ViewInject(R.id.colorflowlayout)
    private TagFlowLayout colorflowlayout;

    @ViewInject(R.id.llSize)
    private LinearLayout llSize;
    @ViewInject(R.id.tvSize)
    private TextView tvSize;
    @ViewInject(R.id.sizeflowlayout)
    private TagFlowLayout sizeflowlayout;

    LayoutInflater mInflater;
    Session mSession;
    @ViewInject(R.id.llUpload)
    private LinearLayout llUpload;
    @ViewInject(R.id.ratingBar)
    private RatingBar ratingBar;
    @ViewInject(R.id.scrollView)
    private ScrollView scrollView;
    String colors[];
    private int[] vals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        ViewUtils.inject(this);
        mInflater = LayoutInflater.from(this);

        _pref = PreferenceUtil.getInstance(this);

        getProductInfo();
        initNewData();

        getLikeList();

        getWebView();

    }

    @OnClick({R.id.ivBack, R.id.btnEnter, R.id.btnFav, R.id.btnPhone, R.id.llUpload, R.id.ivPopu})
    private void clickEvent(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.ivBack:
                finish();
                break;
            case R.id.btnEnter:
                intent = new Intent(ProductDetailActivity.this, MarketActivity.class);
                intent.putExtra("merchant_id", mDetailsBean.getMerchant_id());
                intent.putExtra("address", mMerchantBean.getAddress());
                intent.putExtra("name", mMerchantBean.getWangwangid());
                startActivity(intent);
                break;
            case R.id.btnFav:
                if (_pref.getLoginStatus())
                    concernProduct();
                else {
                    intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.btnPhone:
                new MaterialDialog.Builder(this)
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
                break;
            case R.id.llUpload:
                if (_pref.getLoginStatus())
                    if (_pref.getTaoBaoId().length() != 0)
                        uploadTaobao();
                    else
                        showLogin(llUpload);
                else {
                    intent = new Intent(ProductDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ivPopu:
                showPopu(ivPopu);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackContext.onActivityResult(requestCode, resultCode, data);
    }

    public void showLogin(View view) {
        //调用getService方法来获取服务
        final LoginService loginService = AlibabaSDK.getService(LoginService.class);
        loginService.showLogin(ProductDetailActivity.this, new LoginCallback() {
            @Override
            public void onSuccess(Session session) {
                // 当前是否登录状态           boolean isLogin();
                // 登录授权时间           long getLoginTime();
                // 当前用户ID           String getUserId();
                // 用户其他属性           User getUser();
                //User中：淘宝用户名  淘宝用户ID   淘宝用户头像地址
                _pref.setUrl(session.getUser().avatarUrl);
                _pref.setUserName(session.getUser().nick);

                Map<String, Object> _map = session.getOtherInfo();
                mSession = session;
                taoBaoBind();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(ProductDetailActivity.this, "授权取消" + code + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void taoBaoBind() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.user.bindTaobao");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("taobao_user_nick", mSession.getUser().nick);
        _params.addBodyParameter("taobao_user_id", mSession.getUserId());
        _params.addBodyParameter("access_token", mSession.getAuthorizationCode());
        String signStr = "PHPSESSID=&api_name=koudai.user.bindTaobao&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        DefaultHttpClient dh = (DefaultHttpClient) _http.getHttpClient();
                        CookieStore cs = dh.getCookieStore();
                        List<Cookie> cookies = cs.getCookies();
                        String aa = null;
                        for (int i = 0; i < cookies.size(); i++) {
                            if ("PHPSESSID".equals(cookies.get(i).getName())) {
                                aa = cookies.get(i).getValue();
                                break;
                            }
                        }
                        _pref.setSessionId(aa);

                        PreferenceUtil _pref = PreferenceUtil.getInstance(ProductDetailActivity.this);
                        if (jsonObject1.has("user_id"))
                            _pref.setUserid(jsonObject1.getString("user_id"));
                        if (jsonObject1.has("password"))
                            _pref.setPassword(jsonObject1.getString("password"));
                        _pref.setLoginStatus(true);
                        _pref.setTaoBaoLogin(true);
                        getUserInfo();
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

    private void getUserInfo() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.user.getUserInfo");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.user.getUserInfo&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                        _pref.setTaoBaoId(jsonObject1.getString("taobao_user_id"));
                        if (jsonObject1.getString("mobile").length() == 0) {
                            Intent intent = new Intent(ProductDetailActivity.this, BindTaoBaoActivity.class);
                            startActivity(intent);
                            EventBus.getDefault().post("regist");
                        } else {
                            _pref.setMobile(jsonObject1.getString("mobile"));
                            EventBus.getDefault().post("regist");
                            _pref.setLoginStatus(true);
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

    private void showPopu(ImageView layout) {
        View view = LayoutInflater.from(ProductDetailActivity.this).inflate(R.layout.search_popu, null);
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
                    Intent intent = new Intent(ProductDetailActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    for (Activity activity : MyApplication.activityList)
                        activity.finish();
                } else if (position == 1) {
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
                .withTitle("测试")
                .withText("测试")
                .withMedia(new UMImage(ProductDetailActivity.this, R.mipmap.ic_icon))
                .withTargetUrl("http://www.baidu.com")
                .open();

    }

    private void initNewData() {
        LinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private Context mContext;
        private List<ProductBean> dataList;

        public RecyclerViewAdapter(Context context, List<ProductBean> dataList) {
            mInflater = LayoutInflater.from(context);
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.item_recycleview,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.imageView = (SimpleDraweeView) view.findViewById(R.id.imageView);

            viewHolder.textView = (TextView) view.findViewById(R.id.textView);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
            viewHolder.llItem = (LinearLayout) view.findViewById(R.id.llItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.textView.setText("￥ " + dataList.get(i).getMall_price());
            viewHolder.tvName.setText(dataList.get(i).getItem_name());

            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.imageView.getLayoutParams();
            linearParams.width = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 30)) / 3;
            linearParams.height = linearParams.width;
            viewHolder.imageView.setLayoutParams(linearParams);

            LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) viewHolder.llItem.getLayoutParams();
            linearParams1.width = DensityUtil.getScreenWidth(mContext) / 3;
            viewHolder.llItem.setLayoutParams(linearParams1);

            viewHolder.imageView.setImageURI(Uri.parse(dataList.get(i).getSmall_img()));
        }

        @Override
        public int getItemCount() {
            return dataList != null ? dataList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            SimpleDraweeView imageView;
            TextView textView;
            TextView tvName;
            private LinearLayout llItem;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ProductDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("item_id", dataList.get(getPosition()).getItem_id());
                        mContext.startActivity(intent);
                    }
                });
            }
        }

    }


    private void getProductInfo() {
        RequestParams _params = new RequestParams();
        if (_pref.getSessionId().length() != 0)
            _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.item.getItemInfo");
        _params.addBodyParameter("item_id", getIntent().getStringExtra("item_id"));
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.item.getItemInfo&appid=" + Constants.APPID + "&item_id=" + getIntent().getStringExtra("item_id") + Constants.PRIVATE_KEY;
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
                        mDetailsBean = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), DetailsBean.class);
                        initData();

                        getMerchantInfo();
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

    private void initData() {
        //此处解决弱智后台开发ImageView url前缀时有时无的问题！！！！
        if (!mDetailsBean.getBase_pic().contains("http"))
            ivLogo.setImageURI(Uri.parse(Constants.IMAGE_URL + mDetailsBean.getBase_pic()));
        else
            ivLogo.setImageURI(Uri.parse(mDetailsBean.getBase_pic()));
        if(mDetailsBean.getIs_collected()==1){
            btnFav.setText("已收藏");
            btnFav.setEnabled(false);
        }

        tvName.setText(mDetailsBean.getItem_name());
        tvWholePrice.setText("￥" + mDetailsBean.getWholesale_price());
        tvSalePrice.setText("售价￥" + mDetailsBean.getMall_price());
        if (mDetailsBean.getSkus() == null) {
            llColor.setVisibility(View.GONE);
            llSize.setVisibility(View.GONE);
        } else if (mDetailsBean.getSkus().getSku_name1().length() != 0 && mDetailsBean.getSkus().getSku_name2().length() == 0) {
            llSize.setVisibility(View.GONE);
            tvColor.setText(mDetailsBean.getSkus().getSku_name1());
            String colors[] = new String[mDetailsBean.getSkus().getSku_info().length];
            for (int i = 0; i < mDetailsBean.getSkus().getSku_info().length; i++) {
                colors[i] = mDetailsBean.getSkus().getSku_info()[i].getProperty_name1();
            }
            colorflowlayout.setAdapter(new TagAdapter<String>(colors) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.label_tv,
                            colorflowlayout, false);
                    tv.setText(s);
                    return tv;
                }
            });
        } else {
            tvColor.setText(mDetailsBean.getSkus().getSku_name1());
            colors = new String[mDetailsBean.getSkus().getSku_info().length];
            for (int i = 0; i < mDetailsBean.getSkus().getSku_info().length; i++) {
                colors[i] = mDetailsBean.getSkus().getSku_info()[i].getProperty_name1();
            }
            colors = array_unique(colors);
            colorflowlayout.setAdapter(new TagAdapter<String>(colors) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.label_tv,
                            colorflowlayout, false);
                    tv.setText(s);
                    return tv;
                }
            });

            tvSize.setText(mDetailsBean.getSkus().getSku_name2());

            final ArrayList<String> list = new ArrayList<String>();
            for (int i = 0; i < mDetailsBean.getSkus().getSku_info().length; i++) {
                if (mDetailsBean.getSkus().getSku_info()[i].getProperty_name1().equals(colors[0]))
                    list.add(mDetailsBean.getSkus().getSku_info()[i].getProperty_name2());
            }
            String sizes[] = (String[]) list.toArray(new String[list.size()]);

            sizes = array_unique(sizes);
            sizeflowlayout.setAdapter(new TagAdapter<String>(sizes) {
                @Override
                public View getView(FlowLayout parent, int position, String s) {
                    TextView tv = (TextView) mInflater.inflate(R.layout.label_tv,
                            sizeflowlayout, false);
                    tv.setText(s);
                    return tv;
                }
            });

            colorflowlayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
                @Override
                public boolean onTagClick(View view, int i, FlowLayout flowLayout) {
                    list.clear();
                    for (int j = 0; j < mDetailsBean.getSkus().getSku_info().length; j++) {
                        if (mDetailsBean.getSkus().getSku_info()[j].getProperty_name1().equals(colors[i]))
                            list.add(mDetailsBean.getSkus().getSku_info()[j].getProperty_name2());
                    }
                    String sizes[] = (String[]) list.toArray(new String[list.size()]);

                    sizes = array_unique(sizes);
                    sizeflowlayout.setAdapter(new TagAdapter<String>(sizes) {
                        @Override
                        public View getView(FlowLayout parent, int position, String s) {
                            TextView tv = (TextView) mInflater.inflate(R.layout.label_tv,
                                    sizeflowlayout, false);
                            tv.setText(s);
                            return tv;
                        }
                    });
                    return false;
                }
            });

        }
    }

    public static String[] array_unique(String[] a) {
        Set<String> set = new HashSet<String>();
        set.addAll(Arrays.asList(a));
        return set.toArray(new String[0]);
    }

    private void concernProduct() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.collect.collectItem");
        _params.addBodyParameter("item_id", getIntent().getStringExtra("item_id"));
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.collect.collectItem&appid=" + Constants.APPID + "&item_id=" + getIntent().getStringExtra("item_id") + Constants.PRIVATE_KEY;
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
                        btnFav.setEnabled(false);
                        btnFav.setText("已收藏");
                        ToastUtil.showToast(ProductDetailActivity.this, jsonObject.getString("data"));
                    } else {
                        ToastUtil.showToast(ProductDetailActivity.this, jsonObject.getString("error_msg"));
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


    private void getMerchantInfo() {
        RequestParams _params = new RequestParams();
        //_params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.merchant.getMerchantInfo");
        _params.addBodyParameter("merchant_id", mDetailsBean.getMerchant_id());
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

                        tvAddress.setText(mMerchantBean.getAddress());
                        tvSevice.setText(mMerchantBean.getService_type());
                        tvTaobao.setText(mMerchantBean.getWangwangid());
                        tvQQ.setText(mMerchantBean.getQq());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        String date = sdf.format(new Date(Long.valueOf(mMerchantBean.getReg_time()) * 1000));
                        tvTime.setText(date);
                        ratingBar.setNumStars(Integer.valueOf(mMerchantBean.getTaobao_level()));
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

    private void uploadTaobao() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.item.uploadTaobao");
        _params.addBodyParameter("item_id", getIntent().getStringExtra("item_id"));
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.item.uploadTaobao&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        if (jsonObject.getString("data").equals("1"))
                            ToastUtil.showToast(ProductDetailActivity.this, "上传成功");
                        else
                            ToastUtil.showToast(ProductDetailActivity.this, "该物品已上架,不用重复上架.");
                    } else {
                        ToastUtil.showToast(ProductDetailActivity.this, jsonObject.getString("error_msg"));
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


    private void getLikeList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.item.getItemList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("orderby", "GUESS_LIKE");
        String signStr = "api_name=koudai.item.getItemList&appid=" + Constants.APPID + "&orderby=GUESS_LIKE" + Constants.PRIVATE_KEY;
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
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("item_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            newList.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), ProductBean.class));
                        }
                        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(ProductDetailActivity.this, newList);
                        recyclerView.setAdapter(recyclerViewAdapter);

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

    private void getWebView() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.item.getItemDetail");
        _params.addBodyParameter("item_id", getIntent().getStringExtra("item_id"));
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.item.getItemInfo&appid=" + Constants.APPID + "&item_id=" + getIntent().getStringExtra("item_id") + Constants.PRIVATE_KEY;
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
                        String encoding = "UTF-8";
                        String mimeType = "text/html";
                        String html = jsonObject.getString("data");
                        ysnowswebview.loadDataWithBaseURL("file://", html, mimeType, encoding, "about:blank");
                        WebSettings settings = ysnowswebview.getSettings();
                        settings.setJavaScriptEnabled(true);
                        ysnowswebview.setWebViewClient(new WebViewClient());
                        ysnowswebview.getSettings().setLoadWithOverviewMode(true);
                        ysnowswebview.getSettings().setUseWideViewPort(true);
                        ysnowswebview.getSettings().setSupportZoom(true);
                        ysnowswebview.getSettings().setUseWideViewPort(true);
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
