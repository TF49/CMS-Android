package com.example.cms_android.repository;

import com.example.cms_android.model.Education;

import java.util.List;

/**
 * 教育管理Repository接口
 * 基于SQLite Room数据库的数据访问接口
 */
public interface EducationRepository extends PermissionChecker {
    
    /**
     * 插入教育信息
     */
    void insertEducation(Education education, DataSourceCallback callback);
    
    /**
     * 更新教育信息
     */
    void updateEducation(Education education, DataSourceCallback callback);
    
    /**
     * 删除教育信息
     */
    void deleteEducation(Education education, DataSourceCallback callback);
    
    /**
     * 根据ID查询教育信息
     */
    void getEducationById(Long id, DataSourceCallback callback);
    
    /**
     * 查询所有教育信息
     */
    void getAllEducations(DataSourceCallback callback);
    
    /**
     * 根据居民ID查询教育信息
     */
    void getEducationsByResidentId(Long residentId, DataSourceCallback callback);
    
    /**
     * 根据学校名称查询教育信息
     */
    void getEducationsBySchoolName(String schoolName, DataSourceCallback callback);
    
    /**
     * 数据源操作回调接口
     */
    interface DataSourceCallback {
        void onSuccess(Object result);
        void onError(String errorMessage);
    }
}