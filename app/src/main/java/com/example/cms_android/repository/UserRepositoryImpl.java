package com.example.cms_android.repository;

import android.content.Context;

import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.UserDao;
import com.example.cms_android.model.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 用户管理Repository实现类
 */
public class UserRepositoryImpl implements UserRepository {
    
    private final UserDao userDao;
    private final ExecutorService executorService;
    
    public UserRepositoryImpl(Context context) {
        this.userDao = AppDatabase.getDatabase(context).userDao();
        this.executorService = Executors.newSingleThreadExecutor();
    }
    
    @Override
    public void login(String username, String password, DataSourceCallback<User> callback) {
        executorService.execute(() -> {
            try {
                // 先检查用户是否存在
                User existingUser = userDao.getUserByUsername(username);
                if (existingUser == null) {
                    if (callback != null) {
                        callback.onError("该用户不存在");
                        return;
                    }
                }
                
                // 用户存在，检查密码是否正确
                User user = userDao.login(username, password);
                if (user != null) {
                    if (callback != null) {
                        callback.onSuccess(user);
                    }
                } else {
                    if (callback != null) {
                        callback.onError("用户名或密码错误");
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("登录失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void register(User user, DataSourceCallback<Long> callback) {
        executorService.execute(() -> {
            try {
                // 检查用户名是否已存在
                int usernameCount = userDao.checkUsernameExists(user.getUsername());
                if (usernameCount > 0) {
                    if (callback != null) {
                        callback.onError("用户名已存在");
                    }
                    return;
                }
                
                // 检查身份证是否已存在
                int idCardCount = userDao.checkIdCardExists(user.getIdCard());
                if (idCardCount > 0) {
                    if (callback != null) {
                        callback.onError("身份证已被注册");
                    }
                    return;
                }
                
                // 如果是管理员角色，检查是否已经存在管理员
                if ("admin".equals(user.getRole())) {
                    int adminCount = userDao.getAdminCount();
                    if (adminCount > 0) {
                        if (callback != null) {
                            callback.onError("系统中已存在管理员账户，无法创建新的管理员账户");
                        }
                        return;
                    }
                }
                
                long userId = userDao.insert(user);
                if (callback != null) {
                    callback.onSuccess(userId);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("注册失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getUserByUsername(String username, DataSourceCallback<User> callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByUsername(username);
                if (user != null) {
                    if (callback != null) {
                        callback.onSuccess(user);
                    }
                } else {
                    if (callback != null) {
                        callback.onError("用户不存在");
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询用户失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void getUserByEmail(String email, DataSourceCallback<User> callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByEmail(email);
                if (user != null) {
                    if (callback != null) {
                        callback.onSuccess(user);
                    }
                } else {
                    if (callback != null) {
                        callback.onError("邮箱未注册");
                    }
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("查询用户失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void updateUser(User user, DataSourceCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                userDao.update(user);
                if (callback != null) {
                    callback.onSuccess(true);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("更新用户信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void updatePassword(String username, String newPassword, DataSourceCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                userDao.updatePassword(username, newPassword);
                if (callback != null) {
                    callback.onSuccess(true);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("更新密码失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void checkUsernameExists(String username, DataSourceCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                int count = userDao.checkUsernameExists(username);
                if (callback != null) {
                    callback.onSuccess(count > 0);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("检查用户名失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void checkEmailExists(String email, DataSourceCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                int count = userDao.checkEmailExists(email);
                if (callback != null) {
                    callback.onSuccess(count > 0);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("检查邮箱失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void checkIdCardExists(String idCard, DataSourceCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                int count = userDao.checkIdCardExists(idCard);
                if (callback != null) {
                    callback.onSuccess(count > 0);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("检查身份证失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void checkUserCredentials(String username, String idCard, DataSourceCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                User user = userDao.getUserByUsernameAndIdCard(username, idCard);
                if (callback != null) {
                    callback.onSuccess(user != null);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("验证用户信息失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void updateLastLoginTime(long userId, String loginTime, DataSourceCallback<Boolean> callback) {
        executorService.execute(() -> {
            try {
                userDao.updateLastLoginTime(userId, loginTime);
                if (callback != null) {
                    callback.onSuccess(true);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("更新登录时间失败: " + e.getMessage());
                }
            }
        });
    }
    
    @Override
    public void checkAdminCount(DataSourceCallback<Integer> callback) {
        executorService.execute(() -> {
            try {
                int adminCount = userDao.getAdminCount();
                if (callback != null) {
                    callback.onSuccess(adminCount);
                }
            } catch (Exception e) {
                if (callback != null) {
                    callback.onError("检查管理员账户数量失败: " + e.getMessage());
                }
            }
        });
    }
}