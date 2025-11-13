package com.example.cms_android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.cms_android.R;
import com.example.cms_android.utils.SharedPreferencesManager;

public class MainActivity extends AppCompatActivity {
    private CardView btnHouseholdManagement;
    private CardView btnResidentManagement;
    private CardView btnEducationManagement;
    private CardView btnMedicalManagement;
    private Button btnLogout; // 添加退出登录按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化控件
        initializeViews();

        //设置点击监听器
        setupClickListeners();
    }

    private void initializeViews() {
        btnHouseholdManagement = findViewById(R.id.btn_household_management);
        btnResidentManagement = findViewById(R.id.btn_resident_management);
        btnEducationManagement = findViewById(R.id.btn_education_management);
        btnMedicalManagement = findViewById(R.id.btn_medical_management);
        btnLogout = findViewById(R.id.btn_logout); // 初始化退出按钮
    }

    private void setupClickListeners() {
        btnHouseholdManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HouseholdManagementActivity.class);
                startActivity(intent);
            }
        });

        btnResidentManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ResidentManagementActivity.class);
                startActivity(intent);
            }
        });

        btnEducationManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EducationManagementActivity.class);
                startActivity(intent);
            }
        });

        btnMedicalManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MedicalManagementActivity.class);
                startActivity(intent);
            }
        });

        // 添加退出登录功能
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 清除登录状态
                SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(MainActivity.this);
                sharedPreferencesManager.clearLoginInfo();
                
                // 跳转到登录页面
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}