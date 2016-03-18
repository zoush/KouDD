package activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import bean.ImageBean;
import yd.koudd.BaseActivity;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/2/22.
 * qq:756350775
 */
public class WebViewActivity extends BaseActivity {
    @ViewInject(R.id.webview)
    private WebView webview;
    ImageBean bean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bean = (ImageBean) getIntent().getSerializableExtra("image");
        getTitleActionBar().setTitle(bean.getTitle());
        initView();
    }

    private void initView() {
        webview.loadUrl(bean.getLink());
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setSupportZoom(true);
        webview.getSettings().setUseWideViewPort(true);
        //webview.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webview.canGoBack()) {
                webview.goBack();//返回上一页面
                return true;
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
