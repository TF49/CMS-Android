package com.example.cms_android.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

/**
 * 输入验证工具类
 */
public class ValidationUtils {
    
    // 用户名正则：任意字符串，1-50位
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^.{1,50}$");
    
    // 密码正则：任意字符，至少6位
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^.{6,}$");
    
    // 手机号正则：11位数字
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    
    // 身份证正则：15位或18位数字，最后一位可以是X
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^(\\d{15}|\\d{18}|\\d{17}[Xx])$");
    
    /**
     * 验证用户名格式
     */
    public static boolean isValidUsername(String username) {
        if (TextUtils.isEmpty(username)) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }
    
    /**
     * 验证密码格式
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * 验证邮箱格式
     */
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * 验证手机号格式
     */
    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * 验证身份证格式
     */
    public static boolean isValidIdCard(String idCard) {
        if (TextUtils.isEmpty(idCard)) {
            return false;
        }
        return ID_CARD_PATTERN.matcher(idCard).matches();
    }
    
    /**
     * 获取用户名验证错误信息
     */
    public static String getUsernameError(String username) {
        if (TextUtils.isEmpty(username)) {
            return "用户名不能为空";
        }
        if (username.length() > 50) {
            return "用户名最多50位";
        }
        return null;
    }
    
    /**
     * 获取密码验证错误信息
     */
    public static String getPasswordError(String password) {
        if (TextUtils.isEmpty(password)) {
            return "密码不能为空";
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return "密码至少需要6位";
        }
        return null;
    }
    
    /**
     * 获取邮箱验证错误信息
     */
    public static String getEmailError(String email) {
        if (TextUtils.isEmpty(email)) {
            return "邮箱不能为空";
        }
        if (!isValidEmail(email)) {
            return "邮箱格式不正确";
        }
        return null;
    }
    
    /**
     * 获取手机号验证错误信息
     */
    public static String getPhoneError(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return "手机号不能为空";
        }
        if (!isValidPhone(phone)) {
            return "手机号格式不正确";
        }
        return null;
    }
    
    /**
     * 获取身份证验证错误信息
     */
    public static String getIdCardError(String idCard) {
        if (TextUtils.isEmpty(idCard)) {
            return "身份证号不能为空";
        }
        if (!isValidIdCard(idCard)) {
            return "身份证格式不正确（15位或18位数字）";
        }
        return null;
    }
}