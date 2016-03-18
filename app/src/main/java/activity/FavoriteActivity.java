package activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
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
import com.pulltorefresh.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bean.CollectProductBean;
import bean.CollectShopBean;
import utils.DensityUtil;
import utils.MD5;
import utils.PreferenceUtil;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/30.
 * qq:756350775
 */
public class FavoriteActivity extends BaseActivity {
    @ViewInject(R.id.listview)
    private SwipeMenuListView listview;
    ShelfAdapter adapter;
    PreferenceUtil _pref;

    @ViewInject(R.id.diectListView)
    private SwipeMenuListView diectListView;
    DiectAdapter diectAdapter;
    @ViewInject(R.id.button1)
    private RadioButton button1;
    @ViewInject(R.id.button2)
    private RadioButton button2;
    @ViewInject(R.id.refresh_view1)
    private PullToRefreshLayout refresh_view1;
    private int page = 0;
    private int size = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favor);

        _pref = PreferenceUtil.getInstance(this);

        ViewUtils.inject(this);

        adapter = new ShelfAdapter(this);
        listview.setAdapter(adapter);

        diectAdapter = new DiectAdapter(this);
        diectListView.setAdapter(diectAdapter);


        getProductList();

        getMerchantList();


        initListView();
        initDiectListView();

        findViewById(R.id.common_title_bar_btn_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        refresh_view1.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        if (listview.getVisibility() == View.VISIBLE) {
                            page = 0;
                            getProductList();
                        } else {
                            size = 0;
                            getMerchantList();
                        }
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                        if (listview.getVisibility() == View.VISIBLE) {
                            page++;
                            getProductList();
                        } else {
                            size++;
                            getMerchantList();
                        }
                    }
                }, 2000);
            }
        });

    }

    @OnClick({R.id.button1, R.id.button2})
    private void clickEvent(View v) {
        switch (v.getId()) {
            case R.id.button1:
                button1.setSelected(true);
                button2.setSelected(false);
                listview.setVisibility(View.VISIBLE);
                diectListView.setVisibility(View.GONE);
                break;
            case R.id.button2:
                button1.setSelected(false);
                button2.setSelected(true);
                listview.setVisibility(View.GONE);
                diectListView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item

                SwipeMenuItem openItem2 = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem2.setBackground(new ColorDrawable(Color.rgb(0xF4, 0x0B,
                        0x1E)));
                // set item width
                openItem2.setWidth(DensityUtil.dip2px(FavoriteActivity.this, 70));
                // set item title
                openItem2.setTitle("取消");
                // set item title fontsize
                openItem2.setTitleSize(18);
                // set item title font color
                openItem2.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem2);
            }
        };

        listview.setMenuCreator(creator);
        listview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int arg2, SwipeMenu menu,
                                           int index) {
                switch (index) {
                    case 0:
                        cancelCollect(adapter._listData.get(arg2).getCollect_id());
                        adapter._listData.remove(arg2);
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoriteActivity.this, ProductDetailActivity.class);
                intent.putExtra("item_id", adapter._listData.get(position).getItem_id());
                startActivity(intent);
            }
        });
    }

    private void initDiectListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item

                SwipeMenuItem openItem2 = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem2.setBackground(new ColorDrawable(Color.rgb(0xF4, 0x0B,
                        0x1E)));
                // set item width
                openItem2.setWidth(DensityUtil.dip2px(FavoriteActivity.this, 70));
                // set item title
                openItem2.setTitle("取消");
                // set item title fontsize
                openItem2.setTitleSize(18);
                // set item title font color
                openItem2.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem2);
            }
        };

        diectListView.setMenuCreator(creator);
        diectListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
        diectListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final int arg2, SwipeMenu menu,
                                           int index) {
                switch (index) {
                    case 0:
                        cancelCollect(diectAdapter._listData.get(arg2).getCollect_id());
                        diectAdapter._listData.remove(arg2);
                        diectAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        diectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(FavoriteActivity.this, MarketActivity.class);
                intent.putExtra("merchant_id", diectAdapter._listData.get(position).getMerchant_id());
                intent.putExtra("name", diectAdapter._listData.get(position).getTaobaoid());
                intent.putExtra("address", diectAdapter._listData.get(position).getAddress());
                startActivity(intent);
            }
        });
    }

    class ShelfAdapter extends BaseAdapter {
        public List<CollectProductBean> _listData;
        private LayoutInflater inflater;

        public ShelfAdapter(Context context) {
            _listData = new ArrayList<CollectProductBean>();
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return _listData == null ? 0 : _listData.size();
        }

        @Override
        public Object getItem(int position) {
            return _listData == null ? null : _listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return _listData == null ? 0 : position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.shelf_item, null);
                holder.image = (SimpleDraweeView) convertView.findViewById(R.id.image);
                holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            CollectProductBean bean = _listData.get(position);
            holder.image.setImageURI(Uri.parse(Constants.IMAGE_URL + bean.getSmall_img()));
            holder.tvPrice.setText("￥" + bean.getMall_price());
            holder.tvAddress.setText(bean.getAddress());
            holder.tvName.setText(bean.getItem_name());
            return convertView;
        }

        private final class ViewHolder {
            private SimpleDraweeView image;
            private TextView tvName;
            private TextView tvPrice;
            private TextView tvAddress;
        }
    }


    class DiectAdapter extends BaseAdapter {
        public List<CollectShopBean> _listData;
        private LayoutInflater inflater;

        public DiectAdapter(Context context) {
            _listData = new ArrayList<CollectShopBean>();
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return _listData == null ? 0 : _listData.size();
        }

        @Override
        public Object getItem(int position) {
            return _listData == null ? null : _listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return _listData == null ? 0 : position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.item_rich_recycleview, null);
                holder.ivLevel = (ImageView) convertView.findViewById(R.id.ivLevel);
                holder.tvTaoBaoName = (TextView) convertView.findViewById(R.id.tvTaoBaoName);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvCount = (TextView) convertView.findViewById(R.id.tvCount);
                holder.mRatingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.ivLevel.setVisibility(View.GONE);
            holder.tvTaoBaoName.setText(_listData.get(position).getTaobaoid());
            holder.tvName.setText(_listData.get(position).getAddress());
            holder.tvCount.setText(_listData.get(position).getItem_num() + "件商品");
            if (_listData.get(position).getTaobao_level().length() == 0 || Integer.valueOf(_listData.get(position).getTaobao_level()) == 0) {
                holder.mRatingBar.setVisibility(View.GONE);
            } else {
                holder.mRatingBar.setVisibility(View.VISIBLE);
                holder.mRatingBar.setNumStars(Integer.valueOf(_listData.get(position).getTaobao_level()));
            }
            return convertView;
        }

        private final class ViewHolder {
            ImageView ivLevel;
            TextView tvTaoBaoName;
            TextView tvName;
            TextView tvCount;
            RatingBar mRatingBar;
        }
    }

    private void getProductList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * page + "");
        _params.addBodyParameter("fetchNum", Constants.FETCH_NUM + "");
        _params.addBodyParameter("api_name", "koudai.collect.getCollectedItemList");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.collect.getCollectedItemList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        final HttpUtils _http = new HttpUtils();
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        if (page == 0)
                            adapter._listData.clear();
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("collect_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            adapter._listData.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), CollectProductBean.class));
                        }
                        adapter.notifyDataSetChanged();
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


    private void getMerchantList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * size + "");
        _params.addBodyParameter("fetchNum", Constants.FETCH_NUM + "");
        _params.addBodyParameter("api_name", "koudai.collect.getCollectedShopList");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.collect.getCollectedShopList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        final HttpUtils _http = new HttpUtils();
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("collect_list");
                        if (size == 0)
                            diectAdapter._listData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            diectAdapter._listData.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), CollectShopBean.class));
                        }
                        diectAdapter.notifyDataSetChanged();
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

    private void cancelCollect(String id) {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.collect.cancelCollect");
        _params.addBodyParameter("collect_id", id);
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.collect.cancelCollect&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        final HttpUtils _http = new HttpUtils();
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
