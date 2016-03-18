package adapter;

/**
 * Created by zoushaohua on 2016/1/21.
 * qq:756350775
 */


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;


public class SpaceItemDecoration extends RecyclerView.ItemDecoration{

    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

        if(parent.getChildPosition(view) != 0)
            outRect.top = space;
    }
}
