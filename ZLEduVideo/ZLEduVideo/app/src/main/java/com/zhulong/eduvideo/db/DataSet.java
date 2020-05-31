package com.zhulong.eduvideo.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.zhulong.eduvideo.entity.DownloadInfo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressLint("SimpleDateFormat")
public class DataSet {
	private final static String DOWNLOADINFO = "downloadinfo";
	private final static String VIDEOPOSITION = "videoposition";
	private static Map<String, DownloadInfo> downloadInfoMap;
	
	private static SQLiteOpenHelper sqLiteOpenHelper;
	
	public static void init(Context context){
		if (sqLiteOpenHelper != null) {
			return;
		}
		sqLiteOpenHelper = new SQLiteOpenHelper(context, "demo", null, 1) {
			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			}
			
			@Override
			public void onCreate(SQLiteDatabase db) {
				String sql = "CREATE TABLE IF NOT EXISTS downloadinfo(" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"videoId VERCHAR, " +
						"title VERCHAR, " +
						"progress INTEGER, " +
						"progressText VERCHAR, " +
						"status INTEGER, " +
						"createTime DATETIME, " +
						"definition INTEGER)";
				
				String videoSql = "CREATE TABLE IF NOT EXISTS uploadinfo(" +
						"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"uploadId VERCHAR, status INTEGER, progress INTEGER, progressText VERCHAR, " +
						"videoId VERCHAR, title VERCHAR, tags VERCHAR, description VERCHAR, " +
						"filePath VERCHAR, fileName VERCHAR, fileByteSize VERCHAR, md5 VERCHAR, " +
						"uploadServer VERCHAR, serviceType VERCHAR, priority VERCHAR, encodeType VERCHAR, " +
						"uploadOrResume VERCHAR, createTime DATETIME)";
			
				String videoPositionSql = "CREATE TABLE IF NOT EXISTS videoposition(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
						"videoId VERCHAR, " +
						"position INTEGER)";
				
				db.execSQL(sql);
				db.execSQL(videoSql);
				db.execSQL(videoPositionSql);
				
			}
		};
		
		downloadInfoMap = new HashMap<String, DownloadInfo>();
		reloadData();
	}
	
	private static void reloadData(){
		
		SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			// 重载下载信息
			synchronized (downloadInfoMap) {
				cursor = db.rawQuery("SELECT * FROM ".concat(DOWNLOADINFO), null);
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					try {
						DownloadInfo downloadInfo = buildDownloadInfo(cursor);
						downloadInfoMap.put(downloadInfo.getTitle(), downloadInfo);
						
					} catch (ParseException e) {
						Log.e("Parse date error", e.getMessage());
					}
				}
			}
		} catch (Exception e) {
			Log.e("cursor error", e.getMessage());
		} finally{
			if (cursor != null) {
				cursor.close();
			}
		}
	}
	
	public static void saveData(){
		SQLiteDatabase db = sqLiteOpenHelper.getReadableDatabase();
		db.beginTransaction();
		
		try {
			//清除当前数据
			db.delete(DOWNLOADINFO, null, null);
			
			for(DownloadInfo downloadInfo : downloadInfoMap.values()){
				ContentValues values = new ContentValues();
				values.put("videoId", downloadInfo.getVideoId());
				values.put("title", downloadInfo.getTitle());
				values.put("progress", downloadInfo.getProgress());
				values.put("progressText", downloadInfo.getProgressText());
				values.put("status", downloadInfo.getStatus());
				values.put("definition", downloadInfo.getDefinition());
				
				SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				values.put("createTime", formater.format(downloadInfo.getCreateTime()));
				
				db.insert(DOWNLOADINFO, null, values);
			}
			
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.e("db error", e.getMessage());
		} finally {
			db.endTransaction();
		}
		
		db.close();
	}
	
	public static List<DownloadInfo> getDownloadInfos(){
		
		return new ArrayList<DownloadInfo>(downloadInfoMap.values());
	}
	
	public static boolean hasDownloadInfo(String Title){
		return downloadInfoMap.containsKey(Title);
	}
	
	public static DownloadInfo getDownloadInfo(String Title){
		return downloadInfoMap.get(Title);
	}
	
	public static void addDownloadInfo(DownloadInfo downloadInfo){
		synchronized (downloadInfoMap) {
			if (downloadInfoMap.containsKey(downloadInfo.getTitle())) {
				return ;
			}
			
			downloadInfoMap.put(downloadInfo.getTitle(), downloadInfo);
		}
	}
	
	public static void removeDownloadInfo(String title){
		synchronized (downloadInfoMap) {
			downloadInfoMap.remove(title);
		}
	}
	
	public static void updateDownloadInfo(DownloadInfo downloadInfo){
		synchronized (downloadInfoMap) {
			downloadInfoMap.put(downloadInfo.getTitle(), downloadInfo);
		}
		
	}
	
	private static DownloadInfo buildDownloadInfo(Cursor cursor) throws ParseException {
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date createTime = formater.parse(cursor.getString(cursor.getColumnIndex("createTime")));
		DownloadInfo downloadInfo = new DownloadInfo(cursor.getString(cursor.getColumnIndex("videoId")), 
				cursor.getString(cursor.getColumnIndex("title")),
				cursor.getInt(cursor.getColumnIndex("progress")), 
				cursor.getString(cursor.getColumnIndex("progressText")), 
				cursor.getInt(cursor.getColumnIndex("status")), 
				createTime,
				cursor.getInt(cursor.getColumnIndex("definition")));
		return downloadInfo;
	}

	public static void insertVideoPosition(String videoId, int position) {
		
		SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
		if (database.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("videoId", videoId);
			values.put("position", position);
			database.insert(VIDEOPOSITION, null, values);
			Log.e("demo", "insert " + videoId + " position" + position);
			database.close();
		}
	}
	
	public static int getVideoPosition(String videoId) {
		int position = 0;
		SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
		if (database.isOpen()) {
			Cursor cursor = database.query(VIDEOPOSITION, new String[]{"position"}, "videoId=?", new String[]{videoId}, null, null, null);
			if (cursor.moveToFirst()) {
				position = cursor.getInt(cursor.getColumnIndex("position"));
			}
			cursor.close();
			database.close();
		}
		return position;
	}
	
	public static void updateVideoPosition(String videoId, int position) {
		SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
		if (database.isOpen()) {
			ContentValues values = new ContentValues();
			values.put("position", position);
			database.update(VIDEOPOSITION, values, "videoId=?", new String[]{videoId});
			Log.e("demo", "update " + videoId + " position" + position);
			database.close();
		}
	}
}
