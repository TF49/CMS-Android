package com.example.cms_android.repository;

import com.example.cms_android.model.User;

/**
 * 权限检查接口
 */
public interface PermissionChecker {
    
    /**
     * 检查用户是否有权限添加数据
     */
    boolean canAdd(User user);
    
    /**
     * 检查用户是否有权限编辑数据
     */
    boolean canEdit(User user);
    
    /**
     * 检查用户是否有权限删除数据
     */
    boolean canDelete(User user);
}