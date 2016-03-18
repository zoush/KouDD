package fragments.home;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import activity.ProductDetailActivity;
import bean.ProductBean;
import de.greenrobot.event.EventBus;
import utils.DoubleClickUtils;
import utils.MD5;
import widgh.MyGridView;
import yd.koudd.BaseFragment;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/2/24.
 * qq:756350775
 */
public class LikeFragment extends BaseFragment {
    @ViewInject(R.id.listview)
    private MyGridView listview;
    MeseurmAdapter adapter;
    private int page = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View _view = inflater.inflate(R.layout.fragment_page1, null);
        ViewUtils.inject(this, _view);
        initView();

        getProductList();

        EventBus.getDefault().register(this);
        return _view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(String msg) {
        if (msg.equals("3refresh")) {
            page = 0;
            getProductList();
        } else if (msg.equals("3load")) {
            page++;
            getProductList();
        }
    }

    private void initView() {
        adapter = new MeseurmAdapter(getActivity());
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                startActivity(intent);
            }
        });
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
                holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.imageView.setImageURI(Uri.parse(_listData.get(position).getSmall_img()));
            holder.tvAddress.setText(_listData.get(position).getMarket_name());
            holder.tvName.setText(_listData.get(position).getItem_name());
            holder.tvPrice.setText("￥ " + _listData.get(position).getMall_price());
            holder.llItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (DoubleClickUtils.isFastClick()) {
                        Intent intent = new Intent(getActivity(), ProductDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("item_id", _listData.get(position).getItem_id());
                        startActivity(intent);
                    }
                }
            });
            return convertView;
        }

        private final class ViewHolder {
            private SimpleDraweeView imageView;
            private TextView tvPrice;
            private TextView tvName;
            private TextView tvAddress;
            private LinearLayout llItem;
        }
    }

    private void getProductList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * page + "");
        _params.addBodyParameter("fetchNum", 10 + "");
        _params.addBodyParameter("api_name", "koudai.item.getItemList");
        _params.addBodyParameter("appid", Constants.APPID);
        _params.addBodyParameter("orderby", "GUESS_LIKE");
        String signStr = "api_name=koudai.item.getItemList&appid=" + Constants.APPID + "&orderby=GUESS_LIKE" + Constants.PRIVATE_KEY;
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
