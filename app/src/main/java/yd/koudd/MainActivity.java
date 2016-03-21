package yd.koudd;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.apache.http.client.CookieStore;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import fragments.DiectFragment;
import fragments.HomeFragment;
import fragments.MineFragment;
import fragments.TypeFragment;
import utils.MD5;
import utils.PreferenceUtil;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {
    @ViewInject(R.id.llHome)
    private LinearLayout llHome;
    @ViewInject(R.id.llType)
    private LinearLayout llType;
    @ViewInject(R.id.llDiect)
    private LinearLayout llDiect;
    @ViewInject(R.id.llMine)
    private LinearLayout llMine;

    @ViewInject(R.id.ivHome)
    private ImageView ivHome;
    @ViewInject(R.id.ivType)
    private ImageView ivType;
    @ViewInject(R.id.ivDiect)
    private ImageView ivDiect;
    @ViewInject(R.id.ivMine)
    private ImageView ivMine;

    @ViewInject(R.id.tvHome)
    private TextView tvHome;
    @ViewInject(R.id.tyType)
    private TextView tyType;
    @ViewInject(R.id.tvDiect)
    private TextView tvDiect;
    @ViewInject(R.id.tvMine)
    private TextView tvMine;

    @ViewInject(R.id.flContent)
    private FrameLayout flContent;

    private FragmentManager mFragMgr;
    private HomeFragment homeFragment;
    private TypeFragment typeFragment;
    private DiectFragment diectFragment;
    private MineFragment mineFragment;
    PreferenceUtil _pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewUtils.inject(this);

        setViewListener();
        clickHomeBtn();
        mFragMgr = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        showFragment(homeFragment);
        _pref = PreferenceUtil.getInstance(this);

        getNoticeNum();
        if (_pref.getPassword().length() != 0) {
            login();
        }
    }

    private void setViewListener() {

        if (llHome != null)
            llHome.setOnClickListener(this);
        if (llType != null)
            llType.setOnClickListener(this);
        if (llMine != null)
            llMine.setOnClickListener(this);
        if (llDiect != null)
            llDiect.setOnClickListener(this);
    }

    private void clickHomeBtn() {
        switchContent(HomeFragment.class);

        llHome.setSelected(true);
        ivHome.setSelected(true);
        tvHome.setSelected(true);

        llType.setSelected(false);
        ivType.setSelected(false);
        tyType.setSelected(false);

        llDiect.setSelected(false);
        ivDiect.setSelected(false);
        tvDiect.setSelected(false);

        llMine.setSelected(false);
        ivMine.setSelected(false);
        tvMine.setSelected(false);
    }

    private void clickTypeBtn() {
        switchContent(TypeFragment.class);

        llHome.setSelected(false);
        ivHome.setSelected(false);
        tvHome.setSelected(false);

        llType.setSelected(true);
        ivType.setSelected(true);
        tyType.setSelected(true);

        llDiect.setSelected(false);
        ivDiect.setSelected(false);
        tvDiect.setSelected(false);

        llMine.setSelected(false);
        ivMine.setSelected(false);
        tvMine.setSelected(false);
    }

    private void clickDiectBtn() {
        switchContent(DiectFragment.class);

        llHome.setSelected(false);
        ivHome.setSelected(false);
        tvHome.setSelected(false);

        llType.setSelected(false);
        ivType.setSelected(false);
        tyType.setSelected(false);

        llDiect.setSelected(true);
        ivDiect.setSelected(true);
        tvDiect.setSelected(true);

        llMine.setSelected(false);
        ivMine.setSelected(false);
        tvMine.setSelected(false);
    }

    private void clickMineBtn() {
        switchContent(MineFragment.class);

        llHome.setSelected(false);
        ivHome.setSelected(false);
        tvHome.setSelected(false);

        llType.setSelected(false);
        ivType.setSelected(false);
        tyType.setSelected(false);

        llDiect.setSelected(false);
        ivDiect.setSelected(false);
        tvDiect.setSelected(false);

        llMine.setSelected(true);
        ivMine.setSelected(true);
        tvMine.setSelected(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.llHome:
                clickHomeBtn();
                if (homeFragment == null)
                    homeFragment = new HomeFragment();
                showFragment(homeFragment);
                break;
            case R.id.llType:
                clickTypeBtn();
                if (typeFragment == null)
                    typeFragment = new TypeFragment();
                showFragment(typeFragment);
                break;
            case R.id.llDiect:
                clickDiectBtn();
                if (diectFragment == null)
                    diectFragment = new DiectFragment();
                showFragment(diectFragment);
                break;
            case R.id.llMine:
                clickMineBtn();
                if (mineFragment == null)
                    mineFragment = new MineFragment();
                showFragment(mineFragment);
                break;
        }
    }

    private void showFragment(Fragment frag) {
        FragmentTransaction _trans = mFragMgr.beginTransaction();
        List<Fragment> _fragments = mFragMgr.getFragments();
        if (_fragments != null) {
            for (int _i = 0, _l = _fragments.size(); _i < _l; _i++) {
                _trans.hide(_fragments.get(_i));
            }
        }
        if (frag.isAdded()) {
            _trans.show(frag).commit();
        } else {
            _trans.add(R.id.flContent, frag, null);
            _trans.commit();
        }
    }

    private Fragment mContent;

    public void switchContent(Class<? extends Fragment> clazz) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment userFragment = fm.findFragmentByTag(clazz.getName());
        if (userFragment == null) {
            try {
                userFragment = clazz.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // ft.addToBackStack(null);
        if (mContent != null && mContent != userFragment) {
            ft.hide(mContent);
        }

        if (!userFragment.isAdded()) {
            ft.add(R.id.flContent, userFragment, clazz.getName());
        } else {
            ft.show(userFragment);
        }

        ft.commitAllowingStateLoss();
        mContent = userFragment;
    }

    private void getNoticeNum() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.notice.getNoticeNum");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.notice.getNoticeNum&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        MyApplication.notice_num = Integer.valueOf(jsonObject.getJSONObject("data").getString("notice_num"));
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getRepeatCount() == 0) {
                this.openQiutDialog();// 这是自定义的代码
            }
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    private long mExitTime;

    private void openQiutDialog() {
        if ((System.currentTimeMillis() - mExitTime) > 2000) {
            Object mHelperUtils;
            Toast.makeText(this, "再按一次退出程序~", Toast.LENGTH_SHORT).show();
            mExitTime = System.currentTimeMillis();

        } else {
            finish();
        }
    }

    private void login() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.user.signin");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("mobile", _pref.getMobile());
        _params.addBodyParameter("password", _pref.getPassword());
        String signStr = "api_name=koudai.user.signin&appid=" + Constants.APPID + "&mobile=" + _pref.getMobile() + "&password=" + _pref.getPassword() + Constants.PRIVATE_KEY;
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
                        PreferenceUtil _pref = PreferenceUtil.getInstance(MainActivity.this);
                        if (resultObject.has("user_id"))
                            _pref.setUserid(resultObject.getString("user_id"));
                        if (resultObject.has("system_money_name"))
                            _pref.setSystemMoneyName(resultObject.getString("system_money_name"));
                        if (resultObject.has("password"))
                            _pref.setPassword(resultObject.getString("password"));
                        _pref.setLoginStatus(true);

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
                        _pref.setAvatar(jsonObject1.getString("headimgurl"));
                        if (jsonObject1.getString("taobao_user_id").length() != 0)
                            _pref.setTaoBaoId(jsonObject1.getString("taobao_user_id"));
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
