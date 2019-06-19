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
public class SessionModel extends Model{

    /**
     * 消息体存储查询
     */
    public final static String SESSION = "SESSION";
    public final static String SQL_SESSION = "create table if not exists SESSION (" +
            "USER_ID varchar(30), " +       //当前用户
            "TO_ID varchar(200), " +        //会话目标 user/group
            "TIME varchar(30), " +          //最新消息时间
            "TYPE varchar(30), " +          //最新消息类型
            "TEXT varchar(2000) " +         //会话显示文本    !
            "NUM varchar(10) " +            //会话显示红点    !
            "TO_NAME varchar(200), " +      //目标名字       ！
            ") ";
    /**
     * 存储
     */
    public static Bean save(BaseDao dao, String nowUserId, String toId, String toName, String time, String type, String text, String num){

        int count = -1;
        Map<String, Object> getmap = dao.findOne("select * from " + SESSION + " where USER_ID=? and TO_ID=? ",  nowUserId, toId);
        if(getmap == null){
            count = dao.executeSql("insert into " + SESSION + " values(?,?,?,?,?,?,?) ",
                    nowUserId, toId, time, type, text, num, toName);
        }else{
            Bean get = new Bean(getmap);
            nowUserId = nvl(nowUserId, get.get("USER_ID"));
            toId = nvl(toId, get.get("TO_ID", ""));
            time = nvl(time, get.get("TIME", ""));
            type = nvl(type, get.get("TYPE", ""));
            text = nvl(text, get.get("TEXT", ""));
            toName = nvl(toName, get.get("TO_NAME", ""));
            num = nvl(num, get.get("NUM", ""));

            count = dao.executeSql("update " + SESSION + " set USER_ID=?,TO_ID=?,TIME=?,TYPE=?,TEXT=?,NUM=?,TO_NAME=? where USER_ID=? and TO_ID=? ",
                    nowUserId,toId,time,type,text,num,toName,nowUserId,toId);
        }

        Bean bean = new Bean()
                .set(Key.ID, toId)
                .set(Key.NAME, toName.length() > 0 ? toName : toId)
                .set(Key.TIME, time)
                .set(Key.TYPE, type)
                .set(Key.TEXT, text)
                .set(Key.NUM, num)
        ;
        return bean;
    }

    /**
     * 自动会话
     * @return
     */
    public static Bean getDd(){
        User dd = NowUser.getDd();
        Bean sessionDd = new Bean()
                .set(Key.ID, dd.getId())
                .set(Key.NAME, dd.getName())
                .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
                .set(Key.TYPE, Key.TEXT)
                .set(Key.TEXT, "auto echo")
                .set(Key.NUM, 1)
                ;
        return dd;
    }


    /**
     * 查询时间之前的  15条 按照倒序
     */
    public static List<Bean> finds(BaseDao dao, String nowUserId, int count){
        List<Map<String, Object>> list = dao.findPage("select * from " + SESSION + " where USER_ID=? order by TIME DESC ", 1, count, nowUserId);
        List<Bean> res = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {

            Bean bean = new Bean(list.get(i));

            String toId = bean.get("TO_ID", "");
            String toName = bean.get("TO_NAME", "");
            String time = bean.get("TIME", "");
            String type = bean.get("TYPE", "");
            String text = bean.get("TEXT", "");
            String num = bean.get("NUN", "");
            res.add(new Bean()
                    .set(Key.ID, toId)
                    .set(Key.NAME, toName.length() > 0 ? toName : toId)
                    .set(Key.TIME, time)
                    .set(Key.TYPE, type)
                    .set(Key.TEXT, text)
                    .set(Key.NUM, num)
            );
        }
        return res;
    }



}
