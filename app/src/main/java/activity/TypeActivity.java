package activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.util.ArrayList;

import adapter.MyFragmentPagerAdapter;
import de.greenrobot.event.EventBus;
import type.framents.Fragment1;
import type.framents.Fragment2;
import type.framents.Fragment3;
import type.framents.Fragment4;
import yd.koudd.BaseActivity;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/25.
 * qq:756350775
 */
public class TypeActivity extends BaseActivity {
    private int width;
    private int tab_count = 4;
    Animation animation = null;
    private int offset = 0;
    private int position_one = 0;
    private int currIndex = 0;

    @ViewInject(R.id.llLine)
    private RelativeLayout llLine;

    @ViewInject(R.id.viewpager)
    private ViewPager viewpager;
    @ViewInject(R.id.tvRecommond)
    private TextView tvRecommond;
    @ViewInject(R.id.tvTheme)
    private TextView tvTheme;
    @ViewInject(R.id.tvHot)
    private TextView tvHot;
    @ViewInject(R.id.tvAttention)
    private TextView tvAttention;

    private ArrayList<Fragment> _listFragemt;
    private Fragment1 fragment1;
    private Fragment2 fragment2;
    private Fragment3 fragment3;
    private Fragment4 fragment4;
    public static String classId = "";
    public static String sortId = "";
    public static String merchant_class_id = "";
    public static String merchant_sort_id = "";

    @ViewInject(R.id.llPrice)
    private LinearLayout llPrice;
    @ViewInject(R.id.ivArrow)
    private ImageView ivArrow;
    private boolean isDown = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);

        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle(getIntent().getExtras().getString("typename"));

        if (getIntent().hasExtra("class_id"))
            classId = getIntent().getExtras().getString("class_id");
        if (getIntent().hasExtra("sort_id"))
            sortId = getIntent().getExtras().getString("sort_id");
        if (getIntent().hasExtra("merchant_class_id"))
            merchant_class_id = getIntent().getExtras().getString("merchant_class_id");
        if (getIntent().hasExtra("merchant_sort_id"))
            merchant_sort_id = getIntent().getExtras().getString("merchant_sort_id");

        initWidth();
        initView();
        EventBus.getDefault().register(this);
    }

    private void initWidth() {
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        width = (screenWidth / tab_count);

        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) llLine.getLayoutParams();
        linearParams.width = width;
        llLine.setLayoutParams(linearParams);
    }

    private void initView() {
        _listFragemt = new ArrayList<Fragment>();
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        _listFragemt.add(fragment1);
        _listFragemt.add(fragment2);
        _listFragemt.add(fragment3);
        _listFragemt.add(fragment4);

        tvRecommond.setSelected(true);

        viewpager.setOffscreenPageLimit(_listFragemt.size());
        viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), _listFragemt));
        viewpager.setCurrentItem(0);
        viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    tvRecommond.setSelected(true);
                    tvTheme.setSelected(false);
                    tvHot.setSelected(false);
                    tvAttention.setSelected(false);
                    move(currIndex, 0);
                    currIndex = 0;
                    break;
                case 1:
                    tvRecommond.setSelected(false);
                    tvTheme.setSelected(true);
                    tvHot.setSelected(false);
                    tvAttention.setSelected(false);
                    move(currIndex, 1);
                    currIndex = 1;
                    break;
                case 2:
                    tvRecommond.setSelected(false);
                    tvTheme.setSelected(false);
                    tvHot.setSelected(true);
                    tvAttention.setSelected(false);
                    move(currIndex, 2);
                    currIndex = 2;
                    break;
                case 3:
                    tvRecommond.setSelected(false);
                    tvTheme.setSelected(false);
                    tvHot.setSelected(false);
                    tvAttention.setSelected(true);
                    move(currIndex, 3);
                    currIndex = 3;
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

    }

    @OnClick({R.id.tvRecommond, R.id.tvTheme, R.id.tvHot, R.id.tvAttention})
    private void clickEvent(View v) {
        tvRecommond.setSelected(false);
        tvTheme.setSelected(false);
        tvHot.setSelected(false);
        tvAttention.setSelected(false);
        ((TextView) v).setSelected(true);
        switch (v.getId()) {
            case R.id.tvRecommond:
                viewpager.setCurrentItem(0);
                tvRecommond.setSelected(true);
                break;
            case R.id.tvTheme:
                viewpager.setCurrentItem(1);
                tvTheme.setSelected(true);
                break;
            case R.id.tvHot:
                viewpager.setCurrentItem(2);
                tvHot.setSelected(true);
                break;
            case R.id.tvAttention:
                viewpager.setCurrentItem(3);
                tvAttention.setSelected(true);
                if (isDown) {
                    isDown = false;
                    ivArrow.setImageResource(R.mipmap.ic_up_arrow);
                    EventBus.getDefault().post("ace");
                } else {
                    isDown = true;
                    ivArrow.setImageResource(R.mipmap.ic_down_arrow);
                    EventBus.getDefault().post("dese");
                }
                break;
        }
    }

    private void move(int currIndex, int toIndex) {
        position_one = width;
        animation = new TranslateAnimation(position_one * currIndex, position_one * toIndex, 0, 0);
        animation.setFillAfter(true);
        animation.setDuration(200);
        llLine.startAnimation(animation);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(String msg) {
    }
}
