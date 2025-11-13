package com.example.cms_android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

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

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class ResidentManagementActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CardView emptyState;
    private ResidentDao residentDao;
    private HouseholdDao householdDao;
    private ResidentAdapter adapter;
    private EditText searchInput;
    private Button btnSearch;
    private List<Resident> allResidents;
    private List<Resident> filteredResidents;
    
    // 定义请求码
    private static final int REQUEST_ADD_RESIDENT = 1001;
    private static final int REQUEST_EDIT_RESIDENT = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_management);

        residentDao = AppDatabase.getDatabase(this).residentDao();
        householdDao = AppDatabase.getDatabase(this).householdDao();

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
                editResident(resident);
            }

            @Override
            public void onDeleteClick(Resident resident) {
                deleteResident(resident);
            }
        });
    }

    private void setupSearchFunctionality() {
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
                if (resident.getName().toLowerCase().contains(lowerCaseQuery) ||
                    resident.getIdCard().toLowerCase().contains(lowerCaseQuery) ||
                    resident.getGender().toLowerCase().contains(lowerCaseQuery) ||
                    resident.getPhoneNumber().contains(query)) {
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
        
        // 为浮动按钮设置监听器
        com.google.android.material.floatingactionbutton.FloatingActionButton fabAdd = findViewById(R.id.fab_add);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> {
                Intent intent = new Intent(ResidentManagementActivity.this, ResidentFormActivity.class);
                startActivityForResult(intent, REQUEST_ADD_RESIDENT);
            });
        }

        Button btnAddFirst = findViewById(R.id.btn_add_first);
        btnAddFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResidentManagementActivity.this, ResidentFormActivity.class);
                startActivityForResult(intent, REQUEST_ADD_RESIDENT);
            }
        });
    }

    private void showResidentDetails(Resident resident) {
        // 在新线程中获取户籍信息
        new Thread(() -> {
            Household household = householdDao.getHouseholdById(resident.getHouseholdId());
            runOnUiThread(() -> {
                String householdInfo = (household != null) ? 
                    household.getHouseholdNumber() + " (" + household.getHouseholderName() + ")" : 
                    "未知户籍";
                
                StringBuilder details = new StringBuilder();
                details.append("姓名: ").append(resident.getName()).append("\n\n");
                details.append("身份证号: ").append(resident.getIdCard()).append("\n\n");
                details.append("性别: ").append(resident.getGender()).append("\n\n");
                details.append("出生日期: ").append(resident.getBirthDate()).append("\n\n");
                details.append("联系电话: ").append(resident.getPhoneNumber()).append("\n\n");
                details.append("所属户籍: ").append(householdInfo).append("\n\n");
                details.append("备注: ").append(resident.getNotes());

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("居民详细信息")
                        .setMessage(details.toString())
                        .setPositiveButton("确定", null)
                        .setNeutralButton("编辑", (dialog, which) -> editResident(resident))
                        .show();
            });
        }).start();
    }

    private void editResident(Resident resident) {
        Intent intent = new Intent(ResidentManagementActivity.this, ResidentFormActivity.class);
        intent.putExtra("resident_id", resident.getId());
        startActivityForResult(intent, REQUEST_EDIT_RESIDENT);
    }

    private void deleteResident(Resident resident) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除居民 " + resident.getName() + " 的信息吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                residentDao.delete(resident);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadResidents();
                                    }
                                });
                            }
                        }).start();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void loadResidents() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Resident> residents = residentDao.getAllResidents();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        allResidents = residents;
                        filteredResidents = new ArrayList<>(residents);
                        updateUI();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResidents();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_RESIDENT || requestCode == REQUEST_EDIT_RESIDENT) {
            // 无论添加还是编辑，都重新加载数据
            loadResidents();
        }
    }
}