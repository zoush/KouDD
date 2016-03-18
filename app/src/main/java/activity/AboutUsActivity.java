package activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
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

import utils.MD5;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/27.
 * qq:756350775
 */
public class AboutUsActivity extends BaseActivity {
    @ViewInject(R.id.tvPhone)
    private TextView tvPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("关于我们");

        getAboutInfo();

        getPhone();
    }

    @OnClick({R.id.llPhone})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.llPhone:
                new MaterialDialog.Builder(this)
                        .positiveText("拨打")
                        .content(tvPhone.getText().toString())
                        .contentColor(getResources().getColor(R.color.rule_color))
                        .backgroundColor(getResources().getColor(R.color.white))
                        .negativeText("取消")
                        .onAny(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                if (which.name().equals("POSITIVE")) {
                                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + tvPhone.getText().toString()));
                                    startActivity(intent);
                                }
                            }
                        })
                        .show();
                break;
        }
    }

    private void getPhone() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.help.getServicePhone");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.help.getServicePhone&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        tvPhone.setText(jsonObject.getJSONObject("data").getString("service_phone"));
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

    private void getAboutInfo() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.help.getAboutInfo");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.help.getAboutInfo&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
