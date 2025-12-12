package com.example.cms_android.utils;

import com.example.cms_android.model.User;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.Education;
import com.example.cms_android.model.Medical;

/**
 * 权限管理工具类
 */
public class PermissionManager {
    public static final String ROLE_ADMIN = "admin";
    public static final String ROLE_USER = "user";
    
    /**
     * 检查是否为管理员
     */
    public static boolean isAdmin(User user) {
        return user != null && ROLE_ADMIN.equals(user.getRole());
    }
    
    /**
     * 检查是否为普通用户
     */
    public static boolean isUser(User user) {
        return user != null && ROLE_USER.equals(user.getRole());
    }
    
    /**
     * 检查是否有权限执行删除操作
     */
    public static boolean canDelete(User user) {
        return user != null; // 所有登录用户都可以删除数据
    }
    
    /**
     * 检查是否有权限执行编辑操作
     */
    public static boolean canEdit(User user) {
        return user != null; // 所有登录用户都可以编辑数据
    }
    
    /**
     * 检查是否有权限执行添加操作
     */
    public static boolean canAdd(User user) {
        return user != null; // 所有登录用户都可以添加数据
    }
    
    /**
     * 检查用户是否有权限访问特定居民信息
     * 管理员可以访问所有居民信息，普通用户只能访问自己创建的居民信息
     */
    public static boolean canAccessResident(User user, Resident resident) {
        if (user == null || resident == null) {
            return false;
        }
        
        if (isAdmin(user)) {
            return true; // 管理员可以访问所有居民信息
        }
        
        // 普通用户只能访问自己拥有的居民信息
        return user.getId() == resident.getOwnerId();
    }
    
    /**
     * 检查用户是否有权限访问特定户籍信息
     * 管理员可以访问所有户籍信息，普通用户只能访问自己创建的户籍信息
     */
    public static boolean canAccessHousehold(User user, Household household) {
        if (user == null || household == null) {
            return false;
        }
        
        if (isAdmin(user)) {
            return true; // 管理员可以访问所有户籍信息
        }
        
        // 普通用户只能访问自己拥有的户籍信息
        return user.getId() == household.getOwnerId();
    }
    
    /**
     * 检查用户是否有权限访问特定教育信息
     * 管理员可以访问所有教育信息，普通用户只能访问自己创建的教育信息
     */
    public static boolean canAccessEducation(User user, Education education) {
        if (user == null || education == null) {
            return false;
        }
        
        if (isAdmin(user)) {
            return true; // 管理员可以访问所有教育信息
        }
        
        // 普通用户只能访问自己拥有的教育信息
        return user.getId() == education.getOwnerId();
    }
    
    /**
     * 检查用户是否有权限访问特定医疗信息
     * 管理员可以访问所有医疗信息，普通用户只能访问自己创建的医疗信息
     */
    public static boolean canAccessMedical(User user, Medical medical) {
        if (user == null || medical == null) {
            return false;
        }
        
        if (isAdmin(user)) {
            return true; // 管理员可以访问所有医疗信息
        }
        
        // 普通用户只能访问自己拥有的医疗信息
        return user.getId() == medical.getOwnerId();
    }
    
    /**
     * 检查用户是否有权限修改特定居民信息
     */
    public static boolean canModifyResident(User user, Resident resident) {
        return canAccessResident(user, resident) && canEdit(user);
    }
    
    /**
     * 检查用户是否有权限修改特定户籍信息
     */
    public static boolean canModifyHousehold(User user, Household household) {
        return canAccessHousehold(user, household) && canEdit(user);
    }
    
    /**
     * 检查用户是否有权限修改特定教育信息
     */
    public static boolean canModifyEducation(User user, Education education) {
        return canAccessEducation(user, education) && canEdit(user);
    }
    
    /**
     * 检查用户是否有权限修改特定医疗信息
     */
    public static boolean canModifyMedical(User user, Medical medical) {
        return canAccessMedical(user, medical) && canEdit(user);
    }
    
    /**
     * 检查用户是否有权限删除特定居民信息
     */
    public static boolean canRemoveResident(User user, Resident resident) {
        return canAccessResident(user, resident) && canDelete(user);
    }
    
    /**
     * 检查用户是否有权限删除特定户籍信息
     */
    public static boolean canRemoveHousehold(User user, Household household) {
        return canAccessHousehold(user, household) && canDelete(user);
    }
    
    /**
     * 检查用户是否有权限删除特定教育信息
     */
    public static boolean canRemoveEducation(User user, Education education) {
        return canAccessEducation(user, education) && canDelete(user);
    }
    
    /**
     * 检查用户是否有权限删除特定医疗信息
     */
    public static boolean canRemoveMedical(User user, Medical medical) {
        return canAccessMedical(user, medical) && canDelete(user);
    }
}