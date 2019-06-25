package com.walker.dd.struct;

import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.dd.service.NowUser;
import com.walker.socket.server_1.Key;
import com.walker.socket.server_1.Msg;
import com.walker.socket.server_1.session.User;

public class Message  {
    String msgId;
    String msgType;
    String fromUserId;
    String fromUserName;
    String toUserId;
    String time;
    String text;
    String file;
    String sta;
    String sessionId;


    /**
     * 不存数据库
     */
    String info;


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }



    public String getSta() {
        return sta;
    }

    public void setSta(String sta) {
        this.sta = sta;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Message(){}
    public Message(Msg msg) {
        String nowUserId = NowUser.getId();

        Bean data = msg.getData();
        msgId = data.get(Key.ID, Key.ID);
        msgType = data.get(Key.TYPE, Key.TEXT);

        User fromUser = msg.getUserFrom();
        fromUserId = fromUser.getId();
        fromUserName = fromUser.getName();
        toUserId = msg.getUserTo()[0];
        time = TimeUtil.format(msg.getTimeDo(), "yyyy-MM-dd HH:mm:ss:SSS");
        text = data.get(Key.TEXT, "");
        file = data.get(Key.FILE, "");   //存储key -> 下载路径 -> 下载 或者 本地路径path

        sessionId = toUserId.equals(nowUserId) ? fromUserId : toUserId;

        //聊天时收到消息 文本不涉及下载  文件类涉及下载
        sta = data.get(Key.STA, Key.STA_DEF);
    }




}
