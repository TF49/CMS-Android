package com.example.cms_android.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.User;
import com.example.cms_android.repository.UserRepository;
import com.example.cms_android.repository.UserRepositoryImpl;
import com.example.cms_android.utils.ValidationUtils;
import com.example.cms_android.utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 注册Activity
 */
public class RegisterActivity extends AppCompatActivity
{
    private UserRepository userRepository;
    private TextInputEditText etUsername, etPassword, etConfirmPassword, etIdCard;
    private TextInputLayout tilUsername, tilPassword, tilConfirmPassword, tilIdCard;
    private MaterialButton btnRegister;
    private RadioGroup rgUserRole;
    private RadioButton rbAdmin, rbUser;
    private SharedPreferencesManager sharedPreferencesManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        // 初始化Repository
        userRepository = new UserRepositoryImpl(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        
        // 初始化视图
        initViews();
        
        // 设置点击监听器
        setupClickListeners();
    }
    
    private void initViews()
    {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etIdCard = findViewById(R.id.etIdCard);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);
        tilIdCard = findViewById(R.id.tilIdCard);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        
        btnRegister = findViewById(R.id.btnRegister);
        
        // 角色选择
        rgUserRole = findViewById(R.id.rgUserRole);
        rbAdmin = findViewById(R.id.rbAdmin);
        rbUser = findViewById(R.id.rbUser);
        
        // 默认选择普通用户
        rbUser.setChecked(true);
    }
    
    private void setupClickListeners()
    {
        // 注册按钮点击事件
        btnRegister.setOnClickListener(v -> attemptRegister());
        
        // 返回登录链接点击事件
        findViewById(R.id.tvBackToLogin).setOnClickListener(v -> finish());
    }
    
    private void attemptRegister()
    {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // 获取用户选择的角色
        String role = rbAdmin.isChecked() ? "admin" : "user";
        
        if (validateInputs(username, password, confirmPassword, idCard))
        {
            // 开始注册
            performRegister(username, password, idCard, role);
        }
    }
    
    private boolean validateInputs(String username, String password, String confirmPassword, String idCard)
    {
        // 清除之前的错误提示
        tilUsername.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilIdCard.setError(null);
        
        // 验证用户名
        if (TextUtils.isEmpty(username))
        {
            tilUsername.setError("请输入用户名");
            etUsername.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isValidUsername(username))
        {
            tilUsername.setError(ValidationUtils.getUsernameError(username));
            etUsername.requestFocus();
            return false;
        }
        
        // 验证密码
        if (TextUtils.isEmpty(password))
        {
            tilPassword.setError("请输入密码");
            etPassword.requestFocus();
            return false;
        }

        if (!ValidationUtils.isValidPassword(password))
        {
            tilPassword.setError(ValidationUtils.getPasswordError(password));
            etPassword.requestFocus();
            return false;
        }
        
        // 验证确认密码
        if (TextUtils.isEmpty(confirmPassword))
        {
            tilConfirmPassword.setError("请确认密码");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        if (!password.equals(confirmPassword))
        {
            tilConfirmPassword.setError("两次输入的密码不一致");
            etConfirmPassword.requestFocus();
            return false;
        }
        
        // 验证身份证
        if (TextUtils.isEmpty(idCard))
        {
            tilIdCard.setError("请输入身份证号码");
            etIdCard.requestFocus();
            return false;
        }
        
        if (!ValidationUtils.isValidIdCard(idCard))
        {
            tilIdCard.setError(ValidationUtils.getIdCardError(idCard));
            etIdCard.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void performRegister(String username, String password, String idCard, String role)
    {
        // 检查用户名是否已存在
        userRepository.checkUsernameExists(username, new UserRepository.DataSourceCallback<Boolean>()
        {
            @Override
            public void onSuccess(Boolean usernameExists)
            {
                if (usernameExists)
                {
                    // 用户名已存在
                    runOnUiThread(() ->
                    {
                        tilUsername.setError("用户名已存在");
                        etUsername.requestFocus();
                    });
                    return;
                }
                
                // 检查身份证是否已存在
                userRepository.checkIdCardExists(idCard, new UserRepository.DataSourceCallback<Boolean>()
                {
                    @Override
                    public void onSuccess(Boolean idCardExists)
                    {
                        if (idCardExists)
                        {
                            runOnUiThread(() ->
                            {
                                tilIdCard.setError("身份证已被注册");
                                etIdCard.requestFocus();
                            });
                            return;
                        }
                        
                        // 如果是管理员角色，检查是否已经存在管理员
                        if ("admin".equals(role))
                        {
                            userRepository.checkAdminCount(new UserRepository.DataSourceCallback<Integer>()
                            {
                                @Override
                                public void onSuccess(Integer adminCount)
                                {
                                    if (adminCount > 0)
                                    {
                                        runOnUiThread(() ->
                                        {
                                            Toast.makeText(RegisterActivity.this, "系统中已存在管理员账户，无法创建新的管理员账户", Toast.LENGTH_LONG).show();
                                        });
                                        return;
                                    }
                                    
                                    // 创建新用户
                                    createNewUser(username, password, idCard, role);
                                }
                                
                                @Override
                                public void onError(String error)
                                {
                                    runOnUiThread(() ->
                                    {
                                        Toast.makeText(RegisterActivity.this, "检查管理员账户失败: " + error, Toast.LENGTH_SHORT).show();
                                    });
                                }
                            });
                        }
                        else
                        {
                            // 创建新用户
                            createNewUser(username, password, idCard, role);
                        }
                    }
                    
                    @Override
                    public void onError(String error)
                    {
                        runOnUiThread(() ->
                        {
                            Toast.makeText(RegisterActivity.this, "检查身份证失败: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                });
            }
            
            @Override
            public void onError(String error)
            {
                runOnUiThread(() ->
                {
                    Toast.makeText(RegisterActivity.this, "检查用户名失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void createNewUser(String username, String password, String idCard, String role)
    {
        // 创建新用户
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setIdCard(idCard);
        newUser.setRole(role); // 用户选择的角色
        newUser.setCreateTime(currentTime);
        newUser.setLastLoginTime(currentTime);
        newUser.setActive(true);
        
        // 注册用户
        userRepository.register(newUser, new UserRepository.DataSourceCallback<Long>()
        {
            @Override
            public void onSuccess(Long userId)
            {
                runOnUiThread(() ->
                {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    // 保存登录状态
                    newUser.setId(userId);
                    sharedPreferencesManager.saveLoginInfo(newUser);
                    finish(); // 返回登录页面
                });
            }
            
            @Override
            public void onError(String error)
            {
                runOnUiThread(() ->
                {
                    Toast.makeText(RegisterActivity.this, "注册失败: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}