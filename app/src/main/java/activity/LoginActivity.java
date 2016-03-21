package activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.sdk.android.AlibabaSDK;
import com.alibaba.sdk.android.callback.CallbackContext;
import com.alibaba.sdk.android.login.LoginService;
import com.alibaba.sdk.android.login.callback.LoginCallback;
import com.alibaba.sdk.android.session.model.Session;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import utils.DoubleClickUtils;
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
public class LoginActivity extends BaseActivity {
    @ViewInject(R.id.etPhone)
    private EditText etPhone;
    @ViewInject(R.id.etCode)
    private EditText etCode;
    @ViewInject(R.id.btnCode)
    private TextView btnCode;
    private MyCount myCount;
    PreferenceUtil _pref;
    @ViewInject(R.id.ivTaobao)
    private ImageView ivTaobao;
    Session mSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ViewUtils.inject(this);
        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("登录");

        _pref = PreferenceUtil.getInstance(this);
        MyApplication.registList.add(this);
    }

    @OnClick({R.id.btnCode, R.id.btnLogin, R.id.ivTaobao})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.btnCode:
                checkPhone();
                break;
            case R.id.btnLogin:
                ckeckCode();
                break;
            case R.id.ivTaobao:
                if (DoubleClickUtils.isFastClick())
                    showLogin(ivTaobao);
                break;
        }
    }

    private boolean checkPhone() {
        if (etPhone.getText().toString().length() == 0) {
            ToastUtil.showToast(LoginActivity.this, "请输入手机号码");
            return false;
        }
        if (etPhone.getText().toString().length() != 11) {
            ToastUtil.showToast(LoginActivity.this, "手机号码必须为11位");
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
            ToastUtil.showToast(LoginActivity.this, "请输入验证码");
            return false;
        }
        if (etCode.getText().toString().length() != 6) {
            ToastUtil.showToast(LoginActivity.this, "验证码不正确");
            return false;
        }
        checkVerifyCodeValid();
        return true;
    }


    /**
     * 发送验证码
     */
    private void sendCode() {
        RequestParams _params = new RequestParams();
        //_params.addBodyParameter("PHPSESSID", "");
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
                            checkMobileRegistered();
                        else
                            ToastUtil.showToast(LoginActivity.this, "验证码错误");
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
     * 验证手机是否已注册
     */
    private void checkMobileRegistered() {
        RequestParams _params = new RequestParams();
        //_params.addBodyParameter("PHPSESSID", "");
        _params.addBodyParameter("api_name", "koudai.user.checkMobileRegistered");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("mobile", etPhone.getText().toString());
        String signStr = "PHPSESSID=&api_name=koudai.user.checkMobileRegistered&appid=" + Constants.APPID + "&mobile=" + etPhone.getText().toString() + Constants.PRIVATE_KEY;
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
                        String data = jsonObject.getString("data");
                        if (data.equals("1")) {
                            login();
                        } else {
                            Reg();
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

    /**
     * 注册
     */
    private void Reg() {
        RequestParams _params = new RequestParams();
        //_params.addBodyParameter("PHPSESSID", "");
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

                        _pref.setMobile(etPhone.getText().toString());
                        JSONObject resultObject = jsonObject.getJSONObject("data");
                        ToastUtil.showToast(LoginActivity.this, "注册成功!");
                        PreferenceUtil _pref = PreferenceUtil.getInstance(LoginActivity.this);
                        if (resultObject.has("user_id"))
                            _pref.setUserid(resultObject.getString("user_id"));
                        if (resultObject.has("system_money_name"))
                            _pref.setSystemMoneyName(resultObject.getString("system_money_name"));
                        if (resultObject.has("password"))
                            _pref.setPassword(resultObject.getString("password"));
                        finish();
                        EventBus.getDefault().post("regist");
                        _pref.setLoginStatus(true);
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
     * 登录
     */
    private void login() {
        RequestParams _params = new RequestParams();
        //_params.addBodyParameter("PHPSESSID", "");
        _params.addBodyParameter("api_name", "koudai.user.signin");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("mobile", etPhone.getText().toString());
        _params.addBodyParameter("verify_code", etCode.getText().toString());
        String signStr = "PHPSESSID=&api_name=koudai.user.signin&appid=" + Constants.APPID + "&mobile=" + etPhone.getText().toString() + "&verify_code=" + etCode.getText().toString() + Constants.PRIVATE_KEY;
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

                        JSONObject resultObject = jsonObject.getJSONObject("data");
                        PreferenceUtil _pref = PreferenceUtil.getInstance(LoginActivity.this);
                        _pref.setMobile(etPhone.getText().toString());
                        if (resultObject.has("user_id"))
                            _pref.setUserid(resultObject.getString("user_id"));
                        if (resultObject.has("system_money_name"))
                            _pref.setSystemMoneyName(resultObject.getString("system_money_name"));
                        if (resultObject.has("password"))
                            _pref.setPassword(resultObject.getString("password"));
                        finish();
                        EventBus.getDefault().post("regist");
                        _pref.setLoginStatus(true);
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


    //*************************************淘宝登录
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackContext.onActivityResult(requestCode, resultCode, data);
    }

    public void showLogin(View view) {
        //调用getService方法来获取服务
        final LoginService loginService = AlibabaSDK.getService(LoginService.class);
        loginService.showLogin(LoginActivity.this, new LoginCallback() {
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
                taoBaoLogin();
            }

            @Override
            public void onFailure(int code, String message) {
                Toast.makeText(LoginActivity.this, "授权取消" + code + message,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void taoBaoLogin() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.user.taobaoLogin");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("taobao_user_nick", mSession.getUser().nick);
        _params.addBodyParameter("taobao_user_id", mSession.getUserId());
        _params.addBodyParameter("access_token", mSession.getAuthorizationCode());
        String signStr = "PHPSESSID=&api_name=koudai.user.taobaoLogin&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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

                        PreferenceUtil _pref = PreferenceUtil.getInstance(LoginActivity.this);
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
                        if (jsonObject1.getString("taobao_user_id").length() != 0)
                            _pref.setTaoBaoId(jsonObject1.getString("taobao_user_id"));
                        if (jsonObject1.getString("mobile").length() == 0) {
                            Intent intent = new Intent(LoginActivity.this, BindTaoBaoActivity.class);
                            startActivity(intent);
                            EventBus.getDefault().post("regist");
                        } else {
                            _pref.setMobile(jsonObject1.getString("mobile"));
                            LoginActivity.this.finish();
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
}
