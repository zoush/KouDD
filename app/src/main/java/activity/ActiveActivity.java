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
import com.pulltorefresh.PullToRefreshLayout;
import com.pulltorefresh.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.InfomationBean;
import utils.MD5;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by d on 2016/3/9.
 * 店铺动态
 */
public class ActiveActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {
    private String merchant_id = "";
    @ViewInject(R.id.listview)
    private PullableListView listview;
    ActiveAdapter adapter;

    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active);


        ViewUtils.inject(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("店铺动态");

        adapter = new ActiveAdapter(this);
        listview.setAdapter(adapter);

        merchant_id = getIntent().getStringExtra("merchant_id");

        getActiveList();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ActiveActivity.this, ActiveDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("info", adapter._listData.get(position));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        refresh_view.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                page = 0;
                getActiveList();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                page++;
                getActiveList();
            }
        }, 2000);
    }

    class ActiveAdapter extends BaseAdapter {
        public List<InfomationBean> _listData;
        private LayoutInflater inflater;

        public ActiveAdapter(Context context) {
            _listData = new ArrayList<InfomationBean>();
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
                convertView = inflater.inflate(R.layout.item_active, null);
                holder.image = (SimpleDraweeView) convertView.findViewById(R.id.image);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            InfomationBean bean = _listData.get(position);
            holder.image.setImageURI(Uri.parse(Constants.IMAGE_URL + bean.getImg_path()));
            holder.tvName.setText(bean.getTitle());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date(Long.valueOf(bean.getAddtime()) * 1000));
            holder.tvTime.setText(date);
            return convertView;
        }

        private final class ViewHolder {
            private SimpleDraweeView image;
            private TextView tvName;
            private TextView tvTime;
        }
    }

    private void getActiveList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * page + "");
        _params.addBodyParameter("fetchNum", Constants.FETCH_NUM + "");
        _params.addBodyParameter("api_name", "koudai.merchant.getMerchantInfomationList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("merchant_id", merchant_id);
        String signStr = "api_name=koudai.merchant.getMerchantInfomationList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("infomation_list");
                        if (page == 0)
                            adapter._listData.clear();
                        if (jsonArray.length() != 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                adapter._listData.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), InfomationBean.class));
                            }
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
