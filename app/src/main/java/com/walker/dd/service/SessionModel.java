package com.walker.dd.service;


import com.walker.common.util.Bean;
import com.walker.common.util.TimeUtil;
import com.walker.core.database.BaseDao;
import com.walker.dd.struct.Session;

import com.walker.mode.*;
import com.walker.socket.server_1.plugin.*;



import java.util.ArrayList;
import java.util.Arrays;
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
    public final static String SQL_SESSION = "create table if not exists " + SESSION + " (" +
            "USER_ID varchar(30), " +       //当前用户
            "TO_ID varchar(200), " +        //会话目标 user/group
            "TIME varchar(30), " +          //最新消息时间
            "TYPE varchar(30), " +          //最新消息类型
            "TEXT varchar(2000), " +         //会话显示文本    !
            "NUM varchar(10), " +            //会话显示红点    !
            "TO_NAME varchar(200) " +      //目标名字       ！
            ") ";

    public static Session del(BaseDao dao, String nowUserId, Session session){

        dao.executeSql("delete from " + SESSION + " where USER_ID=? and TO_ID=? ", nowUserId, session.getId());

        return session;
    }

    public static Session get(BaseDao dao, String nowUserId, String id){
        Map<String, Object> getmap = dao.findOne("select * from " + SESSION + " where USER_ID=? and TO_ID=? ",  nowUserId, id);
        if(getmap == null) return null;
        return sqlbean2Session(new Bean(getmap));
    }
    /**
     * 存储
     */
    public static Session save(BaseDao dao, Session session){

        int count = -1;
        Map<String, Object> getmap = dao.findOne("select * from " + SESSION + " where USER_ID=? and TO_ID=? ",  session.getNowUserId(), session.getId());
        if(getmap == null){
            count = dao.executeSql("insert into " + SESSION + " values(?,?,?,?,?,?,?) ",
                    session.getNowUserId(), session.getId(),session.getTime(),session.getType(),session.getText(),session.getNum(),session.getName());
        }else{
            Bean get = new Bean(getmap);
            session.setNowUserId(nvl(session.getNowUserId(), get.get("USER_ID")));
            session.setId(nvl(session.getId(), get.get("TO_ID", "")));
            session.setTime(nvl(session.getTime(), get.get("TIME", "")));
            session.setType(nvl(session.getType(), get.get("TYPE", "")));
            session.setText(nvl(session.getText(), get.get("TEXT", "")));
            session.setName(nvl(session.getName(), get.get("TO_NAME", "")));
//            session.setNum(nvl(session.getNum(), get.get("NUM", "")));
            List<Object> args = new ArrayList<>();
            String sql = "update " + SESSION + " set USER_ID=?,TO_ID=?,TIME=?,TYPE=?,TEXT=?";
            args.addAll(Arrays.asList(session.getNowUserId(),session.getId(),session.getTime(),session.getType(),session.getText()));
            if(session.getNum() >= 0){
                sql += ",NUM=?";
                args.add(session.getNum());
            }
            sql += ",TO_NAME=? where USER_ID=? and TO_ID=? ";
            args.addAll(Arrays.asList(session.getName(),session.getNowUserId(),session.getId()));
            count = dao.executeSql(sql, args.toArray());
//            count = dao.executeSql("update " + SESSION + " set USER_ID=?,TO_ID=?,TIME=?,TYPE=?,TEXT=?,NUM=?,TO_NAME=? where USER_ID=? and TO_ID=? ",
//                    nowUserId,session.getId(),session.getTime(),session.getType(),session.getText(),session.getNum(),session.getName(),nowUserId,session.getId());
        }
        return session;
    }

    /**
     * 自动会话
     * @return
     */
    public static Session getDd(){
        UserSocket dd = NowUser.getDd();
//        Bean sessionDd = new Bean()
//                .set(Key.ID, dd.getId())
//                .set(Key.NAME, dd.getName())
//                .set(Key.TIME, TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"))
//                .set(Key.TYPE, Key.TEXT)
//                .set(Key.TEXT, "auto echo")
//                .set(Key.NUM, 1)
//                ;
        Session session = new Session();
        session.setNowUserId(NowUser.getId());
        session.setId(dd.getId());
        session.setName(dd.getName());
        session.setTime(TimeUtil.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
        session.setType(Key.TEXT);
        session.setText("auto echo");
        session.setNum(1);
        return session;
    }


    /**
     * 查询时间之前的  15条 按照倒序
     */
    public static List<Session> finds(BaseDao dao, String nowUserId, int count){
        List<Map<String, Object>> list = dao.findPage("select * from " + SESSION + " where USER_ID=? order by TIME DESC ", 1, count, nowUserId);
        List<Session> res = new ArrayList<>();
        for(int i = 0; i < list.size(); i++) {

            Bean bean = new Bean(list.get(i));
            Session session = sqlbean2Session(bean);
            res.add(session);
        }
        return res;
    }

    private static Session sqlbean2Session(Bean bean) {
        Session session = new Session();
        String userId = bean.get("USER_ID", "");
        String toId = bean.get("TO_ID", "");
        String toName = bean.get("TO_NAME", "");
        String time = bean.get("TIME", "");
        String type = bean.get("TYPE", "");
        String text = bean.get("TEXT", "");
        String num = bean.get("NUM", "0");

        session.setNowUserId(userId);
        session.setId(toId);
        session.setName(toName.length() > 0 ? toName : toId);
        session.setTime(time);
        session.setType( type);
        session.setText(text);
        session.setNum(Integer.valueOf(num));
        return session;
    }


}
