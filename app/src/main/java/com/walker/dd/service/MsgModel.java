package com.walker.dd.service;


import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.core.database.BaseDao;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.session.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 消息体的存储
 */
public class MsgModel extends Model{

    /**
     * 消息体存储查询
     */
    public final static String MSG = "MSG";
    public final static String SQL_MSG = "create table if not exists MSG (" +
            "USER_ID varchar(30), " +
            "STA varchar(30), " +
            "SESSION_ID varchar(200), MSG_ID varchar(30) primary key, TYPE varchar(30), " +
            "FROM_ID varchar(200), FROM_NAME varchar(200), " +
            "TO_ID varchar(200), TIME varchar(30), TEXT varchar(2000), FILE varchar(600) ) ";

    /**
     * 存储
     */
    public static Bean addMsg(BaseDao dao, Msg msg){
        String nowUserId = NowUser.getId();

        Bean data = msg.getData();
        String msgId = data.get(Key.ID, Key.ID);
        String msgType = data.get(Key.TYPE, Key.TEXT);


        //聊天时收到消息 文本不涉及下载  文件类涉及下载
        String sta = msgType.equals(Key.TEXT)? Key.STA_TRUE : data.get(Key.STA, Key.STA_DEF);


        User fromUser = msg.getUserFrom();
        String fromUserId = fromUser.getId();
        String fromUserName = fromUser.getName();
        String toUserId = msg.getUserTo()[0];
        String time = TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss:SSS");
        String text = data.get(Key.TEXT, "");
        String file = data.get(Key.FILE, "");

        String sessionId = toUserId.equals(nowUserId) ? fromUserId : toUserId;

        int count = -1;
        Map<String, Object> getmap = dao.findOne("select * from " + MSG + " where MSG_ID=? and USER_ID=? ",  msgId, nowUserId);
        if(getmap == null){
            count = dao.executeSql("insert into " + MSG + " values(?,?,?,?,?,?,?,?,?,?,?) ",nowUserId,sta,sessionId,msgId, msgType, fromUserId, fromUserName, toUserId, time, text, file);
        }else{
            Bean get = new Bean(getmap);

//            "ID varchar(30) primary key, TYPE varchar(30), " +
//                    "FROM_ID varchar(200), FROM_NAME varchar(200), " +
//                    "TO_ID varchar(200), TIME varchar(30), TEXT varchar(2000), FILE varchar(600) ) ";

            sta = nvl(sta, get.get("STA", ""));
            sessionId = nvl(sessionId, get.get("SESSION_ID", ""));
            msgId = nvl(msgId, get.get("MSG_ID", ""));
            msgType = nvl(msgType, get.get("TYPE", ""));
            fromUserId = nvl(fromUserId, get.get("FROM_ID", ""));
            fromUserName = nvl(fromUserName, get.get("FROM_NAME", ""));
            toUserId = nvl(toUserId, get.get("TO_ID", ""));
            time = nvl(time, get.get("TIME", ""));
            text = nvl(text, get.get("TEXT", ""));
            file = nvl(file, get.get("FILE", ""));

            count = dao.executeSql("update " + MSG + " set USER_ID=?,STA=?,SESSION_ID=?,MSG_ID=?,TYPE=?,FROM_ID=?,FROM_NAME=?,TO_ID=?,TIME=?,TEXT=?,FILE=? where MSG_ID=? and USER_ID=? ",
                    nowUserId,sta,sessionId,msgId,msgType,fromUserId,fromUserName,toUserId,time,text,file, msgId, nowUserId);
        }


        Bean bean = new Bean()
                .set(Key.ID, msgId)
                .set(Key.STA, sta)
                .set(Key.TYPE, msgType)
                .set(Key.FROM, fromUser)
                .set(Key.TO, toUserId)
                .set(Key.TIME, time)
                .set(Key.TEXT, text)
                .set(Key.FILE, file)
        ;
        return bean;
    }

    /**
     * 查询时间之前的  15条 按照倒序
     * @param dao
     * @param sessionId
     * @param timeBefore
     * @param count
     * @return
     */
    public static List<Bean> findMsg(BaseDao dao, String sessionId, String timeBefore, int count){
        List<Map<String, Object>> list = dao.findPage("select * from " + MSG + " where USER_ID=? and SESSION_ID=? and time <? order by TIME DESC ", 1, count, NowUser.getId(), sessionId, timeBefore);
        List<Bean> res = new ArrayList<>();
        for(int i = list.size() - 1; i >= 0; i--) {

            Bean bean = new Bean(list.get(i));
//            String sessionId = bean.get("SESSION_ID", "");
            String sta = bean.get("STA", "");
            String msgId = bean.get("MSG_ID", "");
            String msgType = bean.get("TYPE", Key.TEXT);
            User fromUser = new User();
            fromUser.setId(bean.get("FROM_ID", ""));
            fromUser.setName(bean.get("FROM_NAME", ""));
            String toUserId = bean.get("TO_ID", "");
            String time = bean.get("TIME", "");
            String text = bean.get("TEXT", "");
            String file = bean.get("FILE", "");
            Bean item = new Bean()
                    .set(Key.ID, msgId)
                    .set(Key.STA, sta)
                    .set(Key.TYPE, msgType)
                    .set(Key.FROM, fromUser)
                    .set(Key.TO, toUserId)
                    .set(Key.TIME, time)
                    .set(Key.TEXT, text)
                    .set(Key.FILE, file)
            ;
            res.add(item );
        }
        return res;
    }



}
