package activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;

import yd.koudd.BaseActivity;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/22.
 * qq:756350775
 */
public class ForgetPasswordActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgetpassword);
        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("忘记密码");
    }

    @OnClick({R.id.btnNext})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.btnNext:
                Intent intent = new Intent(ForgetPasswordActivity.this, ModifyPasswordActivity.class);
                startActivity(intent);
                break;
        }
    }
}
