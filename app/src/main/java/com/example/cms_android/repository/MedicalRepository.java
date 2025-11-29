package com.example.cms_android.repository;

import com.example.cms_android.model.Medical;

import java.util.List;

/**
 * 医疗管理Repository接口
 * 基于SQLite Room数据库的数据访问接口
 */
public interface MedicalRepository extends PermissionChecker {
    
    /**
     * 插入医疗信息
     */
    void insertMedical(Medical medical, DataSourceCallback callback);
    
    /**
     * 更新医疗信息
     */
    void updateMedical(Medical medical, DataSourceCallback callback);
    
    /**
     * 删除医疗信息
     */
    void deleteMedical(Medical medical, DataSourceCallback callback);
    
    /**
     * 根据ID查询医疗信息
     */
    void getMedicalById(Long id, DataSourceCallback callback);
    
    /**
     * 查询所有医疗信息
     */
    void getAllMedicals(DataSourceCallback callback);
    
    /**
     * 根据居民ID查询医疗信息
     */
    void getMedicalsByResidentId(Long residentId, DataSourceCallback callback);
    
    /**
     * 根据医院名称查询医疗信息
     */
    void getMedicalsByHospital(String hospital, DataSourceCallback callback);
    
    /**
     * 数据源操作回调接口
     */
    interface DataSourceCallback {
        void onSuccess(Object result);
        void onError(String errorMessage);
    }
}