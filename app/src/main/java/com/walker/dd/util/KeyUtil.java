package com.walker.dd.util;

import com.walker.dd.service.NetModel;

/**
 * 键包装工具
 */
public class KeyUtil {
    public static String getProfile(String id){
        return id + ".png";
    }
    public static String getProfileHttp(String id){
        return NetModel.httpDownload(id + ".png");
    }

    public static String getFileHttp(String key){
        return NetModel.httpDownload(key);
    }
    public static String getFileLocal(String key){
        return Constant.dirFile + key;
    }

    public static String getFileCache(String key){
        return Constant.dirCache + key;
    }


    public static String getUpload(){
        return NetModel.httpUpload();
    }





}
