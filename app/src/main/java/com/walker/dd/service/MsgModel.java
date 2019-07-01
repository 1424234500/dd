package com.walker.dd.service;


import com.walker.common.util.Bean;
import com.walker.common.util.FileUtil;
import com.walker.common.util.TimeUtil;
import com.walker.core.database.BaseDao;
import com.walker.dd.struct.Message;
import com.walker.dd.util.AndroidTools;
import com.walker.dd.util.Constant;
import com.walker.dd.util.KeyUtil;
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
     * 消息体存储查询 USER_ID MSG_ID
     */
    public final static String MSG = "MSG";
    public final static String SQL_MSG = "create table if not exists MSG (" +
            "USER_ID varchar(30), " +
            "STA varchar(30), " +
            "SESSION_ID varchar(200), MSG_ID varchar(30), TYPE varchar(30), " +
            "FROM_ID varchar(200), FROM_NAME varchar(200), " +
            "TO_ID varchar(200), TIME varchar(30), TEXT varchar(2000), FILE varchar(600) ) ";

    /**
     * 存储
     */
    public static Message addMsg(BaseDao dao, Message msg){
        String nowUserId = NowUser.getId();

//        Bean data = msg.getData();
        String msgId = msg.getMsgId();//data.get(Key.ID, Key.ID);
        String msgType = msg.getMsgType();//data.get(Key.TYPE, Key.TEXT);

//        User fromUser = msg.getUserFrom();
        String fromUserId = msg.getFromUserId();//fromUser.getId();
        String fromUserName = msg.getFromUserName();//fromUser.getName();
        String toUserId = msg.getToUserId();//msg.getUserTo()[0];
        String time = msg.getTime();//TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss:SSS");
        String text = msg.getText();//data.get(Key.TEXT, "");
        String file = msg.getFile();//data.get(Key.FILE, "");   //存储key -> 下载路径 -> 下载 或者 本地路径path
        String sessionId = msg.getSessionId();//toUserId.equals(nowUserId) ? fromUserId : toUserId;

        //聊天时收到消息 文本不涉及下载  文件类涉及下载
        String sta = msg.getSta();//data.get(Key.STA, Key.STA_DEF);

        //语音 文件 则 需要下载状态等操作 点击后执行
        //检查目标文件本地是否存在
        //文本无加载状态 图片采用网络加载 不需要状态
        if(msgType.equals(Key.TEXT) || msgType.equals(Key.PHOTO)){
            sta = Key.STA_TRUE;
        }else if(sta.equals(Key.STA_FALSE) || sta.length() == 0){    //只对认为失败的做文件检测 其他认为手动更新 以更新为准
            String localPath = KeyUtil.getFileLocal(file);
            if(FileUtil.check(localPath) == 0 ){
                sta = Key.STA_TRUE;
            }else{//若不存在
                sta = Key.STA_FALSE;
            }
        }
        msg.setSta(sta);


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

            msg.setSta(sta);
            msg.setSessionId(sessionId);
            msg.setMsgId(msgId);
            msg.setMsgType(msgType);
            msg.setFromUserId(fromUserId);
            msg.setFromUserName(fromUserName);
            msg.setToUserId(toUserId);
            msg.setTime(time);
            msg.setText(text);
            msg.setFile(file);

        }


        return msg;
    }


    /**
     * 获取最新消息时间节点
     */
    public static String getLastMsgTime(BaseDao dao){
        Map<String, Object> map = dao.findOne("select TIME from " + MSG + " where USER_ID=? order by TIME DESC ", NowUser.getId());
        if(map != null){
            AndroidTools.log(TimeUtil.format(String.valueOf(map.get("TIME")), "yyyy-MM-dd HH:mm:ss:SSS").getTime());
            return String.valueOf(map.get("TIME"));
        }
        return TimeUtil.getTime("yyyy-MM-dd HH:mm:ss:SSS", -1);

    }

    /**
     * 查询时间之前的  15条 按照倒序
     * @param dao
     * @param sessionId
     * @param timeBefore
     * @param count
     * @return
     */
    public static List<Message> findMsg(BaseDao dao, String sessionId, String timeBefore, int count){
        List<Map<String, Object>> list = dao.findPage("select * from " + MSG + " where USER_ID=? and SESSION_ID=? and time <? order by TIME DESC ", 1, count, NowUser.getId(), sessionId, timeBefore);
        List<Message> res = new ArrayList<>();
        for(int i = list.size() - 1; i >= 0; i--) {
            Message item = new Message();

            Bean bean = new Bean(list.get(i));

            item.setSessionId(sessionId);
            item.setMsgId(bean.get("MSG_ID", ""));
            item.setSta(bean.get("STA", ""));
            item.setMsgType(bean.get("TYPE", Key.TEXT));
            item.setFromUserId(bean.get("FROM_ID", ""));
            item.setFromUserName(bean.get("FROM_NAME", ""));
            item.setToUserId(bean.get("TO_ID", ""));
            item.setTime(bean.get("TIME", ""));
            item.setText(bean.get("TEXT", ""));
            item.setFile(bean.get("FILE", ""));

            res.add(item );
            AndroidTools.log(bean.toString());
        }
        return res;
    }



}
