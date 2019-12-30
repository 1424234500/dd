package com.walker.dd.core.push.jpush;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.walker.dd.R;
import com.walker.dd.core.AndroidTools;
import com.walker.dd.test.jpush.TagAliasOperatorHelper;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class JpushJPushMessageReceiver extends JPushMessageReceiver {


    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        AndroidTools.log("onTagOperatorResult", jPushMessage);
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context,jPushMessage);
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context,JPushMessage jPushMessage){
        AndroidTools.log("onCheckTagOperatorResult", jPushMessage);

        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context,jPushMessage);
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        AndroidTools.log("onAliasOperatorResult", jPushMessage);

        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context,jPushMessage);
        super.onAliasOperatorResult(context, jPushMessage);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        AndroidTools.log("onMobileNumberOperatorResult", jPushMessage);

        TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        AndroidTools.log("onNotifyMessageArrived", notificationMessage);

        String title = notificationMessage.notificationTitle;
        String text = notificationMessage.notificationContent;
        String appkey = notificationMessage.appkey;     //557bf3f8c230ec7cdefb0e06
        String extras = notificationMessage.notificationExtras; //{"hhh":"www"}
        int id = Integer.valueOf(notificationMessage.msgId);  //514982759

        AndroidTools.sendNotification(context, id, title, text);

        super.onNotifyMessageArrived(context, notificationMessage);
    }

    @Override
    public Notification getNotification(Context context, NotificationMessage notificationMessage) {
//        AndroidTools.log("getNotification", notificationMessage);

        return super.getNotification(context, notificationMessage);
    }

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        AndroidTools.log("onMessage", customMessage);

        super.onMessage(context, customMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        AndroidTools.log("onNotifyMessageOpened", notificationMessage);

        super.onNotifyMessageOpened(context, notificationMessage);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
        AndroidTools.log("onNotifyMessageDismiss", notificationMessage);

        super.onNotifyMessageDismiss(context, notificationMessage);
    }

    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
    }

    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        super.onCommandResult(context, cmdMessage);
    }

    @Override
    public void onMultiActionClicked(Context context, Intent intent) {
        super.onMultiActionClicked(context, intent);
    }

    @Override
    public void onNotificationSettingsCheck(Context context, boolean b, int i) {
        super.onNotificationSettingsCheck(context, b, i);
    }
}
