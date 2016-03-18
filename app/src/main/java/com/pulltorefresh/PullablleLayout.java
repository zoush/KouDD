package com.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class PullablleLayout extends RelativeLayout implements Pullable{

	public PullablleLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	public PullablleLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public PullablleLayout(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	@Override
	public boolean canPullDown() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean canPullUp() {
		// TODO Auto-generated method stub
		return true;
	}
	
}
