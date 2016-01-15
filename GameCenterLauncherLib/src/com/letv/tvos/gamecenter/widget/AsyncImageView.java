package com.letv.tvos.gamecenter.widget;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.ImageView;

public class AsyncImageView extends SimpleDraweeView {

	// // private int[] attrs = { android.R.attr.radius, R.attr.scaleWidth, R.attr.scaleHeight };
	// //
	// // private int RADIUS_INDEX = 0;
	// // private int RADIUS_SCALE_WIDTH = 1;
	// // private int RADIUS_SCALE_HEIGHT = 2;
	//
	// private int mRadius;
	// private int mScaleWidth;
	// private int mScaleHeight;
	//
	// // 设置图片是否清除背景
	// private boolean clearBackground;
	//
	// // 是否需要处理Bimap
	// private boolean isProcessor;
	//
	// private DrawableShowProcessor drawableShowProcessor = null;
	//
	// private BitmapProcessor bitmapProcessor;
	//
	// private boolean isForceToPng = false;
	//
	// private boolean isFadeIn = true;

	// public static class CircleBitmapProcessor {
	//
	// }
	private String mImageUrl = null;

	public AsyncImageView(Context context) {
		super(context);
//		setLayerType(LAYER_TYPE_SOFTWARE, null);
//		RoundingParams roundingParams = RoundingParams.fromCornersRadius(30f);
		GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
		GenericDraweeHierarchy hierarchy = builder.setFadeDuration(0).setRoundingParams(getHierarchy() != null && getHierarchy().getRoundingParams() != null ? getHierarchy().getRoundingParams() : null).build();
		setHierarchy(hierarchy);

	}

	public AsyncImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		setLayerType(LAYER_TYPE_SOFTWARE, null);
//		RoundingParams roundingParams = RoundingParams.fromCornersRadius(30f);
		GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
		GenericDraweeHierarchy hierarchy = builder.setFadeDuration(0).setRoundingParams(getHierarchy() != null && getHierarchy().getRoundingParams() != null ? getHierarchy().getRoundingParams() : null).build();
		setHierarchy(hierarchy);
	}

	public AsyncImageView(Context context, AttributeSet attrsSet, int defStyle) {
		super(context, attrsSet, defStyle);
//		setLayerType(LAYER_TYPE_SOFTWARE, null);
//		RoundingParams roundingParams = RoundingParams.fromCornersRadius(30f);
		GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
		GenericDraweeHierarchy hierarchy = builder.setFadeDuration(0).setRoundingParams(getHierarchy() != null && getHierarchy().getRoundingParams() != null ? getHierarchy().getRoundingParams() : null).build();
		setHierarchy(hierarchy);
	}

	// public boolean isForceToPng() {
	// return isForceToPng;
	// }
	//
	// /**
	// * 缓存文件强制存储为png格式 ps：解决图片黑边问题
	// * */
	// public void setForceToPng(boolean isForceToPng) {
	// this.isForceToPng = isForceToPng;
	// }

	public void setRoundingParams(float radius){
		if (getHierarchy() != null) {
			RoundingParams roundingParams = 
				    getHierarchy().getRoundingParams();
			if(roundingParams != null){
				roundingParams.setCornersRadius(radius);
				getHierarchy().setRoundingParams(roundingParams);
			}else{
				getHierarchy().setRoundingParams(RoundingParams.fromCornersRadius(radius));
			}
				
		}
	}
	public boolean setUrl(String url) {
		return setUrl(url,338,190, null,null);
	}

	public boolean setUrl(String url, Drawable defaultDrawable) {
		return setUrl(url,338,190,defaultDrawable,null);
	}

	public boolean setUrl(@NonNull String url, Drawable defaultDrawable, BaseControllerListener callback) {
		// if(bitmapProcessor != null){
		// Picasso.with(getContext()).load(url).placeholder(defaultDrawable).into(this,bitmapProcessor, callback);
		// }else{
		// Picasso.with(getContext()).load(url).placeholder(defaultDrawable).into(this);
		// }
		// \
		if (getHierarchy() != null) {
			getHierarchy().setPlaceholderImage(defaultDrawable);
		}
		if (url != null) {
			if (!url.equals(mImageUrl)) {
				mImageUrl = url;
				if (getHierarchy() != null) {
					getHierarchy().setPlaceholderImage(defaultDrawable);
				}
				ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setProgressiveRenderingEnabled(true).build();
				PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(getController()).build();
				if (callback != null) {
					controller.addControllerListener(callback);
				}
				setController(controller);
			}

		} else {
			mImageUrl = null;
			setImageURI(null);
		}
		return true;
	}

	public boolean setUrl(@NonNull String url, int width, int height, Drawable defaultDrawable, BaseControllerListener callback) {
		// if(bitmapProcessor != null){
		// Picasso.with(getContext()).load(url).placeholder(defaultDrawable).into(this,bitmapProcessor, callback);
		// }else{
		// Picasso.with(getContext()).load(url).placeholder(defaultDrawable).into(this);
		// }
		//
		if (getHierarchy() != null) {
			getHierarchy().setPlaceholderImage(defaultDrawable);
		}
		if (url != null) {
			if (!url.equals(mImageUrl)) {
				mImageUrl = url;
				
				ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(url)).setProgressiveRenderingEnabled(true).setResizeOptions(new ResizeOptions(width, height)).build();
				PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder().setImageRequest(request).setOldController(getController()).build();
				if (callback != null) {
					controller.addControllerListener(callback);
				}
				setController(controller);
			}
		} else {
			mImageUrl = null;
			setImageURI(null);
		}
		return true;
	}

	public boolean setUrl(String url, int res) {
		Drawable drawable = getResources().getDrawable(res);
		return setUrl(url, drawable, null);
	}

	public boolean setUrl(String url, int res, BaseControllerListener callback) {
		Drawable drawable = getResources().getDrawable(res);
		return setUrl(url, drawable, callback);
	}

	public boolean setUrl(String url, BaseControllerListener callback) {
		return setUrl(url, null, callback);
	}

	// public class MBaseControllerListener extends BaseControllerListener<String> {
	//
	// private Context context;
	// private String pageName;
	// private String url;
	//
	// private long requestStartTime;
	//
	// public MBaseControllerListener(String pageName, String url) {
	// super();
	// this.context = context;
	// this.pageName = pageName;
	// this.url = url;
	// }
	//
	// @Override
	// public void onSubmit(String id, Object callerContext) {
	// super.onSubmit(id, callerContext);
	// requestStartTime = System.currentTimeMillis();
	// }
	//
	// @Override
	// public void onFinalImageSet(String id, String imageInfo, Animatable animatable) {
	// super.onFinalImageSet(id, imageInfo, animatable);
	// long requestEndTime = System.currentTimeMillis();
	// System.err.println(pageName + "_" + StringUtil.subImageUrlToName(url) + ":" + TimeUtils.formatRequestTimeDecimal(requestStartTime, requestEndTime));
	//
	// // LetvEventAgent.onEvent(context, pageName + "_" + StringUtil.subImageUrlToName(url), TimeUtils.formatRequestTimeDecimal(requestStartTime, requestEndTime));
	// AndroidApplication.dataEveDCEvent(pageName + "_" + StringUtil.subImageUrlToName(url), TimeUtils.formatRequestTimeDecimal(requestStartTime, requestEndTime));
	// }
	//
	// @Override
	// public void onIntermediateImageSet(String id, String imageInfo) {
	// // TODO Auto-generated method stub
	// super.onIntermediateImageSet(id, imageInfo);
	// }
	//
	// @Override
	// public void onIntermediateImageFailed(String id, Throwable throwable) {
	// // TODO Auto-generated method stub
	// super.onIntermediateImageFailed(id, throwable);
	// }
	//
	// @Override
	// public void onFailure(String id, Throwable throwable) {
	// // TODO Auto-generated method stub
	// super.onFailure(id, throwable);
	// }
	//
	// @Override
	// public void onRelease(String id) {
	// // TODO Auto-generated method stub
	// super.onRelease(id);
	// }
	//
	// }

	// public void setForceToPng(boolean b) {
	// // TODO Auto-generated method stub
	//
	// }

	//
	// public void setBitmapProcessor(BitmapProcessor bitmapProcessor) {
	//
	// }

	// public void setClearBackground(boolean clear) {
	// this.clearBackground = clear;
	// }
	//
	// public boolean isFadeIn() {
	// return isFadeIn;
	// }
	//
	// public void setFadeIn(boolean isFadeIn) {
	// this.isFadeIn = isFadeIn;
	// }

	// @SuppressWarnings("deprecation")
	// @Override
	// public void setImageDrawable(Drawable drawable) {
	// if (clearBackground) {
	// setBackgroundDrawable(null);
	// }
	// super.setImageDrawable(drawable);
	// }

	// @Override
	// public String toString() {
	// return "[mRadius=" + mRadius + ", mScaleX=" + mScaleWidth + ", mScaleY=" + mScaleHeight + "]";
	// }
	//
	// public DrawableShowProcessor getDrawableShowProcessor() {
	// return drawableShowProcessor;
	// }
	//
	// // public void setDrawableShowProcessor(DrawableShowProcessor drawableShowProcessor) {
	// // this.drawableShowProcessor = drawableShowProcessor;
	// // }
	//
	// public BitmapProcessor getBitmapProcessor() {
	// return bitmapProcessor;
	// }
	//
	// public void setBitmapProcessor(BitmapProcessor bitmapProcessor) {
	// this.bitmapProcessor = bitmapProcessor;
	// }
	//
	// /**
	// * 生成圆角图片
	// *
	// * @param source
	// * @param radius
	// * @return
	// */
	// public static Bitmap roundCorners(final Bitmap source, final float radius) {
	// if (source == null) {
	// return null;
	// }
	//
	// int width = source.getWidth();
	// int height = source.getHeight();
	// Paint paint = new Paint();
	// paint.setAntiAlias(true);
	//
	// paint.setColor(android.graphics.Color.WHITE);
	// Bitmap clipped = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	// Canvas canvas = new Canvas(clipped);
	// canvas.drawRoundRect(new RectF(0, 0, width, height), radius, radius, paint);
	// paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
	// canvas.drawBitmap(source, 0, 0, paint);
	// source.recycle();
	// return clipped;
	// }
	//
	// /**
	// * 生成圆形图片
	// *
	// * @param source
	// * @return
	// */
	// public static Bitmap circle(final Bitmap source) {
	// if (source == null) {
	// return null;
	// }
	//
	// int width = source.getWidth();
	// int height = source.getHeight();
	//
	// float radius;
	// // 新图的边长,要画成正方形
	// int clippedBitmapLength;
	// if (width > height) {
	// radius = height / 2;
	// clippedBitmapLength = height;
	// } else {
	// radius = width / 2;
	// clippedBitmapLength = width;
	// }
	//
	// Paint paint = new Paint();
	// paint.setAntiAlias(true);
	// paint.setColor(android.graphics.Color.WHITE);
	//
	// Bitmap clipped = Bitmap.createBitmap(clippedBitmapLength, clippedBitmapLength, Bitmap.Config.ARGB_8888);
	// Canvas canvas = new Canvas(clipped);
	// canvas.drawCircle(clippedBitmapLength / 2, clippedBitmapLength / 2, radius, paint);
	// paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
	// if (width > height) {
	// canvas.drawBitmap(source, (clippedBitmapLength - width) / 2, 0, paint);
	// } else {
	// canvas.drawBitmap(source, 0, (clippedBitmapLength - height) / 2, paint);
	// }
	//
	// source.recycle();
	// return clipped;
	// }
	//
	// // public static class RoundCornerBitmapProcessor implements BitmapProcessor {
	// //
	// // private float radius;
	// //
	// // public RoundCornerBitmapProcessor(float radius) {
	// // this.radius = radius;
	// // }
	// //
	// // @Override
	// // public String getExternalKey() {
	// // return "[mRadius=" + radius + "]";
	// // }
	// //
	// // @Override
	// // public Bitmap processBitmap(Bitmap sourceBitmap) {
	// // return roundCorners(sourceBitmap, radius);
	// // }
	// //
	// // }
	// //
	// // public static class CircleBitmapProcessor implements BitmapProcessor {
	// //
	// // public CircleBitmapProcessor() {
	// // }
	// //
	// // @Override
	// // public String getExternalKey() {
	// // return "[circle=true]";
	// // }
	// //
	// // @Override
	// // public Bitmap processBitmap(Bitmap sourceBitmap) {
	// // return circle(sourceBitmap);
	// // }
	// //
	// // }
	// //
	// // public static class DarkBitmapProcessor implements BitmapProcessor {
	// //
	// // @Override
	// // public String getExternalKey() {
	// // // TODO Auto-generated method stub
	// // return "[dark=true]";
	// // }
	// //
	// // @Override
	// // public Bitmap processBitmap(Bitmap sourceBitmap) {
	// // int width, height;
	// // height = sourceBitmap.getHeight();
	// // width = sourceBitmap.getWidth();
	// //
	// // Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
	// // Canvas c = new Canvas(bmpGrayscale);
	// // Paint paint = new Paint();
	// // ColorMatrix cm = new ColorMatrix();
	// // cm.setSaturation(0);
	// // ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	// // paint.setColorFilter(f);
	// // c.drawBitmap(sourceBitmap, 0, 0, paint);
	// // return bmpGrayscale;
	// // }
	// //
	// // }
	// //
	// public static class CircleBitmapProcessor implements BitmapProcessor{
	//
	// @Override
	// public Drawable getDrawable(Bitmap source) {
	// // TODO Auto-generated method stub
	// return new CircleDrawable(source);
	// }
	//
	// }
	// public static class CircleDrawable extends Drawable{
	// protected final RectF mRect = new RectF(),
	// mBitmapRect;
	// protected final BitmapShader bitmapShader;
	// protected final Paint paint;
	//
	// public CircleDrawable(Bitmap bitmap) {
	//
	// bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
	// mBitmapRect = new RectF (0, 0, bitmap.getWidth(), bitmap.getHeight());
	//
	// paint = new Paint();
	// paint.setAntiAlias(true);
	// paint.setShader(bitmapShader);
	// }
	//
	// @Override
	// protected void onBoundsChange(Rect bounds) {
	// super.onBoundsChange(bounds);
	// mRect.set(0, 0, bounds.width(), bounds.height());
	//
	// // Resize the original bitmap to fit the new bound
	// Matrix shaderMatrix = new Matrix();
	// shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
	// bitmapShader.setLocalMatrix(shaderMatrix);
	//
	// }
	//
	// @Override
	// public void draw(Canvas canvas) {
	// canvas.drawCircle(mRect.width() / 2f, mRect.height() /2f, Math.min(mRect.width(),mRect.height()) /2f, paint);
	// }
	//
	// @Override
	// public int getOpacity() {
	// return PixelFormat.TRANSLUCENT;
	// }
	//
	// @Override
	// public void setAlpha(int alpha) {
	// paint.setAlpha(alpha);
	// }
	//
	// @Override
	// public void setColorFilter(ColorFilter cf) {
	// paint.setColorFilter(cf);
	// }
	//
	//
	// }
	//
}
