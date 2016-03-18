package widgh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import activity.WebViewActivity;
import bean.ImageBean;
import utils.DoubleClickUtils;
import utils.MD5;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/2/22.
 * qq:756350775
 */
public class SlideShowView extends FrameLayout {

    // 轮播图图片数量
    private final static int IMAGE_COUNT = 5;
    // 自动轮播的时间间隔
    private final static int TIME_INTERVAL = 5;
    // 自动轮播启动开关
    private final static boolean isAutoPlay = true;
    // 自定义轮播图的资源
    private String[] imageUrls;
    // 放轮播图片的ImageView 的list
    private List<SimpleDraweeView> imageViewsList;
    // 放圆点的View的list
    private List<View> dotViewsList;
    private ViewPager viewPager;
    private int currentItem = 0;
    // 定时任务
    private ScheduledExecutorService scheduledExecutorService;
    private Context context;
    TextView tvTitle;
    public static List<ImageBean> imageList = new ArrayList<ImageBean>();

    // Handler
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            viewPager.setCurrentItem(currentItem);
        }
    };

    public SlideShowView(Context context) {
        this(context, null);
        this.context = context;
        // TODO Auto-generated constructor stub
    }

    public SlideShowView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        this.context = context;
        initData();
        if (isAutoPlay) {
            startPlay();
        }
    }

    public SlideShowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        initData();
        if (isAutoPlay) {
            startPlay();
        }
    }

    /**
     * 开始轮播图切换
     */
    private void startPlay() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 1, 4,
                TimeUnit.SECONDS);
    }

    /**
     * 停止轮播图切换
     */
    private void stopPlay() {
        scheduledExecutorService.shutdown();
    }

    /**
     * 初始化相关Data
     */
    private void initData() {
        getImage();
    }

    private void getImage() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.mall.getMallHomeImgList");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.mall.getMallHomeImgList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        JSONArray jsonoArray = jsonObject.getJSONArray("data");
                        imageList.clear();
                        for (int i = 0; i < jsonoArray.length(); i++) {
                            imageList.add(new Gson().fromJson(jsonoArray.getJSONObject(i).toString(), ImageBean.class));
                        }
                        imageViewsList = new ArrayList<SimpleDraweeView>();
                        dotViewsList = new ArrayList<View>();
                        imageUrls = new String[imageList.size()];
                        for (int i = 0, j = imageList.size(); i < j; i++) {
                            imageUrls[i] = imageList.get(i).getPic();
                        }
                        initUI();
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
     * 初始化Views等UI
     */
    private void initUI() {
        if (imageUrls == null || imageUrls.length == 0)
            return;
        LayoutInflater.from(context).inflate(R.layout.layout_slideshow, this,
                true);
        LinearLayout dotLayout = (LinearLayout) findViewById(R.id.dotLayout);
        dotLayout.removeAllViews();
        for (int _i = 0, _l = imageUrls.length; _i < _l; _i++) {
            SimpleDraweeView view = new SimpleDraweeView(context);
            GenericDraweeHierarchy _hierarchy = view.getHierarchy();
            //_hierarchy.setPlaceholderImage(R.drawable.icon_loading);
            _hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            //view.setScaleType(ImageView.ScaleType.FIT_XY);
            imageViewsList.add(view);
            ImageView dotView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    12, 12);
            params.leftMargin = 4;
            params.rightMargin = 4;
            dotLayout.addView(dotView, params);
            dotViewsList.add(dotView);
        }
        tvTitle = (TextView) findViewById(R.id.tvTitle);


        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setFocusable(true);
        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new MyPageChangeListener());
    }

    /**
     * 填充ViewPager的页面适配器
     */
    private class MyPagerAdapter extends PagerAdapter {

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            // TODO Auto-generated method stub
            container.removeView(imageViewsList.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            SimpleDraweeView imageView = imageViewsList.get(position);
            container.addView(imageView);
            imageView.setImageURI(Uri.parse(imageUrls[position]));
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(DoubleClickUtils.isFastClick()) {
                        Intent intent = new Intent(context, WebViewActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("image", imageList.get(position));
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        ((Activity) context).overridePendingTransition(R.anim.push_in_from_right, R.anim.hold);
                    }
                }
            });
            return imageView;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return imageViewsList.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            // TODO Auto-generated method stub
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
            // TODO Auto-generated method stub

        }

        @Override
        public Parcelable saveState() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void finishUpdate(View arg0) {
            // TODO Auto-generated method stub

        }

    }

    /**
     * ViewPager�ļ����� ��ViewPager��ҳ���״̬����ı�ʱ����
     */
    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        boolean isAutoPlay = false;

        @Override
        public void onPageScrollStateChanged(int arg0) {
            // TODO Auto-generated method stub
            switch (arg0) {
                case 1:// 手势滑动，空闲中
                    isAutoPlay = false;
                    break;
                case 2:// 界面切换中
                    isAutoPlay = true;
                    break;
                case 0:// 滑动结束，即切换完毕或者加载完毕
                    // 当前为最后一张，此时从右向左滑，则切换到第一张
                    if (viewPager.getCurrentItem() == viewPager.getAdapter()
                            .getCount() - 1 && !isAutoPlay) {
                        viewPager.setCurrentItem(0);
                    }
                    // 当前为第一张，此时从左向右滑，则切换到最后一张
                    else if (viewPager.getCurrentItem() == 0 && !isAutoPlay) {
                        viewPager
                                .setCurrentItem(viewPager.getAdapter().getCount() - 1);
                    }
                    break;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onPageSelected(int pos) {
            // TODO Auto-generated method stub

            currentItem = pos;
            for (int i = 0; i < dotViewsList.size(); i++) {
                if (i == pos) {
                    ((View) dotViewsList.get(pos))
                            .setBackgroundResource(R.mipmap.dot_focus);
                } else {
                    ((View) dotViewsList.get(i))
                            .setBackgroundResource(R.mipmap.dot_blur);
                }
            }
            //tvTitle.setText(LearnBallApplication.getNewsList().get(pos).getTitle());
        }

    }

    /**
     * ִ执行轮播图切换任务
     */
    private class SlideShowTask implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            synchronized (viewPager) {
                currentItem = (currentItem + 1) % imageViewsList.size();
                handler.obtainMessage().sendToTarget();
            }
        }

    }
}