package com.walker.dd.core.service;

import java.util.*;

import android.content.Context;
import android.util.Log;

import com.walker.common.util.Watch;
import com.walker.core.database.BaseDaoAdapter;
import com.walker.core.database.SqlUtil;


public class BaseServiceImpl extends BaseDaoAdapter {
	String dbName = "db_walker";
	String dsName = "sqlite";
	SqLiteControl sqLite;
	 
	public BaseServiceImpl(Context ctx){
		sqLite = new SqLiteControl(ctx, dbName);
	}




    public void setDs(String dsName) {
        this.dsName = dsName;
    }

    @Override
    public String getDs() {
        return this.dsName;
    }

    @Override
    public List<Map<String, Object>> find(String sql, Object... objects) {
        sql = SqlUtil.filter(sql);
        Watch w = new Watch(SqlUtil.makeSql(sql, objects));
        List<Map<String, Object>> res = null;
        try {
            res = sqLite.findList(sql, objects);
            w.res(res);
        } catch (Exception e) {
            w.exceptionWithThrow(e);
        } finally {
            out(w.toString());
        }
        return res;

    }
    @Override
    public Integer executeSql(String sql, Object... objects) {
        sql = SqlUtil.filter(sql);
        Watch w = new Watch(SqlUtil.makeSql(sql, objects));
        int res = 0;
        try {
            res = sqLite.execSQL(sql, objects);
            w.res(res);
        } catch (Exception e) {
            w.exceptionWithThrow(e);
        } finally {
            out(w.toString());
        }
        return res;
    }

    public Integer[] executeSql(String sql, List<List<Object>> objs) {
        return null;
    }

    /**
     *
     * 调用存储过程的语句，call后面的就是存储过程名和需要传入的参数
     *
     * @param proc    "{call countBySal(?,?)}"
     * @param objects
     * @return
     */
    @Override
    public Integer executeProc(String proc, Object... objects) {
        return 0;
    }



    public void out(String s) {
        Log.e("sql", s);
    }
}
