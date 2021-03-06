package com.walker.dd.service;

import com.walker.common.util.Bean;
import com.walker.common.util.Page;
import com.walker.common.util.TimeUtil;
import com.walker.core.database.BaseDao;
import com.walker.core.database.Dao;
import com.walker.dd.activity.AcBase;
import com.walker.dd.activity.main.ActivityRegiste;


import com.walker.mode.*;
import com.walker.socket.server_1.plugin.*;



import java.util.*;

public class SocketService extends  Model{
    /**
     * 私有构造器
     */
    private SocketService(){}
    /**
     * 私有静态内部类
     */
    private static class SingletonFactory{
        private static SocketService instance;
        static {
            System.out.println("静态内部类初始化" + SingletonFactory.class);
            instance = new SocketService();
        }
    }
    /**
     * 内部类模式 可靠
     */
    public static SocketService getInstance(){
        return SingletonFactory.instance;
    }

    /**
     * 用户登陆信息表，成功登录记录
     */
    public final static String LOGIN_USER = "LOGIN_USER";
    public final static String SQL_LOGIN_USER = "create table if not exists LOGIN_USER (ID varchar(30) primary key, PWD varchar(50), NAME varchar(200), PROFILE varchar(200) ) ";


    public static List<Bean> finds(BaseDao sqlDao) {
        List<Bean> res = new ArrayList<>();
        List<Map<String,Object>> list = sqlDao.findPage( "select * from " + LOGIN_USER + " ", 1, 6);
        for(Map<String, Object> map : list){
            res.add(new Bean(map));
        }
        return res;
    }
    public static Bean get(BaseDao sqlDao, String id) {
        Map<String, Object> map = sqlDao.findOne("select * from " + LOGIN_USER + " where ID=? ",  id);
        if(map == null){
            return null;
        }
        return new Bean(map);
    }
    public static int save(BaseDao sqlDao, String id, String pwd, String name, String profile){
        Map<String, Object> llu = sqlDao.findOne("select * from " + LOGIN_USER + " where ID=? ",  id);
        if(llu == null){
            return sqlDao.executeSql("insert into " + LOGIN_USER + " values(?,?,?,?) ",id,pwd,name,profile);
        }else{
            id = nvl(id, llu.get("ID"));
            pwd = nvl(pwd, llu.get("PWD"));
            name = nvl(name,llu.get("NAME"));
            profile = nvl(profile,llu.get("PROFILE"));

            return sqlDao.executeSql("update " + LOGIN_USER + " set ID=?,PWD=?,NAME=?,PROFILE=? where ID=? ",id,pwd,name,profile, id);
        }
    }

    public static int delete(BaseDao sqlDao, String id) {
        return sqlDao.executeSql("delete from " + LOGIN_USER + " where ID=? ", id);
    }


    public static void login(AcBase socket, String id, String pwd, String name, String timeBefore){
        socket.sendSocket(Plugin.KEY_LOGIN, new Bean().put(Key.ID, id).put(Key.PWD, pwd).put(Key.NAME, name).put(Key.BEFORE, timeBefore) );
    }
    public static void login(AcBase socket, String id, String pwd, String name){
        login(socket, id, pwd, name, TimeUtil.getTimeYmdHmss());
    }

    public static void registe(ActivityRegiste activityRegiste, String username, String email, String sex, String pwd) {


    }


}
