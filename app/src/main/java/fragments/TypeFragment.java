package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import activity.LoginActivity;
import activity.MessageActivity;
import activity.SearchActivity;
import bean.TypeBean;
import type.Fragment_pro_type;
import utils.MD5;
import utils.PreferenceUtil;
import yd.koudd.BaseFragment;
import yd.koudd.Constants;
import yd.koudd.MyApplication;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/20.
 * qq:756350775
 */
public class TypeFragment extends BaseFragment {
    private String toolsList[];
    private TextView toolsTextViews[];
    private View views[];
    @ViewInject(R.id.tools_scrlllview)
    private ScrollView scrollView;
    private int scrllViewWidth = 0, scrollViewMiddle = 0;
    @ViewInject(R.id.goods_pager)
    private ViewPager shop_pager;
    private int currentItem = 0;
    private ShopAdapter shopAdapter;
    @ViewInject(R.id.tools)
    private LinearLayout toolsLayout;


    private ArrayList<TypeBean> typeBeanArrayList = new ArrayList<TypeBean>();

    @ViewInject(R.id.tvNumber)
    private TextView tvNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.fragment_type, null);
        ViewUtils.inject(this, _view);

        getItemList();

        initNoticeView();

        return _view;
    }

    @OnClick({R.id.ivMessage, R.id.llSearch})
    private void clickEvent(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.llSearch:
                intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.ivMessage:
                if (PreferenceUtil.getInstance(getActivity()).getLoginStatus()) {
                    intent = new Intent(getActivity(), MessageActivity.class);
                    startActivity(intent);
                } else {
                    intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    private void initNoticeView() {
        if (MyApplication.notice_num == 0) {
            tvNumber.setVisibility(View.GONE);
        } else {
            tvNumber.setVisibility(View.VISIBLE);
            tvNumber.setText(MyApplication.notice_num + "");
        }
    }

    /**
     * 动态生成显示items中的textview
     */
    private void showToolsView() {
        toolsList = new String[typeBeanArrayList.size()];
        for (int i = 0; i < typeBeanArrayList.size(); i++) {
            toolsList[i] = typeBeanArrayList.get(i).getClass_name();
        }
        toolsTextViews = new TextView[toolsList.length];
        views = new View[toolsList.length];

        for (int i = 0; i < toolsList.length; i++) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_b_top_nav_layout, null);
            view.setId(i);
            view.setOnClickListener(toolsItemListener);
            TextView textView = (TextView) view.findViewById(R.id.text);
            textView.setText(toolsList[i]);
            toolsLayout.addView(view);
            toolsTextViews[i] = textView;
            views[i] = view;
        }
        changeTextColor(0);
    }

    private View.OnClickListener toolsItemListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            shop_pager.setCurrentItem(v.getId());
        }
    };


    /**
     * initPager<br/>
     * 初始化ViewPager控件相关内容
     */
    private void initPager() {
        shop_pager.setAdapter(shopAdapter);
        shop_pager.setOnPageChangeListener(onPageChangeListener);
    }

    /**
     * OnPageChangeListener<br/>
     * 监听ViewPager选项卡变化事的事件
     */

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0) {
            if (shop_pager.getCurrentItem() != arg0) shop_pager.setCurrentItem(arg0);
            if (currentItem != arg0) {
                changeTextColor(arg0);
                changeTextLocation(arg0);
            }
            currentItem = arg0;
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    /**
     * ViewPager 加载选项卡
     *
     * @author Administrator
     */
    private class ShopAdapter extends FragmentPagerAdapter {
        public ShopAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            Fragment fragment = new Fragment_pro_type();
            Bundle bundle = new Bundle();
            String str = toolsList[arg0];
            bundle.putString("typename", str);
            bundle.putSerializable("type", typeBeanArrayList.get(arg0));
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return toolsList.length;
        }
    }


    /**
     * 改变textView的颜色
     *
     * @param id
     */
    private void changeTextColor(int id) {
        for (int i = 0; i < toolsTextViews.length; i++) {
            if (i != id) {
                toolsTextViews[i].setBackgroundResource(android.R.color.transparent);
                toolsTextViews[i].setTextColor(0xff000000);
            }
        }
        toolsTextViews[id].setBackgroundResource(R.mipmap.ic_type_bg);
        toolsTextViews[id].setTextColor(0xff00B6E8);
    }


    /**
     * 改变栏目位置
     *
     * @param clickPosition
     */
    private void changeTextLocation(int clickPosition) {

        int x = (views[clickPosition].getTop() - getScrollViewMiddle() + (getViewheight(views[clickPosition]) / 2));
        scrollView.smoothScrollTo(0, x);
    }

    /**
     * 返回scrollview的中间位置
     *
     * @return
     */
    private int getScrollViewMiddle() {
        if (scrollViewMiddle == 0)
            scrollViewMiddle = getScrollViewheight() / 2;
        return scrollViewMiddle;
    }

    /**
     * 返回ScrollView的宽度
     *
     * @return
     */
    private int getScrollViewheight() {
        if (scrllViewWidth == 0)
            scrllViewWidth = scrollView.getBottom() - scrollView.getTop();
        return scrllViewWidth;
    }

    /**
     * 返回view的宽度
     *
     * @param view
     * @return
     */
    private int getViewheight(View view) {
        return view.getBottom() - view.getTop();
    }


    private void getItemList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.category.getCategoryList");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.category.getCategoryList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        typeBeanArrayList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            typeBeanArrayList.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), TypeBean.class));
                        }
                        shopAdapter = new ShopAdapter(getChildFragmentManager());
                        showToolsView();
                        initPager();
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

