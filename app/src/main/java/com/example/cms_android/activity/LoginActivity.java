package com.example.cms_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.model.User;
import com.example.cms_android.repository.UserRepository;
import com.example.cms_android.repository.UserRepositoryImpl;
import com.example.cms_android.utils.SharedPreferencesManager;
import com.example.cms_android.utils.ValidationUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 登录Activity
 */
public class LoginActivity extends AppCompatActivity
{
    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvRegister;
    private TextView tvForgotPassword;
    private ProgressBar progressBar;
    
    private UserRepository userRepository;
    private SharedPreferencesManager sharedPreferencesManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // 初始化Repository和SharedPreferences
        userRepository = new UserRepositoryImpl(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);
        
        // 初始化视图
        initViews();
        
        // 设置点击监听器
        setupClickListeners();
    }
    
    private void initViews()
    {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegister = findViewById(R.id.tv_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        progressBar = findViewById(R.id.progress_bar);
    }
    
    private void setupClickListeners()
    {
        btnLogin.setOnClickListener(v -> attemptLogin());
        
        tvRegister.setOnClickListener(v ->
        {
            // 跳转到注册页面
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        
        tvForgotPassword.setOnClickListener(v ->
        {
            // 跳转到忘记密码页面
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }
    
    private void attemptLogin()
    {
        // 重置错误
        etUsername.setError(null);
        etPassword.setError(null);
        
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        
        boolean cancel = false;
        View focusView = null;
        
        // 验证用户名格式
        String usernameError = ValidationUtils.getUsernameError(username);
        if (usernameError != null)
        {
            etUsername.setError(usernameError);
            focusView = etUsername;
            cancel = true;
        }
        
        // 验证密码格式
        String passwordError = ValidationUtils.getPasswordError(password);
        if (passwordError != null)
        {
            etPassword.setError(passwordError);
            if (focusView == null)
            {
                focusView = etPassword;
            }
            cancel = true;
        }
        
        if (cancel)
        {
            // 有错误，聚焦到第一个错误字段
            focusView.requestFocus();
        }
        else
        {
            // 显示进度条，开始登录
            showProgress(true);
            performLogin(username, password);
        }
    }
    
    private void performLogin(String username, String password)
    {
        userRepository.login(username, password, new UserRepository.DataSourceCallback<User>()
        {
            @Override
            public void onSuccess(User user)
            {
                runOnUiThread(() ->
                {
                    showProgress(false);
                    
                    // 更新最后登录时间
                    updateLastLoginTime(user.getId());
                    
                    // 登录成功，跳转到主页面
                    loginSuccess(user);
                });
            }
            
            @Override
            public void onError(String errorMessage)
            {
                runOnUiThread(() ->
                {
                    showProgress(false);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                });
            }
        });
    }
    
    private void updateLastLoginTime(long userId)
    {
        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(new Date());
        
        userRepository.updateLastLoginTime(userId, currentTime, new UserRepository.DataSourceCallback<Boolean>()
        {
            @Override
            public void onSuccess(Boolean success)
            {
                // 登录时间更新成功
            }
            
            @Override
            public void onError(String errorMessage)
            {
                // 更新失败，不影响主要登录流程
            }
        });
    }
    
    private void loginSuccess(User user)
    {
        // 保存登录状态到SharedPreferences
        sharedPreferencesManager.saveLoginInfo(user);
        
        // 跳转到主页面
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        
        Toast.makeText(this, "欢迎回来，" + user.getUsername() + "！", Toast.LENGTH_SHORT).show();
    }
    
    private void showProgress(boolean show)
    {
        if (show)
        {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            tvRegister.setEnabled(false);
            tvForgotPassword.setEnabled(false);
        }
        else
        {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            tvRegister.setEnabled(true);
            tvForgotPassword.setEnabled(true);
        }
    }
    
    @Override
    public void onBackPressed()
    {
        // 按返回键退出应用
        moveTaskToBack(true);
    }
}