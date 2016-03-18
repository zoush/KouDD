package fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.pulltorefresh.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import activity.LoginActivity;
import activity.MarketActivity;
import activity.MessageActivity;
import activity.ProductListActivity;
import activity.RankActivity;
import activity.SearchActivity;
import adapter.MyFragmentPagerAdapter;
import adapter.RecyclerViewAdapter;
import bean.ProductBean;
import bean.RankMerchantBean;
import de.greenrobot.event.EventBus;
import fragments.home.LikeFragment;
import fragments.home.RecommondFragment;
import fragments.home.TodayFragment;
import utils.DoubleClickUtils;
import utils.MD5;
import utils.PreferenceUtil;
import widgh.AutoHeightViewPager;
import widgh.FullyLinearLayoutManager;
import yd.koudd.BaseFragment;
import yd.koudd.Constants;
import yd.koudd.MyApplication;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/20.
 * qq:756350775
 */
public class HomeFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener {

    @ViewInject(R.id.recyclerview)
    private RecyclerView recyclerView;
    private List<ProductBean> newList = new ArrayList<ProductBean>();

    @ViewInject(R.id.richRecycleview)
    private RecyclerView richRecycleview;
    private List<RankMerchantBean> richList = new ArrayList<RankMerchantBean>();

    @ViewInject(R.id.llSearch)
    private LinearLayout llSearch;


    @ViewInject(R.id.tvNumber)
    private TextView tvNumber;

    @ViewInject(R.id.tvToday)
    private TextView tvToday;
    @ViewInject(R.id.tvRecommond)
    private TextView tvRecommond;
    @ViewInject(R.id.tvLike)
    private TextView tvLike;

    @ViewInject(R.id.viewpager)
    private AutoHeightViewPager viewpager;
    private ArrayList<Fragment> _listFragemt;
    private TodayFragment todayFragment;
    private RecommondFragment recommondFragment;
    private LikeFragment likeFragment;
    PreferenceUtil _pref;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;
    private int tag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.fragment_home, null);
        ViewUtils.inject(this, _view);

        _pref = PreferenceUtil.getInstance(getActivity());
        //新品
        initNewData();

        //排行榜
        initRichData();

        getNewList();

        getRankList();

        initViewPager();


        getNoticeNum();

        refresh_view.setOnRefreshListener(this);
        //EventBus.getDefault().register(this);
        return _view;
    }

    @OnClick({R.id.llSearch, R.id.tvMore, R.id.tvMoreNew, R.id.ivMessage})
    private void clickEvent(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.llSearch:
                if (DoubleClickUtils.isFastClick()) {
                    intent = new Intent(getActivity(), SearchActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tvMore:
                if (DoubleClickUtils.isFastClick()) {
                    intent = new Intent(getActivity(), RankActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.tvMoreNew:
                if (DoubleClickUtils.isFastClick()) {
                    intent = new Intent(getActivity(), ProductListActivity.class);
                    intent.putExtra("title", "新品首发");
                    intent.putExtra("orderby", "NEW");
                    startActivity(intent);
                }
                break;
            case R.id.ivMessage:
                if (DoubleClickUtils.isFastClick()) {
                    if (PreferenceUtil.getInstance(getActivity()).getLoginStatus()) {
                        intent = new Intent(getActivity(), MessageActivity.class);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                }
                break;
        }
    }

    private void initNewData() {
        LinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void initRichData() {
        LinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        richRecycleview.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                getNewList();

                getRankList();

                initViewPager();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                if (tag == 1) {
                    EventBus.getDefault().post("1load");
                } else if (tag == 2) {
                    EventBus.getDefault().post("2load");
                } else if (tag == 3) {
                    EventBus.getDefault().post("3load");
                }
            }
        }, 2000);
    }

    class RichAdapter extends RecyclerView.Adapter<RichAdapter.ViewHolder> {

        private LayoutInflater mInflater;

        private List<RankMerchantBean> dataList;

        public RichAdapter(Context context, List<RankMerchantBean> dataList) {
            mInflater = LayoutInflater.from(context);
            this.dataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.item_rich_recycleview,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.ivLevel = (ImageView) view.findViewById(R.id.ivLevel);
            viewHolder.tvTaoBaoName = (TextView) view.findViewById(R.id.tvTaoBaoName);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
            viewHolder.tvCount = (TextView) view.findViewById(R.id.tvCount);
            viewHolder.mRatingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            if (i == 0)
                viewHolder.ivLevel.setImageResource(R.mipmap.ic_first);
            else if (i == 1)
                viewHolder.ivLevel.setImageResource(R.mipmap.ic_second);
            else if (i == 2)
                viewHolder.ivLevel.setImageResource(R.mipmap.ic_third);
            viewHolder.tvTaoBaoName.setText(dataList.get(i).getTaobaoid());
            viewHolder.tvName.setText(dataList.get(i).getAddress());
            viewHolder.tvCount.setText(dataList.get(i).getItem_num() + "件商品");
            if (dataList.get(i).getTaobao_level().length() == 0 || Integer.valueOf(dataList.get(i).getTaobao_level()) == 0) {
                viewHolder.mRatingBar.setVisibility(View.GONE);
            } else {
                viewHolder.mRatingBar.setVisibility(View.VISIBLE);
                viewHolder.mRatingBar.setNumStars(Integer.valueOf(dataList.get(i).getTaobao_level()));
            }
        }

        @Override
        public int getItemCount() {
            return dataList != null ? dataList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ImageView ivLevel;
            TextView tvTaoBaoName;
            TextView tvName;
            TextView tvCount;
            RatingBar mRatingBar;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (DoubleClickUtils.isFastClick()) {
                            Intent intent = new Intent(getActivity(), MarketActivity.class);
                            intent.putExtra("merchant_id", dataList.get(getPosition()).getMerchant_id());
                            intent.putExtra("address", dataList.get(getPosition()).getAddress());
                            intent.putExtra("name", dataList.get(getPosition()).getTaobaoid());
                            startActivity(intent);
                        }
                    }
                });
            }
        }

    }

    /**
     * 新品
     */
    private void getNewList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.item.getItemList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("orderby", "NEW");
        String signStr = "api_name=koudai.user.getItemList&appid=" + Constants.APPID + "&orderby=NEW" + Constants.PRIVATE_KEY;
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
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("item_list");
                        newList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            newList.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), ProductBean.class));
                        }
                        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getActivity(), newList);
                        recyclerView.setAdapter(recyclerViewAdapter);
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
     * 排行榜
     */
    private void getRankList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.merchant.getMerchantList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("orderby", "RANK");
        String signStr = "api_name=koudai.merchant.getMerchantList&appid=" + Constants.APPID + "&orderby=RANK" + Constants.PRIVATE_KEY;
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
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("user_list");
                        richList.clear();
                        int length = jsonArray.length() < 3 ? jsonArray.length() : 3;
                        for (int i = 0; i < length; i++) {
                            richList.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), RankMerchantBean.class));
                        }

                        RichAdapter recyclerViewAdapter = new RichAdapter(getActivity(), richList);
                        richRecycleview.setAdapter(recyclerViewAdapter);
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

    private void initViewPager() {
        tvToday.setSelected(true);
        _listFragemt = new ArrayList<Fragment>();
        todayFragment = new TodayFragment();
        recommondFragment = new RecommondFragment();
        likeFragment = new LikeFragment();
        _listFragemt.add(todayFragment);
        _listFragemt.add(recommondFragment);
        _listFragemt.add(likeFragment);

        viewpager.setOffscreenPageLimit(_listFragemt.size());
        viewpager.setAdapter(new MyFragmentPagerAdapter(getChildFragmentManager(), _listFragemt));
        viewpager.setCurrentItem(0);
        viewpager.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            switch (arg0) {
                case 0:
                    tag = 1;
                    tvToday.setSelected(true);
                    tvRecommond.setSelected(false);
                    tvLike.setSelected(false);
                    break;
                case 1:
                    tag = 2;
                    tvToday.setSelected(false);
                    tvRecommond.setSelected(true);
                    tvLike.setSelected(false);
                    break;
                case 2:
                    tag = 3;
                    tvToday.setSelected(false);
                    tvRecommond.setSelected(false);
                    tvLike.setSelected(true);
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

    @OnClick({R.id.tvToday, R.id.tvRecommond, R.id.tvLike})
    private void viewPagerClickEvent(View v) {
        tvRecommond.setSelected(false);
        tvLike.setSelected(false);
        tvToday.setSelected(false);
        ((TextView) v).setSelected(true);
        switch (v.getId()) {
            case R.id.tvToday:
                tag = 1;
                viewpager.setCurrentItem(0);
                tvToday.setSelected(true);
                break;
            case R.id.tvRecommond:
                tag = 2;
                viewpager.setCurrentItem(1);
                tvRecommond.setSelected(true);
                break;
            case R.id.tvLike:
                tag = 3;
                viewpager.setCurrentItem(2);
                tvLike.setSelected(true);
                break;
        }
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

                        if (MyApplication.notice_num == 0) {
                            tvNumber.setVisibility(View.GONE);
                        } else {
                            tvNumber.setVisibility(View.VISIBLE);
                            tvNumber.setText(MyApplication.notice_num + "");
                        }
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
