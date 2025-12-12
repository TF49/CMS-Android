package com.example.cms_android.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cms_android.R;
import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.HouseholdDao;
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.model.Resident;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ResidentFormActivity extends AppCompatActivity {
    private Button btnSave;
    private Spinner spinnerHousehold;
    private EditText etName;
    private EditText etIdCard;
    private Spinner spinnerGender; // 修改为Spinner
    private EditText etBirthDate;
    private EditText etPhoneNumber;
    private EditText etNotes;

    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private Resident resident;
    private boolean isEditMode = false;
    private ResidentDao residentDao;
    private HouseholdDao householdDao;
    private long residentId = -1;
    private List<Household> householdList; // 添加户籍列表
    private User currentUser; // 添加当前用户字段

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_form);

        residentDao = AppDatabase.getDatabase(this).residentDao();
        householdDao = AppDatabase.getDatabase(this).householdDao();
        
        // 获取当前用户
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        currentUser = sharedPreferencesManager.getCurrentUser();

        initViews();
        setupDatePicker();
        setupGenderSpinner(); // 添加性别Spinner设置
        setupHouseholdSpinner(); // 这将在新线程中加载数据
        setupClickListeners();
        loadResidentData();
    }

    private void initViews() {
        btnSave = findViewById(R.id.btn_save);
        spinnerHousehold = findViewById(R.id.spinner_household);
        etName = findViewById(R.id.et_name);
        etIdCard = findViewById(R.id.et_id_card);
        spinnerGender = findViewById(R.id.spinner_gender); // 修改为spinner_gender
        etBirthDate = findViewById(R.id.et_birth_date);
        etPhoneNumber = findViewById(R.id.et_phone); // 修改为et_phone
        etNotes = findViewById(R.id.et_notes);

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    // 添加设置性别Spinner的方法
    private void setupGenderSpinner() {
        List<String> genders = new ArrayList<>();
        genders.add("男");
        genders.add("女");
        
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genders
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGender.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etBirthDate.setOnClickListener(v -> showDatePicker());
    }

    private void showDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        etBirthDate.setText(dateFormatter.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupHouseholdSpinner() {
        // 从数据库获取真实的户籍数据
        new Thread(() -> {
            // 根据用户权限获取户籍数据
            householdList = householdDao.getHouseholdsByOwner(currentUser.getId()); // 只获取当前用户的户籍
            runOnUiThread(() -> {
                List<String> householdNumbers = new ArrayList<>();
                for (Household household : householdList) {
                    householdNumbers.add(household.getHouseholdNumber() + " - " + household.getHouseholderName() + "家庭");
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        householdNumbers
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerHousehold.setAdapter(adapter);
                
                // 如果是编辑模式，设置选中项
                if (isEditMode && resident != null) {
                    for (int i = 0; i < householdList.size(); i++) {
                        if (householdList.get(i).getId() == resident.getHouseholdId()) {
                            spinnerHousehold.setSelection(i);
                            break;
                        }
                    }
                }
            });
        }).start();
    }

    private void setupClickListeners() {
        // 添加取消按钮的点击监听
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveResident());
        
        // 为工具栏的返回按钮设置监听器
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void loadResidentData() {
        // 检查是否传递了居民ID用于编辑
        if (getIntent().hasExtra("resident_id")) {
            isEditMode = true;
            residentId = getIntent().getLongExtra("resident_id", -1);
            // tvTitle.setText("编辑居民信息");
            
            // 加载居民数据
            loadResidentFromDatabase();
        }
    }

    private void loadResidentFromDatabase() {
        new Thread(() -> {
            resident = residentDao.getResidentById(residentId);
            if (resident != null) {
                runOnUiThread(this::populateFormData);
            }
        }).start();
    }

    private void populateFormData() {
        if (resident != null) {
            // 设置户籍选择（这里简化处理，实际应该根据householdId查找户籍）
            // spinnerHousehold.setSelection(...);
            
            etName.setText(resident.getName());
            etIdCard.setText(resident.getIdCard());
            // 设置性别选择
            if ("男".equals(resident.getGender())) {
                spinnerGender.setSelection(0);
            } else if ("女".equals(resident.getGender())) {
                spinnerGender.setSelection(1);
            }
            etBirthDate.setText(resident.getBirthDate());
            etPhoneNumber.setText(resident.getPhoneNumber());
            etNotes.setText(resident.getNotes());
        }
    }

    private void saveResident() {
        if (validateForm()) {
            Resident record = new Resident();
            if (isEditMode && resident != null) {
                record.setId(resident.getId());
            }
            
            // 设置所有者ID
            if (currentUser != null) {
                record.setOwnerId(currentUser.getId());
            }
            
            // 获取选中的户籍ID
            if (householdList != null && !householdList.isEmpty()) {
                int selectedPosition = spinnerHousehold.getSelectedItemPosition();
                if (selectedPosition >= 0 && selectedPosition < householdList.size()) {
                    long householdId = householdList.get(selectedPosition).getId();
                    record.setHouseholdId(householdId);
                } else {
                    record.setHouseholdId(1); // 默认值
                }
            } else {
                record.setHouseholdId(1); // 默认值
            }
            
            record.setName(etName.getText().toString());
            record.setIdCard(etIdCard.getText().toString());
            record.setGender(spinnerGender.getSelectedItem().toString());
            record.setBirthDate(etBirthDate.getText().toString());
            record.setPhoneNumber(etPhoneNumber.getText().toString());
            record.setNotes(etNotes.getText().toString());
            
            // 设置其他默认值
            record.setRelationship("户主"); // 默认值
            record.setEthnicGroup("汉族"); // 默认值
            record.setEducationLevel(""); // 默认值
            record.setOccupation(""); // 默认值
            record.setMaritalStatus("未婚"); // 默认值
            record.setHealthStatus("健康"); // 默认值
            record.setBloodType(""); // 默认值
            record.setHouseholder(false); // 默认值

            new Thread(() -> {
                if (isEditMode && resident != null) {
                    residentDao.update(record);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "居民信息更新成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                } else {
                    residentDao.insert(record);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "居民信息创建成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }
            }).start();
        }
    }

    private boolean validateForm() {
        if (etName.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入姓名", Toast.LENGTH_SHORT).show();
            etName.requestFocus();
            return false;
        }

        if (etIdCard.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入身份证号", Toast.LENGTH_SHORT).show();
            etIdCard.requestFocus();
            return false;
        }

        if (etBirthDate.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请选择出生日期", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}