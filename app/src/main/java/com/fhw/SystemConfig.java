package com.fhw;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


/**
 * Created by fhw on 2017/3/11 0011.
 * email 11044818@qq.com
 */
public class SystemConfig {

	private static final String SHARE_PRE_NAME = "systm_config";
	static SharedPreferences preferences;

	private static Context mContext;

	
	private static final String WIDTH = "WIDTH"; //设备宽度
	private static final String HIGHT = "HIGHT"; //设备高度
	private static final String TOP = "TOP"; //设备的状态栏高度

	public static void init(Context context) {
		mContext = context;
		preferences = mContext.getSharedPreferences(SHARE_PRE_NAME,
				Context.MODE_PRIVATE);
	}


	
	public static void setHight(int hight){
		saveIntDataToSharePreference(HIGHT, hight);
	}
	
	public static int getHight(){
		return getIntDataByKey(HIGHT, 1280);
	}
	
	public static void setWidth(int width){
		saveIntDataToSharePreference(WIDTH, width);
	}
	
	public static int getWidth(){
		return getIntDataByKey(WIDTH, 720);
		
	}
	
	public static void setTop(int top){
		saveIntDataToSharePreference(TOP, top);
	}
	
	public static int getTop(){
		return getIntDataByKey(TOP, 48);
	}
	
	/***
	 * 保存
	 *
	 * @param key
	 * @param value
	 */
	public static void saveStringDataToSharePreference(String key, String value) {

		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/***
	 *
	 * @param key
	 * @param value
	 */
	public static void saveLongDataToSharePreference(String key, long value) {

		SharedPreferences.Editor editor = preferences.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	/***
	 *
	 * @param key
	 * @param value
	 */
	public static void saveBooleanDataToSharePreference(String key,
			boolean value) {

		SharedPreferences.Editor editor = preferences.edit();

		editor.putBoolean(key, value);

		editor.commit();
	}

	/***
	 *
	 * @param key
	 * @param value
	 */
	public static void saveIntDataToSharePreference(String key, int value) {

		SharedPreferences.Editor editor = preferences.edit();

		editor.putInt(key, value);

		editor.commit();
	}

	/***
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getStringDataByKey(String key, String defaultValue) {

		return preferences.getString(key, defaultValue);
	}

    public static String getWorkPhone(String key, String defaultValue) {
		
		return preferences.getString(key, defaultValue);
	}
	
	
    
	
	/***
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getIntDataByKey(String key, int defaultValue) {

		return preferences.getInt(key, defaultValue);

	}

	/***
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static boolean getBooleanDataByKey(String key, boolean defaultValue) {

		return preferences.getBoolean(key, defaultValue);

	}

	/***
	 *
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static long getLongDataByKey(String key, long defaultValue) {

		return preferences.getLong(key, defaultValue);
	}

	public static boolean isNull(String string) {
		return TextUtils.isEmpty(string) || string.equals("null");
	}



	/**
	 * 获得状态栏的高度
	 *
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {

		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return statusHeight;
	}
}

