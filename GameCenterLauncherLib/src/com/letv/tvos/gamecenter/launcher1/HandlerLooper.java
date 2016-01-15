package com.letv.tvos.gamecenter.launcher1;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.Handler;
import android.os.Message;

public class HandlerLooper extends Handler{

	public HashMap<Integer,WeakReference<LoopInterface>> loops = new HashMap<Integer, WeakReference<LoopInterface>>();
	@Override
	public void handleMessage(Message msg) {
		super.handleMessage(msg);
		WeakReference<LoopInterface> loopIReferenceWeakReference = loops.get(msg.what);
		if(loopIReferenceWeakReference != null){
			LoopInterface loop = loopIReferenceWeakReference.get();
			if(loop != null){
				loop.onLoop();
				sendEmptyMessageDelayed(loop.hashCode(), loop.getLoopTime());
			}
		}
	}
	
	public interface LoopInterface{
		public void onLoop();
		public long getLoopTime();
	}
	
	public void startLoop(LoopInterface loop){
		loops.put(loop.hashCode(), new WeakReference<LoopInterface>(loop));
		removeMessages(loop.hashCode());
		sendEmptyMessageDelayed(loop.hashCode(), loop.getLoopTime());
	}
	
	public void stopLoop(LoopInterface loop){
		removeMessages(loop.hashCode());
		loops.remove(loop.hashCode());
	}
	
}
