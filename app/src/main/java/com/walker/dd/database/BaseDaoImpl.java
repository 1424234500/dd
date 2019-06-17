package com.walker.dd.database;

import java.util.*;

import android.content.Context;
import android.util.Log;

import com.walker.common.util.MapListUtil;
import com.walker.common.util.Page;
import com.walker.common.util.Tools;
import com.walker.core.database.BaseDao;
import com.walker.core.database.SqlUtil;
import com.walker.core.mode.Watch;
import com.walker.dd.util.AndroidTools;

public class BaseDaoImpl implements BaseDao {
	String dbName = "db_walker";
	String dsName = "sqlite";
	SqLiteControl sqLite;
	 
	public BaseDaoImpl(Context ctx){
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
    public List<String> getColumnsByTableName(String tableName) {
        List<String> res = null;
        List<List<String>> list = MapListUtil.toArrayAndTurn(this.find(SqlUtil.makeSqlColumn(getDs(), tableName)));
        list = MapListUtil.turnRerix(list);
        if (list.size() > 0) {
            res = list.get(0);
        } else {
            res = new ArrayList<String>();
        }
        return res;
    }



    @Override
    public Map<String, Object> findOne(String sql, Object... objects) {
        List<Map<String, Object>> list = this.findPage(sql, 1, 1, objects);
        Map<String, Object> res = null;
        if (list.size() >= 1) {
            res = list.get(0);
        }
        return res;
    }

    /**
     * 获得结果集
     *
     * @param sql    SQL语句
     * @param objects 参数
     * @param page   要显示第几页
     * @param rows   每页显示多少条
     * @return 结果集
     */
    @Override
    public List<Map<String, Object>> findPage(String sql, int page, int rows, Object... objects) {
        int offset = (page - 1) * rows;

//        sql = SqlUtil.makeSqlPage(getDs(), sql, page, rows);
        sql = "select * from ( " + sql + " ) t limit " + rows + " offset " + offset + " ";

        return this.find(sql, objects);
    }

    @Override
    public List<Map<String, Object>> findPage(Page page, String sql, Object... objects) {
        page.setNUM(this.count(sql, objects));
        sql = SqlUtil.makeSqlOrder(sql, page.getORDER());
        return this.findPage(sql, page.getNOWPAGE(), page.getSHOWNUM(), objects);
    }

    @Override
    public int count(String sql, Object... objects) {
        int res = 0;
        sql = SqlUtil.makeSqlCount(sql);
        List<List<String>> list = MapListUtil.toArray(this.find(sql, objects));
        if(list != null && list.size() > 0) {
            List<String> row = list.get(0);
            if(row != null && row.size() > 0) {
                res = Integer.valueOf(row.get(0));
            }
        }
        return res;
    }



    @Override
    public List<String> getColumnsBySql(String sql) {
        List<String> res = null;
        return res;
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
    public int executeSql(String sql, Object... objects) {
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

    public int[] executeSql(String sql, List<List<Object>> objs) {
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
    public int executeProc(String proc, Object... objects) {
        return 0;
    }



    public void out(String s) {
        Log.e("sql", s);
    }
}
