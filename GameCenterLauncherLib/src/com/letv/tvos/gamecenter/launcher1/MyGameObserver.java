package com.letv.tvos.gamecenter.launcher1;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.letv.tvos.gamecenter.GameCenterAction;
import com.letv.tvos.gamecenter.launcher1.UriConstants.Project;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel;
import com.letv.tvos.gamecenter.model.DesktopItem;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel.DeskTopItem;
import com.letv.tvos.gamecenter.util.Logger;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;

public class MyGameObserver extends ContentObserver{

	private static final String tag = "MyGameObserver";
	private Context context;
	private ExecutorService executorService;
	private MyGameCursorloaderInterface mGameCursorloaderInterface;
	public MyGameObserver(Handler handler,Context context,MyGameCursorloaderInterface cursorloaderInterface,ExecutorService executorService) {
		super(handler);
		this.context = context;
		this.mGameCursorloaderInterface = cursorloaderInterface;
		new MyQueryTask().executeOnExecutor(executorService, null);
		this.executorService = executorService;
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		new MyQueryTask().executeOnExecutor(executorService, null);
		
	}
	
	private class MyQueryTask extends AsyncTask<Void, Void, List<DeskTopItem>>{

		@Override
		protected List<DeskTopItem> doInBackground(Void... params) {
			List<DeskTopResponseModel.DeskTopItem> apps = new ArrayList<DeskTopResponseModel.DeskTopItem>();
			Cursor cursor = context.getContentResolver().query(UriConstants.CONTENT_URI, null, null, null, new StringBuffer().append(Project.APP_LATEST_OPERATE_TIME).append(" DESC").append(" limit 4").toString());
			if (null != cursor && cursor.getCount() > 0) {
				if (cursor.moveToFirst()) {
					do {
						DeskTopResponseModel.DeskTopItem desktopItem = new DeskTopResponseModel.DeskTopItem();
						desktopItem.id = cursor.getInt(cursor.getColumnIndex(Project.APP_ID));
						desktopItem.packageName = cursor.getString(cursor.getColumnIndex(Project.APP_PAG_NAGE));
						desktopItem.name = cursor.getString(cursor.getColumnIndex(Project.APP_LABEL_NAME));
						desktopItem.desktopPic = cursor.getString(cursor.getColumnIndex(Project.APP_DESKTOP_PIC));
						desktopItem.icon = cursor.getString(cursor.getColumnIndex(Project.APP_ICON));
						desktopItem.itemType = GameCenterAction.FUNCATION_TYPE_APP;
						apps.add(desktopItem);
					} while (cursor.moveToNext());
				}
				cursor.close();
			}
			return apps;
		}

		@Override
		protected void onPostExecute(List<DeskTopItem> result) {
			super.onPostExecute(result);
			if (mGameCursorloaderInterface != null) {
				mGameCursorloaderInterface.onDataChagned(result);
			}
		}
		
		
		
	}
	public interface MyGameCursorloaderInterface{
		public void onDataChagned(List<DeskTopItem> data);
	}
}
