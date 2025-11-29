package com.example.cms_android.repository;

import android.content.Context;

import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.EducationDao;
import com.example.cms_android.model.Education;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.PermissionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 教育管理Repository实现类
 * 基于SQLite Room数据库
 */
public class EducationRepositoryImpl implements EducationRepository {
    
    private EducationDao educationDao;
    private ExecutorService executorService;
    
    public EducationRepositoryImpl(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        this.educationDao = database.educationDao();
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public void insertEducation(Education education, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                educationDao.insert(education);
                if (callback != null) {
                    callback.onSuccess("教育信息添加成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("添加教育信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void updateEducation(Education education, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                educationDao.update(education);
                if (callback != null) {
                    callback.onSuccess("教育信息更新成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("更新教育信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void deleteEducation(Education education, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                educationDao.delete(education);
                if (callback != null) {
                    callback.onSuccess("教育信息删除成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("删除教育信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getEducationById(Long id, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                Education education = educationDao.getEducationById(id);
                if (callback != null) {
                    callback.onSuccess(education);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询教育信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getAllEducations(DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                List<Education> educations = educationDao.getAllEducationRecords();
                if (callback != null) {
                    callback.onSuccess(educations);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询所有教育信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getEducationsByResidentId(Long residentId, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                List<Education> educations = educationDao.getEducationByResident(residentId);
                if (callback != null) {
                    callback.onSuccess(educations);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("根据居民ID查询失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getEducationsBySchoolName(String schoolName, DataSourceCallback callback) {
        // 这个方法在DAO中没有直接对应的方法，我们暂时留空或给出错误提示
        if (callback != null) {
            callback.onError("根据学校名称查询功能暂未实现");
        }
    }
    
    @Override
    public boolean canAdd(User user) {
        return PermissionManager.canAdd(user);
    }
    
    @Override
    public boolean canEdit(User user) {
        return PermissionManager.canEdit(user);
    }
    
    @Override
    public boolean canDelete(User user) {
        return PermissionManager.canDelete(user);
    }
    
    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}