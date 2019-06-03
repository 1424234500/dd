package com.walker.dd.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.walker.common.util.Tools;
import com.walker.dd.R;
import com.walker.dd.util.AndroidTools;
import com.walker.socket.client.Client;
import com.walker.socket.client.ClientNetty;
import com.walker.socket.client.OnSocket;

public class NetService extends Service implements OnSocket{
 
	public Client client;//网络工具
	LocalBroadcastManager localBroadcastManager;	//本地的activity广播机制
	NotificationManager notificationManager;//推送栏广播
	@Override
	public void onCreate() {
		super.onCreate();
		out("   onCreate    ");
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);//推送栏广播

 		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		client = new ClientNetty("39.106.111.11", 8093);
		client.setOnSocket(this);
		client.start();
	}

	@Override
	public void onDestroy() {
		client.stop();		//关闭service时自动关闭net连接
		super.onDestroy();
		out("  onDestroy       ");
	}
	
	//之后多次startService方式来传递指令
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null){
			 Bundle bun = intent.getExtras();
			 if(bun != null)  {
				String jsonstr =bun.getString("msg");
				this.client.send(jsonstr);
			 }else{
				 out("NetService. onStartCommand intent.getExtras() 为null ？？？");
			 }
		}else{
			 out("NetService. onStartCommand intent 为null ？？？");
		}
		
		return super.onStartCommand(intent, flags, startId);
	}


	public void push(String str){

		Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.ic_home_black_24dp)//设置小图标
				.setContentTitle("title")
				.setContentText("text")
				.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);
		AndroidTools.systemVoiceToast(this);
	}


	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}


	@Override
	public String out(Object... objects) {
		AndroidTools.out(objects);
		return "";
	}

	@Override
	public void onRead(String socket, String jsonstr) {
		localBroadcastManager.sendBroadcast(new Intent("111").putExtra("msg", jsonstr)); //发送应用内广播

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
