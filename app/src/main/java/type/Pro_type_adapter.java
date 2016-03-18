package type;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;

import bean.SortBean;
import yd.koudd.Constants;
import yd.koudd.R;


public class Pro_type_adapter extends BaseAdapter {
    // 定义Context
    private LayoutInflater mInflater;
    private ArrayList<SortBean> list;
    private Context context;
    private SortBean type;

    public Pro_type_adapter(Context context, ArrayList<SortBean> list) {
        mInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (list != null && list.size() > 0)
            return list.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyView view;
        if (convertView == null) {
            view = new MyView();
            convertView = mInflater.inflate(R.layout.list_pro_type_item, null);
            view.icon = (SimpleDraweeView) convertView.findViewById(R.id.typeicon);
            view.name = (TextView) convertView.findViewById(R.id.typename);
            convertView.setTag(view);
        } else {
            view = (MyView) convertView.getTag();
        }
        if (list != null && list.size() > 0) {
            type = list.get(position);
            view.name.setText(type.getSort_name());
            view.icon.setImageURI(Uri.parse(Constants.IMAGE_URL + type.getSort_icon()));
        }

        return convertView;
    }


    private class MyView {
        private SimpleDraweeView icon;
        private TextView name;
    }

}
