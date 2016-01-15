package com.letv.tvos.gamecenter.launcher;

import android.support.v4.app.Fragment;

import com.letv.tvos.gamecenter.launcher1.GameCenterLauncherFragment1;
import com.stv.launcher.plugin.api.ILePluginCreateApi;

public class GameCenterLauncherPlugin implements ILePluginCreateApi{

	@Override
	public Fragment createFragment() {
		return new GameCenterLauncherFragment1();
	}

}
