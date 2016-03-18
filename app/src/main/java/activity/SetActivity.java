package activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
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
import utils.DataCleanManager;
import utils.MD5;
import utils.PreferenceUtil;
import utils.ToastUtil;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/28.
 * qq:756350775
 */
public class SetActivity extends BaseActivity {
    @ViewInject(R.id.btnLogout)
    private Button btnLogout;
    PreferenceUtil _pref;
    @ViewInject(R.id.tvCash)
    private TextView tvCash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set);

        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("系统设置");
        _pref = PreferenceUtil.getInstance(this);
        if (_pref.getLoginStatus()) {
            btnLogout.setVisibility(View.VISIBLE);
        } else {
            btnLogout.setVisibility(View.GONE);
        }
        try {
            tvCash.setText(DataCleanManager.getTotalCacheSize(this) + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.llCash, R.id.btnLogout})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.llCash:
                new MaterialDialog.Builder(this)
                        .positiveText("清除")
                        .content("是否清除缓存")
                        .contentColor(getResources().getColor(R.color.rule_color))
                        .negativeText("取消")
                        .backgroundColor(getResources().getColor(R.color.white))
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (which.name().equals("POSITIVE")) {
                                    if (!tvCash.getText().toString().equals("0KB"))
                                        DataCleanManager.clearAllCache(SetActivity.this);
                                    ToastUtil.showToast(SetActivity.this, "清除缓存成功");
                                    tvCash.setText("0KB");
                                }
                            }
                        })
                        .show();
                break;
            case R.id.btnLogout:
                new MaterialDialog.Builder(this)
                        .positiveText("退出")
                        .content("退出登录")
                        .contentColor(getResources().getColor(R.color.rule_color))
                        .negativeText("取消")
                        .backgroundColor(getResources().getColor(R.color.white))
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (which.name().equals("POSITIVE")) {
                                    logout();
                                }
                            }
                        })
                        .show();
                break;
        }
    }

    private void logout() {
        RequestParams _params = new RequestParams();
        _params.addHeader("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.user.logout");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.user.logout&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        _pref.setLoginStatus(false);
                        _pref.setPassword("");
                        _pref.setTaoBaoId("");
                        _pref.setUrl("");
                        _pref.setMobile("");
                        _pref.setTaoBaoLogin(false);
                        //_pref.setTaoBaoId("");
                        finish();
                        EventBus.getDefault().post("logout");
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
    }
}
