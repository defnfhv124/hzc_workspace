package com.letv.tvos.gamecenter.launcher;


import android.content.Context;
import android.os.Bundle;
import letv.plugin.framework.base.WidgetActivator;
import letv.plugin.framework.base.WidgetContext;
import letv.plugin.framework.core.ContextProvider;

public class GameCenterActivator implements WidgetActivator{

	private static Context mContext = null;

	@Override
	public void start(WidgetContext context) throws Exception {
		initContext(context);
	}

	@Override
	public void start(WidgetContext context, Bundle arg1) throws Exception {
		initContext(context);
	}

	@Override
	public void stop(WidgetContext context) throws Exception {

	}

	private void initContext(WidgetContext context) {
		mContext = context;
//		ContextProvider.init(context);
	}

	public static Context getContext() {
		return mContext;
	}
	
//	public static void setContext(Context context){
//		mContext = context;
//	}

}
