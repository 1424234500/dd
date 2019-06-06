package com.walker.dd.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;
import com.walker.socket.client.Client;
import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.OnSocket;

public class SocketClient implements OnSocket{
 
	Client client;//网络工具
	LocalBroadcastManager localBroadcastManager;	//本地的activity广播机制
	NotificationManager notificationManager;//推送栏广播


	@Override
	public String out(Object... objects) {
		AndroidTools.out(objects);
		return "";
	}

	@Override
	public void onRead(String socket, String jsonstr) {
//		sendBroadcast(new Intent("111").putExtra("msg", jsonstr)); //发送应用内广播

	}

	@Override
	public void onSend(String s, String s1) {

	}

	@Override
	public void onConnect(String s) {

	}

	@Override
	public void onDisconnect(String s) {

	}
}
