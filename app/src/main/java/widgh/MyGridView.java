package widgh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

/**
 * �Զ���gridview�����ListView��Ƕ��gridview��ʾ�����������⣨1�а룩
 * 
 * @author wangyx
 * @version 1.0.0 2012-9-14
 */
/*public class MyGridView extends GridView {
 public OnTouchBlankPositionListener mTouchBlankPosListener;

 public MyGridView(Context context, AttributeSet attrs) {
 super(context, attrs);
 }

 public MyGridView(Context context) {
 super(context);
 }

 public MyGridView(Context context, AttributeSet attrs, int defStyle) {
 super(context, attrs, defStyle);
 }

 @Override
 public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

 int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
 super.onMeasure(widthMeasureSpec, expandSpec);
 }

 public interface OnTouchBlankPositionListener {
 *//** * * @return �Ƿ�Ҫ��ֹ�¼���·�� */
/*
 * boolean onTouchBlankPosition(); }
 * 
 * public void setOnTouchBlankPositionListener(OnTouchBlankPositionListener
 * listener) { mTouchBlankPosListener = listener; }
 * 
 * @Override public boolean onTouchEvent(MotionEvent event) { if
 * (mTouchBlankPosListener != null) { if (!isEnabled()) { // A disabled view
 * that is clickable still consumes the touch // events, it just doesn't respond
 * to them. return isClickable() || isLongClickable(); } if
 * (event.getActionMasked() == MotionEvent.ACTION_UP) { final int motionPosition
 * = pointToPosition((int) event.getX(), (int) event.getY()); if (motionPosition
 * == -1) { return mTouchBlankPosListener.onTouchBlankPosition(); } } } return
 * super.onTouchEvent(event); } }
 */
public class MyGridView extends GridView {
	public MyGridView(Context context,
			AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * ���ò�����
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
}