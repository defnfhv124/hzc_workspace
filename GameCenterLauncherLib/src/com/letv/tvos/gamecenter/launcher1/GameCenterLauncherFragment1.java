package com.letv.tvos.gamecenter.launcher1;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.animation.AnimatorSet;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnHoverListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.dataeye.channel.tv.DCChannelAgent;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.letv.tvos.gamecenter.AppConfig;
import com.letv.tvos.gamecenter.GameCenterAction;
import com.letv.tvos.gamecenter.application.network.HttpRequestManager;
import com.letv.tvos.gamecenter.application.network.IRequest;
import com.letv.tvos.gamecenter.application.network.OnNetworkCompleteListener;
import com.letv.tvos.gamecenter.application.network.RequestMaker;
import com.letv.tvos.gamecenter.data.spfs.SharedPrefHelper;
import com.letv.tvos.gamecenter.launcher.GameCenterActivator;
import com.letv.tvos.gamecenter.launcher.plugin.R;
import com.letv.tvos.gamecenter.launcher1.HandlerLooper.LoopInterface;
import com.letv.tvos.gamecenter.launcher1.MyGameObserver.MyGameCursorloaderInterface;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel.DeskTopItem;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel.DeskTopRowItem;
import com.letv.tvos.gamecenter.util.AnimationUtil;
import com.letv.tvos.gamecenter.util.FileUtil;
import com.letv.tvos.gamecenter.util.InstalledApkRecordUtil;
import com.letv.tvos.gamecenter.util.Logger;
import com.letv.tvos.gamecenter.util.MD5Utils;
import com.letv.tvos.gamecenter.util.StatisticsUtil;
import com.letv.tvos.gamecenter.util.StringUtil;
import com.letv.tvos.gamecenter.widget.AsyncImageView;
import com.letv.tvos.gamecenter.widget.BannerIgnoreChildPadingLinerLayout;
import com.letv.tvos.gamecenter.widget.BannerIgnoreChildPadingLinerLayout.SelectChangedListener;
import com.letv.tvos.gamecenter.widget.BannerIgnoreChildPadingLinerLayout.UseroperationInterface;
import com.letv.tvos.gamecenter.widget.CListView.OnScrollListener;
import com.letv.tvos.gamecenter.widget.EllipsizeText;
import com.letv.tvos.gamecenter.widget.FadeInSwitchImageView;
import com.letv.tvos.gamecenter.widget.GameCenterLauncherListView;
import com.letv.tvos.gamecenter.widget.GameCenterLauncherListView.OnBackPressedListener;
import com.letv.tvos.statistics.LetvEventAgent;
import com.stv.launcher.fragment.BaseFragment;
import com.stv.launcher.listener.FragmentActionHandler;

public class GameCenterLauncherFragment1 extends BaseFragment implements OnScrollListener, OnBackPressedListener, OnClickListener {

	private boolean isInit = false;
	private static ExecutorService executorService = (ExecutorService) Executors.newFixedThreadPool(3);
	private static final String TAG = GameCenterLauncherFragment1.class.getSimpleName();
	private GameCenterLauncherListView gclc_gamecenter_launcher_main_container;
	/** 这个值时数据的一个md5 值 用来判断新数据是否和旧数据完全一样 如果一样就不刷新view了 */
	private String dateSingture;
	protected boolean needRefresh = false;
	private DeskTopResponseModel.DeskTopRowItem mGames = new DeskTopRowItem();
	private List<DeskTopRowItem> dataList = new ArrayList<DeskTopResponseModel.DeskTopRowItem>();
	private List<DeskTopRowItem> focusList = new ArrayList<DeskTopResponseModel.DeskTopRowItem>();
	private long lastUpdateTime;
	private PackageInstallReceiver packageInstallReceiver = new PackageInstallReceiver();
	private NetWorkStateBroadcastReceiver mNetWorkStateBroadcastReceiver = new NetWorkStateBroadcastReceiver();
	private DeskTopAdapter mAdapter = new DeskTopAdapter(mGames, dataList);
	private FadeInSwitchImageView fisiv_gamecenter_launcher_background;
	private View header;
	private TextView launcher_item_header_title;
	private EllipsizeText launcher_item_header_msg;
	private View launcher_item_header_text_container;
	private BannerIgnoreChildPadingLinerLayout launcher_item_header_banners;
	private HandlerLooper mHandlerLooper = new HandlerLooper();
	private long loadTime = System.currentTimeMillis();
	private boolean isReportLoadTime = true;
	private boolean PageScrolling = false;
	private int savedTopItemIndex = 0;
	private int savedLastFocusItemIndex = 1;
	private long lastLoadDataTime = 0;
	private DeskTopResponseModel mDataModel;
	private int savedRequestFocused = -1;
	private MyGameObserver myGameObserver;
	/**五秒切换焦点图循环*/
	private MyBannerLooper bannerLooper = new MyBannerLooper();
	/**每小时刷新数据循环*/
	private LoopInterface myDataLoopInterface = new LoopInterface() {

		@Override
		public void onLoop() {
			featchDate();
		}

		@Override
		public long getLoopTime() {
			return 3600000;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Context context = getActivity().getApplicationContext();
		Logger.d(TAG, "onCreate");
		// 预加载图片资源图片
		LetvEventAgent.setAppKey("b2ff638521f421de4cafc81499ecf3ef");
		if(AppConfig.DEBUG_MODE){
			LetvEventAgent.setDebugMode(true);
		}
		DCChannelAgent.initConfig(getActivity().getApplicationContext(), AppConfig.getDataEyeAppId(), "Letv_Debug");
		context.registerReceiver(mNetWorkStateBroadcastReceiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		intentFilter.addAction(Intent.ACTION_PACKAGE_REPLACED);
		intentFilter.addDataScheme("package");
		context.registerReceiver(packageInstallReceiver, intentFilter);

		mGames.itemType = GameCenterAction.FUNCATION_TYPE_APP;
		mGames.name = "我的游戏";
		mGames.type = GameCenterAction.FUNCATION_TYPE_WDYX;
		mGames.picture = "file://" + (AppConfig.DEBUG_IN_SELF ? context : GameCenterActivator.getContext()).getCacheDir() + "/my_game_icon.png";
		mGames.items = new ArrayList<DeskTopResponseModel.DeskTopItem>();
		myGameObserver = new MyGameObserver(new Handler(), context, new MyGamesDataSetChangeListener(),executorService);
		context.getContentResolver().registerContentObserver(UriConstants.CONTENT_URI, true, myGameObserver);

	}

	@Override
	protected boolean onFocusRequested(int arg0) {
		boolean result = false;
		int focusDirection = 0;
		Logger.d(TAG, "onFocusRequested");
		switch (arg0) {
		case BaseFragment.FOCUS_LEFT_IN:
			focusDirection = View.FOCUS_LEFT;
			result =  gclc_gamecenter_launcher_main_container.requestFocus(View.FOCUS_LEFT, gclc_gamecenter_launcher_main_container.getLastFocusItemIndex());
			break;
		case BaseFragment.FOCUS_TOP_IN:
			focusDirection = View.FOCUS_UP;
			result =   gclc_gamecenter_launcher_main_container.requestFocus(View.FOCUS_UP, -1);
			break;
		case BaseFragment.FOCUS_RIGHT_IN:
			focusDirection = View.FOCUS_RIGHT;
			result =   gclc_gamecenter_launcher_main_container.requestFocus(View.FOCUS_RIGHT, gclc_gamecenter_launcher_main_container.getLastFocusItemIndex());
			break;
		case BaseFragment.FOCUS_BOTTOM_IN:
			focusDirection = View.FOCUS_DOWN;
			result =   gclc_gamecenter_launcher_main_container.requestFocus(View.FOCUS_DOWN, -1);
			break;
		}
		if(!result){
			savedRequestFocused = focusDirection;
		}
		return result;
	}

	@Override
	protected View onInflaterContent(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
		Logger.d(TAG, "onInflaterContent");
		LayoutInflater mLayoutInflater = LayoutInflater.from(AppConfig.DEBUG_IN_SELF ? layoutInflater.getContext() : GameCenterActivator.getContext());
		View root = mLayoutInflater.inflate(R.layout.fragment_gamecneter_launcher1, null);
		fisiv_gamecenter_launcher_background = (FadeInSwitchImageView) root.findViewById(R.id.fisiv_gamecenter_launcher_background);
		gclc_gamecenter_launcher_main_container = (GameCenterLauncherListView) root.findViewById(R.id.gclc_gamecenter_launcher_main_container);
		gclc_gamecenter_launcher_main_container.setOnBackPressedListener(this);
		String catcheData = null;
		if (bundle != null) {
			catcheData = bundle.getString("data");
			lastUpdateTime = bundle.getLong("lastUpdateTime", 0);
			savedTopItemIndex = bundle.getInt("savedTopItemIndex");
			savedLastFocusItemIndex = bundle.getInt("savedLastFocusItemIndex");
		}
		loadCacheAndCreateView(catcheData);
		gclc_gamecenter_launcher_main_container.setOnScrollListener(this);
		return root;
	}

	@Override
	protected void onFragmentSeletedPre(boolean isShow) {
		Logger.d(TAG, "onFragmentSeletedPre");
		// PageScrolling = true;
		Logger.e("PageScrolling", "" + true);
	}

	@Override
	protected void onFragmentShowChanged(boolean isShow) {
		Logger.d(TAG, "onFragmentShowChanged");
		if (isShow) {
			if (isInit) {
				featchDate();
			}
			mHandlerLooper.startLoop(myDataLoopInterface);
			StatisticsUtil.onEvent(gclc_gamecenter_launcher_main_container.getContext().getApplicationContext(), "GameCenterLauncherFragmentOnShow");
			checkStateAndStartBannerLoop();
		} else {
			stopBannerLoop();
			mHandlerLooper.stopLoop(myDataLoopInterface);
		}
		PageScrolling = false;
		Logger.e("PageScrolling", "" + false);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		Logger.d(TAG, "onSaveInstanceState");
		super.onSaveInstanceState(outState);
		outState.putInt("savedTopItemIndex", gclc_gamecenter_launcher_main_container.getTopFullVisableItemIndex());
		outState.putInt("savedLastFocusItemIndex", gclc_gamecenter_launcher_main_container.getLastFocusItemIndex());
		outState.putLong("lastUpdateTime", lastUpdateTime);
		if (mDataModel != null) {
			outState.putString("data", new Gson().toJson(mDataModel));
		}

	}

	/** 填充数据 */
	private void fillView(DeskTopResponseModel responseModel) {
		Logger.d(TAG, "fillView");
		if (needRefresh && responseModel != null) {
			if (responseModel.myGame != null) {
				mGames.background = responseModel.myGame.background;
				mGames.cellType = responseModel.myGame.cellType;
				mGames.desktopPic = responseModel.myGame.desktopPic;
				mGames.name = responseModel.myGame.name;
				mGames.parameter = responseModel.myGame.parameter;
				mGames.picture = responseModel.myGame.picture;
				mGames.title = responseModel.myGame.title;
				mGames.type = responseModel.myGame.type;
			}
			dataList.clear();
			if(responseModel.operateItems != null){
				dataList.addAll(responseModel.operateItems);
			}
			focusList.clear();
			if(responseModel.focusPics != null){
				focusList.addAll(responseModel.focusPics);
			}
			
			
			mAdapter.notifyDataSetChanged();
			fillHeader(focusList);
			needRefresh = false;
			if (isReportLoadTime) {
				reportLoadTime();
			}
			checkStateAndStartBannerLoop();
		}

	}

	private void fillHeader(List<DeskTopRowItem> items) {
		if (launcher_item_header_banners != null) {
			int count = launcher_item_header_banners.getChildCount();
			ViewHolder viewHolder;
			if (items != null) {
				for (int i = 0; i < count; i++) {
					View v = launcher_item_header_banners.getChild(i);
					viewHolder = (ViewHolder) v.getTag();
					if (viewHolder == null) {
						viewHolder = createHeaderViewHolder(v);
						v.setOnClickListener(this);
						v.setOnFocusChangeListener(new OnFocusChangeListener() {

							@Override
							public void onFocusChange(View v, boolean hasFocus) {
								if (hasFocus) {
									launcher_item_header_banners.setSelect(v);
									Logger.printException(new RuntimeException());
								}
								
							}
						});
					}
					if (i < items.size()) {
						DeskTopRowItem deskTopRowItem = items.get(i);
						viewHolder.pic.setUrl(deskTopRowItem.picture, v.getResources().getDrawable(R.drawable.gc_launcher_default_bg));
						v.setVisibility(View.VISIBLE);
						v.setTag(R.id.tag_data, deskTopRowItem);
						v.setTag(R.id.tag_index, i);
					} else {
						v.setVisibility(View.GONE);
					}
				}
			}
			launcher_item_header_banners.selectNext();
		}

	}

	private ViewHolder createHeaderViewHolder(View v) {
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.pic = (AsyncImageView) v.findViewById(R.id.v_banner_pic);
		viewHolder.shadow = v.findViewById(R.id.v_banner_shadow);
//		viewHolder.pic.setRoundingParams(v.getResources().getDimension(R.dimen.gs_6_6));
		v.setTag(viewHolder);
		return viewHolder;

	}

	private void reportLoadTime() {
		Logger.d(TAG, "reportLoadTime");
		isReportLoadTime = false;
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("loadTime", String.valueOf(System.currentTimeMillis() - this.loadTime));
		StatisticsUtil.onEvent(gclc_gamecenter_launcher_main_container.getContext().getApplicationContext(), "GameCenterLauncherFragmentLoadTime", params);

	}

	/** 开始解析缓存数据，如不存在则加载磁盘缓存数据 还不存在 加载默认数据 并开始创建view */
	private void loadCacheAndCreateView(String cacheString) {
		Logger.d(TAG, "loadCache");
		new AsyncTask<String, Void, DeskTopResponseModel>() {
			@Override
			protected DeskTopResponseModel doInBackground(String... params) {
				// 创建view
				createViews();
				InstalledApkRecordUtil.getInstance().init(gclc_gamecenter_launcher_main_container.getContext());
				// 加载缓存数据
				String cacheDate = null;
				if (params != null && params.length > 0) {
					cacheDate = params[0];
				}
				// 加载磁盘缓存数据
				if (StringUtil.isEmptyString(cacheDate)) {
					cacheDate = SharedPrefHelper.getInstance(AppConfig.DEBUG_IN_SELF ? gclc_gamecenter_launcher_main_container.getContext().getApplicationContext() : GameCenterActivator.getContext()).getDataCache();
				}
				// 加载默认数据
				if (StringUtil.isEmptyString(cacheDate)) {
					InputStream is = null;
					BufferedInputStream bis = null;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try {

						byte[] buffer = new byte[1024];
						is = (AppConfig.DEBUG_IN_SELF ? gclc_gamecenter_launcher_main_container.getContext().getApplicationContext() : GameCenterActivator.getContext()).getResources().getAssets().open("default_date.dat");
						bis = new BufferedInputStream(is);
						int readlength = bis.read(buffer);
						while (readlength != -1) {
							baos.write(buffer, 0, readlength);
							readlength = bis.read(buffer);
						}
						cacheDate = baos.toString();
						cacheDate = cacheDate.replace("local://", "file://" + gclc_gamecenter_launcher_main_container.getContext().getCacheDir() + "/");
						Logger.d(TAG, "load cache from default dateSingture :" + dateSingture);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							is.close();
						} catch (Exception e2) {
						}
						try {
							bis.close();
						} catch (Exception e2) {
						}
						try {
							baos.close();
						} catch (Exception e2) {
						}
					}
					baos = new ByteArrayOutputStream();
					try {

						byte[] buffer = new byte[1024];
						is = (AppConfig.DEBUG_IN_SELF ? gclc_gamecenter_launcher_main_container.getContext().getApplicationContext() : GameCenterActivator.getContext()).getResources().getAssets().open("copylist.data");
						bis = new BufferedInputStream(is);
						int readlength = bis.read(buffer);
						while (readlength != -1) {
							baos.write(buffer, 0, readlength);
							readlength = bis.read(buffer);
						}
						String copyList = baos.toString();
						List<String> fileLists = new Gson().fromJson(copyList, new TypeToken<ArrayList<String>>() {
						}.getType());
						for (String string : fileLists) {
							InputStream fileInputStream = (AppConfig.DEBUG_IN_SELF ? gclc_gamecenter_launcher_main_container.getContext().getApplicationContext() : GameCenterActivator.getContext()).getResources().getAssets().open(string);
							File file = new File(gclc_gamecenter_launcher_main_container.getContext().getApplicationContext().getCacheDir(), string);
							if (!file.exists()) {
								FileUtil.copyFile(fileInputStream, file);
							}

						}
						Logger.d(TAG, "load cache from default dateSingture :" + dateSingture);
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							is.close();
						} catch (Exception e2) {
						}
						try {
							bis.close();
						} catch (Exception e2) {
						}
						try {
							baos.close();
						} catch (Exception e2) {
						}
					}

				}

				if (cacheDate != null && !"".equals(cacheDate)) {
					try {
						dateSingture = MD5Utils.MD5(cacheDate);
						Logger.d(TAG, "load cache from cache date dateSingture :" + dateSingture);
						return parseDate(cacheDate);
					} catch (Exception e) {
						Logger.printException(e);
					}

				}
				return null;
			}

			@Override
			protected void onPostExecute(DeskTopResponseModel result) {
				Logger.d(TAG, "onPostExecute");
				isInit = true;
				super.onPostExecute(result);
				initHeader(header);
				gclc_gamecenter_launcher_main_container.addHeader(header);
				gclc_gamecenter_launcher_main_container.setAdapter(mAdapter);
				if (result != null) {
					mDataModel = result;
					needRefresh = true;
					if (!PageScrolling) {
						fillView(result);
						gclc_gamecenter_launcher_main_container.saveCurrentFocusItemIndex(savedLastFocusItemIndex);
						gclc_gamecenter_launcher_main_container.setCurrentTopItem(savedTopItemIndex);
						if(savedRequestFocused != -1){
							gclc_gamecenter_launcher_main_container.requestFocus(savedRequestFocused, savedLastFocusItemIndex);
							savedRequestFocused = -1;
						}
					}
					if (isReportLoadTime) {
						reportLoadTime();
					}
				}
				featchDate();

			}

		}.executeOnExecutor(executorService, cacheString);
	}

	@Override
	protected boolean onHomeKeyEventHandled() {
		Logger.d(TAG, "onHomeKeyEventHandled");
		gclc_gamecenter_launcher_main_container.scrollTo(0, 0);
		gclc_gamecenter_launcher_main_container.saveCurrentFocusItemIndex(1);
		return false;
	}

	@Override
	public void setHoverListener(OnHoverListener arg0) {

	}

	/** 解析数据 */
	private DeskTopResponseModel parseDate(String dateString) {
		Logger.d(TAG, "parseDate");
		try {
			DeskTopResponseModel deskTopResponseModel = new Gson().fromJson(dateString, DeskTopResponseModel.class);
			dateSingture = MD5Utils.MD5(dateString);
			return deskTopResponseModel;
		} catch (Exception e) {
			Logger.printException(e);
			return null;
		}
	}

	/** 初始化headerView */
	private void initHeader(View v) {
		launcher_item_header_title = (TextView) v.findViewById(R.id.launcher_item_header_title);
		launcher_item_header_msg = (EllipsizeText) v.findViewById(R.id.launcher_item_header_msg);
		launcher_item_header_msg.setMaxLines(2);
		launcher_item_header_text_container = v.findViewById(R.id.launcher_item_header_text_container);
		launcher_item_header_banners = (BannerIgnoreChildPadingLinerLayout) v.findViewById(R.id.launcher_item_header_banners);
		launcher_item_header_banners.setItemSpacing(v.getResources().getDimensionPixelSize(R.dimen.gs_6_6));
		launcher_item_header_banners.setSelectChangedListener(new MyBannerSelectedChangedListener());
		launcher_item_header_banners.setmUseroperationInterface(new MyBannerOperatedListener());
	}



	@Override
	public void onScrollStart(View v, long scrollY) {
	}

	@Override
	public void onScrolling(View v, long scrollY) {
	}

	@Override
	public void onScrollEnd(View v, long scrollY) {
		if (header != null) {
			if (scrollY > header.getMeasuredHeight()) {
				stopBannerLoop();
			} else {
				checkStateAndStartBannerLoop();
			}
		}
	}


	@Override
	public void onBackPressed() {
		
		if (mFragmentHandler != null) {
			try {
				mFragmentHandler.onFragmentAction(this, FragmentActionHandler.FRAGMENT_ACTION_BACK_KEY, null);
				Logger.d(TAG, "onBackPressed");
			} catch (Throwable e) {
				e.printStackTrace();
				Logger.d(TAG, "exception");
			}

		}

	}

	private boolean onOperated = false;
	private long lastClickEventTime;

	@Override
	public void onPause() {
		super.onPause();
		stopBannerLoop();
	}

	@Override
	public void onResume() {
		super.onResume();
		checkStateAndStartBannerLoop();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopBannerLoop();
		mHandlerLooper.stopLoop(myDataLoopInterface);
		Context context = getActivity().getApplicationContext();
		context.unregisterReceiver(mNetWorkStateBroadcastReceiver);
		context.unregisterReceiver(packageInstallReceiver);
		context.getContentResolver().unregisterContentObserver(myGameObserver);
	}

	/** 创建headerView 和listview里面要用到的item 因为以目前设计最多展示五行 所以创建五个 此步在异步线程执行以免造成卡顿 */
	private void createViews() {
		LayoutInflater layoutInflater = LayoutInflater.from(gclc_gamecenter_launcher_main_container.getContext());
		for (int i = 0; i < 5; i++) {
			gclc_gamecenter_launcher_main_container.addCacheViews(layoutInflater.inflate(R.layout.launcher_row_item, null), 0);
		}
		header = layoutInflater.inflate(R.layout.launcher_row_header_item, null);
	}

	/** 检测header是否可见 如果可见开始滚动循环 当主view滑动Y方向的值大于header的高度时header不可见 */
	private void checkStateAndStartBannerLoop() {
		if (header != null) {
			if (gclc_gamecenter_launcher_main_container.getScrollY() < header.getMeasuredHeight()) {
				mHandlerLooper.startLoop(bannerLooper);
			}
		}
	}

	/** 停止header滚动 */
	private void stopBannerLoop() {
		mHandlerLooper.stopLoop(bannerLooper);
	}

	/** 看不懂的是白痴 */
	@Override
	public void onClick(View v) {
		if (System.currentTimeMillis() - lastClickEventTime > 500) {
			lastClickEventTime = System.currentTimeMillis();
			DeskTopRowItem deskTopRowItem = (DeskTopRowItem) v.getTag(R.id.tag_data);
			if (deskTopRowItem != null) {
				Intent intent = new Intent(GameCenterAction.GAMECENTER_LAUNCHER_ACTION);
				intent.putExtra("resource", GameCenterAction.GAMECENT_LAUNCHER_RESOURCE);
				intent.putExtra("value", deskTopRowItem.parameter);
				intent.putExtra("typeString", deskTopRowItem.type);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("type", deskTopRowItem.type);
				params.put("name", deskTopRowItem.name);
				params.put("index", String.valueOf((Integer) v.getTag(R.id.tag_index)));
				StatisticsUtil.onEvent(v.getContext(), "gclauncher_item_click", params);
				try {
					v.getContext().startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(v.getContext(), "请安装或升级你的游戏中心", 0).show();
				}
			}
		}

	}

	/** 从网上拉取数据 */
	private void featchDate() {
		Logger.d(TAG, "featchDate");
		if (getActivity() == null) {
			return;
		}
		if (System.currentTimeMillis() - lastLoadDataTime < 50000) {
			return;
		}
		lastLoadDataTime = System.currentTimeMillis();
		final long startTime = System.currentTimeMillis();
		IRequest<DeskTopResponseModel> request = RequestMaker.getInstance().getDeskTopItems1Request();
		HttpRequestManager.getInstance(getActivity()).start(request, new OnNetworkCompleteListener<DeskTopResponseModel>() {

			@Override
			public void onNetworkCompleteSuccess(IRequest<DeskTopResponseModel> result, String resultString) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("success", "true");
				params.put("featchTime", String.valueOf(System.currentTimeMillis() - startTime));
				StatisticsUtil.onEvent(gclc_gamecenter_launcher_main_container.getContext().getApplicationContext(), "gamecenter_launcher_featching_time", params);
				if (result != null && result.getResponseObject() != null && result.getResponseObject().getEntity() != null) {
					DeskTopResponseModel deskTopResponseModel = result.getResponseObject().getEntity();
					String dataString = new Gson().toJson(deskTopResponseModel);
					String currentDateSingture = MD5Utils.MD5(dataString);
					if (currentDateSingture.equals(dateSingture)) {
						Logger.d("featch data", "same data return");
						return;
					}
					mDataModel = deskTopResponseModel;
					needRefresh = true;
					if (!PageScrolling) {
						fillView(mDataModel);
					}
					long startTime = System.currentTimeMillis();
					SharedPrefHelper.getInstance(gclc_gamecenter_launcher_main_container.getContext().getApplicationContext()).setDataCache(dataString);
					Logger.d("save using time", "time : " + (System.currentTimeMillis() - startTime));
					if (isReportLoadTime) {
						reportLoadTime();
					}

				}
			}

			@Override
			public void onNetworkCompleteFailed(IRequest<DeskTopResponseModel> result, String resultString) {
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("success", "true");
				params.put("featchTime", String.valueOf(System.currentTimeMillis() - startTime));
				StatisticsUtil.onEvent(gclc_gamecenter_launcher_main_container.getContext().getApplicationContext(), "gamecenter_launcher_featching_time", params);
				Logger.e("netwrok", "server error :" + resultString);
			}
		});
	}

	public class PackageInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = null == intent ? "" : intent.getAction();
			Uri dataUri = intent.getData();
			String packageName = dataUri == null ? "" : dataUri.getSchemeSpecificPart();
			if (action.equals(Intent.ACTION_PACKAGE_REMOVED)) {
				InstalledApkRecordUtil.getInstance().removeInstalledPackage(packageName);
				mAdapter.notifyDataSetChanged();
			} else if (action.equals(Intent.ACTION_PACKAGE_ADDED)) {
				InstalledApkRecordUtil.getInstance().addInstalledPackage(packageName);
				mAdapter.notifyDataSetChanged();
			}
		}

	}

	public class NetWorkStateBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Logger.d(TAG, "onReceive");
			ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (manager != null) {
				NetworkInfo networkInfo = manager.getActiveNetworkInfo();
				if (networkInfo != null && networkInfo.isConnected()) {
					featchDate();
				}

			}
		}

	}
	
	private class MyGamesDataSetChangeListener implements MyGameCursorloaderInterface {

		/** 这个是我的游戏观察者的数据变化接口 检测到数据发生变化 更新数据 */
		@Override
		public void onDataChagned(List<DeskTopItem> data) {
			Logger.d(TAG, "onDataChagned");
			mGames.items.clear();
			mGames.items.addAll(data);
			needRefresh = true;
			if (!PageScrolling) {
				mAdapter.notifyDataSetChanged();
			}
			View v;
		}
		
	}
	
	@Override
	protected void onCrush() {
		super.onCrush();
//		InstalledApkRecordUtil.getInstance().destory();
	}

	private class MyBannerOperatedListener implements UseroperationInterface {

		/** 这个是banner再用户操作的时候产生的回调 当用户操作banner时调用 用户操作banner是暂停一次banner的滚动 */
		@Override
		public void onOperated() {
			onOperated = true;
		}
	}

	private class MyBannerSelectedChangedListener implements SelectChangedListener {
		/** 这个是banner上面被选中的item发生变化的时候发生的回调 有点类似焦点变化监听 */
		@Override
		public void onSelectChanged(View v, boolean isSelect) {
			Logger.d(TAG, "onSelectChanged");
			View shadow = v.findViewById(R.id.v_banner_shadow);
			if (isSelect) {
				v.bringToFront();
				DeskTopRowItem deskTopRowItem = (DeskTopRowItem) v.getTag(R.id.tag_data);
				if (deskTopRowItem != null) {
					if (!StringUtil.isEmptyString(deskTopRowItem.name)) {
						launcher_item_header_title.setText(deskTopRowItem.name);
						launcher_item_header_title.setVisibility(View.VISIBLE);
					} else {
						launcher_item_header_title.setVisibility(View.INVISIBLE);
					}
					if (!StringUtil.isEmptyString(deskTopRowItem.title)) {
						launcher_item_header_msg.setText(deskTopRowItem.title);
						launcher_item_header_msg.setVisibility(View.VISIBLE);
					} else {
						launcher_item_header_msg.setVisibility(View.INVISIBLE);
					}
					if (StringUtil.isEmptyString(deskTopRowItem.name) && StringUtil.isEmptyString(deskTopRowItem.title)) {
						launcher_item_header_text_container.setVisibility(View.INVISIBLE);
					} else {
						launcher_item_header_text_container.setVisibility(View.VISIBLE);
					}
					AnimatorSet as = (AnimatorSet) v.getTag(R.id.tag_anim);
					if (as != null) {
						as.cancel();
					}
					as = AnimationUtil.startScaleToBigAnimation(v, 1.17f, null);
					v.setTag(R.id.tag_anim, as);
					fisiv_gamecenter_launcher_background.setUrl(deskTopRowItem.background);
					if (shadow != null) {
						shadow.setVisibility(View.GONE);
					}
				}
			} else {
				if (shadow != null) {
					shadow.setVisibility(View.VISIBLE);
				}
				AnimatorSet as = (AnimatorSet) v.getTag(R.id.tag_anim);
				if (as != null) {
					as.cancel();
				}
				as = AnimationUtil.startScaleToSmallAnimation(v, 1.17f, null);
				v.setTag(R.id.tag_anim, as);

			}
		}
	}
	
	private class MyBannerLooper implements LoopInterface {

		@Override
		public void onLoop() {
			// 当onOperate 为真时说明 用户刚刚操作过一次banner 所以这时候不能再自动滚动banner 每次用户操作一次都跳过一次自动滚动
			if (!onOperated) {
				launcher_item_header_banners.selectNext();
			} else {
				onOperated = false;
			}

		}

		@Override
		public long getLoopTime() {
			return 5000;
		}
	}
	
	public class ViewHolder {
		public View shadow;
		public AsyncImageView pic;
	}

}
