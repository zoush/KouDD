package activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
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
import com.viewpagerindicator.TabPageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import bean.FloorBean;
import bean.MarketBean;
import bean.ProductBean;
import fragments.diect.ItemFragment;
import utils.DensityUtil;
import utils.MD5;
import widgh.AutoHeightViewPager;
import widgh.FullyLinearLayoutManager;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/2/3.
 * qq:756350775
 */
public class DiectDetailsActivity extends BaseActivity {
    @ViewInject(R.id.recyclerview)
    private RecyclerView recyclerView;
    MarketBean mMarketBean;
    @ViewInject(R.id.tvTitle)
    private TextView tvTitle;
    @ViewInject(R.id.tvDistance)
    private TextView tvDistance;
    @ViewInject(R.id.tvAddress)
    private TextView tvAddress;
    @ViewInject(R.id.imageView)
    private SimpleDraweeView imageView;
    private List<ProductBean> newList = new ArrayList<ProductBean>();

    private List<FloorBean> floorList = new ArrayList<FloorBean>();

    @ViewInject(R.id.indicator)
    private TabPageIndicator indicator;
    @ViewInject(R.id.pager)
    private AutoHeightViewPager pager;
    /**
     * Tab标题
     */
    private String[] TITLE = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diect_details);

        ViewUtils.inject(this);

        mMarketBean = (MarketBean) getIntent().getSerializableExtra("market");

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle(mMarketBean.getMarket_name());
        tvTitle.setText(mMarketBean.getMarket_name());
        DecimalFormat df = new DecimalFormat("0.00");
        tvDistance.setText(df.format(Double.valueOf(mMarketBean.getDistance()) / 1000) + "km");
        tvAddress.setText("地址：" + mMarketBean.getAddress());
        imageView.setImageURI(Uri.parse(Constants.IMAGE_URL + mMarketBean.getImg_path()));

        initNewData();

        getProductList();

        getFloorList();
    }

    @OnClick({R.id.llMore})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.llMore:
                Intent intent = new Intent(DiectDetailsActivity.this, ProductListActivity.class);
                intent.putExtra("title", "市场热卖");
                intent.putExtra("market_id", mMarketBean.getMarket_id());
                startActivity(intent);
                break;
        }
    }

    private void initViewPager() {
        FragmentPagerAdapter adapter = new TabPageIndicatorAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);
        indicator.setVisibility(View.VISIBLE);
        //如果我们要对ViewPager设置监听，用indicator设置就行了
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                //Toast.makeText(getApplicationContext(), TITLE[arg0], Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private void initNewData() {
        LinearLayoutManager linearLayoutManager = new FullyLinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private LayoutInflater mInflater;
        private Context mContext;
        public List<ProductBean> dataList;

        public RecyclerViewAdapter(Context context, List<ProductBean> dataList) {
            mInflater = LayoutInflater.from(context);
            this.dataList = dataList;
            this.mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.item_recycleview,
                    viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(view);

            viewHolder.imageView = (SimpleDraweeView) view.findViewById(R.id.imageView);

            viewHolder.textView = (TextView) view.findViewById(R.id.textView);
            viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
            viewHolder.llItem = (LinearLayout) view.findViewById(R.id.llItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.imageView.setImageResource(R.mipmap.ic_p1);
            viewHolder.textView.setText("￥ " + dataList.get(i).getMall_price());
            viewHolder.tvName.setText(dataList.get(i).getItem_name());

            LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.imageView.getLayoutParams();
            linearParams.width = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 30)) / 3;
            linearParams.height = linearParams.width;
            viewHolder.imageView.setLayoutParams(linearParams);

            LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) viewHolder.llItem.getLayoutParams();
            linearParams1.width = DensityUtil.getScreenWidth(mContext) / 3;
            viewHolder.llItem.setLayoutParams(linearParams1);

            viewHolder.imageView.setImageURI(Uri.parse(dataList.get(i).getSmall_img()));
        }

        @Override
        public int getItemCount() {
            return dataList != null ? dataList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            SimpleDraweeView imageView;
            TextView textView;
            TextView tvName;
            private LinearLayout llItem;

            public ViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, ProductDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("item_id", dataList.get(getPosition()).getItem_id());
                        mContext.startActivity(intent);
                    }
                });
            }
        }

    }


    private void getFloorList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.market.getFloorList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("market_id", mMarketBean.getMarket_id());
        String signStr = "api_name=koudai.market.getFloorList&appid=" + Constants.APPID + "&market_id=" + mMarketBean.getMarket_id() + Constants.PRIVATE_KEY;
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
                        for (int i = 0; i < jsonArray.length(); i++) {
                            floorList.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), FloorBean.class));
                        }
                        if (floorList.size() != 0) {
                            TITLE = new String[floorList.size()];
                            for (int i = 0; i < floorList.size(); i++) {
                                TITLE[i] = floorList.get(i).getFloor_name();
                            }
                        }
                        initViewPager();
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

    private void getProductList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("api_name", "koudai.item.getItemList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("orderby", "HOT");
        _params.addBodyParameter("market_id", mMarketBean.getMarket_id());
        String signStr = "api_name=koudai.user.getItemList&appid=" + Constants.APPID + "&orderby=HOT" + "&merchant_id=" + mMarketBean.getMarket_id() + Constants.PRIVATE_KEY;
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

                        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(DiectDetailsActivity.this, newList);
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
     * ViewPager适配器
     *
     * @author len
     */
    class TabPageIndicatorAdapter extends FragmentPagerAdapter {
        public TabPageIndicatorAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //新建一个Fragment来展示ViewPager item的内容，并传递参数
            Fragment fragment = new ItemFragment();
            Bundle args = new Bundle();
            args.putString("arg", floorList.get(position).getFloor_id());
            fragment.setArguments(args);

            return fragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE[position % TITLE.length];
        }

        @Override
        public int getCount() {
            return TITLE.length;
        }
    }
}
