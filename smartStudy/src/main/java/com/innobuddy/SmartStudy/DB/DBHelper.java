package com.innobuddy.SmartStudy.DB;

import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	private final static String DATABASE_NAME = "SmartStudy.db";
	private final static int DATABASE_VERSION = 1;

	private final static String TABLE_DOWNLOAD = "download_video";
	private final static String TABLE_OFFLINE = "offline_video";
	private final static String TABLE_COLLECT = "collect_video";
	private final static String TABLE_RECENT_WATCH = "recent_watch_video";
	
	public final static String VIDEO_ID = "id";
	public final static String VIDEO_NAME = "name";
	public final static String VIDEO_POSTER = "poster";
	public final static String VIDEO_URL = "url";
	public final static String VIDEO_CACHE_URL = "cache_url";
	public final static String VIDEO_HOT = "hot";
	public final static String VIDEO_DOWNLOAD_SIZE = "download_size";
	public final static String VIDEO_TOTAL_SIZE = "total_size";
	public final static String VIDEO_POSTION = "postion";

	private static DBHelper instance = null;
	
	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper(context);
		}
		return instance;
	}
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("create table download_video(_id integer primary key autoincrement, id integer, name text, poster text, url text, cache_url text, hot integer, download_size int8, total_size int8)");
	
		db.execSQL("create table offline_video(_id integer primary key autoincrement, id integer, name text, poster text, url text, cache_url text, hot integer, total_size int8, postion integer)");

		db.execSQL("create table collect_video(_id integer primary key autoincrement, id integer, name text, poster text, url text, cache_url text, hot integer, postion integer)");

		db.execSQL("create table recent_watch_video(_id integer primary key autoincrement, id integer, name text, poster text, url text, cache_url text, hot integer, postion integer)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public Cursor queryDownload(int id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_DOWNLOAD, null,  VIDEO_ID + "= ?", new String[]{String.valueOf(id)}, null, null, null);
		return c;
	}
	
	public Cursor queryDownload(String cacheUrl) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_DOWNLOAD, null,  VIDEO_URL + "= ?", new String[]{cacheUrl}, null, null, null);
		return c;
	}
	
	public Cursor queryDownload() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_DOWNLOAD, null, null, null, null, null, "_id");
		return c;
	}

	
	public void insertDownload(JSONObject jsonObject) {
		
		try {
			
			if (jsonObject != null) {
				int id = jsonObject.getInt(VIDEO_ID);
				Cursor c1 = queryDownload(id);
				Cursor c2 = queryOffline(id);
				if ((c1 != null && c1.getCount() > 0) || c2 != null && c2.getCount() > 0) {
					
				} else {
					SQLiteDatabase db = getWritableDatabase();
					ContentValues cv = new ContentValues();
					cv.put(VIDEO_ID, jsonObject.getInt(VIDEO_ID));
					cv.put(VIDEO_NAME, jsonObject.getString(VIDEO_NAME));
					cv.put(VIDEO_POSTER, jsonObject.getString(VIDEO_POSTER));
					cv.put(VIDEO_URL, jsonObject.getString(VIDEO_URL));
					cv.put(VIDEO_CACHE_URL, jsonObject.getString(VIDEO_CACHE_URL));
					cv.put(VIDEO_HOT, jsonObject.getInt(VIDEO_HOT));
					db.insert(TABLE_DOWNLOAD, null, cv);
				}
				
				if (c1 != null) {
					c1.close();
				}
				
				if (c2 != null) {
					c2.close();
				}
				
			}

		} catch (Exception e) {
			
		}

	}
	
	public void updateDownload(int id, int downloadedSize, int totalSize) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_ID + " = ?";
		String[] whereValue = {Integer.toString(id)};
		ContentValues cv = new ContentValues();
		cv.put(VIDEO_DOWNLOAD_SIZE, downloadedSize);
		cv.put(VIDEO_TOTAL_SIZE, totalSize);		
		db.update(TABLE_DOWNLOAD, cv, where, whereValue);
	}
	
	public void updateDownload(String cacheUrl, long downloadedSize, long totalSize) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_URL + " = ?";
		String[] whereValue = {cacheUrl};
		ContentValues cv = new ContentValues();
		cv.put(VIDEO_DOWNLOAD_SIZE, downloadedSize);
		cv.put(VIDEO_TOTAL_SIZE, totalSize);		
		db.update(TABLE_DOWNLOAD, cv, where, whereValue);
	}
	
	public void deleteDownload(int id) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_ID + " = ?";
		String[] whereValue = {Integer.toString(id)};
		db.delete(TABLE_DOWNLOAD, where, whereValue);
	}
	
	public void deleteDownload(String cacheUrl) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_URL + " = ?";
		String[] whereValue = {cacheUrl};
		db.delete(TABLE_DOWNLOAD, where, whereValue);
	}
	
	public Cursor queryRecentWatch(int id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_RECENT_WATCH, null,  VIDEO_ID + "= ?", new String[]{String.valueOf(id)}, null, null, null);
		return c;
	}
	
	public Cursor queryRecentWatch() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_RECENT_WATCH, null, null, null, null, null, "_id desc");
		return c;
	}
	
	public void insertRecentWatch(JSONObject jsonObject) {
		
		try {
			
			if (jsonObject != null) {
				int id = jsonObject.getInt(VIDEO_ID);
				Cursor c = queryRecentWatch(id);
				if (c != null && c.getCount() > 0) {
					
				} else {
					SQLiteDatabase db = getWritableDatabase();
					ContentValues cv = new ContentValues();
					cv.put(VIDEO_ID, jsonObject.getInt(VIDEO_ID));
					cv.put(VIDEO_NAME, jsonObject.getString(VIDEO_NAME));
					cv.put(VIDEO_POSTER, jsonObject.getString(VIDEO_POSTER));
					cv.put(VIDEO_URL, jsonObject.getString(VIDEO_URL));
					cv.put(VIDEO_CACHE_URL, jsonObject.getString(VIDEO_CACHE_URL));
					cv.put(VIDEO_HOT, jsonObject.getInt(VIDEO_HOT));
					db.insert(TABLE_RECENT_WATCH, null, cv);
				}
				
				if (c != null) {
					c.close();
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateRecentWatch(int id, int postion) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_ID + " = ?";
		String[] whereValue = {Integer.toString(id)};
		ContentValues cv = new ContentValues();
		cv.put(VIDEO_POSTION, postion);
		db.update(TABLE_RECENT_WATCH, cv, where, whereValue);
		db.update(TABLE_OFFLINE, cv, where, whereValue);
		db.update(TABLE_COLLECT, cv, where, whereValue);
	}
	
	public void deleteRecentWatch(int id) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_ID + " = ?";
		String[] whereValue = {Integer.toString(id)};
		db.delete(TABLE_RECENT_WATCH, where, whereValue);
	}
	
	public void deleteRecentWatch() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_RECENT_WATCH, null, null);
	}
	
	public Cursor queryCollect(int id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_COLLECT, null,  VIDEO_ID + "= ?", new String[]{String.valueOf(id)}, null, null, null);
		return c;
	}
	
	public Cursor queryCollect() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_COLLECT, null, null, null, null, null, "_id desc");
		return c;
	}
	
	public void insertCollect(JSONObject jsonObject) {
		
		try {
			
			if (jsonObject != null) {
				int id = jsonObject.getInt(VIDEO_ID);
				Cursor c = queryCollect(id);
				if (c != null && c.getCount() > 0) {
					
				} else {
					SQLiteDatabase db = getWritableDatabase();
					ContentValues cv = new ContentValues();
					cv.put(VIDEO_ID, jsonObject.getInt(VIDEO_ID));
					cv.put(VIDEO_NAME, jsonObject.getString(VIDEO_NAME));
					cv.put(VIDEO_POSTER, jsonObject.getString(VIDEO_POSTER));
					cv.put(VIDEO_URL, jsonObject.getString(VIDEO_URL));
					cv.put(VIDEO_CACHE_URL, jsonObject.getString(VIDEO_CACHE_URL));
					cv.put(VIDEO_HOT, jsonObject.getInt(VIDEO_HOT));
					db.insert(TABLE_COLLECT, null, cv);
				}
				
				if (c != null) {
					c.close();
				}
				
			}
			
		} catch (Exception e) {

		}
	}
	
	public void updateCollect(int id, long postion) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_ID + " = ?";
		String[] whereValue = {Integer.toString(id)};
		ContentValues cv = new ContentValues();
		cv.put(VIDEO_POSTION, postion);
		db.update(TABLE_COLLECT, cv, where, whereValue);
	}
	
	public void deleteCollect(int id) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_ID + " = ?";
		String[] whereValue = {Integer.toString(id)};
		db.delete(TABLE_COLLECT, where, whereValue);
	}
	
	public void deleteCollect() {
		SQLiteDatabase db = getWritableDatabase();
		db.delete(TABLE_COLLECT, null, null);
	}
	
	public Cursor queryOffline(int id) {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_OFFLINE, null,  VIDEO_ID + "= ?", new String[]{String.valueOf(id)}, null, null, null);
		return c;
	}
	
	public Cursor queryOffline() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor c = db.query(TABLE_OFFLINE, null, null, null, null, null, "_id");
		return c;
	}
	
	public void insertOffline(Cursor cursor) {
		
		try {
			
			if (cursor != null && cursor.getCount() > 0) {
				cursor.moveToFirst();
				int id = cursor.getInt(cursor.getColumnIndex(VIDEO_ID));
				Cursor c = queryOffline(id);
				if (c != null && c.getCount() > 0) {
 					
				} else {
					SQLiteDatabase db = getWritableDatabase();
					ContentValues cv = new ContentValues();
					cv.put(VIDEO_ID, cursor.getInt(cursor.getColumnIndex(VIDEO_ID)));
					cv.put(VIDEO_NAME, cursor.getString(cursor.getColumnIndex(VIDEO_NAME)));
					cv.put(VIDEO_POSTER, cursor.getString(cursor.getColumnIndex(VIDEO_POSTER)));
					cv.put(VIDEO_URL, cursor.getString(cursor.getColumnIndex(VIDEO_URL)));
					cv.put(VIDEO_CACHE_URL, cursor.getString(cursor.getColumnIndex(VIDEO_CACHE_URL)));
					cv.put(VIDEO_HOT, cursor.getInt(cursor.getColumnIndex(VIDEO_HOT)));
					db.insert(TABLE_OFFLINE, null, cv);
				}
				
				if (c != null) {
					c.close();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateOffline(int id, long postion) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_ID + " = ?";
		String[] whereValue = {Integer.toString(id)};
		ContentValues cv = new ContentValues();
		cv.put(VIDEO_POSTION, postion);
		db.update(TABLE_OFFLINE, cv, where, whereValue);
	}
	
	public void deleteOffline(int id) {
		SQLiteDatabase db = getWritableDatabase();
		String where = VIDEO_ID + " = ?";
		String[] whereValue = {Integer.toString(id)};
		db.delete(TABLE_OFFLINE, where, whereValue);
	}
	public void deleteAlloffline(){
		SQLiteDatabase db=getWritableDatabase();
		db.delete(TABLE_OFFLINE, null, null);
	}
	
}
