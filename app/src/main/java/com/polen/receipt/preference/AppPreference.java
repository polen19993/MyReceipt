package com.polen.receipt.preference;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.polen.receipt.api.classes.UserInfo;
import com.polen.receipt.global.Global;


public class AppPreference {

	public static SharedPreferences getPreferences(Context ctx) {
		return ctx.getSharedPreferences(Global.SHARED_PREF_KEY, Activity.MODE_PRIVATE);
	}

	public static void setSharedPrefValue(Context ctx, String key, String value) {
		SharedPreferences.Editor editor = getPreferences(ctx).edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getStringPrefValue(Context ctx, String key) {
		return getPreferences(ctx).getString(key, "");
	}

	public static String getStringPrefValue(Context ctx, String key,
											String defval) {
		return getPreferences(ctx).getString(key, defval);
	}

	public static void setSharedPrefValue(Context ctx, String key, int value) {
		SharedPreferences.Editor editor = getPreferences(ctx).edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getIntPrefValue(Context ctx, String key) {
		return getPreferences(ctx).getInt(key, 0);
	}

	public static int getIntPrefValue(Context ctx, String key, int defval) {
		return getPreferences(ctx).getInt(key, defval);
	}

	public static long getLongPrefValue(Context ctx, String key) {
		return getPreferences(ctx).getLong(key, 0L);
	}

	public static long getLongPrefValue(Context ctx, String key, long defval) {
		return getPreferences(ctx).getLong(key, defval);
	}

	public static void setSharedPrefValue(Context ctx, String key, long value) {
		SharedPreferences.Editor editor = getPreferences(ctx).edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public static void setSharedPrefValue(Context ctx, String key, float value) {
		SharedPreferences.Editor editor = getPreferences(ctx).edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public static Float getFloatPrefValue(Context ctx, String key) {
		return getPreferences(ctx).getFloat(key, 0f);
	}

	public static void setSharedPrefValue(Context ctx, String key, boolean value) {
		SharedPreferences.Editor editor = getPreferences(ctx).edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBooleanPrefValue(Context ctx, String key) {
		return getPreferences(ctx).getBoolean(key, false);
	}

	public static boolean getBooleanPrefValue(Context ctx, String key,
											  boolean defval) {
		return getPreferences(ctx).getBoolean(key, defval);
	}

	public static void clearPrefValue(Context ctx, String key) {
		SharedPreferences.Editor editor = getPreferences(ctx).edit();
		editor.remove(key);
		editor.commit();
	}

	public static final String KEY_API_KEY = "key_api_key";
	public static final String KEY_GCM_REGID = "key_gcm_regid";

	public static final String KEY_USER_COOKIE = "key_user_cookie";

	public static final String KEY_USER_ID = "key_user_id";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_USER_PASSWORD = "password";
	public static final String KEY_USER_TYPE = "user_type";
	public static final String KEY_STATE = "state";
	public static final String KEY_POSITION = "position";

	public static void setAPIKey(Context ctx, String apikey) {
		setSharedPrefValue(ctx, KEY_API_KEY, apikey);
	}

	public static String getAPIKey(Context ctx) {
		return getStringPrefValue(ctx, KEY_API_KEY);
	}
	
	public static String getGCMRegId(Context ctx){
		return getStringPrefValue(ctx, KEY_GCM_REGID, "");
	}
	
	public static void setGCMRegId(Context ctx, String regid){
		setSharedPrefValue(ctx, KEY_GCM_REGID, regid);
	}
	
	public static void saveUserData(Context ctx, UserInfo info){
		setSharedPrefValue(ctx, KEY_USER_ID, info.id);
		setSharedPrefValue(ctx, KEY_USER_TYPE, info.type);
		setSharedPrefValue(ctx, KEY_EMAIL, info.email);
		setSharedPrefValue(ctx, KEY_USER_PASSWORD, info.password);
		setSharedPrefValue(ctx, KEY_STATE, info.state);
		setSharedPrefValue(ctx, KEY_POSITION, info.position);
	}

	public static UserInfo getUserData(Context ctx){
		UserInfo info = new UserInfo();
		info.id = getIntPrefValue(ctx, KEY_USER_ID, 0);
		info.type = getIntPrefValue(ctx, KEY_USER_TYPE, 0);
		info.email = getStringPrefValue(ctx, KEY_EMAIL, "");
		info.password = getStringPrefValue(ctx, KEY_USER_PASSWORD, "");
		info.state = getStringPrefValue(ctx, KEY_STATE, "");
		info.position = getStringPrefValue(ctx, KEY_POSITION, "");
		return info;
	}

	public static void clearUserData(Context ctx){
		setSharedPrefValue(ctx, KEY_USER_COOKIE, "");
	}
	
	public static final String KEY_SETTING_ALERT = "key_setting_alert";
	public static final String KEY_SETTING_SOUND = "key_setting_sound";
	public static final String KEY_SETTING_VIBRATION = "key_setting_vibration";
	
	public static void setSettingAlert(Context ctx, boolean alert){
		setSharedPrefValue(ctx,KEY_SETTING_ALERT, alert);
	}
	
	public static boolean getSettingAlert(Context ctx){
		return getBooleanPrefValue(ctx, KEY_SETTING_ALERT, true);
	}
	
	public static void setSettingSound(Context ctx, boolean sound){
		setSharedPrefValue(ctx, KEY_SETTING_SOUND, sound);
	}
	
	public static boolean getSettingSound(Context ctx){
		return getBooleanPrefValue(ctx, KEY_SETTING_SOUND, true);
	}
	
	public static void setSettingVibration(Context ctx, boolean vibration){
		setSharedPrefValue(ctx, KEY_SETTING_VIBRATION, vibration);
	}
	
	public static boolean getSettingVibration(Context ctx){
		return getBooleanPrefValue(ctx, KEY_SETTING_VIBRATION, true);
	}
}
