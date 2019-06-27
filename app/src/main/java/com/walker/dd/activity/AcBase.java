package com.walker.dd.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.walker.common.util.Bean;
import com.walker.common.util.JsonUtil;
import com.walker.common.util.Tools;
import com.walker.core.database.BaseDao;
import com.walker.dd.database.BaseDaoImpl;
import com.walker.dd.service.NowUser;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.socket.server_1.Key;
import com.walker.dd.view.DialogBeats;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.MsgBuilder;

public abstract class AcBase extends FragmentActivity implements View.OnClickListener{
    /**
     * 生命周期
     */
    public abstract   void OnCreate(Bundle savedInstanceState);
    public    void OnStart(){}//111111
    public    void OnResume(){}//回来唤醒

    public    void OnPause(){} //离开暂停
    public    void OnStop(){} //1111111
    public    void OnDestroy(){}//finish销毁
    /**
     * 更多菜单点击
     */
    public    void OnMoreClick(){
        toast("OnMoreClick");
    }
    /**
     * handler处理器
     */
    public    void OnHandler(String type, String msg){}
    /**
     * 回退键处理，返回false则执行finish，否则不处理
     */
    public  abstract boolean OnBackPressed();

    /**
     * 收到广播处理
     */
    public abstract void OnReceive(String msgJson);



    public Context getContext(){
        return this;
    }

    //统一注册广播
    LocalBroadcastManager localBroadcastManager;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String msg = intent.getExtras().getString(Constant.BROAD_KEY);
                log("BaseAc.receive." + JsonUtil.get(msg));
                OnReceive(msg);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    /**
     * 数据库处理
     */
    public BaseDao sqlDao ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //registerReceiver(broadcastReceiver, new IntentFilter(MSGTYPE.broadcastUrl));
        //registerReceiver(mBroadcastReceiver, intentFilter);
        //注册应用内广播接收器
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(broadcastReceiver, new IntentFilter(Constant.BROAD_URL));
        sqlDao = new BaseDaoImpl(this);

        OnCreate(savedInstanceState);
    }



    @Override
    protected void onStart() {
        super.onStart();
        OnStart();
    }
    @Override
    protected void onResume() {
        super.onResume();
        OnResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        OnPause();
    }
    @Override
    protected void onStop() {
        OnStop();
        super.onStop();
    }
    @Override
    protected void onDestroy() {
        OnDestroy();
        //unregisterReceiver(mBroadcastReceiver);
        //取消注册应用内广播接收器
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        //此处只是关闭当前ac，不会关闭net service socket
        super.onDestroy();
    }



    private static DialogBeats beatsDialog;

    /**
     * 打开加载页面
     */
    public void loadingStart( ) {
        beatsDialog = new DialogBeats(this);
        //beatsDialog.init();
        beatsDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadingStop();
            }
        }, 6000);
        beatsDialog.setCancelable(true);
//        beatsDialog.setCancelable(false);
    }

    /**
     * 关闭加载页面
     */
    public void loadingStop() {
        if (beatsDialog != null) {
            if (beatsDialog.isShowing()) {
                beatsDialog.dismiss();
                beatsDialog = null;
            }
        }

    }
    /**
     *     handler异步刷新界面
     */
    protected Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            Bundle b = msg.getData();
            String type = b.getString(Key.TYPE);
            String msg1 = b.getString(Key.MSG);
            OnHandler(type, msg1);
        }
    };

    /**
     * handler异步界面控制
     * @param type
     * @param msg
     */
    public void sendHandler(String type, String msg){
        Message message = new Message();
        Bundle b = new Bundle();
        b.putString(Key.TYPE, type);
        b.putString(Key.MSG, msg);
        message.setData(b);
        handler.sendMessage(message);
    }

    /**
     * 发送socket
     */
    public void sendSocket(Msg msg) {
        getApp().send(msg.toString());
    }
    /**
     * 发送socket
     * @param plugin
     * @param data
     */
    public void sendSocket(String plugin, Bean data) {
        sendSocket(plugin, NowUser.getId(), data);
    }
    /**
     * 发送socket
     * @param plugin
     * @param to
     * @param data
     */
    public void sendSocket(String plugin, String to, Object data){
       sendSocket(MsgBuilder.makeMsg(plugin, to, data));
    }

    public Application getApp(){
        return ((Application)getApplication());
    }

    /**
     * 提醒toast
     * @param objects
     */
    public void toast(Object...objects){
        handler.post(new Runnable() {
            @Override
            public void run() {
                log(objects);
                Toast.makeText(AcBase.this, Tools.objects2string(objects), Toast.LENGTH_SHORT).show();
            }
        });

    }

    /**
     * actionbar菜单 返回按钮事件
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.navigation, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
    /**
     * 回退键事件 是否终止退出
     */
    @Override
    public void onBackPressed() {
        if(!OnBackPressed()){
            super.onBackPressed();
        }
    }

    /**
     * 日志输出
     * @param objects
     */
    public void out(Object...objects){
        log(objects);
    }

    /**
     * 日志
     * @param objects
     */
    public void log(Object...objects){
        AndroidTools.out(this.getClass().getName() + "." + Tools.objects2string(objects));
    }



}
