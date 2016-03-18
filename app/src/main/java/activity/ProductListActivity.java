package activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.pulltorefresh.PullToRefreshLayout;
import com.pulltorefresh.PullableGridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import bean.ProductBean;
import utils.MD5;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/31.
 * qq:756350775
 */
public class ProductListActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {
    @ViewInject(R.id.listview)
    private PullableGridView listview;
    MeseurmAdapter adapter;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle(getIntent().getStringExtra("title"));

        initView();

        getNewList();

        refresh_view.setOnRefreshListener(this);
    }

    private void initView() {
        adapter = new MeseurmAdapter(ProductListActivity.this);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ProductListActivity.this, ProductDetailActivity.class);
                intent.putExtra("item_id", adapter._listData.get(position).getItem_id());
                startActivity(intent);
            }
        });
    }

    @OnItemClick({R.id.listview})
    private void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                page = 0;
                getNewList();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                page++;
                getNewList();
            }
        }, 2000);
    }

    class MeseurmAdapter extends BaseAdapter {
        public List<ProductBean> _listData;
        private LayoutInflater inflater;

        public MeseurmAdapter(Context context) {
            _listData = new ArrayList<ProductBean>();
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
                convertView = inflater.inflate(R.layout.item_grid_recycle, null);
                holder.imageView = (SimpleDraweeView) convertView.findViewById(R.id.imageView);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageView.setImageURI(Uri.parse(_listData.get(position).getSmall_img()));
            holder.tvAddress.setText(_listData.get(position).getMarket_name());
            holder.tvName.setText(_listData.get(position).getItem_name());
            holder.tvPrice.setText("￥ " + _listData.get(position).getMall_price());
            return convertView;
        }

        private final class ViewHolder {
            private SimpleDraweeView imageView;
            private TextView tvName;
            private TextView tvPrice;
            private TextView tvAddress;
        }
    }


    private void getNewList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * page + "");
        _params.addBodyParameter("fetchNum", Constants.FETCH_NUM + "");
        _params.addBodyParameter("api_name", "koudai.item.getItemList");
        _params.addBodyParameter("appid", Constants.APPID);
        if (getIntent().hasExtra("market_id"))
            _params.addBodyParameter("market_id", getIntent().getStringExtra("market_id"));
        if (getIntent().hasExtra("item_name"))
            _params.addBodyParameter("item_name", getIntent().getStringExtra("item_name"));
        _params.addBodyParameter("orderby", getIntent().getStringExtra("orderby"));
        String signStr = "api_name=koudai.user.getItemList&appid=" + Constants.APPID + "&orderby=" + getIntent().getStringExtra("orderby") + Constants.PRIVATE_KEY;
        _params.addBodyParameter("token", MD5.MD5(signStr));
        final HttpUtils _http = new HttpUtils();
        _http.send(HttpRequest.HttpMethod.POST, Constants.ROOT_URL, _params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try {
                    JSONObject jsonObject = new JSONObject(responseInfo.result);
                    if (jsonObject.getInt("code") == 0) {
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("item_list");
                        if (page == 0)
                            adapter._listData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            adapter._listData.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), ProductBean.class));
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
}
