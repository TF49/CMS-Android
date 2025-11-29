package com.example.cms_android.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.cms_android.model.User;

/**
 * SharedPreferences管理类
 */
public class SharedPreferencesManager {
    
    private static final String PREF_NAME = "cms_preferences";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ID_CARD = "id_card";
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    
    public SharedPreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
    
    /**
     * 保存用户登录信息
     */
    public void saveLoginInfo(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_PHONE, user.getPhone());
        editor.putString(KEY_ID_CARD, user.getIdCard());
        editor.apply();
    }
    
    /**
     * 清除登录信息
     */
    public void clearLoginInfo() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_ROLE);
        editor.remove(KEY_PHONE);
        editor.remove(KEY_ID_CARD);
        editor.apply();
    }
    
    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * 获取当前登录用户ID
     */
    public long getCurrentUserId() {
        return sharedPreferences.getLong(KEY_USER_ID, -1);
    }
    
    /**
     * 获取当前登录用户名
     */
    public String getCurrentUsername() {
        return sharedPreferences.getString(KEY_USERNAME, "");
    }
    
    /**
     * 获取当前登录用户邮箱
     */
    public String getCurrentEmail() {
        return sharedPreferences.getString(KEY_EMAIL, "");
    }
    
    /**
     * 获取当前登录用户角色
     */
    public String getCurrentRole() {
        return sharedPreferences.getString(KEY_ROLE, "");
    }
    
    /**
     * 获取当前登录用户电话
     */
    public String getCurrentPhone() {
        return sharedPreferences.getString(KEY_PHONE, "");
    }
    
    /**
     * 获取当前登录用户身份证号
     */
    public String getCurrentIdCard() {
        return sharedPreferences.getString(KEY_ID_CARD, "");
    }
    
    /**
     * 获取用户基本信息
     */
    public User getCurrentUser() {
        if (!isLoggedIn()) {
            return null;
        }
        
        User user = new User();
        user.setId(getCurrentUserId());
        user.setUsername(getCurrentUsername());
        user.setEmail(getCurrentEmail());
        user.setRole(getCurrentRole());
        user.setPhone(getCurrentPhone());
        user.setIdCard(getCurrentIdCard());
        
        return user;
    }
}