package activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.pulltorefresh.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bean.CollectProductBean;
import utils.DensityUtil;
import utils.MD5;
import utils.PreferenceUtil;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by d on 2016/1/30.
 * 已上架宝贝
 */
public class ShelfActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {
    @ViewInject(R.id.listview)
    private SwipeMenuListView listview;
    ShelfAdapter adapter;

    PreferenceUtil _pref;
    private String id;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shelf);

        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("已上架宝贝");

        _pref = PreferenceUtil.getInstance(this);

        adapter = new ShelfAdapter(this);
        listview.setAdapter(adapter);

        initListView();

        getShelfList();
        refresh_view.setOnRefreshListener(this);
    }

    private void initListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // create "open" item

                SwipeMenuItem openItem2 = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem2.setBackground(new ColorDrawable(Color.rgb(0xEE, 0x00,
                        0x00)));
                // set item width
                openItem2.setWidth(DensityUtil.dip2px(ShelfActivity.this, 70));
                // set item title
                openItem2.setTitle("下架");
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
                        cancelCollect(adapter._listData.get(arg2).getItem_id());
                        adapter._listData.remove(arg2);
                        adapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                page = 0;
                getShelfList();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                page++;
                getShelfList();
            }
        }, 2000);
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
            holder.image.setImageURI(Uri.parse(bean.getSmall_img()));
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

    private void getShelfList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * page + "");
        _params.addBodyParameter("fetchNum", Constants.FETCH_NUM + "");
        _params.addBodyParameter("api_name", "koudai.member.getItemOnsaleList");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.member.getItemOnsaleList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("item_list");
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

    private void cancelCollect(String id) {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("api_name", "koudai.member.offsaleItem");
        _params.addBodyParameter("item_id", id);
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "api_name=koudai.member.offsaleItem&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
