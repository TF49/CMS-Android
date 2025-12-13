package com.example.cms_android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.EducationDao;
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.adapter.EducationAdapter;
import com.example.cms_android.model.Education;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.PermissionManager;
import com.example.cms_android.utils.SharedPreferencesManager;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EducationManagementActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private CardView emptyState;
    private EducationDao educationDao;
    private ResidentDao residentDao;
    private EducationAdapter adapter;
    private EditText searchInput;
    private Button btnSearch;
    private Spinner searchFieldSpinner;
    private List<Education> allEducations;
    private List<Education> filteredEducations;
    private SharedPreferencesManager sharedPreferencesManager;
    private User currentUser; // 添加当前用户字段
    private String selectedSearchField = "all"; // 默认搜索所有字段
    
    // 定义请求码
    private static final int REQUEST_ADD_EDUCATION = 1001;
    private static final int REQUEST_EDIT_EDUCATION = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_management);

        educationDao = AppDatabase.getDatabase(this).educationDao();
        residentDao = AppDatabase.getDatabase(this).residentDao();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        // 获取当前用户
        currentUser = sharedPreferencesManager.getCurrentUser();
        
        //初始化控件
        initializeViews();

        //设置点击监听器
        setupClickListeners();

        //加载数据
        loadEducationRecords();
    }

    private void initializeViews()
    {
        recyclerView = findViewById(R.id.recycler_view);
        emptyState = findViewById(R.id.layout_empty);
        searchInput = findViewById(R.id.search_input);
        btnSearch = findViewById(R.id.btn_search);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EducationAdapter();
        // 设置适配器的当前用户
        adapter.setCurrentUser(currentUser);
        recyclerView.setAdapter(adapter);
        
        // 设置搜索功能
        setupSearchFunctionality();
        
        // 设置适配器的点击监听器
        adapter.setOnEducationClickListener(new EducationAdapter.OnEducationClickListener()
        {
            @Override
            public void onItemClick(Education education)
            {
                // 查看教育记录详情
                showEducationDetails(education);
            }

            @Override
            public void onEditClick(Education education)
            {
                if (PermissionManager.canModifyEducation(currentUser, education))
                {
                    editEducation(education);
                }
                else
                {
                    Toast.makeText(EducationManagementActivity.this, "权限不足，无法编辑此教育记录", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(Education education)
            {
                if (PermissionManager.canRemoveEducation(currentUser, education))
                {
                    deleteEducation(education);
                }
                else
                {
                    Toast.makeText(EducationManagementActivity.this, "权限不足，无法删除此教育记录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSearchFunctionality()
    {
        // 初始化搜索字段选择
        searchFieldSpinner = findViewById(R.id.spinner_search_field);
        searchFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                selectedSearchField = parent.getItemAtPosition(position).toString();
                // 当选择字段变化时，重新过滤
                filterEducations(searchInput.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                selectedSearchField = "all";
            }
        });
        
        // 监听搜索输入框的文本变化
        searchInput.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                filterEducations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s)
            {

            }
        });

        // 监听软键盘搜索按钮
        searchInput.setOnEditorActionListener((v, actionId, event) ->
        {
            if (actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                filterEducations(searchInput.getText().toString());
                return true;
            }
            return false;
        });

        // 监听搜索按钮点击
        btnSearch.setOnClickListener(v -> filterEducations(searchInput.getText().toString()));
    }
    
    private void filterEducations(String query)
    {
        if (allEducations == null) return;

        filteredEducations = new ArrayList<>();
        
        if (query.isEmpty())
        {
            filteredEducations.addAll(allEducations);
        }
        else
        {
            String lowerCaseQuery = query.toLowerCase();
            for (Education education : allEducations)
            {
                boolean match = false;
                
                switch (selectedSearchField)
                {
                    case "居民姓名":
                        // 获取居民姓名并匹配
                        Resident resident = residentDao.getResidentById(education.getResidentId());
                        if (resident != null && resident.getName().toLowerCase().contains(lowerCaseQuery))
                        {
                            match = true;
                        }
                        break;
                    case "学校名称":
                        match = education.getSchoolName().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "教育程度":
                        match = education.getEducationLevel().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "专业":
                        match = education.getMajor().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "状态":
                        match = education.getStatus().toLowerCase().contains(lowerCaseQuery);
                        break;
                    default:
                        // 默认搜索居民姓名
                        Resident defaultResident = residentDao.getResidentById(education.getResidentId());
                        if (defaultResident != null && defaultResident.getName().toLowerCase().contains(lowerCaseQuery))
                        {
                            match = true;
                        }
                        break;
                }
                
                if (match)
                {
                    filteredEducations.add(education);
                }
            }
        }
        
        updateUI();
    }

    private void updateUI()
    {
        // 获取所有相关的居民信息
        Map<Long, String> residentNames = new HashMap<>();
        for (Education education : filteredEducations)
        {
            long residentId = education.getResidentId();
            if (!residentNames.containsKey(residentId))
            {
                Resident resident = residentDao.getResidentById(residentId);
                if (resident != null)
                {
                    residentNames.put(residentId, resident.getName());
                }
            }
        }
        
        if (filteredEducations.isEmpty())
        {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else
        {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setEducations(filteredEducations);
            adapter.setResidentNames(residentNames); // 更新居民姓名映射
        }
    }

    private void setupClickListeners()
    {
        // 为工具栏的返回按钮设置监听器
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null)
        {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        
        // 为搜索栏添加按钮设置监听器
        MaterialButton btnAddSearch = findViewById(R.id.btn_add_search);
        if (btnAddSearch != null)
        {
            btnAddSearch.setOnClickListener(v ->
            {
                if (PermissionManager.canAdd(currentUser))
                {
                    Intent intent = new Intent(EducationManagementActivity.this, EducationFormActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_EDUCATION);
                }
                else
                {
                    Toast.makeText(this, "权限不足，无法添加教育记录", Toast.LENGTH_SHORT).show();
                }
            });
        }
        

        // 添加空状态下的"添加教育记录"按钮点击事件
        Button btnAddFirst = findViewById(R.id.btn_add_first);
        btnAddFirst.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (PermissionManager.canAdd(currentUser))
                {
                    Intent intent = new Intent(EducationManagementActivity.this, EducationFormActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_EDUCATION);
                }
                else
                {
                    Toast.makeText(EducationManagementActivity.this, "权限不足，无法添加教育记录", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showEducationDetails(Education education)
    {
        // 在新线程中获取居民信息
        new Thread(() ->
        {
            Resident resident = residentDao.getResidentById(education.getResidentId());
            runOnUiThread(() ->
            {
                String residentName = (resident != null) ? resident.getName() : "未知居民";
                
                StringBuilder details = new StringBuilder();
                details.append("居民姓名: ").append(residentName).append("\n\n");
                details.append("学校名称: ").append(education.getSchoolName()).append("\n\n");
                details.append("教育程度: ").append(education.getEducationLevel()).append("\n\n");
                details.append("专业: ").append(education.getMajor()).append("\n\n");
                details.append("入学日期: ").append(education.getEnrollmentDate()).append("\n\n");
                details.append("毕业日期: ").append(education.getGraduationDate()).append("\n\n");
                details.append("状态: ").append(education.getStatus()).append("\n\n");
                details.append("备注: ").append(education.getNotes());

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("教育记录详情")
                        .setMessage(details.toString())
                        .setPositiveButton("确定", null)
                        .setNeutralButton(PermissionManager.canModifyEducation(currentUser, education) ? "编辑" : null, (dialog, which) -> {
                            if (PermissionManager.canModifyEducation(currentUser, education)) {
                                editEducation(education);
                            }
                            else
                            {
                                Toast.makeText(EducationManagementActivity.this, "权限不足，无法编辑此教育记录", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            });
        }).start();
    }

    private void editEducation(Education education)
    {
        Intent intent = new Intent(EducationManagementActivity.this, EducationFormActivity.class);
        intent.putExtra("education_id", education.getId());
        startActivityForResult(intent, REQUEST_EDIT_EDUCATION);
    }

    private void deleteEducation(Education education)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除 " + education.getSchoolName() + " 的教育记录吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                educationDao.delete(education);
                                runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        loadEducationRecords();
                                    }
                                });
                            }
                        }).start();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void loadEducationRecords()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final List<Education> educationRecords;
                // 根据用户角色选择不同的查询方法
                if (PermissionManager.isAdmin(currentUser))
                {
                    // 管理员可以查看所有数据
                    educationRecords = educationDao.getAllEducationRecords();
                }
                else
                {
                    // 普通用户只能查看自己的数据
                    educationRecords = educationDao.getAllEducationRecordsByOwner(currentUser.getId());
                }
                
                // 获取所有相关的居民信息
                Map<Long, String> residentNames = new HashMap<>();
                for (Education education : educationRecords)
                {
                    long residentId = education.getResidentId();
                    if (!residentNames.containsKey(residentId))
                    {
                        Resident resident = residentDao.getResidentById(residentId);
                        if (resident != null)
                        {
                            residentNames.put(residentId, resident.getName());
                        }
                    }
                }
                
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        allEducations = educationRecords;
                        filteredEducations = new ArrayList<>(educationRecords);
                        adapter.setEducations(filteredEducations);
                        adapter.setResidentNames(residentNames); // 更新居民姓名映射
                        
                        updateUI();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadEducationRecords();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_EDUCATION || requestCode == REQUEST_EDIT_EDUCATION)
        {
            // 无论添加还是编辑，都重新加载数据
            loadEducationRecords();
        }
    }
}