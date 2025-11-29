package com.example.cms_android.repository;

import android.content.Context;

import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.PermissionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 居民管理Repository实现类
 * 基于SQLite Room数据库
 */
public class ResidentRepositoryImpl implements ResidentRepository {
    
    private ResidentDao residentDao;
    private ExecutorService executorService;
    
    public ResidentRepositoryImpl(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        this.residentDao = database.residentDao();
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public void insertResident(Resident resident, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                residentDao.insert(resident);
                if (callback != null) {
                    callback.onSuccess("居民信息添加成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("添加居民信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void updateResident(Resident resident, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                residentDao.update(resident);
                if (callback != null) {
                    callback.onSuccess("居民信息更新成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("更新居民信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void deleteResident(Resident resident, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                residentDao.delete(resident);
                if (callback != null) {
                    callback.onSuccess("居民信息删除成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("删除居民信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getResidentById(Long id, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                Resident resident = residentDao.getResidentById(id);
                if (callback != null) {
                    callback.onSuccess(resident);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询居民信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getAllResidents(DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                List<Resident> residents = residentDao.getAllResidents();
                if (callback != null) {
                    callback.onSuccess(residents);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询所有居民信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getResidentsByName(String name, DataSourceCallback callback) {
        // 这个方法在DAO中没有直接对应的方法，我们使用searchResidents方法替代
        executorService.execute(() -> {
            try {
                List<Resident> residents = residentDao.searchResidents(name);
                if (callback != null) {
                    callback.onSuccess(residents);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("根据姓名查询失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getResidentByIdCard(String idCard, DataSourceCallback callback) {
        // 这个方法在DAO中没有直接对应的方法，我们暂时留空或给出错误提示
        if (callback != null) {
            callback.onError("根据身份证号查询功能暂未实现");
        }
    }
    
    @Override
    public void getResidentsByCurrentUser(User user, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                List<Resident> residents;
                if (PermissionManager.isAdmin(user)) {
                    // 管理员可以查看所有居民信息
                    residents = residentDao.getAllResidents();
                } else {
                    // 普通用户只能查看自己拥有的居民信息
                    residents = residentDao.getResidentsByOwner(user.getId());
                }
                if (callback != null) {
                    callback.onSuccess(residents);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询居民信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void checkResidentAccess(User user, Long residentId, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                boolean hasAccess;
                if (PermissionManager.isAdmin(user)) {
                    // 管理员可以访问所有居民信息
                    hasAccess = true;
                } else {
                    // 普通用户只能访问自己拥有的居民信息
                    Resident resident = residentDao.getResidentByOwner(residentId, user.getId());
                    hasAccess = resident != null;
                }
                if (callback != null) {
                    callback.onSuccess(hasAccess);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("验证居民访问权限失败: " + e.getMessage());
                }
            }
        });
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