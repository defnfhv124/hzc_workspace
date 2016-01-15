package com.letv.tvos.gamecenter.widget;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.SystemClock;

/**
 * An extension of LayerDrawables that is intended to cross-fade between the first and second layer. To start the transition, call {@link #startTransition(int)}. To display just the first layer, call {@link #resetTransition()}.
 * <p>
 * It can be defined in an XML file with the <code>&lt;transition></code> element. Each Drawable in the transition is defined in a nested <code>&lt;item></code>. For more
 * information, see the guide to <a
 * href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a
 * href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a
 * href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}. For more information, see the guide to <a href="{@docRoot}guide/topics/resources/drawable-resource.html">Drawable Resources</a>.
 * </p>
 * 
 * @attr ref android.R.styleable#LayerDrawableItem_left
 * @attr ref android.R.styleable#LayerDrawableItem_top
 * @attr ref android.R.styleable#LayerDrawableItem_right
 * @attr ref android.R.styleable#LayerDrawableItem_bottom
 * @attr ref android.R.styleable#LayerDrawableItem_drawable
 * @attr ref android.R.styleable#LayerDrawableItem_id
 * 
 */
public class TransitionDrawable extends Drawable {
	/**
	 * A transition is about to start.
	 */
	private static final int TRANSITION_STARTING = 0;

	/**
	 * The transition has started and the animation is in progress
	 */
	private static final int TRANSITION_RUNNING = 1;

	/**
	 * No transition will be applied
	 */
	private static final int TRANSITION_NONE = 2;

	/**
	 * The current state of the transition. One of {@link #TRANSITION_STARTING}, {@link #TRANSITION_RUNNING} and {@link #TRANSITION_NONE}
	 */
	private int mTransitionState = TRANSITION_NONE;
	private int mAlpha;
	/** 将要消失掉的drawable */
	private Drawable toBeFadeOut;
	private Drawable mDrawable;
	private boolean isDone = true;
	private long mStartTimeMillis;
	private int mFrom;
	private int mTo;
	private int mDuration;
	private int mOriginalDuration;
	private boolean mCrossFade;
	private AnimListener mAnimListener;

	public TransitionDrawable(Drawable toBeFadeOut, Drawable mDrawable) {
		super();
		this.toBeFadeOut = toBeFadeOut;
		this.mDrawable = mDrawable;
	}

	public AnimListener getmAnimListener() {
		return mAnimListener;
	}
	
	public void setAnimListener(AnimListener mAnimListener) {
		this.mAnimListener = mAnimListener;
	}

	public Drawable getmDrawable() {
		return mDrawable;
	}

	public void setmDrawable(Drawable mDrawable) {
		toBeFadeOut = this.mDrawable;
		this.mDrawable = mDrawable;
		isDone = false;
	}

	public Drawable getDrawable() {
		return mDrawable;
	}

	@Override
	public void draw(Canvas canvas) {
		if (isDone) {
			if (mDrawable != null) {
				mDrawable.draw(canvas);
			}
		} else {
			if (toBeFadeOut != null) {
				toBeFadeOut.draw(canvas);
			}
			if (mDrawable != null) {
				mDrawable.draw(canvas);
			}
			updateAndInvalidate();
		}
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}

	public void startTransition(int durationMillis) {
		mFrom = 0;
		mTo = 255;
		mAlpha = 0;
		mDuration = mOriginalDuration = durationMillis;
		mTransitionState = TRANSITION_STARTING;
		updateAndInvalidate();
		if(mAnimListener != null){
			mAnimListener.onStart();
		}
	}

	public void updateAndInvalidate() {
		switch (mTransitionState) {
		case TRANSITION_STARTING:
			mStartTimeMillis = SystemClock.uptimeMillis();
			isDone = false;
			mTransitionState = TRANSITION_RUNNING;
			break;

		case TRANSITION_RUNNING:
			if (mStartTimeMillis >= 0) {
				float normalized = (float) (SystemClock.uptimeMillis() - mStartTimeMillis) / mDuration;
				isDone = normalized >= 1.0f;
				normalized = Math.min(normalized, 1.0f);
				mAlpha = (int) (mFrom + (mTo - mFrom) * normalized);
			}
			break;
		}
		if (isDone) {
			toBeFadeOut = null;
			if(mAnimListener != null){
				mAnimListener.onEnd();
			}
		}
		if (mDrawable != null) {
			mDrawable.setAlpha(mAlpha);
		}

		if (toBeFadeOut != null) {
			toBeFadeOut.setAlpha(255 - mAlpha);
		}
		invalidateSelf();
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		if (mDrawable != null) {
			mDrawable.setBounds(left, top, right, bottom);
		}
		if (toBeFadeOut != null) {
			toBeFadeOut.setBounds(left, top, right, bottom);
		}
	}

	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
		if (mDrawable != null) {
			mDrawable.setBounds(bounds);
		}
		if (toBeFadeOut != null) {
			toBeFadeOut.setBounds(bounds);
		}
	}

	@Override
	protected boolean onLevelChange(int level) {
		return super.onLevelChange(level);
	}
	
	public interface AnimListener{
		public void onStart();
		public void onEnd();
	}

}
