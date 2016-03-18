package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import activity.ProductListActivity;
import utils.DensityUtil;
import yd.koudd.MyApplication;
import yd.koudd.R;

/**
 * Created by zoushaohua on 2016/1/21.
 * qq:756350775
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private LayoutInflater mInflater;
    private static Context mContext;
    private List<String> dataList;

    public GridAdapter(Context context, List<String> dataList) {
        mInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_grid_recycle,
                viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.imageView = (ImageView) view.findViewById(R.id.imageView);

        viewHolder.textView = (TextView) view.findViewById(R.id.textView);

        LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) viewHolder.imageView.getLayoutParams();
        linearParams.width = (MyApplication.screenWidth - DensityUtil.dip2px(mContext, 20)) / 2;
        linearParams.height = linearParams.width;
        viewHolder.imageView.setLayoutParams(linearParams);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.imageView.setImageResource(R.mipmap.ic_p1);
        viewHolder.textView.setText(dataList.get(i));
    }

    @Override
    public int getItemCount() {
        return dataList != null ? dataList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, ProductListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            });
        }
    }

}
