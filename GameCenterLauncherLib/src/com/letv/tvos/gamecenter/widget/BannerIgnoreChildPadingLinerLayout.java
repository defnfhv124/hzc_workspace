package com.letv.tvos.gamecenter.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

public class BannerIgnoreChildPadingLinerLayout extends IgnoreChildPadingLinerLayout {

	private View currentSelectedView;
	private SelectChangedListener mSelectChangedListener;
	private UseroperationInterface mUseroperationInterface;

	public SelectChangedListener getSelectChangedListener() {
		return mSelectChangedListener;
	}

	public void setSelectChangedListener(SelectChangedListener mSelectChangedListener) {
		this.mSelectChangedListener = mSelectChangedListener;
	}

	public UseroperationInterface getmUseroperationInterface() {
		return mUseroperationInterface;
	}

	public void setmUseroperationInterface(UseroperationInterface mUseroperationInterface) {
		this.mUseroperationInterface = mUseroperationInterface;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (hasFocus()) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT || event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
				if (event.getAction() == KeyEvent.ACTION_DOWN && mUseroperationInterface != null) {
					mUseroperationInterface.onOperated();
				}
			}
		}
		return super.dispatchKeyEvent(event);
	}

	public View selectNext() {
		View next = null;
		if (getChildCount() > 0) {
			if (currentSelectedView == null) {
				next = getChild(0);
				setSelect(next);
			} else {
				int childIndex = childIndexOf(currentSelectedView);
				if (childIndex >= 0 && childIndex < getChildCount()) {
					int loopTime = 0;
					for (;;) {
						childIndex++;
						loopTime ++;
						childIndex %= getChildCount();
						next = getChild(childIndex);
						if (next.getVisibility() == View.VISIBLE) {
							setSelect(next);
							return next;
						}
						if(loopTime >= getChildCount()){
							return null;
						}
					}

				}
			}
		}
		return null;
	}

	public void setSelect(View v) {

		if (v != null) {
			if (childs.contains(v)) {
				if (!v.equals(currentSelectedView)) {
					if (currentSelectedView != null) {
						if (mSelectChangedListener != null) {
							mSelectChangedListener.onSelectChanged(currentSelectedView, false);
						}
					}
					currentSelectedView = v;
					if (currentSelectedView != null) {
						if (mSelectChangedListener != null) {
							mSelectChangedListener.onSelectChanged(currentSelectedView, true);
						}
					}
				} else {
				}

			}
		}
	}

	public BannerIgnoreChildPadingLinerLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public interface SelectChangedListener {
		public void onSelectChanged(View v, boolean isSelect);
	}

	public interface UseroperationInterface {
		public void onOperated();
	}
}
