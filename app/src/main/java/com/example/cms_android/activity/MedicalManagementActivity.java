package com.example.cms_android.activity;

import android.app.AlertDialog;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cms_android.R;
import com.example.cms_android.adapter.MedicalAdapter;
import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.model.Medical;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.User;
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.utils.PermissionManager;
import com.example.cms_android.utils.SharedPreferencesManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MedicalManagementActivity extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private MedicalAdapter adapter;
    private List<Medical> medicalRecords;
    private List<Medical> allMedicalRecords;
    private CardView layoutEmpty;
    private Button btnAddFirst;
    private AppDatabase db;
    private com.example.cms_android.dao.MedicalDao medicalDao;
    private ResidentDao residentDao;
    private EditText searchInput;
    private Button btnSearch;
    private Spinner searchFieldSpinner;
    private String selectedSearchField = "all"; // 默认搜索所有字段
    
    // 添加用户权限相关变量
    private User currentUser;
    private SharedPreferencesManager sharedPreferencesManager;
    
    // 定义请求码
    private static final int REQUEST_ADD_MEDICAL = 1001;
    private static final int REQUEST_EDIT_MEDICAL = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_management);

        // 初始化SharedPreferencesManager并获取当前用户
        sharedPreferencesManager = new SharedPreferencesManager(this);
        currentUser = sharedPreferencesManager.getCurrentUser();

        db = AppDatabase.getDatabase(this);
        medicalDao = db.medicalDao();
        residentDao = db.residentDao();

        initViews();
        setupRecyclerView();
        setupClickListeners();
        setupSearchFunctionality();
        loadMedicalRecords();
        // 根据用户权限更新UI
        updateUIBasedOnPermissions();
    }

    private void initViews()
    {
        recyclerView = findViewById(R.id.recycler_view);
        layoutEmpty = findViewById(R.id.layout_empty);
        btnAddFirst = findViewById(R.id.btn_add_first);
        searchInput = findViewById(R.id.search_input);
        btnSearch = findViewById(R.id.btn_search);
    }

    private void setupRecyclerView()
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        medicalRecords = new ArrayList<>();
        allMedicalRecords = new ArrayList<>();
        adapter = new MedicalAdapter();
        // 设置适配器的当前用户
        adapter.setCurrentUser(currentUser);
        adapter.setOnMedicalClickListener(new MedicalAdapter.OnMedicalClickListener()
        {
            @Override
            public void onItemClick(Medical record)
            {
                // 查看医疗记录详情
                showMedicalRecordDetail(record);
            }

            @Override
            public void onEditClick(Medical record)
            {
                // 检查编辑权限
                if (PermissionManager.canModifyMedical(currentUser, record))
                {
                    editMedicalRecord(record);
                }
                else
                {
                    // 显示权限不足提示
                    showPermissionDeniedMessage();
                }
            }

            @Override
            public void onDeleteClick(Medical record)
            {
                // 检查删除权限
                if (PermissionManager.canRemoveMedical(currentUser, record))
                {
                    deleteMedicalRecord(record);
                }
                else
                {
                    // 显示权限不足提示
                    showPermissionDeniedMessage();
                }
            }
        });
        recyclerView.setAdapter(adapter);
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
                filterMedicalRecords(searchInput.getText().toString());
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
                filterMedicalRecords(s.toString());
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
                filterMedicalRecords(searchInput.getText().toString());
                return true;
            }
            return false;
        });

        // 监听搜索按钮点击
        btnSearch.setOnClickListener(v -> filterMedicalRecords(searchInput.getText().toString()));
    }
    
    private void filterMedicalRecords(String query)
    {
        if (allMedicalRecords == null) return;

        medicalRecords = new ArrayList<>();
        
        if (query.isEmpty())
        {
            medicalRecords.addAll(allMedicalRecords);
        }
        else
        {
            String lowerCaseQuery = query.toLowerCase();
            for (Medical medical : allMedicalRecords)
            {
                boolean match = false;
                
                switch (selectedSearchField)
                {
                    case "居民姓名":
                        // 获取居民姓名并匹配
                        Resident resident = residentDao.getResidentById(medical.getResidentId());
                        if (resident != null && resident.getName().toLowerCase().contains(lowerCaseQuery))
                        {
                            match = true;
                        }
                        break;
                    case "医院":
                        match = medical.getHospital().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "科室":
                        match = medical.getDepartment().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "诊断结果":
                        match = medical.getDiagnosis().toLowerCase().contains(lowerCaseQuery);
                        break;
                    case "医生":
                        match = medical.getDoctor().toLowerCase().contains(lowerCaseQuery);
                        break;
                    default:
                        // 默认搜索居民姓名
                        Resident defaultResident = residentDao.getResidentById(medical.getResidentId());
                        if (defaultResident != null && defaultResident.getName().toLowerCase().contains(lowerCaseQuery))
                        {
                            match = true;
                        }
                        break;
                }
                
                if (match)
                {
                    medicalRecords.add(medical);
                }
            }
        }
        
        updateUI();
    }

    private void updateUI()
    {
        // 获取所有相关的居民信息
        Map<Long, String> residentNames = new HashMap<>();
        for (Medical medical : medicalRecords)
        {
            long residentId = medical.getResidentId();
            if (!residentNames.containsKey(residentId))
            {
                Resident resident = residentDao.getResidentById(residentId);
                if (resident != null)
                {
                    residentNames.put(residentId, resident.getName());
                }
            }
        }
        
        adapter.updateData(medicalRecords);
        adapter.updateResidentNames(residentNames); // 更新居民姓名映射
        
        // 根据数据量显示/隐藏空状态
        updateEmptyState();
    }

    // 根据用户权限更新UI
    private void updateUIBasedOnPermissions()
    {
        // 如果是普通用户，隐藏添加按钮
        if (PermissionManager.isUser(currentUser))
        {
            // 隐藏第一个添加按钮
            btnAddFirst.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners()
    {
        
        btnAddFirst.setOnClickListener(v ->
        {
            // 检查添加权限
            if (PermissionManager.canEdit(currentUser))
            {
                addMedicalRecord();
            }
            else
            {
                showPermissionDeniedMessage();
            }
        });
        
        // 为搜索栏添加按钮设置监听器
        com.google.android.material.button.MaterialButton btnAddSearch = findViewById(R.id.btn_add_search);
        if (btnAddSearch != null)
        {
            btnAddSearch.setOnClickListener(v ->
            {
                // 检查添加权限
                if (PermissionManager.canEdit(currentUser))
                {
                    addMedicalRecord();
                }
                else
                {
                    showPermissionDeniedMessage();
                }
            });
        }
        
        // 为工具栏的返回按钮设置监听器
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null)
        {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
        

    }

    private void loadMedicalRecords()
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                final List<Medical> medicalRecordsFromDB;
                // 根据用户角色选择不同的查询方法
                if (PermissionManager.isAdmin(currentUser))
                {
                    // 管理员可以查看所有数据
                    medicalRecordsFromDB = medicalDao.getAllMedicalRecords();
                }
                else
                {
                    // 普通用户只能查看自己的数据
                    medicalRecordsFromDB = medicalDao.getAllMedicalRecordsByOwner(currentUser.getId());
                }
                
                // 获取所有相关的居民信息
                Map<Long, String> residentNames = new HashMap<>();
                for (Medical medical : medicalRecordsFromDB)
                {
                    long residentId = medical.getResidentId();
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
                        allMedicalRecords = medicalRecordsFromDB;
                        medicalRecords = new ArrayList<>(medicalRecordsFromDB);
                        adapter.updateData(medicalRecords);
                        adapter.updateResidentNames(residentNames); // 更新居民姓名映射
                        
                        // 根据数据量显示/隐藏空状态
                        updateEmptyState();
                    }
                });
            }
        }).start();
    }

    private void updateEmptyState()
    {
        if (medicalRecords.isEmpty())
        {
            layoutEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
        else
        {
            layoutEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void addMedicalRecord()
    {
        Intent intent = new Intent(this, MedicalFormActivity.class);
        startActivityForResult(intent, REQUEST_ADD_MEDICAL);
    }

    private void editMedicalRecord(Medical record)
    {
        Intent intent = new Intent(this, MedicalFormActivity.class);
        intent.putExtra("medical_id", record.getId());
        startActivityForResult(intent, REQUEST_EDIT_MEDICAL);
    }

    private void showMedicalRecordDetail(Medical record)
    {
        // 在新线程中获取居民信息
        new Thread(() ->
        {
            Resident resident = residentDao.getResidentById(record.getResidentId());
            runOnUiThread(() ->
            {
                String residentName = (resident != null) ? resident.getName() : "未知居民";
                
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("医疗记录详情")
                        .setMessage(
                                "居民姓名：" + residentName + "\n\n" +
                                "居民ID：" + record.getResidentId() + "\n\n" +
                                "血型：" + record.getBloodType() + "\n\n" +
                                "过敏史：" + record.getAllergies() + "\n\n" +
                                "慢性疾病：" + record.getChronicDiseases() + "\n\n" +
                                "手术史：" + record.getSurgeries() + "\n\n" +
                                "用药情况：" + record.getMedications() + "\n\n" +
                                "保险类型：" + record.getInsuranceType() + "\n\n" +
                                "保险号：" + record.getInsuranceNumber() + "\n\n" +
                                "最后体检日期：" + record.getLastCheckupDate() + "\n\n" +
                                "医院：" + record.getHospital() + "\n\n" +
                                "科室：" + record.getDepartment() + "\n\n" +
                                "诊断：" + record.getDiagnosis() + "\n\n" +
                                "治疗方案：" + record.getTreatment() + "\n\n" +
                                "主治医生：" + record.getDoctor() + "\n\n" +
                                "总费用：" + record.getCost() + "元\n\n" +
                                "医保报销：" + record.getInsurance() + "元\n\n" +
                                "备注：" + record.getNotes()
                        )
                        .setPositiveButton("确定", null)
                        // 只有具有编辑权限的用户才能看到编辑按钮
                        .setNeutralButton(PermissionManager.canModifyMedical(currentUser, record) ? "编辑" : null, (dialog, which) -> editMedicalRecord(record))
                        .show();
            });
        }).start();
    }

    private void deleteMedicalRecord(Medical record)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("确认删除")
                .setMessage("确定要删除居民ID为" + record.getResidentId() + "的医疗记录吗？")
                .setPositiveButton("删除", (dialog, which) -> {
                    new Thread(() ->
                    {
                        medicalDao.delete(record);
                        runOnUiThread(() ->
                        {
                            allMedicalRecords.remove(record);
                            medicalRecords.remove(record);
                            updateUI();
                        });
                    }).start();
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 显示权限不足提示
    private void showPermissionDeniedMessage()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("权限不足")
                .setMessage("您没有执行此操作的权限。只有管理员可以执行此操作。")
                .setPositiveButton("确定", null)
                .show();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        loadMedicalRecords();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_MEDICAL || requestCode == REQUEST_EDIT_MEDICAL)
        {
            // 无论添加还是编辑，都重新加载数据
            loadMedicalRecords();
        }
    }
}