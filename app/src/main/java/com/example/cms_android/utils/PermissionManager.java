package com.example.cms_android.utils;

import com.example.cms_android.model.User;
import com.example.cms_android.model.Resident;

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
        return isAdmin(user);
    }
    
    /**
     * 检查是否有权限执行编辑操作
     */
    public static boolean canEdit(User user) {
        return isAdmin(user); // 只有管理员可以编辑
    }
    
    /**
     * 检查是否有权限执行添加操作
     */
    public static boolean canAdd(User user) {
        return isAdmin(user);
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
     * 检查用户是否有权限修改特定居民信息
     */
    public static boolean canModifyResident(User user, Resident resident) {
        return canAccessResident(user, resident) && canEdit(user);
    }
    
    /**
     * 检查用户是否有权限删除特定居民信息
     */
    public static boolean canRemoveResident(User user, Resident resident) {
        return canAccessResident(user, resident) && canDelete(user);
    }
}