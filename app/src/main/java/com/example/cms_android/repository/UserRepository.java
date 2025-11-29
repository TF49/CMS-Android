package com.example.cms_android.repository;

import com.example.cms_android.model.User;

/**
 * 用户管理Repository接口
 */
public interface UserRepository {
    
    /**
     * 数据源回调接口
     */
    interface DataSourceCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }
    
    /**
     * 用户登录
     */
    void login(String username, String password, DataSourceCallback<User> callback);
    
    /**
     * 用户注册
     */
    void register(User user, DataSourceCallback<Long> callback);
    
    /**
     * 根据用户名获取用户信息
     */
    void getUserByUsername(String username, DataSourceCallback<User> callback);
    
    /**
     * 根据邮箱获取用户信息
     */
    void getUserByEmail(String email, DataSourceCallback<User> callback);
    
    /**
     * 更新用户信息
     */
    void updateUser(User user, DataSourceCallback<Boolean> callback);
    
    /**
     * 更新密码
     */
    void updatePassword(String username, String newPassword, DataSourceCallback<Boolean> callback);
    
    /**
     * 检查用户名是否存在
     */
    void checkUsernameExists(String username, DataSourceCallback<Boolean> callback);
    
    /**
     * 检查邮箱是否存在
     */
    void checkEmailExists(String email, DataSourceCallback<Boolean> callback);
    
    /**
     * 检查身份证是否存在
     */
    void checkIdCardExists(String idCard, DataSourceCallback<Boolean> callback);
    
    /**
     * 更新最后登录时间
     */
    void updateLastLoginTime(long userId, String loginTime, DataSourceCallback<Boolean> callback);
    
    /**
     * 检查用户名和身份证是否匹配（用于忘记密码验证）
     */
    void checkUserCredentials(String username, String idCard, DataSourceCallback<Boolean> callback);
    
    /**
     * 检查管理员账户数量
     */
    void checkAdminCount(DataSourceCallback<Integer> callback);
}