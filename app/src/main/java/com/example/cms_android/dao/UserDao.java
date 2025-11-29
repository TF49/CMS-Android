package com.example.cms_android.dao;

import androidx.room.*;
import com.example.cms_android.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    long insert(User user);

    @Update
    void update(User user);

    @Delete
    void delete(User user);

    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    User login(String username, String password);

    @Query("SELECT * FROM users WHERE username = :username")
    User getUserByUsername(String username);

    @Query("SELECT * FROM users WHERE email = :email")
    User getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :id")
    User getUserById(long id);

    @Query("SELECT * FROM users ORDER BY username ASC")
    List<User> getAllUsers();

    @Query("UPDATE users SET password = :newPassword WHERE username = :username")
    void updatePassword(String username, String newPassword);

    @Query("UPDATE users SET lastLoginTime = :loginTime WHERE id = :id")
    void updateLastLoginTime(long id, String loginTime);

    @Query("SELECT COUNT(*) FROM users WHERE username = :username")
    int checkUsernameExists(String username);

    @Query("SELECT COUNT(*) FROM users WHERE email = :email")
    int checkEmailExists(String email);
    
    @Query("SELECT COUNT(*) FROM users WHERE idCard = :idCard")
    int checkIdCardExists(String idCard);
    
    @Query("SELECT * FROM users WHERE username = :username AND idCard = :idCard")
    User getUserByUsernameAndIdCard(String username, String idCard);
    
    @Query("SELECT COUNT(*) FROM users WHERE role = 'admin'")
    int getAdminCount();
}