package fragments.rank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.pulltorefresh.PullToRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import activity.MarketActivity;
import bean.RankMerchantBean;
import utils.MD5;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by d on 2016/3/4.
 */
public class RankFragment extends Fragment implements PullToRefreshLayout.OnRefreshListener {
    @ViewInject(R.id.listview)
    private ListView listview;
    DiectAdapter adapter;
    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.fragment_item, container, false);

        ViewUtils.inject(this, contextView);
        adapter = new DiectAdapter(getActivity());
        listview.setAdapter(adapter);

        getRankList();

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MarketActivity.class);
                intent.putExtra("merchant_id", adapter._listData.get(position).getMerchant_id());
                intent.putExtra("address", adapter._listData.get(position).getAddress());
                intent.putExtra("name", adapter._listData.get(position).getTaobaoid());
                startActivity(intent);
            }
        });

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        int screenWidth = wm.getDefaultDisplay().getWidth();
        int height = wm.getDefaultDisplay().getHeight();

        refresh_view.setOnRefreshListener(this);
        return contextView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                page = 0;
                getRankList();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                page++;
                getRankList();
            }
        }, 2000);
    }

    class DiectAdapter extends BaseAdapter {
        public List<RankMerchantBean> _listData;
        private LayoutInflater inflater;

        public DiectAdapter(Context context) {
            _listData = new ArrayList<RankMerchantBean>();
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
            if (position == 0) {
                holder.ivLevel.setVisibility(View.VISIBLE);
                holder.ivLevel.setImageResource(R.mipmap.ic_first);
            } else if (position == 1) {
                holder.ivLevel.setVisibility(View.VISIBLE);
                holder.ivLevel.setImageResource(R.mipmap.ic_second);
            } else if (position == 2) {
                holder.ivLevel.setVisibility(View.VISIBLE);
                holder.ivLevel.setImageResource(R.mipmap.ic_third);
            } else {
                holder.ivLevel.setVisibility(View.INVISIBLE);
            }
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


    private void getRankList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * page + "");
        _params.addBodyParameter("fetchNum", Constants.FETCH_NUM + "");
        _params.addBodyParameter("api_name", "koudai.merchant.getMerchantList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("class_id", getArguments().getString("arg"));
        _params.addBodyParameter("orderby", "RANK");
        String signStr = "api_name=koudai.merchant.getMerchantList&appid=" + Constants.APPID + "&floor_id=" + getArguments().getString("arg") + Constants.PRIVATE_KEY;
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
                        if (page == 0)
                            adapter._listData.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            adapter._listData.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), RankMerchantBean.class));
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
