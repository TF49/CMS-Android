package com.example.cms_android.repository;

import android.content.Context;
import android.os.AsyncTask;

import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.HouseholdDao;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.PermissionManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 户籍管理Repository实现类
 * 基于SQLite Room数据库
 */
public class HouseholdRepositoryImpl implements HouseholdRepository {
    
    private HouseholdDao householdDao;
    private ExecutorService executorService;
    
    public HouseholdRepositoryImpl(Context context) {
        AppDatabase database = AppDatabase.getDatabase(context);
        this.householdDao = database.householdDao();
        this.executorService = Executors.newFixedThreadPool(4);
    }
    
    @Override
    public void insertHousehold(Household household, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                householdDao.insert(household);
                if (callback != null) {
                    callback.onSuccess("户籍信息添加成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("添加户籍信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void updateHousehold(Household household, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                householdDao.update(household);
                if (callback != null) {
                    callback.onSuccess("户籍信息更新成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("更新户籍信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void deleteHousehold(Household household, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                householdDao.delete(household);
                if (callback != null) {
                    callback.onSuccess("户籍信息删除成功");
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("删除户籍信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getHouseholdById(Long id, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                Household household = householdDao.getHouseholdById(id);
                if (callback != null) {
                    callback.onSuccess(household);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询户籍信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getAllHouseholds(DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                List<Household> households = householdDao.getAllHouseholds();
                if (callback != null) {
                    callback.onSuccess(households);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询所有户籍信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getHouseholdsByHouseholderName(String name, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                List<Household> households = householdDao.getHouseholdsByHouseholderName(name);
                if (callback != null) {
                    callback.onSuccess(households);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("根据户主姓名查询失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getHouseholdsByAddress(String address, DataSourceCallback callback) {
        executorService.execute(() -> {
            try {
                List<Household> households = householdDao.getHouseholdsByAddress(address);
                if (callback != null) {
                    callback.onSuccess(households);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("根据地址查询失败: " + e.getMessage());
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