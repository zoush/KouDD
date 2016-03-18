package utils;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import yd.koudd.R;


/**
 * Created by zoushaohua on 2015/11/23.
 * qq:756350775
 */
public class TitleBarAction {
    private ImageView common_title_bar_btn_left;
    private TextView common_title_bar_tv_title;
    private ImageButton common_title_bar_btn_right1;
    private ImageButton common_title_bar_btn_right2;
    private TextView common_title_bar_tv_right1;
    private Window window;

    public TitleBarAction(Window window) {
        this.window = window;
    }


    public ImageView getLeftBtn() {
        if (common_title_bar_btn_left == null && window != null) {
            common_title_bar_btn_left = (ImageView) window.findViewById(R.id.common_title_bar_btn_left);
        }
        return common_title_bar_btn_left;
    }


    public TextView getTitleTv() {
        if (common_title_bar_tv_title == null && window != null) {
            common_title_bar_tv_title = (TextView) window.findViewById(R.id.common_title_bar_tv_title);
        }
        return common_title_bar_tv_title;
    }


    public ImageButton getRightBtn1() {
        if (common_title_bar_btn_right1 == null && window != null) {
            common_title_bar_btn_right1 = (ImageButton) window.findViewById(R.id.common_title_bar_btn_right1);
        }
        return common_title_bar_btn_right1;
    }


    public ImageButton getRightBtn2() {
        if (common_title_bar_btn_right2 == null && window != null) {
            common_title_bar_btn_right2 = (ImageButton) window.findViewById(R.id.common_title_bar_btn_right2);
        }
        return common_title_bar_btn_right2;
    }


    public TextView getRightTv() {
        if (common_title_bar_tv_right1 == null && window != null) {
            common_title_bar_tv_right1 = (TextView) window.findViewById(R.id.common_title_bar_tv_right1);
        }
        return common_title_bar_tv_right1;
    }


    public void setLeftImages(String title, View.OnClickListener clickListener) {
        //getLeftBtn().setImageResource(imagesResouce);
        getLeftBtn().setOnClickListener(clickListener);
        getLeftBtn().setVisibility(View.VISIBLE);
    }


    public void setLeftImages(Drawable imagesDrawable, View.OnClickListener clickListener) {
        //getLeftBtn().setImageDrawable(imagesDrawable);
        getLeftBtn().setOnClickListener(clickListener);
        getLeftBtn().setVisibility(View.VISIBLE);
    }


    public void setTitle(CharSequence charSequence) {
        getTitleTv().setText(charSequence);
    }


    public void setTitle(int titleId) {
        getTitleTv().setText(titleId);
    }


    public void setTitleCkick(int titleId, View.OnClickListener clickListener) {
        getTitleTv().setText(titleId);
        getTitleTv().setOnClickListener(clickListener);
    }


    public void setTitleCkick(CharSequence charSequence, View.OnClickListener clickListener) {
        getTitleTv().setText(charSequence);
        getTitleTv().setOnClickListener(clickListener);
    }


    public void setRightImagesbtn1(int imagesResouce, View.OnClickListener clickListener) {
        getRightBtn1().setImageResource(imagesResouce);
        getRightBtn1().setOnClickListener(clickListener);
        getRightBtn1().setVisibility(View.VISIBLE);
    }


    public void setRightImagesbtn1(Drawable imagesDrawable, View.OnClickListener clickListener) {
        getRightBtn1().setImageDrawable(imagesDrawable);
        getRightBtn1().setOnClickListener(clickListener);
    }


    public void setRightImagesbtn2(int imagesResouce, View.OnClickListener clickListener) {
        getRightBtn2().setImageResource(imagesResouce);
        getRightBtn2().setOnClickListener(clickListener);
    }


    public void setRightImagesbtn2(Drawable imagesDrawable, View.OnClickListener clickListener) {
        getRightBtn2().setImageDrawable(imagesDrawable);
        getRightBtn2().setOnClickListener(clickListener);
    }


    public void setRightTvCkick(int titleId, View.OnClickListener clickListener) {
        getRightTv().setText(titleId);
        getRightTv().setOnClickListener(clickListener);
    }


    public void setRightTvCkick(CharSequence charSequence, View.OnClickListener clickListener) {
        getRightTv().setVisibility(View.VISIBLE);
        getRightTv().setText(charSequence);
        if (clickListener != null) {
            getRightTv().setOnClickListener(clickListener);
        }
    }

}
