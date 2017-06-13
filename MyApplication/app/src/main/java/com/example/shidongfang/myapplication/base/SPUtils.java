package com.example.shidongfang.myapplication.base;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by huangchaoyang on 2017/1/9.
 */

public class SPUtils {
    private static final String SPDIR = "Ora_Sp";

    public static final String LAST_MODIFY = "LastModify";
    public static final String IS_MODIFY = "isModify";
    public static final String IS_NFC_LIST_MODE = "isNfcMode";
    public static final String SUPPORT_EMAIL = "support_email";
    public static final String SUPPORT_BANK = "support_bank";
    public static final String MIFARE_DATA = "mifare_data";
    public static final String HAS_CARD_DATA = "has_card_data";
    public static final String CONN_PHONE_NAME = "conn_phone_name";
    public static final String IS_KEEP_DATA = "is_keep_data";
    public static final String NFC_RF_LISTEN_DISABLE = "rf_listen_disable";
    public static final String IS_DEVICE_LOST = "is_device_lost";

    public static void putLong(String key, long value){
        SharedPreferences sp = getSharedPreferences();
        sp.edit().putLong(key, value).apply();
    }

    public static void putString(String key, String string){
        SharedPreferences sp = getSharedPreferences();
        sp.edit().putString(key,string).apply();
    }

    public static void putStringSet(String key, Set<String> set){
        SharedPreferences sp = getSharedPreferences();
        sp.edit().putStringSet(key, set).apply();
    }

    public static void putBoolean(String key, boolean bool){
        SharedPreferences sp = getSharedPreferences();
        sp.edit().putBoolean(key,bool).apply();
    }

    public static void putInt(String key, int value){
        SharedPreferences sp = getSharedPreferences();
        sp.edit().putInt(key, value).apply();
    }

    public static long getLong(String key, long def){
        SharedPreferences sp = getSharedPreferences();
        return sp.getLong(key, def);
    }

    public static String getString(String key){
        SharedPreferences sp = getSharedPreferences();
        return sp.getString(key,"");
    }

    public static Set<String> getStringSet(String key){
        SharedPreferences sp = getSharedPreferences();
        return sp.getStringSet(key, null);
    }

    public static boolean getBoolean(String key){
        SharedPreferences sp = getSharedPreferences();
        return sp.getBoolean(key, false);
    }

    public static int getInt(String key, int def){
        SharedPreferences sp = getSharedPreferences();
        return sp.getInt(key, def);
    }

    private static SharedPreferences getSharedPreferences() {
        return BaseApplication.getContext().getSharedPreferences(SPDIR, Context.MODE_APPEND);
    }
}
