package com.letv.tvos.gamecenter.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.letv.tvos.gamecenter.util.StringUtil;
import com.letv.tvos.gamecenter.widget.TransitionDrawable.AnimListener;

public class FadeInSwitchImageView extends View implements AnimListener {

	private DraweeHolder mDraweeHolderCurrent, mDraweeHolderNext;
	private Bitmap lastCache;
	private CustomViewBaseControllerListener mControllerListener = new CustomViewBaseControllerListener();
	private TransitionDrawable mDrawable = new TransitionDrawable(null, null);
	private CustomViewLoadListener loadListener;
	private boolean animing = false;
	// 不用之后要指空 否则会陷入死循环
	private String savedUrl = null;

	public FadeInSwitchImageView(Context context) {
		super(context);
		init();
	}

	public FadeInSwitchImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public FadeInSwitchImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public void setLoadListener(CustomViewLoadListener loadListener) {
		this.loadListener = loadListener;
	}

	private void init() {
		System.out.println("init");
		GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(getResources()).setFadeDuration(0).build();
		mDraweeHolderCurrent = DraweeHolder.create(hierarchy, getContext());
		hierarchy = new GenericDraweeHierarchyBuilder(getResources()).setFadeDuration(0).build();
		mDraweeHolderNext = DraweeHolder.create(hierarchy, getContext());
		mDrawable.setCallback(this);
		mDrawable.setAnimListener(this);
	}

	public void setUrl(String url) {
		if (animing) {
			savedUrl = url == null ? "" : url;
		} else {
			startLoad(url);
		}
	}

	public void startLoad(String url) {
		if (StringUtil.isEmptyString(url)) {
			mDraweeHolderNext.setController(null);
			showNext();
		}else{
			ImageRequest request = ImageRequestBuilder.newBuilderWithSource(url != null ? Uri.parse(url) : null).setProgressiveRenderingEnabled(true).setResizeOptions(new ResizeOptions(1280, 720)).build();
			DraweeController controller = Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(mDraweeHolderNext.getController()).setControllerListener(mControllerListener).build();
			mDraweeHolderNext.setController(controller);
		}
		

		
	}

	public void setDrawable(Drawable drawable) {
		mDrawable.setmDrawable(drawable);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		mDrawable.setBounds(0, 0, getWidth(), getHeight());
		mDrawable.draw(canvas);
	}

	@Override
	public void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mDraweeHolderNext.onDetach();
		mDraweeHolderCurrent.onDetach();
	}

	@Override
	public void onStartTemporaryDetach() {
		super.onStartTemporaryDetach();
		mDraweeHolderNext.onDetach();
		mDraweeHolderCurrent.onDetach();
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		mDraweeHolderNext.onAttach();
		mDraweeHolderCurrent.onAttach();
	}

	@Override
	public void onFinishTemporaryDetach() {
		super.onFinishTemporaryDetach();
		mDraweeHolderNext.onAttach();
		mDraweeHolderCurrent.onAttach();
	}

	protected boolean verifyDrawable(Drawable who) {
		if (who == mDrawable) {
			return true;
		} else {
			return false;
		}
	}

	private class CustomViewBaseControllerListener extends BaseControllerListener<ImageInfo> {
		@Override
		public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
			super.onFinalImageSet(id, imageInfo, animatable);
			showNext();
		}

		@Override
		public void onIntermediateImageSet(String id, ImageInfo imageInfo) {
			super.onIntermediateImageSet(id, imageInfo);
			postInvalidate();
		}

		@Override
		public void onFailure(String id, Throwable throwable) {
			super.onFailure(id, throwable);
			postInvalidate();
			if (loadListener != null) {
				loadListener.onFailure(FadeInSwitchImageView.this);
			}
		}
	}

	private void showNext() {
		postInvalidate();
		mDrawable.setmDrawable(mDraweeHolderNext.getTopLevelDrawable());
		mDrawable.startTransition(1000);
		DraweeHolder tempHolder = mDraweeHolderCurrent;
		mDraweeHolderCurrent = mDraweeHolderNext;
		mDraweeHolderNext = tempHolder;
		if (loadListener != null) {
			loadListener.onSuccess(this);
		}
	}

	public interface CustomViewLoadListener {
		public void onSuccess(View v);

		public void onFailure(View v);
	}

	@Override
	public void onStart() {
		animing = true;
	}

	@Override
	public void onEnd() {
		animing = false;
		if (savedUrl != null) {
			startLoad(savedUrl);
			savedUrl = null;
		}else{
			mDraweeHolderNext.setController(null);
		}
	}
}
