package com.letv.tvos.gamecenter.launcher;

import java.util.ArrayList;

import com.android.internal.view.RootViewSurfaceTaker;
import com.letv.tvos.gamecenter.launcher.plugin.R;
import com.letv.tvos.gamecenter.model.DesktopItem;
import com.letv.tvos.gamecenter.util.AnimationUtil;
import com.stv.launcher.fragment.BaseFragment;

import android.animation.AnimatorSet;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.support.v4.view.KeyEventCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.Toast;

public class GameCenterLauncherContentView extends FrameLayout{
	private View gs_launcher_item_1;
	private View gs_launcher_item_2;
	private View gs_launcher_item_3;
	private View gs_launcher_item_4;
	private View gs_launcher_item_5;
	private View gs_launcher_item_6;
	private View gs_launcher_item_7;
	private View gs_launcher_item_8;
	private View gs_launcher_item_9;
	private View gs_launcher_item_10;
	private View gs_launcher_item_11;
	private View gs_launcher_item_12;
	private View gs_launcher_item_13;
	private View gs_launcher_item_14;
	private View gs_launcher_item_15;

	private int defaultItemWidth = 0;
	private int defaultItemHeight = 0;

	private int itemSpacing = 0;
	private int minScroolOffset = 0;
	private int scroolTopBorderline = 0;
	private int scroolbottomBorderline = 0;
	
	public ArrayList<View> itemTypeFuncationsViews = new ArrayList<View>();
	public ArrayList<View> itemType1Views = new ArrayList<View>();
	public ArrayList<View> itemType2Views = new ArrayList<View>();
	public View bannerView;
	public View rankView;

	private Scroller mScroller = new Scroller(getContext()); // 滑动控制器

	private boolean isScrolling; // 是否正在滚动

	private ArrayList<MChild> mChilds = new ArrayList<GameCenterLauncherContentView.MChild>();

	public GameCenterLauncherContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private OnBackPressedListener mOnBackPressedListener;
	
	public void setmOnBackPressedListener(OnBackPressedListener mOnBackPressedListener) {
		this.mOnBackPressedListener = mOnBackPressedListener;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Resources res = getResources();
		itemSpacing = res.getDimensionPixelSize(R.dimen.gs_16);
//		itemSpacing = 36;
		defaultItemWidth = res.getDimensionPixelSize(R.dimen.gs_211_3);
		defaultItemHeight = res.getDimensionPixelSize(R.dimen.gs_152);
//		minScroolOffset = res.getDimensionPixelOffset(R.dimen.gs_88);

		gs_launcher_item_1 = findViewById(R.id.gs_launcher_item_1);
		gs_launcher_item_2 = findViewById(R.id.gs_launcher_item_2);
		gs_launcher_item_3 = findViewById(R.id.gs_launcher_item_3);
		gs_launcher_item_4 = findViewById(R.id.gs_launcher_item_4);
		gs_launcher_item_5 = findViewById(R.id.gs_launcher_item_5);
		gs_launcher_item_6 = findViewById(R.id.gs_launcher_item_6);
		gs_launcher_item_7 = findViewById(R.id.gs_launcher_item_7);
		gs_launcher_item_8 = findViewById(R.id.gs_launcher_item_8);
		gs_launcher_item_9 = findViewById(R.id.gs_launcher_item_9);
		gs_launcher_item_10 = findViewById(R.id.gs_launcher_item_10);
		gs_launcher_item_11 = findViewById(R.id.gs_launcher_item_11);
		gs_launcher_item_12 = findViewById(R.id.gs_launcher_item_12);
		gs_launcher_item_13 = findViewById(R.id.gs_launcher_item_13);
		gs_launcher_item_14 = findViewById(R.id.gs_launcher_item_14);
		gs_launcher_item_15 = findViewById(R.id.gs_launcher_item_15);

		mChilds.add(new MChild(0, 0, gs_launcher_item_1));
		mChilds.add(new MChild(1, 0, gs_launcher_item_2));
		mChilds.add(new MChild(0, 1, gs_launcher_item_3));
		MChild mChild = new MChild(0, 3, gs_launcher_item_4);
		mChild.setMarginLeft(res.getDimensionPixelSize(R.dimen.gs_154_6));
		mChilds.add(mChild);
		mChilds.add(new MChild(2, 0, gs_launcher_item_5));
		mChilds.add(new MChild(2, 1, gs_launcher_item_6));
		mChilds.add(new MChild(2, 2, gs_launcher_item_7));
		mChilds.add(new MChild(2, 3, gs_launcher_item_8));
		mChilds.add(new MChild(2, 4, gs_launcher_item_9));
		mChilds.add(new MChild(3, 0, gs_launcher_item_10));
		mChilds.add(new MChild(3, 3, gs_launcher_item_11));
		mChilds.add(new MChild(4, 0, gs_launcher_item_12));
		mChilds.add(new MChild(4, 3, gs_launcher_item_13));
		mChilds.add(new MChild(5, 0, gs_launcher_item_14));
		mChilds.add(new MChild(5, 3, gs_launcher_item_15));

		gs_launcher_item_1.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_2.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_3.setOnFocusChangeListener(new MFocusChangeListener(1.08f));
		gs_launcher_item_4.setOnFocusChangeListener(new MFocusChangeListener(1.08f));
		gs_launcher_item_5.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_6.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_7.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_8.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_9.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_10.setOnFocusChangeListener(new MFocusChangeListener(1.1f));
		gs_launcher_item_11.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_12.setOnFocusChangeListener(new MFocusChangeListener(1.1f));
		gs_launcher_item_13.setOnFocusChangeListener(new MFocusChangeListener());
		gs_launcher_item_14.setOnFocusChangeListener(new MFocusChangeListener(1.1f));
		gs_launcher_item_15.setOnFocusChangeListener(new MFocusChangeListener());
		
		itemTypeFuncationsViews.add(gs_launcher_item_1);
		itemTypeFuncationsViews.add(gs_launcher_item_2);
		
		itemType1Views.add(gs_launcher_item_5);
		itemType1Views.add(gs_launcher_item_6);
		itemType1Views.add(gs_launcher_item_7);
		itemType1Views.add(gs_launcher_item_8);
		itemType1Views.add(gs_launcher_item_9);
		
		itemType2Views.add(gs_launcher_item_10);
		itemType2Views.add(gs_launcher_item_11);
		itemType2Views.add(gs_launcher_item_12);
		itemType2Views.add(gs_launcher_item_13);
		itemType2Views.add(gs_launcher_item_14);
		itemType2Views.add(gs_launcher_item_15);
		
		bannerView = gs_launcher_item_3;
		rankView = gs_launcher_item_4;
	}

	public boolean requestFirstFocus(int direction) {
		boolean result = false;
		switch (direction) {
		case FOCUS_DOWN:
			result = findFocusInRow(getCurrentTopRowNumber(), FOCUS_LEFT).requestFocus();
			break;
		case FOCUS_LEFT:
			result = findFocusInRow(getCurrentTopRowNumber(), FOCUS_LEFT).requestFocus();
			break;
		case FOCUS_RIGHT:
			result = findFocusInRow(getCurrentTopRowNumber(), FOCUS_RIGHT).requestFocus();
			break;
		case FOCUS_UP:
			result = findFocusInRow(getCurrentTopRowNumber(), FOCUS_LEFT).requestFocus();
			break;
		}
		if(result){
			return true;
		}else{
			return requestFocus(direction);
		}
	}

	private View findFocusInRow(int rowNumber, int direction) {
		switch (rowNumber) {
		case 0:
			if(direction == FOCUS_RIGHT){
				return gs_launcher_item_4;
			}else{
				return gs_launcher_item_1;
			}
		case 1:
			if(direction == FOCUS_RIGHT){
				return gs_launcher_item_4;
			}else{
				return gs_launcher_item_2;
			}
		case 2:
			if(direction == FOCUS_RIGHT){
				return gs_launcher_item_9;
			}else{
				return gs_launcher_item_5;
			}
		case 3:
			if(direction == FOCUS_RIGHT){
				return gs_launcher_item_11;
			}else{
				return gs_launcher_item_10;
			}
		case 4:
			if(direction == FOCUS_RIGHT){
				return gs_launcher_item_13;
			}else{
				return gs_launcher_item_12;
			}
		case 5:
			if(direction == FOCUS_RIGHT){
				return gs_launcher_item_15;
			}else{
				return gs_launcher_item_14;
			}
		}
		return this;
	}

	
	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int mWidth = right - left;
		int mHeight = bottom - top;

		int[] location = new int[2];
		getLocationInWindow(location);
		scroolTopBorderline = location[1] + minScroolOffset + getPaddingTop();
		scroolbottomBorderline = location[1] + mHeight - minScroolOffset;
		Rect rect = new Rect();
		int offset = (mWidth - defaultItemWidth * 5 - itemSpacing * 4) / 2;
		for (MChild mChild : mChilds) {
			rect.left = (defaultItemWidth + itemSpacing) * mChild.cloumn - mChild.mView.getPaddingLeft() + mChild.marginLeft + offset;
			rect.top = (defaultItemHeight + itemSpacing) * mChild.row + itemSpacing - mChild.mView.getPaddingTop() + getPaddingTop();
			rect.bottom = rect.top + mChild.mView.getMeasuredHeight();
			rect.right = rect.left + mChild.mView.getMeasuredWidth();
			mChild.mView.layout(rect.left, rect.top, rect.right, rect.bottom);
			System.out.print(rect);
		}
	}

	private class MFocusChangeListener implements View.OnFocusChangeListener {
		private AnimatorSet animatorSet;
		private float scaleRate = 1.17f;

		public MFocusChangeListener() {
			super();
		}

		public MFocusChangeListener(float scaleRate) {
			super();
			this.scaleRate = scaleRate;
		}

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (null != animatorSet) {
				animatorSet.cancel();
				animatorSet = null;
			}
			if (hasFocus) {
				v.bringToFront();
				int[] location = new int[2];
				boolean stay = true;
				v.getLocationInWindow(location);
				int childBottom = location[1] - v.getPaddingBottom() + v.getMeasuredHeight();
				System.out.println("childBottom = " + childBottom);
				if (childBottom > scroolbottomBorderline) {
					stay = false;
//					smoothScrollTo(0, (int) (getScrollY() + (childBottom - scroolbottomBorderline)), 300);
					smoothScrollToPage(getItemPage(v));
				}
				getLocationInWindow(location);
				int parentTop = location[1];
				v.getLocationInWindow(location);
				v.getTop();
				int childTop = location[1] + v.getPaddingTop();
				System.out.println("childTop = " + childTop);
				if (childTop < scroolTopBorderline) {
					stay = false;
//					smoothScrollTo(0, (int) Math.max(0, getScrollY() + childTop - scroolTopBorderline), 300);
					smoothScrollToPage(getItemPage(v));
				}
				
				if(!mScroller.isFinished() && stay){
//					smoothScrollBy(0, 0, 0);
					smoothScrollToPage(getItemPage(v));
				}
				v.setBackgroundResource(R.drawable.gamecenter_launcher_focus_bg_focus);
				animatorSet = AnimationUtil.startScaleToBigAnimation(v, scaleRate, null);
			} else {
				animatorSet = AnimationUtil.startScaleToSmallAnimation(v, scaleRate, null);
				v.setBackgroundDrawable(null);
			}
		}

	}

	private class MChild {
		public int cloumn;
		public int row;
		public View mView;
		public int marginLeft = 0;

		public MChild(int row, int cloumn, View mView) {
			super();
			this.cloumn = cloumn;
			this.row = row;
			this.mView = mView;
		}

		public void setMarginLeft(int marginLeft) {
			this.marginLeft = marginLeft;
		}

	}
	
	private int getItemPage(View view){
		return (view.getTop() + view.getPaddingTop() + getPaddingTop()) / ((defaultItemHeight + itemSpacing) * 3);
	}

	private void smoothScrollTo(int x, int y, int duration) {
		smoothScrollBy(x - getScrollX(), y - getScrollY(), duration);
	}

	private void smoothScrollBy(int dx, int dy, int duration) {
		mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			isScrolling = true;
		} else {
			isScrolling = false;
		}
		
//		if(getScrollY() == ((defaultItemHeight + itemSpacing) * 3)){
//			for (View v : itemType1Views) {
//				ViewGroup vg = (ViewGroup) v;
//				int count = vg.getChildCount();
//				for (int i = 0; i < count; i++) {
//					View child = vg.getChildAt(i);
//					child.setVisibility(View.INVISIBLE);
//				}
//			}
//		}else{
//			for (View v : itemType1Views) {
//				ViewGroup vg = (ViewGroup) v;
//				int count = vg.getChildCount();
//				for (int i = 0; i < count; i++) {
//					View child = vg.getChildAt(i);
//					if(child.getVisibility() == View.INVISIBLE){
//						child.setVisibility(View.VISIBLE);
//					}else{
//						break;
//					}
//				}
//			}
//		}

	}
	public int getCurrentPageNumber(){
		return getScrollY() / ((defaultItemHeight + itemSpacing) * 3);
	}
	private void smoothScrollToPage(int page){
		int dy = (defaultItemHeight + itemSpacing) * 3 * page;
		smoothScrollTo(0, dy, 500);
	}

	private int getCurrentTopRowNumber() {
		return (getScrollY() + defaultItemHeight + itemSpacing -1 )/ (defaultItemHeight + itemSpacing);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		boolean result = super.dispatchKeyEvent(event) || executeKeyEvent(event);
		return result;
	}
	
	public boolean executeKeyEvent(KeyEvent event) {
		boolean handled = false;
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_DPAD_UP:
				handled = findNextFocus(FOCUS_UP);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
				handled = findNextFocus(FOCUS_LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
				handled = findNextFocus(FOCUS_RIGHT);
				break;
			case KeyEvent.KEYCODE_DPAD_DOWN:
				handled = findNextFocus(FOCUS_DOWN);
				break;
			case KeyEvent.KEYCODE_BACK:
				if(getScrollY() != 0){
					smoothScrollTo(0, 0, 500);
				}
				
				if(mOnBackPressedListener != null){
					mOnBackPressedListener.onBackPressed();
				}
				
				handled = true;
				
			}
		}
		return handled;
	}
	
	private boolean findNextFocus(int direction){
		View currentFocused = findFocus();
		if (currentFocused == this) {
			currentFocused = null;
		} else if (currentFocused != null) {
			boolean isChild = false;
			for (ViewParent parent = currentFocused.getParent(); parent instanceof ViewGroup; parent = parent.getParent()) {
				if (parent == this) {
					isChild = true;
					break;
				}
			}
			if (!isChild) {
				currentFocused = null;
			}
		}

		boolean handled = false;

		View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
		if(nextFocused != null && !nextFocused.equals(currentFocused)){
			boolean result = nextFocused.requestFocus();
			nextFocused.setVisibility(View.VISIBLE);
			return true;
		}
		return false;
	}
	public interface OnBackPressedListener{
		public void onBackPressed();
	}
}
