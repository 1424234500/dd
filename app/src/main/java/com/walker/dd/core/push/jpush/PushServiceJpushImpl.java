package com.walker.dd.core.push.jpush;

import android.content.Context;

import com.walker.dd.core.AndroidTools;
import com.walker.dd.core.push.PushService;
import com.walker.mode.PushType;

import cn.jpush.android.api.JPushInterface;

public class PushServiceJpushImpl implements PushService {

    @Override
    public String getType(){
        return PushType.JPUSH;
    }
    @Override
    public void bind(Context context) throws Exception {
        AndroidTools.log("JPushInterface.init");
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
    }

    @Override
    public void rebind(Context context) throws Exception {
        AndroidTools.log(" JPushInterface.resumePush");

        JPushInterface.resumePush(context);
    }


    @Override
    public void unbind(Context context) throws Exception {
        AndroidTools.log("  JPushInterface.stopPush");

        JPushInterface.stopPush(context);

    }
    @Override
    public String getId(Context context){
        String rid = JPushInterface.getRegistrationID(context);
        AndroidTools.log("JPushInterface.getRegistrationID", rid);
        return rid;
    }
}
