package com.zhulong.eduvideo.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * 添加获取本地配置信息
 * 
 * @author
 * 
 */
public class ConfigDataBase {

	private SharedPreferences shared;

	public static final String FILE_NAME = "config";
	public static final int MODE = Context.MODE_PRIVATE;

	public ConfigDataBase(Context context) {
		// try {
		shared = context.getSharedPreferences(FILE_NAME, MODE);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	public void addStringData(String name, String value) {
		SharedPreferences.Editor editor = shared.edit();
		editor.putString(name, value);
		editor.commit();
	}

	public String getStringData(String name) {
		return shared.getString(name, "");
	}

	public String getStringData(String name,String defaultvalue) {
		return shared.getString(name, defaultvalue);
	}

	public void addIntData(String name, int value) {
		SharedPreferences.Editor editor = shared.edit();
		editor.putInt(name, value);
		editor.commit();
	}

	public int getIntData(String name) {
		return shared.getInt(name, -1);
	}

	public int getIntData(String name, int defaultvalue) {
		return shared.getInt(name, defaultvalue);
	}

	public void addLongData(String name, long value) {
		SharedPreferences.Editor editor = shared.edit();
		editor.putLong(name, value);
		editor.commit();
	}

	public long getLongData(String name) {
		return shared.getLong(name, 0);
	}

	public void addBooleanData(String name, boolean value) {
		SharedPreferences.Editor editor = shared.edit();
		editor.putBoolean(name, value);
		editor.commit();
	}

	public boolean getBooleanData(String name) {
		return shared.getBoolean(name, false);
	}

	public boolean getBooleanData(String name, boolean defaultvalue) {
		return shared.getBoolean(name, defaultvalue);
	}
}
