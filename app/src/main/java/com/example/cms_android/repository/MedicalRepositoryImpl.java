package com.example.cms_android.repository;

import android.content.Context;

import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.MedicalDao;
import com.example.cms_android.model.Medical;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.PermissionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 医疗管理Repository实现类
 * 基于SQLite Room数据库
 */
public class MedicalRepositoryImpl implements MedicalRepository {
    
    private MedicalDao medicalDao;
    private ExecutorService executorService;
    
    public MedicalRepositoryImpl(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        this.medicalDao = database.medicalDao();
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public void insertMedical(Medical medical, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                medicalDao.insert(medical);
                if (callback != null) {
                    callback.onSuccess("医疗信息添加成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("添加医疗信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void updateMedical(Medical medical, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                medicalDao.update(medical);
                if (callback != null) {
                    callback.onSuccess("医疗信息更新成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("更新医疗信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void deleteMedical(Medical medical, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                medicalDao.delete(medical);
                if (callback != null) {
                    callback.onSuccess("医疗信息删除成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("删除医疗信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getMedicalById(Long id, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                Medical medical = medicalDao.getMedicalById(id);
                if (callback != null) {
                    callback.onSuccess(medical);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询医疗信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getAllMedicals(DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                List<Medical> medicals = medicalDao.getAllMedicalRecords();
                if (callback != null) {
                    callback.onSuccess(medicals);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询所有医疗信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getMedicalsByResidentId(Long residentId, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                Medical medical = medicalDao.getMedicalByResident(residentId);
                if (callback != null) {
                    callback.onSuccess(medical);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("根据居民ID查询失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getMedicalsByHospital(String hospital, DataSourceCallback callback) {
        // 这个方法在DAO中没有直接对应的方法，我们暂时留空或给出错误提示
        if (callback != null) {
            callback.onError("根据医院名称查询功能暂未实现");
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