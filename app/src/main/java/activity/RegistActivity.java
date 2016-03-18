package activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
 * Created by zoushaohua on 2016/1/22.
 * qq:756350775
 */
public class RegistActivity extends BaseActivity {
    @ViewInject(R.id.etPhone)
    private EditText etPhone;
    @ViewInject(R.id.btnCode)
    private TextView btnCode;
    private MyCount myCount;
    @ViewInject(R.id.etCode)
    private EditText etCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("注册");

        MyApplication.registList.add(this);
        EventBus.getDefault().register(this);
    }

    @OnClick({R.id.btnCode, R.id.btnReg})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.btnCode:
                checkPhone();
                break;
            case R.id.btnReg:
                ckeckCode();
                break;
        }
    }

    private boolean checkPhone() {
        if (etPhone.getText().toString().length() == 0) {
            ToastUtil.showToast(RegistActivity.this, "请输入手机号码");
            return false;
        }
        if (etPhone.getText().toString().length() != 11) {
            ToastUtil.showToast(RegistActivity.this, "手机号码必须为11位");
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

    private boolean ckeckCode() {
        if (etCode.getText().toString().length() == 0) {
            ToastUtil.showToast(RegistActivity.this, "请输入验证码");
            return false;
        }
        if (etCode.getText().toString().length() != 6) {
            ToastUtil.showToast(RegistActivity.this, "验证码不正确");
            return false;
        }
        Reg();
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
            View v = getCurrentFocus();

            if (isShouldHideInput(v, ev)) {
                hideSoftInput(v.getWindowToken());
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    /**
     * 多种隐藏软件盘方法的其中一种
     *
     * @param token
     */
    private void hideSoftInput(IBinder token) {
        if (token != null) {
            InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    class MyCount extends CountDownTimer {
        public MyCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            btnCode.setEnabled(false);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnCode.setText(millisUntilFinished / 1000 + "s后再次获取");
        }

        @Override
        public void onFinish() {
            btnCode.setEnabled(true);
            btnCode.setText("获取验证码");
            btnCode.setBackgroundResource(R.drawable.blue_rect);
            btnCode.setTextColor(getResources().getColor(R.color.white));
        }
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

    /**
     * 检查验证码是否可用
     */
    private void ckeckPhone() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", "");
        _params.addBodyParameter("api_name", "koudai.user.checkVerifyCodeValid");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("mobile", etPhone.getText().toString());
        _params.addBodyParameter("verify_code", etCode.getText().toString());
        String signStr = "PHPSESSID=&api_name=koudai.user.checkVerifyCodeValid&appid=" + Constants.APPID + "&mobile=" + etPhone.getText().toString() + "&verify_code=" + etCode.getText().toString() + Constants.PRIVATE_KEY;
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
                        Reg();
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

    /**
     * 注册
     */
    private void Reg() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", "");
        _params.addBodyParameter("api_name", "koudai.user.signup");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("mobile", etPhone.getText().toString());
        _params.addBodyParameter("verify_code", etCode.getText().toString());
       // _params.addBodyParameter("password","");
        String signStr = "PHPSESSID=&api_name=koudai.user.signup&appid=" + Constants.APPID + "&mobile=" + etPhone.getText().toString() + "&verify_code=" + etCode.getText().toString() + Constants.PRIVATE_KEY;
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
                        for (Activity activity : MyApplication.registList) {
                            activity.finish();
                        }
                        JSONObject resultObject = jsonObject.getJSONObject("data");
                        ToastUtil.showToast(RegistActivity.this, "注册成功!");
                        PreferenceUtil _pref = PreferenceUtil.getInstance(RegistActivity.this);
                        if (resultObject.has("user_id"))
                            _pref.setUserid(resultObject.getString("user_id"));
                        if (resultObject.has("system_money_name"))
                            _pref.setSystemMoneyName(resultObject.getString("system_money_name"));
                        if (resultObject.has("password"))
                            _pref.setPassword(resultObject.getString("password"));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(String msg) {

    }
}
