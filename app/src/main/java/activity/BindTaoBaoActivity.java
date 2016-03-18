package activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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

import de.greenrobot.event.EventBus;
import utils.MD5;
import utils.PreferenceUtil;
import utils.ToastUtil;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.MyApplication;
import yd.koudd.R;

/**
 * Created by d on 2016/3/10.
 */
public class BindTaoBaoActivity extends BaseActivity {
    PreferenceUtil _pref;
    @ViewInject(R.id.etPhone)
    private EditText etPhone;
    @ViewInject(R.id.etCode)
    private EditText etCode;
    @ViewInject(R.id.btnCode)
    private TextView btnCode;
    private MyCount myCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);


        ViewUtils.inject(this);
        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("绑定手机号");

        _pref = PreferenceUtil.getInstance(this);
        MyApplication.registList.add(this);
    }

    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            btnCode.setEnabled(false);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnCode.setText(millisUntilFinished / 1000 + "S");
        }

        @Override
        public void onFinish() {
            btnCode.setEnabled(true);
            btnCode.setText("获取验证码");
            btnCode.setBackgroundResource(R.drawable.blue_rect);
            btnCode.setTextColor(getResources().getColor(R.color.white));
        }
    }

    @OnClick({R.id.btnCode, R.id.btnLogin})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.btnCode:
                checkPhone();
                break;
            case R.id.btnLogin:
                ckeckCode();
                break;
        }
    }

    private boolean checkPhone() {
        if (etPhone.getText().toString().length() == 0) {
            ToastUtil.showToast(BindTaoBaoActivity.this, "请输入手机号码");
            return false;
        }
        if (etPhone.getText().toString().length() != 11) {
            ToastUtil.showToast(BindTaoBaoActivity.this, "手机号码必须为11位");
            return false;
        }
        btnCode.setEnabled(false);
        myCount = new MyCount(60000, 1000);
        myCount.start();
        btnCode.setBackgroundResource(R.drawable.dark_bg);
        btnCode.setTextColor(getResources().getColor(R.color.rule_color));
        sendCode();
        return true;
    }

    /**
     * 发送验证码
     */
    private void sendCode() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", "");
        _params.addBodyParameter("api_name", "koudai.user.sendVerifyCode");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("mobile", etPhone.getText().toString());
        String signStr = "PHPSESSID=&api_name=koudai.user.sendVerifyCode&appid=" + Constants.APPID + "&mobile=" + etPhone.getText().toString() + Constants.PRIVATE_KEY;
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
                        myCount = new MyCount(60000, 1000);
                        myCount.start();
                        btnCode.setBackgroundResource(R.drawable.dark_bg);
                        btnCode.setTextColor(getResources().getColor(R.color.rule_color));
                    } else {
                        btnCode.setEnabled(true);
                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void onFailure(HttpException e, String s) {
                Log.i("sa", s);
                btnCode.setEnabled(true);
            }
        });
    }

    private boolean ckeckCode() {
        if (etCode.getText().toString().length() == 0) {
            ToastUtil.showToast(BindTaoBaoActivity.this, "请输入验证码");
            return false;
        }
        if (etCode.getText().toString().length() != 6) {
            ToastUtil.showToast(BindTaoBaoActivity.this, "验证码不正确");
            return false;
        }
        checkVerifyCodeValid();
        return true;
    }

    /**
     * 检查验证码是否可用
     */
    private void checkVerifyCodeValid() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.user.checkVerifyCodeValid");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("mobile", etPhone.getText().toString());
        _params.addBodyParameter("verify_code", etCode.getText().toString());
        String signStr = "api_name=koudai.user.checkVerifyCodeValid&appid=" + Constants.APPID + "&mobile=" + etPhone.getText().toString() + "&verify_code=" + etCode.getText().toString() + Constants.PRIVATE_KEY;
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
                        if (jsonObject.getString("data").equals("1"))
                            bindPhone();
                        else
                            ToastUtil.showToast(BindTaoBaoActivity.this, "验证码错误");
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

    private void bindPhone() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.user.bindMobile");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("mobile", etPhone.getText().toString());
        _params.addBodyParameter("verify_code", etCode.getText().toString());
        String signStr = "api_name=koudai.user.bindMobile&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        ToastUtil.showToast(BindTaoBaoActivity.this, jsonObject.getString("data"));
                        for (Activity activity : MyApplication.registList) {
                            activity.finish();
                        }
                        EventBus.getDefault().post("regist");
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
