package com.letv.tvos.gamecenter.launcher1;

import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dataeye.a.c;
import com.google.gson.Gson;
import com.letv.tvos.gamecenter.GameCenterAction;
import com.letv.tvos.gamecenter.launcher.plugin.R;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel.DeskTopItem;
import com.letv.tvos.gamecenter.model.DeskTopResponseModel.DeskTopRowItem;
import com.letv.tvos.gamecenter.util.InstalledApkRecordUtil;
import com.letv.tvos.gamecenter.util.Logger;
import com.letv.tvos.gamecenter.util.StatisticsUtil;
import com.letv.tvos.gamecenter.util.StringUtil;
import com.letv.tvos.gamecenter.widget.AsyncImageView;
import com.letv.tvos.gamecenter.widget.CListView.Adapter;
import com.letv.tvos.gamecenter.widget.IgnoreChildPadingLinerLayout;

public class DeskTopAdapter extends Adapter implements OnClickListener {

	private DeskTopResponseModel.DeskTopRowItem myGames;
	private List<DeskTopResponseModel.DeskTopRowItem> items;
	private long lastClickEventTime;

	public DeskTopAdapter(DeskTopResponseModel.DeskTopRowItem myGameys, List<DeskTopResponseModel.DeskTopRowItem> items) {
		super();
		this.myGames = myGameys;
		this.items = items;
	}


	@Override
	public int getCount() {
		int myGame = 0;
		if (myGames != null) {
			myGame = 1;
		}
		if (items != null) {
			return items.size() + myGame;
		}
		return 0 + myGame;
	}

	// @Override
	// public Object getItem(int position) {
	// return null;
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return 0;
	// }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		long sartTime = System.currentTimeMillis();
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.launcher_row_item, null);
			Logger.d("getView", "InflateView using :" + (System.currentTimeMillis() - sartTime) + "ms");
			viewHolder.name = (TextView) convertView.findViewById(R.id.launcher_row_item_name);
			viewHolder.items = (IgnoreChildPadingLinerLayout) convertView.findViewById(R.id.launcher_row_item_items);
			viewHolder.msg = (TextView) convertView.findViewById(R.id.launcher_row_item_no_data_msg);
			viewHolder.items.setItemSpacing((int) convertView.getResources().getDimension(R.dimen.gs_16));
			convertView.setTag(viewHolder);
		}
		viewHolder = (ViewHolder) convertView.getTag();
		if (viewHolder == null) {
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) convertView.findViewById(R.id.launcher_row_item_name);
			viewHolder.items = (IgnoreChildPadingLinerLayout) convertView.findViewById(R.id.launcher_row_item_items);
			viewHolder.items.setItemSpacing((int) convertView.getResources().getDimension(R.dimen.gs_16));
			viewHolder.msg = (TextView) convertView.findViewById(R.id.launcher_row_item_no_data_msg);
			convertView.setTag(viewHolder);
		}
		if (position == 0 && myGames != null) {
			viewHolder.name.setText(myGames.name);
			fillItems(position, viewHolder.items, myGames);
			if(myGames.items == null || myGames.items.size() == 0){
				viewHolder.msg.setVisibility(View.VISIBLE);
				viewHolder.msg.setText(R.string.m_games_no_data_msg);
			}else{
				viewHolder.msg.setVisibility(View.GONE);
			}
			
			// viewHolder.items.setAdapter(new LauncherRowItemAdapter(myGames,position));
		} else {
			if (myGames != null) {
				position--;
			}
			viewHolder.msg.setVisibility(View.GONE);
			DeskTopRowItem deskTopRowItem = items.get(position);
			viewHolder.name.setText(deskTopRowItem.name);
			fillItems(position, viewHolder.items, deskTopRowItem);
			// viewHolder.items.setAdapter(new LauncherRowItemAdapter(deskTopRowItem,position));
		}
		Logger.d("getView", "getView using :" + (System.currentTimeMillis() - sartTime) + "ms");
		return convertView;
	}

	private void fillItems(int rowIndex, IgnoreChildPadingLinerLayout ignoreChildPadingLinerLayout, DeskTopRowItem deskTopRowItem) {
		int count = ignoreChildPadingLinerLayout.getChildCount();
		ItemViewHolder itemViewHolder;
		for (int i = 0; i < count; i++) {
			View v = ignoreChildPadingLinerLayout.getChild(i);
			itemViewHolder = (ItemViewHolder) v.getTag();
			if (itemViewHolder == null) {
				itemViewHolder = createItemViewHolder(v);
				v.setOnClickListener(this);
				v.setOnFocusChangeListener(new MFocusChangeListener());
			}
			if (i == count - 1) {
				itemViewHolder.name.setVisibility(View.GONE);
				itemViewHolder.icon.setVisibility(View.GONE);
				itemViewHolder.bg.setUrl(deskTopRowItem.picture ,v.getResources().getDrawable(R.drawable.gc_launcher_default_bg) );
				v.setTag(R.id.tag_data, deskTopRowItem);
				v.setTag(R.id.tag_row_index, rowIndex);
				v.setTag(R.id.tag_index, i);
			} else {
				if (deskTopRowItem.items != null && i < deskTopRowItem.items.size()) {
					DeskTopItem desktopItem = deskTopRowItem.items.get(i),oldItem = (DeskTopItem) v.getTag(R.id.tag_data);
					if (GameCenterAction.FUNCATION_TYPE_APP.equals(desktopItem.itemType)) {
						itemViewHolder.name.setVisibility(View.VISIBLE);
						itemViewHolder.name.setText(desktopItem.name);
						 if (InstalledApkRecordUtil.getInstance().isInstalled(desktopItem.packageName)) {
						 itemViewHolder.icon.setVisibility(View.VISIBLE);
						 } else {
						 itemViewHolder.icon.setVisibility(View.GONE);
						 }
						if (!StringUtil.isEmptyString(desktopItem.desktopPic)) {
							itemViewHolder.bg.setUrl(desktopItem.desktopPic, v.getResources().getDrawable(R.drawable.gc_launcher_default_bg));
							itemViewHolder.bg.setBackgroundDrawable(null);
							itemViewHolder.appIcon.setVisibility(View.GONE);
							itemViewHolder.name.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.gamecenter_launcher_item_tv_bg));
						} else {
							itemViewHolder.appIcon.setVisibility(View.VISIBLE);
							itemViewHolder.bg.setUrl(null);
							itemViewHolder.bg.setBackgroundResource(getAppIconBackground(i));
							itemViewHolder.appIcon.setUrl(desktopItem.icon, v.getResources().getDrawable(R.drawable.gc_launcher_default_bg));
							itemViewHolder.name.setBackgroundDrawable(null);
							;
						}

					} else {
						itemViewHolder.bg.setUrl(desktopItem.desktopPic, v.getResources().getDrawable(R.drawable.gc_launcher_default_bg));
						itemViewHolder.bg.setBackgroundDrawable(null);
						itemViewHolder.name.setVisibility(View.GONE);
						itemViewHolder.icon.setVisibility(View.GONE);
						itemViewHolder.appIcon.setVisibility(View.GONE);
					}
					v.setTag(R.id.tag_data, desktopItem);
					v.setVisibility(View.VISIBLE);
					v.setTag(R.id.tag_row_index, rowIndex);
					v.setTag(R.id.tag_index, i);
				} else {
					v.setVisibility(View.GONE);
				}
			}

		}
	}

	private ItemViewHolder createItemViewHolder(View v) {
		ItemViewHolder itemViewHolder = new ItemViewHolder();
		itemViewHolder.name = (TextView) v.findViewById(R.id.launcher_item_name);
		itemViewHolder.appIcon = (AsyncImageView) v.findViewById(R.id.launcher_item_app_icon);
		itemViewHolder.bg = (AsyncImageView) v.findViewById(R.id.launcher_item_bg);
		itemViewHolder.icon = (ImageView) v.findViewById(R.id.launcher_item_icon);
//		itemViewHolder.bg.setRoundingParams(v.getResources().getDimension(R.dimen.gs_6_6));
		return itemViewHolder;
	}

	public class ViewHolder {
		public TextView name;
		public IgnoreChildPadingLinerLayout items;
		public TextView msg;
	}

	public class ItemViewHolder {
		public TextView name;
		public AsyncImageView bg;
		public ImageView icon;
		public AsyncImageView appIcon;
	}

	@Override
	public int getItemtHeight(Resources res, int position) {
		return res.getDimensionPixelSize(R.dimen.gs_186);
	}

	public int getAppIconBackground(int index) {
		switch (index % 4) {
		case 0:
			return R.drawable.app_background_1;
		case 1:
			return R.drawable.app_background_2;
		case 2:
			return R.drawable.app_background_3;
		case 3:
			return R.drawable.app_background_4;

		default:
			return R.drawable.app_background_1;
		}
	}

	@Override
	public void onClick(View v) {
		if (System.currentTimeMillis() - lastClickEventTime > 500) {
			lastClickEventTime = System.currentTimeMillis();
			Object dataObject = v.getTag(R.id.tag_data);
			if (dataObject != null) {
				if (dataObject instanceof DeskTopRowItem) {
					DeskTopRowItem data = (DeskTopRowItem) dataObject;
					Intent intent = new Intent(GameCenterAction.GAMECENTER_LAUNCHER_ACTION);
					intent.putExtra("resource", GameCenterAction.GAMECENT_LAUNCHER_RESOURCE);
					intent.putExtra("value", data.parameter);
					intent.putExtra("typeString", data.type);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("type", data.type);
					params.put("name", data.name);
					params.put("index", String.valueOf((Integer) v.getTag(R.id.tag_index)));
					params.put("rowIndex", String.valueOf((Integer) v.getTag(R.id.tag_row_index)));
					StatisticsUtil.onEvent(v.getContext(), "gclauncher_item_click", params);
					try {
						v.getContext().startActivity(intent);
					} catch (Exception e) {
						Toast.makeText(v.getContext(), "请安装或升级你的游戏中心", 0).show();
					}
					
				} else if (dataObject instanceof DeskTopItem) {
					DeskTopItem data = (DeskTopItem) dataObject;
					Intent intent = new Intent(GameCenterAction.GAMECENTER_LAUNCHER_ACTION);
					intent.putExtra("resource", GameCenterAction.GAMECENT_LAUNCHER_RESOURCE);
					intent.putExtra("value", new Gson().toJson(data));
					intent.putExtra("typeString", data.itemType);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("type", data.type);
					params.put("name", data.name);
					params.put("index", String.valueOf((Integer) v.getTag(R.id.tag_index)));
					params.put("rowIndex", String.valueOf((Integer) v.getTag(R.id.tag_row_index)));
					StatisticsUtil.onEvent(v.getContext(), "gclauncher_item_click", params);
					try {
						v.getContext().startActivity(intent);
					} catch (Exception e) {
						Toast.makeText(v.getContext(), "请安装或升级你的游戏中心", 0).show();
					}

				}
			}

		}
	}

}
