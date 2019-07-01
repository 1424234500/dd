package com.walker.dd.struct;

import com.walker.common.util.Bean;
import com.walker.socket.server_1.Key;

public class Session extends Bean{
    public String getNowUserId() {
        return get("nowUserId", "");
    }

    public void setNowUserId(String nowUserId) {
        set("nowUserId", nowUserId);

    }


    public Session(){}
    public Session(Bean bean){
        super(bean);
    }
    public Session(String nowUserId, String toId, String toName, String time, String type, String text, int num) {
        this.setNowUserId(nowUserId);

        this.setId(toId);
        this.setName(toName.length() > 0 ? toName : toId);
        this.setTime(time);
        this.setType( type);
        this.setText(text);
        this.setNum(num);
    }


    @Override
    public boolean equals(Object obj) {
        return getId().equals(((Session)obj).getId());
    }

    public String getId() {
        return get(Key.ID, "");
    }

    public void setId(String id) {
        set(Key.ID, id);
    }

    public String getName() {
        return get(Key.NAME, "");
    }

    public void setName(String name) {
        set(Key.NAME, name);
    }

    public String getTime() {
        return get(Key.TIME, "");
    }

    public void setTime(String time) {
        set(Key.TIME, time);
    }

    public String getType() {
        return get(Key.TYPE, Key.TEXT);
    }

    public void setType(String type) {
        set(Key.TYPE, type);
    }

    public String getText() {
        return get(Key.TEXT, "");
    }

    public void setText(String text) {
        set(Key.TEXT, text);
    }

    public int getNum() {
        return get(Key.NUM, 0);
    }

    public void setNum(int num) {
        set(Key.NUM, num);
    }
}
