package com.innobuddy.SmartStudy.DB.impl;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.innobuddy.SmartStudy.DB.DBHelper;
import com.innobuddy.SmartStudy.DB.dao.UserDao;
/**
 * 例子
 * @author tangyichao
 *
 */
public class UserImpl extends UserDao {

	private DBHelper dbHelper;
	public UserImpl(Context context) {
		dbHelper = DBHelper.getInstance(context);
	}
	@Override
	public void updateUserPsw() {
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		/***
		 * 
		 */
		db.close();
	}

}
