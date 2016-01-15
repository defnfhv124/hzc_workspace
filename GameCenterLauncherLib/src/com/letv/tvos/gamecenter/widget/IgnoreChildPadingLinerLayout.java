package com.letv.tvos.gamecenter.widget;

import java.util.ArrayList;
import java.util.Iterator;

import javax.crypto.Cipher;

import com.letv.tvos.gamecenter.util.Logger;
import com.qualcomm.util.MpqUtils;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

public class IgnoreChildPadingLinerLayout extends FrameLayout {

	private static final String VIEW_LOG_TAG = "IgnoreChildPadingLinerLayout";
	protected ArrayList<View> childs = new ArrayList<View>();
	private int itemSpacing = 24;
	private int orientation = 0;

	public IgnoreChildPadingLinerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected int childIndexOf(View currentSelectedView2) {
		return childs.indexOf(currentSelectedView2);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),childheightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		for (View child : childs) {
			child.measure(childWidthMeasureSpec, childheightMeasureSpec);
		}
		setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
	}

	public int getItemSpacing() {
		return itemSpacing;
	}

	public void setItemSpacing(int itemSpacing) {
		this.itemSpacing = itemSpacing;
	}

	public View getChild(int index) {
		if (index < childs.size()) {
			return childs.get(index);
		}
		return null;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		int count = getChildCount();
		for (int i = 0; i < count; i++) {
			childs.add(getChildAt(i));
		}
//		reSetViews();
	}



	@Override
	public void removeViewsInLayout(int start, int count) {
		Logger.d(VIEW_LOG_TAG, "start = " + String.valueOf(start) + " count = " + String.valueOf(count));
		for (int i = start + count - 1; i >= start; i--) {
			View v = childs.get(i);
			removeViewInLayout(v);
			childs.remove(v);
			
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		long startTime = System.currentTimeMillis();
		if (orientation == 0) {
			int leftPosition = 0;
			Rect rect = new Rect();
			for (View child : childs) {
				if(child.getVisibility() == View.GONE){
					continue;
				}
				rect.left = leftPosition - child.getPaddingLeft();
				rect.top = 0 - child.getPaddingTop();
				rect.right = rect.left + child.getMeasuredWidth();
				rect.bottom = rect.top + child.getMeasuredHeight();
				child.layout(rect.left, rect.top, rect.right, rect.bottom);
				leftPosition += itemSpacing + child.getMeasuredWidth() - child.getPaddingLeft() - child.getPaddingRight();
			}
		}
	}
	
	public View focusSearchLast(){
		if(getChildCount() > 0){
			return getChild(getChildCount() - 1);
		}
		return null;
	}
	
	public View focusSearchFirst(){
		if(getChildCount() > 0){
			return getChild(0);
		}
		return null;
	}

	@Override
	public View focusSearch(View focused, int direction) {
		if(focused != null){
			int index = childs.indexOf(focused);
			if (index != -1) {
				switch (direction) {
				case FOCUS_LEFT:
					for (int i = index -1; i >= 0; i--) {
						View v = getChild(i);
						if(v.getVisibility() == View.VISIBLE){
							return v;
						}
					}
					break;
				case FOCUS_RIGHT:
					int childCount = getChildCount();
					for (int i = index +1; i < childCount; i++) {
						View v = getChild(i);
						if(v.getVisibility() == View.VISIBLE){
							return v;
						}
					}
					break;
				}
			}
		}
		if (getParent() != null) {
            return getParent().focusSearch(focused, direction);
        }else{
        	return FocusFinder.getInstance().findNextFocus(this, focused, direction);
        }
	}

}
