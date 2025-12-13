package com.example.cms_android.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import java.util.ArrayList;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.HouseholdDao;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.User;
import com.example.cms_android.adapter.HouseholdAdapter;
import com.example.cms_android.utils.PermissionManager;
import com.example.cms_android.utils.SharedPreferencesManager;

import java.util.List;

public class HouseholdManagementActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private HouseholdAdapter adapter;
    private HouseholdDao householdDao;
    private CardView emptyState;
    private SharedPreferencesManager sharedPreferencesManager;
    private User currentUser; // 添加当前用户字段
    private EditText searchInput;
    private Button btnSearch;
    private Spinner searchFieldSpinner;
    private String selectedSearchField = "all"; // 默认搜索所有字段
    private List<Household> allHouseholds;
    private List<Household> filteredHouseholds;
    
    // 定义请求码
    private static final int REQUEST_ADD_HOUSEHOLD = 1001;
    private static final int REQUEST_EDIT_HOUSEHOLD = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_management);

        householdDao = AppDatabase.getDatabase(this).householdDao();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        // 获取当前用户
        currentUser = sharedPreferencesManager.getCurrentUser();

        initializeViews();
        setupClickListeners();
        loadHouseholds();
    }

    private void initializeViews()
    {
        recyclerView = findViewById(R.id.recycler_view);
        emptyState = findViewById(R.id.layout_empty);
        searchInput = findViewById(R.id.search_input);
        btnSearch = findViewById(R.id.btn_search);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HouseholdAdapter();
        // 设置适配器的当前用户
        adapter.setCurrentUser(currentUser);
        recyclerView.setAdapter(adapter);
        
        // 设置搜索功能
        setupSearchFunctionality();
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
        com.google.android.material.button.MaterialButton btnAddSearch = findViewById(R.id.btn_add_search);
        if (btnAddSearch != null)
        {
            btnAddSearch.setOnClickListener(v ->
            {
                if (PermissionManager.canAdd(currentUser))
                {
                    Intent intent = new Intent(HouseholdManagementActivity.this, HouseholdFormActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_HOUSEHOLD);
                }
                else
                {
                    Toast.makeText(HouseholdManagementActivity.this, "权限不足，无法添加户籍信息", Toast.LENGTH_SHORT).show();
                }
            });
        }

        Button btnAddFirst = findViewById(R.id.btn_add_first);
        btnAddFirst.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (PermissionManager.canAdd(currentUser))
                {
                    Intent intent = new Intent(HouseholdManagementActivity.this, HouseholdFormActivity.class);
                    startActivityForResult(intent, REQUEST_ADD_HOUSEHOLD);
                }
                else
                {
                    Toast.makeText(HouseholdManagementActivity.this, "权限不足，无法添加户籍信息", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 设置适配器的点击监听器
        adapter.setOnHouseholdClickListener(new HouseholdAdapter.OnHouseholdClickListener()
        {
            @Override
            public void onItemClick(Household household)
            {
                // 查看户籍详细信息
                showHouseholdDetails(household);
            }

            @Override
            public void onEditClick(Household household)
            {
                // 编辑户籍信息
                if (PermissionManager.canModifyHousehold(currentUser, household))
                {
                    editHousehold(household);
                }
                else
                {
                    Toast.makeText(HouseholdManagementActivity.this, "权限不足，无法编辑此户籍信息", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(Household household)
            {
                // 删除户籍信息
                if (PermissionManager.canRemoveHousehold(currentUser, household))
                {
                    deleteHousehold(household);
                }
                else
                {
                    Toast.makeText(HouseholdManagementActivity.this, "权限不足，无法删除此户籍信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showHouseholdDetails(Household household)
    {
        StringBuilder details = new StringBuilder();
        details.append("户籍编号: ").append(household.getHouseholdNumber()).append("\n\n");
        details.append("地址: ").append(household.getAddress()).append("\n\n");
        details.append("户主姓名: ").append(household.getHouseholderName()).append("\n\n");
        details.append("户主身份证号: ").append(household.getHouseholderIdCard()).append("\n\n");
        details.append("联系电话: ").append(household.getPhoneNumber()).append("\n\n");
        details.append("登记日期: ").append(household.getRegistrationDate()).append("\n\n");
        details.append("户籍类型: ").append(household.getHouseholdType()).append("\n\n");
        details.append("人口数量: ").append(household.getPopulationCount()).append("\n\n");
        details.append("备注: ").append(household.getNotes());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("户籍详细信息")
                .setMessage(details.toString())
                .setPositiveButton("确定", null)
                .setNeutralButton(PermissionManager.canModifyHousehold(currentUser, household) ? "编辑" : null, (dialog, which) -> {
                    if (PermissionManager.canModifyHousehold(currentUser, household))
                    {
                        editHousehold(household);
                    }
                    else
                    {
                        Toast.makeText(HouseholdManagementActivity.this, "权限不足，无法编辑此户籍信息", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void editHousehold(Household household)
    {
        Intent intent = new Intent(HouseholdManagementActivity.this, HouseholdFormActivity.class);
        intent.putExtra("HOUSEHOLD_ID", household.getId());
        startActivityForResult(intent, REQUEST_EDIT_HOUSEHOLD);
    }

    private void deleteHousehold(Household household)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除户籍编号为 " + household.getHouseholdNumber() + " 的户籍信息吗？")
                .setPositiveButton("删除", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        new AsyncTask<Household, Void, Void>()
                        {
                            @Override
                            protected Void doInBackground(Household... households)
                            {
                                householdDao.delete(households[0]);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                loadHouseholds();
                            }
                        }.execute(household);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void loadHouseholds()
    {
        new AsyncTask<Void, Void, List<Household>>()
        {
            @Override
            protected List<Household> doInBackground(Void... voids)
            {
                // 根据用户权限加载不同数据
                if (PermissionManager.isAdmin(currentUser))
                {
                    return householdDao.getAllHouseholds(); // 管理员查看所有数据
                }
                else
                {
                    return householdDao.getHouseholdsByOwner(currentUser.getId()); // 普通用户只能查看自己的数据
                }
            }

            @Override
            protected void onPostExecute(List<Household> households)
            {
                allHouseholds = households;
                // 初始时显示所有数据
                filterHouseholds("");
            }
        }.execute();
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
                filterHouseholds(searchInput.getText().toString());
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
                filterHouseholds(s.toString());
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
                filterHouseholds(searchInput.getText().toString());
                return true;
            }
            return false;
        });

        // 监听搜索按钮点击
        btnSearch.setOnClickListener(v -> filterHouseholds(searchInput.getText().toString()));
    }
    
    private void filterHouseholds(String query)
    {
        if (allHouseholds == null) return;

        filteredHouseholds = new ArrayList<>();
        
        if (query.isEmpty())
        {
            filteredHouseholds.addAll(allHouseholds);
        }
        else
        {
            String lowerCaseQuery = query.toLowerCase();
            for (Household household : allHouseholds)
            {
                boolean match = false;

                switch (selectedSearchField)
                {
                    case "户籍编号":
                        match = household.getHouseholdNumber().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "地址":
                        match = household.getAddress().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "户主姓名":
                        match = household.getHouseholderName().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "联系电话":
                        match = household.getPhoneNumber().contains(query);
                        break;
                    default:
                        // 默认搜索户籍编号
                        match = household.getHouseholdNumber().toLowerCase().contains(lowerCaseQuery);
                        break;
                }
                
                if (match)
                {
                    filteredHouseholds.add(household);
                }
            }
        }
        
        updateUI();
    }
    
    private void updateUI()
    {
        if (filteredHouseholds.isEmpty())
        {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else
        {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.setHouseholds(filteredHouseholds);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadHouseholds();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_HOUSEHOLD || requestCode == REQUEST_EDIT_HOUSEHOLD)
        {
            // 无论添加还是编辑，都重新加载数据
            loadHouseholds();
        }
    }
}