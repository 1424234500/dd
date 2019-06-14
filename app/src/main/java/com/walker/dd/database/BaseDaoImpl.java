package com.walker.dd.database;

import java.util.List;
import java.util.Map;

import android.content.Context;

import com.walker.common.util.Bean;

public class BaseDaoImpl implements BaseDao{
	String dbName = "db_walker";
	
	SqLiteControl sqLite;
	 
	public BaseDaoImpl(Context ctx){
		sqLite = new SqLiteControl(ctx, dbName);
	}
 

	@Override
	public List<Bean> queryList(String sql, Object... objects) {
		return sqLite.queryList(sql, objects);
	}

	@Override
	public Bean queryOne(String sql, Object... objects) {
		return sqLite.queryOne(sql, objects);
	}

	@Override
	public int execSQL(String sql, Object... objects) {
		return sqLite.execSQL(sql, objects);
	}

}
