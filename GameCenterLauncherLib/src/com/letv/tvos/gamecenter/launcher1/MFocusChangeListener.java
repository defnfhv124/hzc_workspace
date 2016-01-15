package com.letv.tvos.gamecenter.launcher1;

import android.animation.AnimatorSet;
import android.view.View;

import com.letv.tvos.gamecenter.launcher.plugin.R;
import com.letv.tvos.gamecenter.util.AnimationUtil;
import com.letv.tvos.gamecenter.util.Logger;

public class MFocusChangeListener implements View.OnFocusChangeListener {
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
			Logger.printException(new RuntimeException());
			animatorSet = AnimationUtil.startScaleToBigAnimation(v, scaleRate, null);
		} else {
			animatorSet = AnimationUtil.startScaleToSmallAnimation(v, scaleRate, null);
		}
	}

}
