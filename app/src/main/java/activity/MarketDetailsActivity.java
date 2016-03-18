package activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import bean.MerchantBean;
import utils.DoubleClickUtils;
import utils.MD5;
import utils.PreferenceUtil;
import utils.ToastUtil;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by d on 2016/3/9.
 */
public class MarketDetailsActivity extends BaseActivity {
    MerchantBean mMerchantBean;
    @ViewInject(R.id.tvName)
    private TextView tvName;
    @ViewInject(R.id.tvAddress)
    private TextView tvAddress;
    @ViewInject(R.id.tvMain)
    private TextView tvMain;
    @ViewInject(R.id.tvPhone)
    private TextView tvPhone;
    @ViewInject(R.id.tvQQ)
    private TextView tvQQ;
    @ViewInject(R.id.tvTime)
    private TextView tvTime;
    @ViewInject(R.id.tvCount)
    private TextView tvCount;
    PreferenceUtil _pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market_details);


        ViewUtils.inject(this);

        _pref = PreferenceUtil.getInstance(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("店铺详情");

        mMerchantBean = (MerchantBean) getIntent().getSerializableExtra("merchant");
        tvName.setText(mMerchantBean.getWangwangid());
        tvAddress.setText(mMerchantBean.getAddress());
        tvQQ.setText(mMerchantBean.getQq());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date(Long.valueOf(mMerchantBean.getReg_time()) * 1000));
        tvTime.setText(date);
        tvMain.setText(mMerchantBean.getClass_name());
        tvPhone.setText(mMerchantBean.getMobile());
        tvCount.setText(mMerchantBean.getItem_num() + "件");

    }

    @OnClick({R.id.btnFav})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.btnFav:
                if (DoubleClickUtils.isFastClick())
                    collectMerchant();
                break;
        }
    }


    private void collectMerchant() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.collect.collectShop");
        _params.addBodyParameter("merchant_id", getIntent().getStringExtra("merchant_id"));
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
                        ToastUtil.showToast(MarketDetailsActivity.this, jsonObject.getString("data"));
                    } else {
                        ToastUtil.showToast(MarketDetailsActivity.this, jsonObject.getString("error_msg"));
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
