package fragments;

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
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.pulltorefresh.MyListener;
import com.pulltorefresh.PullToRefreshLayout;
import com.pulltorefresh.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import activity.DiectDetailsActivity;
import activity.LoginActivity;
import activity.MessageActivity;
import activity.SearchActivity;
import bean.MarketBean;
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
public class DiectFragment extends BaseFragment implements PullToRefreshLayout.OnRefreshListener {
    @ViewInject(R.id.listview)
    private PullableListView listview;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout ptrl;

    DiectAdapter adapter;
    private List<String> newList = new ArrayList<String>();

    @ViewInject(R.id.tvNumber)
    private TextView tvNumber;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.fragment_diect, null);
        ViewUtils.inject(this, _view);

        ptrl.setOnRefreshListener(new MyListener());

        adapter = new DiectAdapter(getActivity());
        listview.setAdapter(adapter);

        getEnterpriseList();

        initNoticeView();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), DiectDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("market", adapter._listData.get(i));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        refresh_view.setOnRefreshListener(this);
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

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                page = 0;
                getEnterpriseList();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                page++;
                getEnterpriseList();
            }
        }, 2000);
    }


    class DiectAdapter extends BaseAdapter {
        public List<MarketBean> _listData;
        private LayoutInflater inflater;

        public DiectAdapter(Context context) {
            _listData = new ArrayList<MarketBean>();
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
                convertView = inflater.inflate(R.layout.item_diect, null);
                holder.ivLogo = (SimpleDraweeView) convertView.findViewById(R.id.image);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
                holder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            MarketBean bean = _listData.get(position);
            holder.tvAddress.setText(bean.getAddress());
            holder.tvName.setText(bean.getMarket_name());
            DecimalFormat df = new DecimalFormat("0.00");
            holder.ivLogo.setImageURI(Uri.parse(Constants.IMAGE_URL + bean.getImg_path()));
            holder.tvDistance.setText(df.format(Double.valueOf(bean.getDistance()) / 1000).toString() + "km");
            return convertView;
        }

        private final class ViewHolder {
            private SimpleDraweeView ivLogo;
            private TextView tvName;
            private TextView tvAddress;
            private TextView tvDistance;
        }
    }


    private void getEnterpriseList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * page + "");
        _params.addBodyParameter("fetchNum", Constants.FETCH_NUM + "");
        _params.addBodyParameter("api_name", "koudai.market.getMarketList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("lon", MyApplication.mLng + "");
        _params.addBodyParameter("lat", MyApplication.mLat + "");
        String signStr = "api_name=koudai.market.getMarketList&appid=" + Constants.APPID + "&lon=" + MyApplication.mLng + "&lat=" + MyApplication.mLat + Constants.PRIVATE_KEY;
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
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("market_list");
                        if (page == 0)
                            adapter._listData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            adapter._listData.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), MarketBean.class));
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
