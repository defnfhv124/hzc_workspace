package com.letv.tvos.gamecenter.launcher;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.letv.tvos.gamecenter.launcher.plugin.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.letv.tvos.gamecenter.AppConfig;
import com.letv.tvos.gamecenter.GameCenterAction;
import com.letv.tvos.gamecenter.application.network.HttpRequestManager;
import com.letv.tvos.gamecenter.application.network.IRequest;
import com.letv.tvos.gamecenter.application.network.OnNetworkCompleteListener;
import com.letv.tvos.gamecenter.application.network.RequestMaker;
import com.letv.tvos.gamecenter.data.spfs.SharedPrefHelper;
import com.letv.tvos.gamecenter.launcher.GameCenterLauncherContentView.OnBackPressedListener;
import com.letv.tvos.gamecenter.model.DesktopItem;
import com.letv.tvos.gamecenter.util.AppUtil;
import com.letv.tvos.gamecenter.util.Logger;
import com.letv.tvos.gamecenter.util.MD5Utils;
import com.letv.tvos.gamecenter.widget.AsyncImageView;
import com.stv.launcher.fragment.BaseFragment;
import com.stv.launcher.listener.FragmentActionHandler;
/**第一版桌面主fragment*/
public class GameCenterLauncherFragment extends BaseFragment implements OnClickListener, OnBackPressedListener {

	private static final int GAMECENT_LAUNCHER_RESOURCE = 1024;
	private static final String TAG = "GameCenterLauncherFragment";
	private boolean needRefresh = true;
	private long lastUpdateTime = 0;
	// private boolean isShow = false;
	GameCenterLauncherContentView gameCenterLauncherContentView;
	// 缓存数据签名 当从网络获取的数据和本地签名一样时 将不进行更新
	private String dateSingture = "";

	private DateModel mdateModel;
	private DateModel mDefaultDateModel;
	private NetWorkStateBroadcastReceiver mNetWorkStateBroadcastReceiver = new NetWorkStateBroadcastReceiver();
	private PackageInstallReceiver packageInstallReceiver = new PackageInstallReceiver();
	private HashMap<String, Object> notifyPackages = new HashMap<String, Object>();

	@Override
	protected boolean onFocusRequested(int dieatcion) {
		switch (dieatcion) {
		case BaseFragment.FOCUS_LEFT_IN:
			return gameCenterLauncherContentView.requestFirstFocus(View.FOCUS_LEFT);
		case BaseFragment.FOCUS_TOP_IN:
			return gameCenterLauncherContentView.requestFirstFocus(View.FOCUS_UP);
		case BaseFragment.FOCUS_RIGHT_IN:
			return gameCenterLauncherContentView.requestFirstFocus(View.FOCUS_RIGHT);
		case BaseFragment.FOCUS_BOTTOM_IN:
			return gameCenterLauncherContentView.requestFirstFocus(View.FOCUS_DOWN);
		}
		return false;
	}

	@Override
	protected void onFragmentSeletedPre(boolean arg0) {
		long passedTime = System.currentTimeMillis() - lastUpdateTime;
		if (arg0) {
			if (passedTime > 1800000) {
				lastUpdateTime = System.currentTimeMillis();
				featchDate();
			}
		}

	}

	private void loadCache() {
		new AsyncTask<Void, Void, DateModel>() {
			@Override
			protected DateModel doInBackground(Void... params) {

				// 读默认数据
				try {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					InputStream is = (AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext() : GameCenterActivator.getContext()).getResources().getAssets().open("default_date.dat");
					BufferedInputStream bis = new BufferedInputStream(is);
					int readlength = bis.read(buffer);
					while (readlength != -1) {
						baos.write(buffer, 0, readlength);
						readlength = bis.read(buffer);
					}
					String date = baos.toString();
					ArrayList<DesktopItem> items = parseDate(date);
					mDefaultDateModel = createDateModel(items);
					Logger.d(TAG, "load cache from default dateSingture :" + dateSingture);
				} catch (IOException e) {
					e.printStackTrace();
				}

				String cacheDate = SharedPrefHelper.getInstance(AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext() : GameCenterActivator.getContext()).getDataCache();
				// 读缓存数据
				if (cacheDate != null && !"".equals(cacheDate)) {
					try {
						ArrayList<DesktopItem> items = parseDate(cacheDate);
						dateSingture = MD5Utils.MD5(cacheDate);
						Logger.d(TAG, "load cache from cache date dateSingture :" + dateSingture);
						return createDateModel(items);
					} catch (Exception e) {
						Logger.printException(e);
					}

				}
				return null;
			}

			private ArrayList<DesktopItem> parseDate(String dateString) {
				try {
					ArrayList<DesktopItem> items = new Gson().fromJson(dateString, new TypeToken<ArrayList<DesktopItem>>() {
					}.getType());
					dateSingture = MD5Utils.MD5(dateString);
					return items;
				} catch (Exception e) {
					Logger.printException(e);
					return null;
				}
			}

			@Override
			protected void onPostExecute(DateModel result) {
				super.onPostExecute(result);
				needRefresh = true;
				mdateModel = result;
				fillDateToView(result);
				Logger.d(TAG, "load cache success date dateSingture :" + dateSingture);
				featchDate();
			}

		}.execute();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		(AppConfig.DEBUG_IN_SELF ? getActivity().getApplicationContext() : GameCenterActivator.getContext()).registerReceiver(mNetWorkStateBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		intentFilter.addDataScheme("package");
		
		
		(AppConfig.DEBUG_IN_SELF ? getActivity().getApplicationContext() : GameCenterActivator.getContext()).registerReceiver(packageInstallReceiver, intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		(AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext().getApplicationContext() : GameCenterActivator.getContext()).unregisterReceiver(mNetWorkStateBroadcastReceiver);
		(AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext().getApplicationContext() : GameCenterActivator.getContext()).unregisterReceiver(packageInstallReceiver);
	}

	private DateModel createDateModel(List<DesktopItem> items) {
		DateModel dateModel = new DateModel();
		if (items != null) {
			for (DesktopItem desktopItem : items) {
				switch (desktopItem.cellType) {
				case 1:
					dateModel.itemsFunctions.add(desktopItem);
					break;
				case 2:
					dateModel.rank = desktopItem;
					break;
				case 3:
					dateModel.banner = desktopItem;
					break;
				case 4:
					dateModel.itemsType1.add(desktopItem);
					break;
				case 5:
					dateModel.itemsType2.add(desktopItem);
					break;
				default:
					break;
				}
			}
		}
		dateModel.sort();
		return dateModel;

	}

	@Override
	protected void onFragmentShowChanged(boolean isShow) {
		if (isShow && needRefresh) {
			fillDateToView(mdateModel);
		}
	}

	@Override
	protected boolean onHomeKeyEventHandled() {
		gameCenterLauncherContentView.scrollTo(0, 0);
		return false;
	}

	@Override
	protected View onInflaterContent(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
		View root = LayoutInflater.from(AppConfig.DEBUG_IN_SELF ? layoutInflater.getContext() : GameCenterActivator.getContext()).inflate(R.layout.fragment_gamecneter_launcher, null);// layoutInflater.inflate(R.layout.fragment_gamecneter_launcher, null);
		gameCenterLauncherContentView = (GameCenterLauncherContentView) (root.findViewById(R.id.gclcv));
		gameCenterLauncherContentView.setmOnBackPressedListener(this);
		gameCenterLauncherContentView.bannerView.setOnClickListener(this);
		gameCenterLauncherContentView.rankView.setOnClickListener(this);
		for (View v : gameCenterLauncherContentView.itemTypeFuncationsViews) {
			v.setOnClickListener(this);
		}
		for (View v : gameCenterLauncherContentView.itemType1Views) {
			v.setOnClickListener(this);
		}
		for (View v : gameCenterLauncherContentView.itemType2Views) {
			v.setOnClickListener(this);
		}
		if (bundle != null) {
			gameCenterLauncherContentView.scrollTo(0, bundle.getInt("scroolValue", 0));
		}
		loadCache();
		return root;
	}

	@Override
	public void setHoverListener(OnHoverListener arg0) {

	}

	private void featchDate() {
		IRequest<ArrayList<DesktopItem>> request = RequestMaker.getInstance().getDeskTopItemsRequest();
		HttpRequestManager.getInstance(AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext() : GameCenterActivator.getContext()).start(request, new OnNetworkCompleteListener<ArrayList<DesktopItem>>() {

			@Override
			public void onNetworkCompleteSuccess(IRequest<ArrayList<DesktopItem>> result, String resultString) {
				if (result != null && result.getResponseObject() != null && result.getResponseObject().getEntity() != null) {
					ArrayList<DesktopItem> items = result.getResponseObject().getEntity();
					String currentDateJSONString = new Gson().toJson(items);
					String currentDateSingture = MD5Utils.MD5(currentDateJSONString);
					Logger.d(TAG, "load date from server dateSingture :" + currentDateSingture);
					if (!currentDateSingture.equals(dateSingture)) {
						dateSingture = currentDateSingture;
						mdateModel = createDateModel(items);
						SharedPrefHelper.getInstance(gameCenterLauncherContentView.getContext()).setDataCache(currentDateJSONString);
						needRefresh = true;
						fillDateToView(mdateModel);
						Logger.d("GameCenterLauncherFragment", "new date wait to update");
					} else {
						Logger.d("GameCenterLauncherFragment", "same dateSingture throw");
					}

				}
			}

			@Override
			public void onNetworkCompleteFailed(IRequest<ArrayList<DesktopItem>> result, String resultString) {
				Logger.e("netwrok", "server error :" + resultString);
			}
		});
	}

	private void fillDateToView(DateModel dateModel) {
		notifyPackages.clear();
		DesktopItem banner = null;
		if (dateModel != null && dateModel.banner != null) {
			banner = dateModel.banner;
		} else if (mDefaultDateModel != null) {
			banner = mDefaultDateModel.banner;
		}
		float radius = gameCenterLauncherContentView.getContext().getResources().getDimension(R.dimen.gs_6_6);
		if (banner != null) {
			gameCenterLauncherContentView.bannerView.setVisibility(View.VISIBLE);
			gameCenterLauncherContentView.bannerView.setFocusable(true);
			TextView name = (TextView) gameCenterLauncherContentView.bannerView.findViewById(R.id.gs_launcher_item_banner_name);
			TextView msg = (TextView) gameCenterLauncherContentView.bannerView.findViewById(R.id.gs_launcher_item_banner_msg);
			AsyncImageView bg = (AsyncImageView) gameCenterLauncherContentView.bannerView.findViewById(R.id.gs_launcher_item_banner_bg);
			bg.setRoundingParams(radius);
			ImageView icon = (ImageView) gameCenterLauncherContentView.bannerView.findViewById(R.id.gs_launcher_item_banner_install_icon);
			if (GameCenterAction.FUNCATION_TYPE_APP.equals(banner.type)){
				try {
					JSONObject jsonObject = new JSONObject(banner.parameter);
					String pakageName = jsonObject.getString("packageName");
					PackageInfo packageInfo = AppUtil.getPackageInfo((AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext() : GameCenterActivator.getContext()), pakageName);
					if (packageInfo != null) {
						icon.setVisibility(View.VISIBLE);
					}else{
						icon.setVisibility(View.GONE);
					}
					notifyPackages.put(pakageName, banner);
				} catch (JSONException e) {
					e.printStackTrace();
					icon.setVisibility(View.GONE);
				}
			}else{
				icon.setVisibility(View.GONE);
			}
			name.setText(banner.name);
			msg.setText(banner.title);
			bg.setUrl(banner.picture);
			gameCenterLauncherContentView.bannerView.setTag(banner);

		} else {
			gameCenterLauncherContentView.bannerView.setFocusable(false);
			gameCenterLauncherContentView.bannerView.setVisibility(View.GONE);
		}

		DesktopItem rank = null;
		if (dateModel != null && dateModel.rank != null) {
			rank = dateModel.rank;
		} else if (mDefaultDateModel != null) {
			rank = mDefaultDateModel.rank;
		}
		if (rank != null) {
			gameCenterLauncherContentView.rankView.setVisibility(View.VISIBLE);
			gameCenterLauncherContentView.rankView.setFocusable(true);
			TextView rankName = (TextView) gameCenterLauncherContentView.rankView.findViewById(R.id.gs_launcher_item_rank_name);
			TextView appName = (TextView) gameCenterLauncherContentView.rankView.findViewById(R.id.gs_launcher_item_rank_app_name);
			AsyncImageView bg = (AsyncImageView) gameCenterLauncherContentView.findViewById(R.id.gs_launcher_item_rank_bg);
			bg.setRoundingParams(radius);
			rankName.setText(rank.title);
			appName.setText(rank.name);
			bg.setUrl(rank.picture);
			gameCenterLauncherContentView.rankView.setTag(rank);
		} else {
			gameCenterLauncherContentView.rankView.setFocusable(false);
			gameCenterLauncherContentView.rankView.setVisibility(View.GONE);
		}

		for (int i = 0; i < gameCenterLauncherContentView.itemTypeFuncationsViews.size(); i++) {
			ViewGroup vg = (ViewGroup) gameCenterLauncherContentView.itemTypeFuncationsViews.get(i);
			TextView name = (TextView) vg.getChildAt(2);
			AsyncImageView bg = (AsyncImageView) vg.getChildAt(0);
			bg.setRoundingParams(radius);
			ImageView icon = (ImageView) vg.getChildAt(1);
			DesktopItem desktopItem = null;
			if (dateModel != null && i < dateModel.itemsFunctions.size()) {
				desktopItem = dateModel.itemsFunctions.get(i);
			} else if (mDefaultDateModel != null && i < mDefaultDateModel.itemsFunctions.size()) {
				desktopItem = mDefaultDateModel.itemsFunctions.get(i);
			}
			if (desktopItem != null) {
				vg.setVisibility(View.VISIBLE);
				vg.setFocusable(true);
				if (GameCenterAction.FUNCATION_TYPE_APP.equals(desktopItem.type)) {
					name.setText(desktopItem.name);
					name.setVisibility(View.VISIBLE);
					bg.setVisibility(View.VISIBLE);
					
					bg.setUrl(desktopItem.picture);
					try {
						JSONObject jsonObject = new JSONObject(desktopItem.parameter);
						String pakageName = jsonObject.getString("packageName");
						PackageInfo packageInfo = AppUtil.getPackageInfo((AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext() : GameCenterActivator.getContext()), pakageName);
						if (packageInfo != null) {
							icon.setVisibility(View.VISIBLE);
						}else{
							icon.setVisibility(View.GONE);
						}
						notifyPackages.put(pakageName, desktopItem);
					} catch (JSONException e) {
						e.printStackTrace();
						icon.setVisibility(View.GONE);
					}
					// 加载图片。。。。。
				} else {
					icon.setVisibility(View.GONE);
					name.setVisibility(View.GONE);
					bg.setVisibility(View.VISIBLE);
					bg.setUrl(desktopItem.picture);
				}
				vg.setTag(desktopItem);
			} else {
				vg.setVisibility(View.GONE);
				vg.setFocusable(false);
			}
		}

		for (int i = 0; i < gameCenterLauncherContentView.itemType1Views.size(); i++) {
			ViewGroup vg = (ViewGroup) gameCenterLauncherContentView.itemType1Views.get(i);
			TextView name = (TextView) vg.getChildAt(2);
			AsyncImageView bg = (AsyncImageView) vg.getChildAt(0);
			ImageView icon = (ImageView) vg.getChildAt(1);
			bg.setRoundingParams(radius);
			DesktopItem desktopItem = null;
			if (dateModel != null && i < dateModel.itemsType1.size()) {
				desktopItem = dateModel.itemsType1.get(i);
			} else if (mDefaultDateModel != null && i < mDefaultDateModel.itemsType1.size()) {
				desktopItem = mDefaultDateModel.itemsType1.get(i);
			}
			if (desktopItem != null) {
				vg.setVisibility(View.VISIBLE);
				vg.setFocusable(true);
				if (GameCenterAction.FUNCATION_TYPE_APP.equals(desktopItem.type)) {
					name.setText(desktopItem.name);
					name.setVisibility(View.VISIBLE);
					bg.setVisibility(View.VISIBLE);
					bg.setUrl(desktopItem.picture);
					try {
						JSONObject jsonObject = new JSONObject(desktopItem.parameter);
						String pakageName = jsonObject.getString("packageName");
						PackageInfo packageInfo = AppUtil.getPackageInfo((AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext() : GameCenterActivator.getContext()), pakageName);
						if (packageInfo != null) {
							icon.setVisibility(View.VISIBLE);
						}else{
							icon.setVisibility(View.GONE);
						}
						notifyPackages.put(pakageName, desktopItem);
					} catch (JSONException e) {
						e.printStackTrace();
						icon.setVisibility(View.GONE);
					}
					// 加载图片。。。。。
				} else {
					icon.setVisibility(View.GONE);
					name.setVisibility(View.GONE);
					bg.setVisibility(View.VISIBLE);
					bg.setUrl(desktopItem.picture);
				}
				vg.setTag(desktopItem);
			} else {
				vg.setVisibility(View.GONE);
				vg.setFocusable(false);
			}
		}

		for (int i = 0; i < gameCenterLauncherContentView.itemType2Views.size(); i++) {
			ViewGroup vg = (ViewGroup) gameCenterLauncherContentView.itemType2Views.get(i);
			AsyncImageView bg = (AsyncImageView) vg.getChildAt(0);
			bg.setRoundingParams(radius);
			DesktopItem desktopItem = null;
			if (dateModel != null && i < dateModel.itemsType2.size()) {
				desktopItem = dateModel.itemsType2.get(i);
			} else if (mDefaultDateModel != null && i < mDefaultDateModel.itemsType2.size()) {
				desktopItem = mDefaultDateModel.itemsType2.get(i);
			}
			if (desktopItem != null) {
				vg.setVisibility(View.VISIBLE);
				vg.setFocusable(true);
				vg.setTag(desktopItem);
				bg.setUrl(desktopItem.picture);
			} else {
				vg.setVisibility(View.GONE);
				vg.setFocusable(false);
			}

		}
		needRefresh = false;
	}

	public static class DateModel {
		public DesktopItem banner;
		public DesktopItem rank;
		public ArrayList<DesktopItem> itemsType1;
		public ArrayList<DesktopItem> itemsType2;
		public ArrayList<DesktopItem> itemsFunctions;

		public DateModel() {
			super();
			itemsType1 = new ArrayList<DesktopItem>();
			itemsType2 = new ArrayList<DesktopItem>();
			itemsFunctions = new ArrayList<DesktopItem>();

		}
		
		public void sort(){
			Collections.sort(itemsFunctions);
			Collections.sort(itemsType1);
			Collections.sort(itemsType2);
			
		}

	}

	@Override
	public void onClick(View v) {
		Object object = v.getTag();
		if (object instanceof DesktopItem) {
			DesktopItem desktopItem = (DesktopItem) object;
			Intent intent = new Intent("com.letv.tvos.gamecenter.external.new");
			intent.putExtra("resource", GAMECENT_LAUNCHER_RESOURCE);
			intent.putExtra("value", desktopItem.parameter);
			intent.putExtra("typeString", desktopItem.type);
			try {
				(AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext() : GameCenterActivator.getContext()).sendBroadcast(intent);
			} catch (Exception e) {
				Logger.printException(e);
				Toast.makeText((AppConfig.DEBUG_IN_SELF ? gameCenterLauncherContentView.getContext() : GameCenterActivator.getContext()), "游戏中心不存在，请下载游戏中心", 0).show();
			}

		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("scroolValue", gameCenterLauncherContentView.getScrollY());
	}

	@Override
	public void onBackPressed() {
		if (mFragmentHandler != null) {
			mFragmentHandler.onFragmentAction(this, FragmentActionHandler.FRAGMENT_ACTION_BACK_KEY, null);
		}
	}

	public class NetWorkStateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manager != null) {
				NetworkInfo networkInfo = manager.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					featchDate();
				}

			}
		}

	}
	
	public class PackageInstallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String packageName = intent.getDataString();
			if (packageName != null && packageName.contains(":")) {
				packageName = packageName.substring(packageName.indexOf(":") + 1);
				if(notifyPackages.get(packageName) != null && mdateModel != null){
					fillDateToView(mdateModel);
				}
			}
			
		}
		
	}

}
