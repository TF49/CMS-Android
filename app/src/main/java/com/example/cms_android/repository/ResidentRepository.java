package com.example.cms_android.repository;

import com.example.cms_android.model.Resident;
import com.example.cms_android.model.User;

import java.util.List;

/**
 * 居民管理Repository接口
 * 基于SQLite Room数据库的数据访问接口
 */
public interface ResidentRepository extends PermissionChecker {
    
    /**
     * 插入居民信息
     */
    void insertResident(Resident resident, DataSourceCallback callback);
    
    /**
     * 更新居民信息
     */
    void updateResident(Resident resident, DataSourceCallback callback);
    
    /**
     * 删除居民信息
     */
    void deleteResident(Resident resident, DataSourceCallback callback);
    
    /**
     * 根据ID查询居民信息
     */
    void getResidentById(Long id, DataSourceCallback callback);
    
    /**
     * 查询所有居民信息
     */
    void getAllResidents(DataSourceCallback callback);
    
    /**
     * 根据姓名查询居民信息
     */
    void getResidentsByName(String name, DataSourceCallback callback);
    
    /**
     * 根据身份证号查询居民信息
     */
    void getResidentByIdCard(String idCard, DataSourceCallback callback);
    
    /**
     * 根据当前用户查询其拥有的居民信息（用于普通用户权限控制）
     */
    void getResidentsByCurrentUser(User user, DataSourceCallback callback);
    
    /**
     * 根据当前用户和居民ID验证是否有权限访问该居民信息
     */
    void checkResidentAccess(User user, Long residentId, DataSourceCallback callback);
    
    /**
     * 数据源操作回调接口
     */
    interface DataSourceCallback {
        void onSuccess(Object result);
        void onError(String errorMessage);
    }
}