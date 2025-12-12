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
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.dao.HouseholdDao;
import com.example.cms_android.adapter.ResidentAdapter;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.PermissionManager;
import com.example.cms_android.utils.SharedPreferencesManager;
import com.example.cms_android.repository.ResidentRepository;
import com.example.cms_android.repository.ResidentRepositoryImpl;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ResidentManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardView emptyState;
    private ResidentDao residentDao;
    private HouseholdDao householdDao;
    private ResidentAdapter adapter;
    private EditText searchInput;
    private Button btnSearch;
    private Spinner searchFieldSpinner;
    private List<Resident> allResidents;
    private List<Resident> filteredResidents;
    private SharedPreferencesManager sharedPreferencesManager;
    private User currentUser; // 添加当前用户字段
    private String selectedSearchField = "all"; // 默认搜索所有字段
    private ResidentRepository residentRepository; // 添加Repository
    
    // 定义请求码
    private static final int REQUEST_ADD_RESIDENT = 1001;
    private static final int REQUEST_EDIT_RESIDENT = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_management);

        residentDao = AppDatabase.getDatabase(this).residentDao();
        householdDao = AppDatabase.getDatabase(this).householdDao();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        // 获取当前用户
        currentUser = sharedPreferencesManager.getCurrentUser();
        // 初始化Repository
        residentRepository = new ResidentRepositoryImpl(this);

        initializeViews();
        setupClickListeners();
        loadResidents();
    }

    private void initializeViews() {
        recyclerView = findViewById(R.id.recycler_view);
        emptyState = findViewById(R.id.layout_empty);
        searchInput = findViewById(R.id.search_input);
        btnSearch = findViewById(R.id.btn_search);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ResidentAdapter();
        // 设置适配器的当前用户
        adapter.setCurrentUser(currentUser);
        recyclerView.setAdapter(adapter);
        
        // 设置搜索功能
        setupSearchFunctionality();
        
        // 设置适配器的点击监听器
        adapter.setOnItemClickListener(new ResidentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Resident resident) {
                // 查看居民详细信息
                showResidentDetails(resident);
            }

            @Override
            public void onEditClick(Resident resident) {
                if (PermissionManager.canModifyResident(currentUser, resident)) {
                    editResident(resident);
                } else {
                    Toast.makeText(ResidentManagementActivity.this, "权限不足，无法编辑居民信息", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(Resident resident) {
                if (PermissionManager.canRemoveResident(currentUser, resident)) {
                    deleteResident(resident);
                } else {
                    Toast.makeText(ResidentManagementActivity.this, "权限不足，无法删除居民信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupSearchFunctionality() {
        // 初始化搜索字段选择
        searchFieldSpinner = findViewById(R.id.spinner_search_field);
        searchFieldSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSearchField = parent.getItemAtPosition(position).toString();
                // 当选择字段变化时，重新过滤
                filterResidents(searchInput.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedSearchField = "all";
            }
        });
        
        // 监听搜索输入框的文本变化
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterResidents(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 监听软键盘搜索按钮
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                filterResidents(searchInput.getText().toString());
                return true;
            }
            return false;
        });

        // 监听搜索按钮点击
        btnSearch.setOnClickListener(v -> filterResidents(searchInput.getText().toString()));
    }
    
    private void filterResidents(String query) {
        if (allResidents == null) return;

        filteredResidents = new ArrayList<>();
        
        if (query.isEmpty()) {
            filteredResidents.addAll(allResidents);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Resident resident : allResidents) {
                boolean match = false;
                
                switch (selectedSearchField) {
                    case "姓名":
                        match = resident.getName().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "身份证号":
                        match = resident.getIdCard().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "性别":
                        match = resident.getGender().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "电话号码":
                        match = resident.getPhoneNumber().contains(query);
                        break;
                    default:
                        // 默认搜索姓名
                        match = resident.getName().toLowerCase().contains(lowerCaseQuery);
                        break;
                }
                
                if (match) {
                    filteredResidents.add(resident);
                }
            }
        }
        
        updateUI();
    }

    private void updateUI() {
        if (filteredResidents.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setResidents(filteredResidents);
        }
    }

    private void setupClickListeners() {
        // 为工具栏的返回按钮设置监听器
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        
        // 为搜索栏添加按钮设置监听器
        com.google.android.material.button.MaterialButton btnAddSearch = findViewById(R.id.btn_add_search);
        if (btnAddSearch != null) {
            btnAddSearch.setOnClickListener(v -> {
                if (PermissionManager.canAdd(currentUser)) {
                    Intent intent = new Intent(ResidentManagementActivity.this, ResidentFormActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_RESIDENT);
                } else {
                    Toast.makeText(ResidentManagementActivity.this, "权限不足，只有管理员可以添加居民信息", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnAddFirst = findViewById(R.id.btn_add_first);
        btnAddFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PermissionManager.canAdd(currentUser)) {
                    Intent intent = new Intent(ResidentManagementActivity.this, ResidentFormActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_RESIDENT);
                } else {
                    Toast.makeText(ResidentManagementActivity.this, "权限不足，只有管理员可以添加居民信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadResidents() {
        // 使用Repository加载居民数据
        residentRepository.getResidentsByCurrentUser(currentUser, new ResidentRepository.DataSourceCallback() {
            @Override
            public void onSuccess(Object result) {
                allResidents = (List<Resident>) result;
                filteredResidents = new ArrayList<>(allResidents);
                runOnUiThread(() -> updateUI());
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    Toast.makeText(ResidentManagementActivity.this, "加载居民数据失败: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showResidentDetails(Resident resident) {
        new Thread(() -> {
            try {
                Household household = householdDao.getHouseholdById(resident.getHouseholdId());
                StringBuilder details = new StringBuilder();
                details.append("姓名: ").append(resident.getName()).append("\n");
                details.append("身份证号: ").append(resident.getIdCard()).append("\n");
                details.append("性别: ").append(resident.getGender()).append("\n");
                details.append("出生日期: ").append(resident.getBirthDate()).append("\n");
                details.append("联系电话: ").append(resident.getPhoneNumber()).append("\n");
                details.append("所属户籍: ").append(household != null ? household.getAddress() : "未知").append("\n");
                details.append("备注: ").append(resident.getNotes());

                runOnUiThread(() -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("居民详细信息")
                            .setMessage(details.toString())
                            .setPositiveButton("确定", null)
                            .setNeutralButton(PermissionManager.canModifyResident(currentUser, resident) ? "编辑" : null, (dialog, which) -> {
                                if (PermissionManager.canModifyResident(currentUser, resident)) {
                                    editResident(resident);
                                } else {
                                    Toast.makeText(ResidentManagementActivity.this, "权限不足，无法编辑居民信息", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .show();
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "获取居民详情失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void editResident(Resident resident) {
        Intent intent = new Intent(ResidentManagementActivity.this, ResidentFormActivity.class);
        intent.putExtra("resident_id", resident.getId());
        startActivityForResult(intent, REQUEST_EDIT_RESIDENT);
    }

    private void deleteResident(Resident resident) {
        new AlertDialog.Builder(this)
                .setTitle("确认删除")
                .setMessage("确定要删除居民 \"" + resident.getName() + "\" 吗？此操作不可撤销。")
                .setPositiveButton("删除", (dialog, which) -> {
                    new Thread(() -> {
                        try {
                            residentDao.delete(resident);
                            runOnUiThread(() -> {
                                Toast.makeText(this, "居民信息删除成功", Toast.LENGTH_SHORT).show();
                                loadResidents(); // 重新加载数据
                            });
                        } catch (Exception e) {
                            runOnUiThread(() -> {
                                Toast.makeText(this, "删除居民信息失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadResidents(); // 重新加载数据
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 关闭Repository资源
        if (residentRepository instanceof ResidentRepositoryImpl) {
            ((ResidentRepositoryImpl) residentRepository).shutdown();
        }
    }
}