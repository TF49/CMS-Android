package com.example.cms_android.repository;

import com.example.cms_android.model.Household;

import java.util.List;

/**
 * 户籍管理Repository接口
 * 基于SQLite Room数据库的数据访问接口
 */
public interface HouseholdRepository extends PermissionChecker {
    
    /**
     * 插入户籍信息
     */
    void insertHousehold(Household household, DataSourceCallback callback);
    
    /**
     * 更新户籍信息
     */
    void updateHousehold(Household household, DataSourceCallback callback);
    
    /**
     * 删除户籍信息
     */
    void deleteHousehold(Household household, DataSourceCallback callback);
    
    /**
     * 根据ID查询户籍信息
     */
    void getHouseholdById(Long id, DataSourceCallback callback);
    
    /**
     * 查询所有户籍信息
     */
    void getAllHouseholds(DataSourceCallback callback);
    
    /**
     * 根据户主姓名查询户籍信息
     */
    void getHouseholdsByHouseholderName(String name, DataSourceCallback callback);
    
    /**
     * 根据地址查询户籍信息
     */
    void getHouseholdsByAddress(String address, DataSourceCallback callback);
    
    /**
     * 数据源操作回调接口
     */
    interface DataSourceCallback {
        void onSuccess(Object result);
        void onError(String errorMessage);
    }
}