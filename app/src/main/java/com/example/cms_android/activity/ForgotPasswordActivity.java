package com.example.cms_android.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cms_android.R;
import com.example.cms_android.repository.UserRepository;
import com.example.cms_android.repository.UserRepositoryImpl;
import com.example.cms_android.utils.ValidationUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ForgotPasswordActivity extends AppCompatActivity {
    private TextInputEditText etUsername, etIdCard, etNewPassword, etConfirmPassword;
    private TextInputLayout tilUsername, tilIdCard, tilNewPassword, tilConfirmPassword;
    private MaterialButton btnVerify, btnResetPassword;
    private TextView tvBackToLogin;
    
    private UserRepository userRepository;
    
    // 用于存储验证通过的用户名
    private String verifiedUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        
        userRepository = new UserRepositoryImpl(this);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        etUsername = findViewById(R.id.etUsername);
        etIdCard = findViewById(R.id.etIdCard);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        tilUsername = findViewById(R.id.tilUsername);
        tilIdCard = findViewById(R.id.tilIdCard);
        tilNewPassword = findViewById(R.id.tilNewPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        btnVerify = findViewById(R.id.btnVerify);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);
    }
    
    private void setupClickListeners() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyUserCredentials();
            }
        });
        
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
        
        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    private void verifyUserCredentials() {
        String username = etUsername.getText().toString().trim();
        String idCard = etIdCard.getText().toString().trim();
        
        // 验证输入
        if (!validateVerificationInputs(username, idCard)) {
            return;
        }
        
        // 显示进度条
        showProgress(true);
        
        // 检查用户名和身份证是否匹配
        userRepository.checkUserCredentials(username, idCard, new UserRepository.DataSourceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean isValid) {
                runOnUiThread(() -> {
                    showProgress(false);
                    if (isValid) {
                        // 验证成功，显示密码输入框
                        verifiedUsername = username;
                        showPasswordFields(true);
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "身份验证成功，请输入新密码", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "用户名和身份证不匹配，请检查后重试", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "验证失败：" + error, 
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private void resetPassword() {
        String newPassword = etNewPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        
        // 验证密码输入
        if (!validatePasswordInputs(newPassword, confirmPassword)) {
            return;
        }
        
        // 显示进度条
        showProgress(true);
        
        // 更新密码
        userRepository.updatePassword(verifiedUsername, newPassword, new UserRepository.DataSourceCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean success) {
                runOnUiThread(() -> {
                    showProgress(false);
                    if (success) {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "密码重置成功，请使用新密码登录", 
                            Toast.LENGTH_LONG).show();
                        // 延迟返回登录页面
                        findViewById(R.id.btnResetPassword).postDelayed(() -> finish(), 2000);
                    } else {
                        Toast.makeText(ForgotPasswordActivity.this, 
                            "密码重置失败，请稍后重试", 
                            Toast.LENGTH_SHORT).show();
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    showProgress(false);
                    Toast.makeText(ForgotPasswordActivity.this, 
                        "密码重置失败：" + error, 
                        Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
    
    private boolean validateVerificationInputs(String username, String idCard) {
        boolean isValid = true;
        
        // 验证用户名
        if (TextUtils.isEmpty(username)) {
            tilUsername.setError("请输入用户名");
            isValid = false;
        } else if (!ValidationUtils.isValidUsername(username)) {
            tilUsername.setError(ValidationUtils.getUsernameError(username));
            isValid = false;
        } else {
            tilUsername.setError(null);
        }
        
        // 验证身份证
        if (TextUtils.isEmpty(idCard)) {
            tilIdCard.setError("请输入身份证号");
            isValid = false;
        } else if (!ValidationUtils.isValidIdCard(idCard)) {
            tilIdCard.setError(ValidationUtils.getIdCardError(idCard));
            isValid = false;
        } else {
            tilIdCard.setError(null);
        }
        
        return isValid;
    }
    
    private boolean validatePasswordInputs(String newPassword, String confirmPassword) {
        boolean isValid = true;
        
        // 验证新密码
        if (TextUtils.isEmpty(newPassword)) {
            tilNewPassword.setError("请输入新密码");
            isValid = false;
        } else if (!ValidationUtils.isValidPassword(newPassword)) {
            tilNewPassword.setError(ValidationUtils.getPasswordError(newPassword));
            isValid = false;
        } else {
            tilNewPassword.setError(null);
        }
        
        // 验证确认密码
        if (TextUtils.isEmpty(confirmPassword)) {
            tilConfirmPassword.setError("请确认密码");
            isValid = false;
        } else if (!newPassword.equals(confirmPassword)) {
            tilConfirmPassword.setError("两次输入的密码不一致");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }
        
        return isValid;
    }
    
    private void showPasswordFields(boolean show) {
        if (show) {
            tilNewPassword.setVisibility(View.VISIBLE);
            tilConfirmPassword.setVisibility(View.VISIBLE);
            btnVerify.setVisibility(View.GONE);
            btnResetPassword.setVisibility(View.VISIBLE);
        } else {
            tilNewPassword.setVisibility(View.GONE);
            tilConfirmPassword.setVisibility(View.GONE);
            btnVerify.setVisibility(View.VISIBLE);
            btnResetPassword.setVisibility(View.GONE);
        }
    }
    
    private void showProgress(boolean show) {
        if (show) {
            findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.progress_bar).setVisibility(View.GONE);
        }
    }
}