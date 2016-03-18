package fragments.diect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import activity.MarketActivity;
import bean.RankMerchantBean;
import utils.MD5;
import utils.PreferenceUtil;
import widgh.ListViewForScrollView;
import yd.koudd.Constants;
import yd.koudd.R;


public class ItemFragment extends Fragment {
    @ViewInject(R.id.listview)
    private ListViewForScrollView listview;
    DiectAdapter adapter;
    PreferenceUtil _pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View contextView = inflater.inflate(R.layout.fragment_diect_item, container, false);

        _pref = PreferenceUtil.getInstance(getActivity());

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

        return contextView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            holder.ivLevel.setVisibility(View.GONE);

            RankMerchantBean bean = _listData.get(position);
            holder.tvTaoBaoName.setText(bean.getTaobaoid());
            holder.tvCount.setText(bean.getItem_num() + "件商品");
            holder.tvName.setText(bean.getAddress());
            if (_listData.get(position).getTaobao_level().length()==0 || Integer.valueOf(_listData.get(position).getTaobao_level()) == 0) {
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
        _params.addBodyParameter("api_name", "koudai.merchant.getMerchantList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("floor_id", getArguments().getString("arg"));
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
                        //adapter._listData.clear();
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