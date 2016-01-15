package com.letv.tvos.gamecenter.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.letv.tvos.gamecenter.util.Logger;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.Toast;

public class CListView extends ViewGroup {

	public static final int HEAD_VIEW_ITEM_TYPE = Integer.MAX_VALUE;
	public static String VIEW_LOG_TAG;
	private Adapter mAdapter;
	protected int currentTopItem = 0;
	private int mTop = 0;
	private int mBottom = 0;
	private ArrayList<ViewItem> views = new ArrayList<CListView.ViewItem>();
	private ArrayList<ViewItem> recyledViews = new ArrayList<CListView.ViewItem>();
	private int mWidth;
	private int mHeight;
	private boolean isScrolling = true;
	private Scroller mScroller = new Scroller(getContext()); // ����������
	private OnScrollListener mOnScrollListener;
	private static final int defaultScrollTime = 500;
	private DataSetObserver mDataSetObserver;
	protected ArrayList<View> headerViews = new ArrayList<View>();
	protected int currentFocusItemIndex = 0;

	public CListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		VIEW_LOG_TAG = getClass().getName();
		init();
	}

	public CListView(Context context) {
		super(context);
		VIEW_LOG_TAG = getClass().getName();
		init();
	}

	public CListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		VIEW_LOG_TAG = getClass().getName();
		init();
	}

	public void setOnScrollListener(OnScrollListener mOnScrollListener) {
		this.mOnScrollListener = mOnScrollListener;
	}

	public void addCacheViews(View v, int type) {
		if (v != null) {
			ViewItem viewItem = new ViewItem();
			viewItem.view = v;
			viewItem.index = -1;
			viewItem.type = type;
			recyledViews.add(viewItem);
		}
	}

	public void init() {
		mDataSetObserver = new DataSetObserver() {

			@Override
			public void onChanged() {
				super.onChanged();
				fillViewAll();
			}

			@Override
			public void onInvalidated() {
				super.onInvalidated();
				fillViewAll();
			}

		};
		setWillNotDraw(false);
	};

	public Adapter getAdapter() {
		return mAdapter;
	}

	public void setAdapter(Adapter mAdapter) {
		if (this.mAdapter != null) {
			this.mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
		if (mAdapter != null) {
			if (headerViews.size() > 0) {
				this.mAdapter = new HeaderViewAdapter(mAdapter);
			} else {
				this.mAdapter = mAdapter;
			}
		}

		if (this.mAdapter != null) {
			this.mAdapter.registerDataSetObserver(mDataSetObserver);
		}
	}

	public void addHeader(View v) {
		if (mAdapter != null && !(mAdapter instanceof HeaderViewAdapter)) {
			throw new IllegalStateException("Cannot add header view to list -- setAdapter has already been called.");
		}
		headerViews.add(v);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		for (ViewItem viewItem : views) {
			measureChilItem(viewItem);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (mWidth != r - l || mHeight != b - t - getPaddingTop() - getPaddingBottom()) {
			mWidth = r - l;
			mHeight = b - t - getPaddingTop() - getPaddingBottom();
			mTop = getScrollY() + getPaddingTop();
			mBottom = mTop + mHeight;
			fillViewAll();
		} else {
			for (ViewItem viewItem : views) {
				viewItem.view.layout(0, viewItem.location, viewItem.view.getMeasuredWidth(), viewItem.getItemBottom());
			}
		}

	}
/**依据当前最滚动位置确定最上边一个itme 并依此往下填充直至填充完可是区域*/
	private void fillViewAll() {
		long startTime = System.currentTimeMillis();
		currentTopItem = computeTopItemIndex();
		int currentItem = Math.max(0, currentTopItem - 1);
		recyleViews(views, false);
		int i = 0;
		for (;;) {
			if (mAdapter != null) {
				if (currentItem < mAdapter.getCount()) {
					ViewItem viewItemTemp = findViewItemWithSameType(recyledViews, mAdapter.getItemType(currentItem), currentItem);
					if (viewItemTemp == null) {
						viewItemTemp = new ViewItem();
					}
					viewItemTemp.index = currentItem;
					viewItemTemp.location = getViewItemLocation(currentItem);
					viewItemTemp.type = mAdapter.getItemType(currentItem);
					viewItemTemp.view = mAdapter.getView(currentItem, viewItemTemp.view, this);
					if (!viewItemTemp.isAttached) {
						addViewItem(viewItemTemp);
					}
					measureChilItem(viewItemTemp);
					layoutItemtChild(viewItemTemp);
					views.add(viewItemTemp);
					if (viewItemTemp.getItemBottom() > mBottom) {
						break;
					}

				} else {
					break;
				}
			} else {
				break;
			}
			currentItem++;
		}
		removeViewFromLayout();
		Collections.sort(views);
	}

	private void fillViewUp() {
		recycleViewNotVisable();
		if (views.size() > 0) {
			ViewItem topItem = views.get(0);
			int nextItemIndex = 0;
			for (;;) {
				if (topItem.getItemTop() > mTop) {
					nextItemIndex = topItem.index - 1;
					if (nextItemIndex >= 0) {
						topItem = findViewItemWithSameType(recyledViews, mAdapter.getItemType(nextItemIndex), nextItemIndex);
						if (topItem == null) {
							topItem = new ViewItem();
						}
						topItem.index = nextItemIndex;
						topItem.location = getViewItemLocation(nextItemIndex);
						topItem.type = mAdapter.getItemType(nextItemIndex);
						topItem.view = mAdapter.getView(nextItemIndex, topItem.view, this);
						if (!topItem.isAttached) {
							addViewItem(topItem);
						}
						measureChilItem(topItem);
						layoutItemtChild(topItem);
						views.add(0, topItem);
					} else {
						break;
					}
				} else {
					break;
				}
			}
		} else {
			fillViewAll();
		}
		removeViewFromLayout();

	}

	private void fillViewDown() {
		recycleViewNotVisable();
		if (views.size() > 0) {
			ViewItem bottomItem = views.get(views.size() - 1);
			int nextItemIndex = 0;
			for (;;) {
				if (bottomItem.getItemBottom() <= mBottom) {
					nextItemIndex = bottomItem.index + 1;
					if (nextItemIndex < mAdapter.getCount()) {
						bottomItem = findViewItemWithSameType(recyledViews, mAdapter.getItemType(nextItemIndex), nextItemIndex);
						if (bottomItem == null) {
							bottomItem = new ViewItem();
						}
						bottomItem.index = nextItemIndex;
						bottomItem.location = getViewItemLocation(nextItemIndex);
						bottomItem.type = mAdapter.getItemType(nextItemIndex);
						bottomItem.view = mAdapter.getView(nextItemIndex, bottomItem.view, this);
						if (!bottomItem.isAttached) {
							addViewItem(bottomItem);
						}
						measureChilItem(bottomItem);
						layoutItemtChild(bottomItem);
						views.add(bottomItem);
					} else {
						break;
					}
				} else {
					break;
				}
			}
		} else {
			fillViewAll();
		}
		removeViewFromLayout();
	}

	private void recycleViewNotVisable() {
		int count = this.views.size();
		for (int i = 0; i < count; i++) {
			ViewItem viewItem = views.get(i);
			if (viewItem.getItemBottom() < mTop || viewItem.getItemTop() > mBottom) {
				recyleView(viewItem, false);
			}
		}
		views.removeAll(recyledViews);
	}

	private int getViewItemLocation(int position) {
		int location = 0;
		for (int i = 0; i < position; i++) {
			location += mAdapter.getItemtHeight(getResources(), i);
		}
		return location + getPaddingTop();
	}

	private void measureChilItem(ViewItem viewItem) {
		viewItem.view.measure(MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(mAdapter.getItemtHeight(getResources(), viewItem.index), MeasureSpec.UNSPECIFIED));
	}

	private void layoutItemtChild(ViewItem viewItem) {
		viewItem.view.layout(0, viewItem.location, viewItem.view.getMeasuredWidth(), viewItem.location + viewItem.view.getMeasuredHeight());
	}

	private void addViewItem(ViewItem viewItem) {
		long sartTime = System.currentTimeMillis();
		viewItem.isAttached = true;
		addViewInLayout(viewItem.view, 0, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

	}

	public void removeViewFromLayout() {
		for (ViewItem viewItem : recyledViews) {
			removeViewInLayout(viewItem.view);
			viewItem.isAttached = false;
		}
	}

	public void recyleViews(List<ViewItem> viewItems, boolean removeFromLayout) {
		for (ViewItem viewItem : viewItems) {
			recyleView(viewItem, removeFromLayout);
		}
		views.removeAll(recyledViews);
	}

	public void recyleView(ViewItem viewItem, boolean removeFromLayout) {
		recyledViews.add(viewItem);
		if (removeFromLayout) {
			removeViewInLayout(viewItem.view);
			viewItem.isAttached = false;
		}

	}

	private ViewItem findViewItemWithSameType(List<ViewItem> views, int itemType, int index) {
		ViewItem bestViewItem = null;
		for (ViewItem viewItem : views) {
			if (viewItem.type == itemType) {

				bestViewItem = viewItem;
				if (viewItem.index == index) {
					break;
				}
			}
		}
		if (bestViewItem != null) {
			recyledViews.remove(bestViewItem);
		}

		return bestViewItem;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event) || executeKeyEvent(event);
	}

	private boolean executeKeyEvent(KeyEvent event) {
		if (isScrolling) {
			return true;
		}
		View currentFocused = findFocus();
		if (currentFocused != null) {
			int direction = -1;
			if (event.getAction() == KeyEvent.ACTION_DOWN) {
				switch (event.getKeyCode()) {
				case KeyEvent.KEYCODE_DPAD_DOWN:
					direction = FOCUS_DOWN;
					break;
				case KeyEvent.KEYCODE_DPAD_LEFT:
					direction = FOCUS_LEFT;
					break;
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					direction = FOCUS_RIGHT;
					break;
				case KeyEvent.KEYCODE_DPAD_UP:
					direction = FOCUS_UP;
					break;
				default:
					return false;
				}
				
				View nextFocus = currentFocused.focusSearch(direction);
				//如果下一个焦点在CListView里面 则开始处理
				if (isChildInViewGroup(this, nextFocus)) {
					if (nextFocus != null) {
						if (nextFocus.requestFocus()) {
							//开始查找下一个焦点具体在那个item上
							ViewItem focusItem = finViewItem(nextFocus);
							if (focusItem != null) {
								//记录当前焦点所在的item index
								currentFocusItemIndex = focusItem.index;
								//检测当前焦点是否可见 如不可见滑动到使他可见的位置
								scrollIfNotVisable(focusItem);
							}
							return true;
						}

					}
				}

			}
		}
		return false;
	}
/**根据所给的item来判断是否需要滑动，上滑时滑到指定item的顶部，下滑时滑动到指定item的底部*/
	private void scrollIfNotVisable(ViewItem viewItem) {
		if (viewItem != null) {
			if (viewItem.getItemTop() < mTop) {
				smoothScrollTo(0, Math.max(0, getScrollY() - (mHeight + (mTop - viewItem.getItemBottom()))), 500);
			} else if (viewItem.getItemBottom() > mBottom) {
				smoothScrollTo(0, viewItem.getItemTop() - getPaddingTop(), 500);
			}
		}

	}
/**通过一个view 来查找他在那个item上*/
	public ViewItem finViewItem(View v) {
		ViewItem item = null;
		if (item == null) {
			for (ViewItem viewItem : views) {
				if (viewItem.view instanceof ViewGroup) {
					if (isChildInViewGroup((ViewGroup) viewItem.view, v)) {
						item = viewItem;
						break;
					}
				} else {
					if (v.equals(viewItem.view)) {
						item = viewItem;
						break;
					}
				}
			}
		}
		return item;
	}
/**判断一个view 是否在一个viewgroup里面*/
	public boolean isChildInViewGroup(ViewGroup vg, View v) {
		if (vg == null || v == null) {
			return false;
		}
		if (vg.indexOfChild(v) != -1) {
			return true;
		}
		int childCount = vg.getChildCount();
		for (int i = 0; i < childCount; i++) {
			View child = vg.getChildAt(i);
			if (child instanceof ViewGroup) {
				if (isChildInViewGroup((ViewGroup) child, v)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	public static abstract class Adapter {
		private final DataSetObservable mDataSetObservable = new DataSetObservable();

		public void registerDataSetObserver(DataSetObserver observer) {
			mDataSetObservable.registerObserver(observer);
		}

		public void unregisterDataSetObserver(DataSetObserver observer) {
			mDataSetObservable.unregisterObserver(observer);
		}

		/**
		 * Notifies the attached observers that the underlying data has been changed and any View reflecting the data set should refresh itself.
		 */
		public void notifyDataSetChanged() {
			mDataSetObservable.notifyChanged();
		}

		/**
		 * Notifies the attached observers that the underlying data is no longer valid or available. Once invoked this adapter is no longer valid and should not report further data set changes.
		 */
		public void notifyDataSetInvalidated() {
			mDataSetObservable.notifyInvalidated();
		}

		public abstract View getView(int position, View convertView, ViewGroup viewParent);

		public abstract int getCount();

		public int getItemType(int position) {
			return 0;
		}

		public abstract int getItemtHeight(Resources res, int position);
	}

	protected class ViewItem implements Comparable<ViewItem> {
		public View view;
		public int type;
		public int index;
		public int location;
		public boolean isAttached = false;

		public int getItemBottom() {
			if (view != null) {
				return view.getBottom();
			} else {
				return 0;
			}

		}

		public int getItemTop() {
			return view.getTop();
		}

		@Override
		public int compareTo(ViewItem another) {
			if (this.index < another.index) {
				return -1;
			} else if (this.index > another.index) {
				return 1;
			} else {
				return 0;
			}
		}
	}

	protected void smoothScrollTo(int x, int y, int duration) {
		smoothScrollBy(x - getScrollX(), y - getScrollY(), duration);
	}

	private void smoothScrollBy(int dx, int dy, int duration) {
		if (dy == 0) {
			return;
		}
		if (mOnScrollListener != null) {
			mOnScrollListener.onScrollStart(this, getScrollY());
		}
		mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, duration);
		invalidate();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			if (mOnScrollListener != null) {
				mOnScrollListener.onScrolling(this, getScrollY());
			}
			isScrolling = true;
			invalidate();
		} else {
			if (isScrolling) {
				if (mOnScrollListener != null) {
					mOnScrollListener.onScrollEnd(this, getScrollY());
				}
				scrollTo(mScroller.getFinalX(), mScroller.getFinalY());
				invalidate();
			}
			isScrolling = false;
		}
		Logger.d(VIEW_LOG_TAG, "CListView computeScroll");

	}

	@Override
	public void scrollTo(int x, int y) {
		long startTime = System.currentTimeMillis();
		int curentY = getScrollY();
		mTop = y + getPaddingTop();
		mBottom = mTop + mHeight;
		if (y < curentY) {
			fillViewUp();
		} else if (y > curentY) {

			fillViewDown();

		}
		currentTopItem = getTopFullVisableItemIndex();
		super.scrollTo(x, y);

	}
/**获取所有存在的item中最上边一个的index*/
	public int getTopItemIndex() {
		if (views.size() > 0) {
			return views.get(0).index;
		} else {
			return 0;
		}
	}
	/**取得可是区域内 最上边的一个item的*/
	public ViewItem getTopFullVisableItem() {
		int size = views.size();
		for (int i = 0; i < size; i++) {
			ViewItem viewItem = views.get(i);
			if (viewItem.location >= mTop) {
				return viewItem;
			}
		}
		return null;
	}
/**取得可是区域内 最上边的一个item的index*/
	public int getTopFullVisableItemIndex() {
		int size = views.size();
		for (int i = 0; i < size; i++) {
			ViewItem viewItem = views.get(i);
			if (viewItem.location >= mTop) {
				return viewItem.index;
			}
		}
		return 0;
	}

	public interface OnScrollListener {
		public void onScrollStart(View v, long scrollY);

		public void onScrolling(View v, long scrollY);

		public void onScrollEnd(View v, long scrollY);
	}

	private class HeaderViewAdapter extends Adapter {

		private Adapter mAdapter;
		private DataSetObserver mDataSetObserver = new DataSetObserver() {

			@Override
			public void onChanged() {
				super.onChanged();
				notifyDataSetChanged();
			}

			@Override
			public void onInvalidated() {
				super.onInvalidated();
				notifyDataSetInvalidated();
			}

		};

		public HeaderViewAdapter(Adapter mAdapter) {
			super();
			this.mAdapter = mAdapter;
			mAdapter.registerDataSetObserver(mDataSetObserver);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup viewParent) {
			if (position < headerViews.size()) {
				return headerViews.get(position);
			} else {
				return mAdapter.getView(position - headerViews.size(), convertView, viewParent);
			}
		}

		@Override
		public int getCount() {
			return headerViews.size() + mAdapter.getCount();
		}

		@Override
		public int getItemType(int position) {
			if (position < headerViews.size()) {
				return HEAD_VIEW_ITEM_TYPE;
			} else {
				return mAdapter.getItemType(position - headerViews.size());
			}
		}

		@Override
		public int getItemtHeight(Resources res, int position) {
			if (position < headerViews.size()) {
				View header = headerViews.get(position);
				header.measure(MeasureSpec.makeMeasureSpec(mWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
				return header.getMeasuredHeight();
			} else {
				return mAdapter.getItemtHeight(getResources(), position - headerViews.size());
			}
		}

		@Override
		public void unregisterDataSetObserver(DataSetObserver observer) {
			super.unregisterDataSetObserver(observer);
			mAdapter.unregisterDataSetObserver(mDataSetObserver);
		}
	}
/**手动设置最后一个获取焦点的view所在item的Index*/
	public void saveCurrentFocusItemIndex(int currentFocusItemIndex) {
		this.currentFocusItemIndex = currentFocusItemIndex;
	}
/**获取最后一个取得焦点的view 所属的item 的index*/
	public int getLastFocusItemIndex() {
		return this.currentFocusItemIndex;
	}
/**设置当前最上边一个item的index有点类似listview的setSelection 这个方法用来快速定位到某一个item*/
	public void setCurrentTopItem(int topItem) {
		if (mAdapter != null && topItem < mAdapter.getCount()) {
			currentTopItem = topItem;
			mScroller.setFinalX(0);
			mScroller.setFinalY(getViewItemLocation(topItem) - getPaddingTop());
			scrollTo(0, mScroller.getFinalY());
		}

	}
/**依据当前滑动位置计算最上边一个item的index*/
	private int computeTopItemIndex() {
		if (mAdapter != null) {
			int count = mAdapter.getCount();
			int location = getPaddingTop();
			int i = 0;
			for (; i < count; i++) {
				if (location >= mTop) {
					return i;
				}
				location += mAdapter.getItemtHeight(getResources(), i);
			}
			return i;
		}
		return 0;
	}
	/**尝试取得一个Item 只可取得当前可视区域内部的，不可见的拿不到*/
	public ViewItem getItemAt(int index) {
		int size = views.size();
		for (int i = 0; i < size; i++) {
			ViewItem viewItem = views.get(i);
			if (viewItem.index == index) {
				return viewItem;
			}
		}
		return null;
	}
	/**获取item的总个数*/
	public int getItemCount() {
		if (mAdapter != null) {
			return mAdapter.getCount();
		} else {
			return 0;
		}
	}

}
