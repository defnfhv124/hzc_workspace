package com.letv.tvos.gamecenter.util;

import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
/**
 * @author linan1 2013年11月15日 下午7:02:58
 */
public class AnimationUtil {

	/**
	 * 播放放大动画
	 * 
	 * @param view
	 */
	public static AnimatorSet startScaleToBigAnimation(final View view, float rate, AnimatorListener animatorListener) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, rate);
		oa.setDuration(240);
		oa.setInterpolator(new DecelerateInterpolator());

		ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, rate);
		oa1.setDuration(240);
		oa1.setInterpolator(new DecelerateInterpolator());
		set.playTogether(oa, oa1);
		if (null != animatorListener) {
			set.addListener(animatorListener);
		}

		set.start();

		return set;

	}

	public static AnimatorSet startScaleToBigAnimation(final View view, float rate, AnimatorListener animatorListener, long duration) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, rate);
		oa.setDuration(duration);
		oa.setInterpolator(new DecelerateInterpolator());

		ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, rate);
		oa1.setDuration(duration);
		oa1.setInterpolator(new DecelerateInterpolator());
		set.playTogether(oa, oa1);
		if (null != animatorListener) {
			set.addListener(animatorListener);
		}

		set.start();

		return set;

	}

	/**
	 * 播放缩小动画
	 * 
	 * @param view
	 */
	public static AnimatorSet startScaleToSmallAnimation(View view, float rate, AnimatorListener animatorListener) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "scaleY", rate, 1.0f);
		oa.setDuration(140);
		oa.setInterpolator(new DecelerateInterpolator());

		ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", rate, 1.0f);
		oa1.setDuration(140);
		oa1.setInterpolator(new DecelerateInterpolator());

		set.playTogether(oa, oa1);
		set.start();
		return set;
	}

	public static AnimatorSet startScaleToSmallAnimation(View view, float rate, AnimatorListener animatorListener, long duration) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "scaleY", rate, 1.0f);
		oa.setDuration(duration);
		oa.setInterpolator(new DecelerateInterpolator());

		ObjectAnimator oa1 = ObjectAnimator.ofFloat(view, "scaleX", rate, 1.0f);
		oa1.setDuration(duration);
		oa1.setInterpolator(new DecelerateInterpolator());

		set.playTogether(oa, oa1);
		set.start();
		return set;
	}

	public static void startRotateAnimation(View v, int duration) {
		ObjectAnimator oa = ObjectAnimator.ofFloat(v, "rotation", 0f, 360f);
		oa.setDuration(duration);
		oa.setInterpolator(null);
		oa.setRepeatCount(ValueAnimator.INFINITE);
		oa.start();
	}

	public static void clearAnimation(View v) {
		if (null == v) {
			return;
		}

		if (null != v.getAnimation()) {
			v.getAnimation().cancel();
			v.clearAnimation();
		}
	}

	/**
	 * 向上移动动画
	 * 
	 * @param view
	 * @return
	 */
	public static AnimatorSet startTranlateUpAnimation(final View view, float translationY, float scaleRate,long duration) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();

		AnimatorSet set = new AnimatorSet();

		// 向上位移
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "translationY", 1.0f, translationY);
		oa.setDuration(duration);
		oa.setInterpolator(new DecelerateInterpolator());

		// 放大Y
		ObjectAnimator oaY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scaleRate);
		oaY.setDuration(duration);
		oaY.setInterpolator(new DecelerateInterpolator());
		// 放大X
		ObjectAnimator oaX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scaleRate);
		oaX.setDuration(duration);
		oaX.setInterpolator(new DecelerateInterpolator());

		// set.play(oa);
		set.playTogether(oa, oaY, oaX);
		set.start();

		return set;
	}
	/**
	 * 向上移动动画
	 * 
	 * @param view
	 * @return
	 */
	public static AnimatorSet startTranlateUpAnimation(final View view, float translationY, float scaleRate) {
		return startTranlateUpAnimation(view, translationY, scaleRate, 240);
	}

	
	/**
	 * 向下移动动画
	 * 
	 * @param view
	 * @return
	 */
	public static AnimatorSet startTranlateDownAnimation(final View view, float translationY, float scaleRate) {
		if (null != view.getAnimation()) {
			view.getAnimation().cancel();
		}
		view.clearAnimation();

		AnimatorSet set = new AnimatorSet();

		// 向下位移
		ObjectAnimator oa = ObjectAnimator.ofFloat(view, "translationY", translationY, 1.0f);
		oa.setDuration(240);
		oa.setInterpolator(new DecelerateInterpolator());

		// 缩小Y
		ObjectAnimator oaY = ObjectAnimator.ofFloat(view, "scaleY", scaleRate, 1.0f);
		oaY.setDuration(140);
		oaY.setInterpolator(new DecelerateInterpolator());
		// 缩小X
		ObjectAnimator oaX = ObjectAnimator.ofFloat(view, "scaleX", scaleRate, 1.0f);
		oaX.setDuration(140);
		oaX.setInterpolator(new DecelerateInterpolator());

		// set.play(oa);
		set.playTogether(oa, oaY, oaX);
		set.start();

		return set;
	}
}
