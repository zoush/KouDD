package activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.Date;

import bean.InfomationBean;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by d on 2016/3/9.
 */
public class ActiveDetailsActivity extends BaseActivity {
    InfomationBean infomationBean;
    @ViewInject(R.id.image)
    private SimpleDraweeView image;
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;
    @ViewInject(R.id.tvTime)
    private TextView tvTime;
    @ViewInject(R.id.tvContent)
    private TextView tvContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_active_details);


        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("店铺动态");

        infomationBean = (InfomationBean) getIntent().getSerializableExtra("info");
        image.setImageURI(Uri.parse(Constants.IMAGE_URL + infomationBean.getImg_path()));
        tvContent.setText(infomationBean.getDescription());
        tvTitle.setText(infomationBean.getTitle());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date = sdf.format(new Date(Long.valueOf(infomationBean.getAddtime()) * 1000));
        tvTime.setText(date);
    }
}
