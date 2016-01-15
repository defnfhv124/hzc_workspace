package com.letv.tvos.gamecenter.util;

import java.util.Map;

import android.content.Context;

import com.dataeye.channel.tv.DCEvent;
import com.dataeye.channel.tv.DCResourcePair;
import com.dataeye.channel.tv.DCResourcePair$DCResourcePairBuilder;
import com.letv.tvos.statistics.LetvEventAgent;

public class StatisticsUtil {
	public static void onEvent(Context context,String id) {
		DCEvent.onEvent(id);
		LetvEventAgent.onEvent(context, id);
		Logger.d("DELog", "dataEveDCEvent ID:" + id);
	}

	public static void onEvent(Context context,String id, Map<String, String> params) {
		DCEvent.onEvent(id, params);
		LetvEventAgent.onEvent(context, id,params);
		Logger.d("DELog", "dataEveDCEvent ID:" + id + " params:" + params.toString());
	}

	public static void onEvent(Context context,String id, String name) {
		DCEvent.onEvent(id, name);
		LetvEventAgent.onEvent(context, name);
		Logger.d("DELog", "dataEveDCEvent ID:" + id + " params:" + name);
	}
}
