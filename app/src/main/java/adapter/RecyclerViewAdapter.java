package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import activity.ProductDetailActivity;
import bean.ProductBean;
import utils.DensityUtil;
import utils.DoubleClickUtils;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/21.
 * qq:756350775
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private static Context mContext;
    public static List<ProductBean> dataList;

    public RecyclerViewAdapter(Context context, List<ProductBean> dataList) {
        mInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_recycleview,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.imageView = (SimpleDraweeView) view.findViewById(R.id.imageView);

        viewHolder.textView = (TextView) view.findViewById(R.id.textView);
        viewHolder.tvName = (TextView) view.findViewById(R.id.tvName);
        viewHolder.llItem = (LinearLayout) view.findViewById(R.id.llItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.imageView.setImageResource(R.mipmap.ic_p1);
        viewHolder.textView.setText("￥ " + dataList.get(i).getMall_price());
        viewHolder.tvName.setText(dataList.get(i).getItem_name());

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.imageView.getLayoutParams();
        linearParams.width = (DensityUtil.getScreenWidth(mContext) - DensityUtil.dip2px(mContext, 10)) / 3;
        linearParams.height = linearParams.width;
        viewHolder.imageView.setLayoutParams(linearParams);

        LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) viewHolder.llItem.getLayoutParams();
        linearParams1.width = DensityUtil.getScreenWidth(mContext) / 3;
        viewHolder.llItem.setLayoutParams(linearParams1);

        viewHolder.imageView.setImageURI(Uri.parse(dataList.get(i).getSmall_img()));
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        SimpleDraweeView imageView;
        TextView textView;
        TextView tvName;
        private LinearLayout llItem;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(DoubleClickUtils.isFastClick()) {
                        Intent intent = new Intent(mContext, ProductDetailActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("item_id", dataList.get(getPosition()).getItem_id());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

}
