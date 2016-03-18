package activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.pulltorefresh.PullableListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bean.NoticeBean;
import utils.MD5;
import utils.PreferenceUtil;
import yd.koudd.BaseActivity;
import yd.koudd.Constants;
import yd.koudd.R;

/**
 * Created by d on 2016/1/30.
 */
public class MessageActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {
    MessageAdapter adapter;
    @ViewInject(R.id.lvMessage)
    private PullableListView lvMessage;
    PreferenceUtil _pref;

    @ViewInject(R.id.refresh_view)
    private PullToRefreshLayout refresh_view;
    private int page = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ViewUtils.inject(this);

        _pref = PreferenceUtil.getInstance(this);

        getTitleActionBar().setLeftImages("", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        getTitleActionBar().setTitle("我的消息");

        adapter = new MessageAdapter(this);
        lvMessage.setAdapter(adapter);

        getNoticeList();
        refresh_view.setOnRefreshListener(this);
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        // 下拉刷新操作
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                page = 0;
                getNoticeList();
            }
        }, 2000);
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                page++;
                getNoticeList();
            }
        }, 2000);
    }

    class MessageAdapter extends BaseAdapter {
        public List<NoticeBean> _listData;
        private LayoutInflater inflater;

        public MessageAdapter(Context context) {
            _listData = new ArrayList<NoticeBean>();
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
                convertView = inflater.inflate(R.layout.sys_notifiition_item, null);
                holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
                holder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
                holder.tvPoint = (TextView) convertView.findViewById(R.id.tvPoint);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            NoticeBean bean = _listData.get(position);
            holder.tvContent.setText(bean.getContent());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf.format(new Date(Long.valueOf(bean.getAddtime()) * 1000));
            holder.tvTime.setText(date);
            return convertView;
        }

        private final class ViewHolder {
            private TextView tvContent;
            private TextView tvTime;
            private TextView tvPoint;
        }
    }

    private void getNoticeList() {
        RequestParams _params = new RequestParams();
        _params.addBodyParameter("PHPSESSID", _pref.getSessionId());
        _params.addBodyParameter("firstRow", Constants.FETCH_NUM * page + "");
        _params.addBodyParameter("fetchNum", Constants.FETCH_NUM + "");
        _params.addBodyParameter("api_name", "koudai.notice.getNoticeList");
        _params.addBodyParameter("appid", Constants.APPID);
        String signStr = "PHPSESSID=" + _pref.getSessionId() + "&api_name=koudai.notice.getNoticeList&appid=" + Constants.APPID + Constants.PRIVATE_KEY;
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
                        if (page == 0)
                            adapter._listData.clear();
                        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("notice_list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            adapter._listData.add(new Gson().fromJson(jsonArray.getJSONObject(i).toString(), NoticeBean.class));
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
