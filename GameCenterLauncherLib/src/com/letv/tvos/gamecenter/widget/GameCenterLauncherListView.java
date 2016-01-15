package com.letv.tvos.gamecenter.widget;

import com.letv.tvos.gamecenter.launcher.plugin.R;
import com.letv.tvos.gamecenter.util.Logger;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;

public class GameCenterLauncherListView extends CListView {
	private Drawable backgroundDrawable;
	private OnBackPressedListener mOnBackPressedListener;
	private int headerHeight = 460;

	public void setOnBackPressedListener(OnBackPressedListener mOnBackPressedListener) {
		this.mOnBackPressedListener = mOnBackPressedListener;
	}

	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if(changed){
			backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
		}
		super.onLayout(changed, l, t, r, b);
	}


	public GameCenterLauncherListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			 if (event.getAction() == KeyEvent.ACTION_DOWN) {
					if (getScrollY() != 0) {
						smoothScrollTo(0, 0, 500);
					}

					if (mOnBackPressedListener != null) {
						mOnBackPressedListener.onBackPressed();
					}
					return true;
			 }
		}
		return super.dispatchKeyEvent(event);
	}
	@Override
	protected void onFinishInflate() {
		backgroundDrawable = getResources().getDrawable(R.drawable.gc_launcher_bg);
		super.onFinishInflate();
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(getScrollY() < headerHeight){
			float f = (float)getScrollY() / (float)headerHeight;
			backgroundDrawable.setAlpha((int) (255 * 1 - f));
			canvas.drawARGB((int) (216 * f), 0, 0, 0);
			backgroundDrawable.setBounds(0, getScrollY(), getMeasuredWidth(), getScrollY() + getMeasuredHeight());
			backgroundDrawable.draw(canvas);
		}else{
			canvas.drawARGB(216,0,0,0);
		}
		Logger.d(VIEW_LOG_TAG, "GameCenterLauncherListView : onDraw");
		
	}

	public boolean requestFocus(int direction, int rowIndex) {
		ViewItem viewItem = getItemAt(rowIndex);
		if(viewItem == null){
			viewItem = getTopFullVisableItem();
		}
		if (viewItem != null) {
			IgnoreChildPadingLinerLayout ignoreChildPadingLinerLayout = (IgnoreChildPadingLinerLayout) viewItem.view.findViewById(R.id.launcher_row_item_items);
			if(ignoreChildPadingLinerLayout == null){
				ignoreChildPadingLinerLayout = (IgnoreChildPadingLinerLayout) viewItem.view.findViewById(R.id.launcher_item_header_banners);
			}
			if (ignoreChildPadingLinerLayout != null) {
				if (direction == FOCUS_LEFT || direction == FOCUS_UP || direction == FOCUS_DOWN) {
					int childCount = ignoreChildPadingLinerLayout.getChildCount();
					if (childCount > 0) {
						for (int i = 0; i < childCount; i++) {
							View v = ignoreChildPadingLinerLayout.getChild(i);
							if(v != null && v.getVisibility() == View.VISIBLE){
								return v.requestFocus();
							}
						}
					}
				} else if (direction == FOCUS_RIGHT) {
					int childCount = ignoreChildPadingLinerLayout.getChildCount();
					if (childCount > 0) {
						for (int i = childCount -1; i >= 0; i--) {
							View v = ignoreChildPadingLinerLayout.getChild(i);
							if(v != null && v.getVisibility() == View.VISIBLE){
								return v.requestFocus();
							}
						}
					}
				}
			}

		}
		return false;
	}
	@Override
	public View focusSearch(View focused, int direction) {
		switch (direction) {
		case FOCUS_LEFT:
		case FOCUS_RIGHT:
			return null;
		case FOCUS_DOWN:
//			View currentFocusedItem = finViewItem(focused);
//			int currentFocusedItemIndex = -1;
//			int nextFocusedItemIndex = -1;
//			View nextFocusedItem;
//			if (currentFocusedItem != null) {
//				currentFocusedItemIndex = getItemIndex(currentFocusedItem);
//				if(direction == FOCUS_UP){
//					nextFocusedItemIndex = currentFocusedItemIndex - 1;
//				}else{
//					nextFocusedItemIndex = currentFocusedItemIndex + 1;
//				}
//				if(nextFocusedItemIndex >= 0 && nextFocusedItemIndex < getCount()){
//					nextFocusedItem = getItem(nextFocusedItemIndex);
//					if(nextFocusedItem != null){
//						
//					}
//				}
//			}
			
			View nextFocusView = super.focusSearch(focused, direction);
			ViewItem currentFocusedItem = finViewItem(focused);
					ViewItem nextFocusedItem = finViewItem(nextFocusView);
			int currentFocusedItemIndex = -1;
			int nextFocusedItemIndex = -1;

			if (nextFocusedItem != null) {
				nextFocusedItemIndex = nextFocusedItem.index;
			}

			if (currentFocusedItem != null) {
				currentFocusedItemIndex = currentFocusedItem.index;
				if(currentFocusedItemIndex >= 0 && currentFocusedItemIndex < getItemCount() -1){
					if(nextFocusedItemIndex - currentFocusedItemIndex == 1){
						return nextFocusView;
					}else{
						nextFocusedItem = getItemAt(currentFocusedItemIndex + 1);
						if(nextFocusedItem != null){
							
								IgnoreChildPadingLinerLayout vg = (IgnoreChildPadingLinerLayout) nextFocusedItem.view.findViewById(R.id.launcher_row_item_items);
								if(vg == null){
									vg = (IgnoreChildPadingLinerLayout) nextFocusedItem.view.findViewById(R.id.launcher_item_header_banners);
								}
								if(vg != null){
									View temp = vg.focusSearchLast();
									if(temp != null){
										return temp;
									}
								}
						}
					}
				}
			}
			return nextFocusView;
		case FOCUS_UP:
			nextFocusView = super.focusSearch(focused, direction);
			currentFocusedItem = finViewItem(focused);
			nextFocusedItem = finViewItem(nextFocusView);
			currentFocusedItemIndex = -1;
			nextFocusedItemIndex = -1;

			if (nextFocusedItem != null) {
				nextFocusedItemIndex = nextFocusedItem.index;
			}

			if (currentFocusedItem != null) {
				currentFocusedItemIndex = currentFocusedItem.index;
				if(currentFocusedItemIndex > 0 && currentFocusedItemIndex < getItemCount()){
					if(currentFocusedItemIndex - nextFocusedItemIndex   == 1){
						return nextFocusView;
					}else{
						nextFocusedItem = getItemAt(currentFocusedItemIndex - 1);
						if(nextFocusedItem != null){
							IgnoreChildPadingLinerLayout vg = (IgnoreChildPadingLinerLayout) nextFocusedItem.view.findViewById(R.id.launcher_row_item_items);
							if(vg == null){
								vg = (IgnoreChildPadingLinerLayout) nextFocusedItem.view.findViewById(R.id.launcher_item_header_banners);
							}
							if(vg != null){
								View temp = vg.focusSearchLast();
								if(temp != null){
									return temp;
								}
							}
						}
					}
				}else{
					return nextFocusView;
				}
			}
			return nextFocusView;
		}
		return super.focusSearch(focused, direction);
	}
	public interface OnBackPressedListener {
		public void onBackPressed();
	}
}
