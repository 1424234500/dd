package com.walker.dd.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.walker.common.util.TimeUtil;

public class MySP {
	public static void  put(Context context, String name , String value){
		 SharedPreferences sp = context.getSharedPreferences("mysharedpreferences",  0);
		 sp.edit().putString(name, value).commit();
	} 
	public static String get(Context context, String name, String defaultValue  ){
		 SharedPreferences sp = context.getSharedPreferences("mysharedpreferences",  0);
		return  sp.getString(name, defaultValue  );
	}

	public static String KEY = "TIME:";

    /**
     * 过期时间
     */
    public static void  putTime(Context context, String name , String value, long timill){
        SharedPreferences sp = context.getSharedPreferences("mysharedpreferences",  0);
        String time = TimeUtil.format(System.currentTimeMillis() +timill, "yyyy-MM-dd HH:mm:ss:SSS");
        AndroidTools.log(name, value, timill, "过期时间",time);
        sp.edit().putString(name, value).putString(KEY+name, time).commit();
    }
    public static String getTime(Context context, String name, String defaultValue  ){
        SharedPreferences sp = context.getSharedPreferences("mysharedpreferences",  0);
        String t = sp.getString(KEY+name, "");
        if(t.compareTo(TimeUtil.getTimeYmdHmss()) < 0){ //过期
            sp.edit().remove(KEY + name).remove(name).commit();
            AndroidTools.log("过期 ", name, defaultValue, t);
            return defaultValue;
        }
        return  sp.getString(name, defaultValue  );
    }
	
}
