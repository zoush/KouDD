package activity;

import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;

import yd.koudd.BaseActivity;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/22.
 * qq:756350775
 */
public class ModifyPasswordActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);

        ViewUtils.inject(this);
        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("忘记密码");
    }
}
