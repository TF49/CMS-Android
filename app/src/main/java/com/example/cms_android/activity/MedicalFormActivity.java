package com.example.cms_android.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cms_android.R;
import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.MedicalDao;
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.model.Medical;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MedicalFormActivity extends AppCompatActivity {

    private Button btnSave;
    private Spinner spinnerResident;
    private EditText etBloodType;
    private EditText etAllergies;
    private EditText etChronicDiseases;
    private EditText etSurgeries;
    private EditText etHospital;
    private EditText etDepartment;
    private EditText etDiagnosis;
    private EditText etTreatment;
    private EditText etDoctor;
    private EditText etCost;
    private EditText etInsurance;
    private EditText etMedications;
    private EditText etInsuranceType;
    private EditText etInsuranceNumber;
    private EditText etLastCheckupDate;
    private EditText etNotes;

    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private Medical medical;
    private boolean isEditMode = false;
    private MedicalDao medicalDao;
    private ResidentDao residentDao;
    private long medicalId = -1;
    private List<com.example.cms_android.model.Resident> residentList; // 添加居民列表
    private SharedPreferencesManager sharedPreferencesManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medical_form);

        medicalDao = AppDatabase.getDatabase(this).medicalDao();
        residentDao = AppDatabase.getDatabase(this).residentDao();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        currentUser = sharedPreferencesManager.getCurrentUser();

        initViews();
        setupDatePicker();
        setupResidentSpinner();
        setupClickListeners();
        loadMedicalData();
        setupToolbar();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void initViews() {
        btnSave = findViewById(R.id.btn_save);
        spinnerResident = findViewById(R.id.spinner_resident);
        etBloodType = findViewById(R.id.et_blood_type);
        etAllergies = findViewById(R.id.et_allergies);
        etChronicDiseases = findViewById(R.id.et_chronic_diseases);
        etSurgeries = findViewById(R.id.et_surgeries);
        etHospital = findViewById(R.id.et_hospital);
        etDepartment = findViewById(R.id.et_department);
        etDiagnosis = findViewById(R.id.et_diagnosis);
        etTreatment = findViewById(R.id.et_treatment);
        etDoctor = findViewById(R.id.et_doctor);
        etCost = findViewById(R.id.et_cost);
        etInsurance = findViewById(R.id.et_insurance);
        etMedications = findViewById(R.id.et_medications);
        etInsuranceType = findViewById(R.id.et_insurance_type);
        etInsuranceNumber = findViewById(R.id.et_insurance_number);
        etLastCheckupDate = findViewById(R.id.et_last_checkup_date);
        etNotes = findViewById(R.id.et_notes);

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    private void setupDatePicker() {
        if (etLastCheckupDate != null) {
            etLastCheckupDate.setOnClickListener(v -> showDatePicker());
        }
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
                        if (etLastCheckupDate != null) {
                            etLastCheckupDate.setText(dateFormatter.format(calendar.getTime()));
                        }
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupResidentSpinner() {
        new Thread(() -> {
            residentList = residentDao.getResidentsByOwner(currentUser.getId()); // 只获取当前用户的居民
            runOnUiThread(() -> {
                List<String> residentNames = new ArrayList<>();
                for (com.example.cms_android.model.Resident resident : residentList) {
                    residentNames.add(resident.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_item,
                        residentNames
                );
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerResident.setAdapter(adapter);
                
                // 如果是编辑模式，设置选中项
                if (isEditMode && medical != null) {
                    for (int i = 0; i < residentList.size(); i++) {
                        if (residentList.get(i).getId() == medical.getResidentId()) {
                            spinnerResident.setSelection(i);
                            break;
                        }
                    }
                }
            });
        }).start();
    }

    private void setupClickListeners()
    {   // 添加取消按钮的点击监听
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> saveMedicalRecord());

        // 为工具栏的返回按钮设置监听器
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void loadMedicalData() {
        // 检查是否传递了医疗记录ID用于编辑
        if (getIntent().hasExtra("medical_id")) {
            isEditMode = true;
            medicalId = getIntent().getLongExtra("medical_id", -1);
            
            // 加载医疗记录数据
            loadMedicalRecordFromDatabase();
        }
    }

    private void loadMedicalRecordFromDatabase() {
        new Thread(() -> {
            medical = medicalDao.getMedicalById(medicalId);
            if (medical != null) {
                runOnUiThread(this::populateFormData);
            }
        }).start();
    }

    private void populateFormData() {
        if (medical != null) {
            // 设置居民选择
            if (residentList != null && !residentList.isEmpty()) {
                for (int i = 0; i < residentList.size(); i++) {
                    if (residentList.get(i).getId() == medical.getResidentId()) {
                        spinnerResident.setSelection(i);
                        break;
                    }
                }
            }
            
            etBloodType.setText(medical.getBloodType());
            etAllergies.setText(medical.getAllergies());
            etChronicDiseases.setText(medical.getChronicDiseases());
            etSurgeries.setText(medical.getSurgeries());
            if (etMedications != null) etMedications.setText(medical.getMedications());
            if (etInsuranceType != null) etInsuranceType.setText(medical.getInsuranceType());
            if (etInsuranceNumber != null) etInsuranceNumber.setText(medical.getInsuranceNumber());
            if (etLastCheckupDate != null) etLastCheckupDate.setText(medical.getLastCheckupDate());
            if (etNotes != null) etNotes.setText(medical.getNotes());
            etHospital.setText(medical.getHospital());
            etDepartment.setText(medical.getDepartment());
            etDiagnosis.setText(medical.getDiagnosis());
            etTreatment.setText(medical.getTreatment());
            etDoctor.setText(medical.getDoctor());
            etCost.setText(String.valueOf(medical.getCost()));
            etInsurance.setText(String.valueOf(medical.getInsurance()));
        }
    }

    private void saveMedicalRecord() {
        if (validateForm()) {
            Medical record = new Medical();
            if (isEditMode && medical != null) {
                record.setId(medical.getId());
            }
            
            // 正确设置居民ID
            if (residentList != null && !residentList.isEmpty()) {
                int selectedPosition = spinnerResident.getSelectedItemPosition();
                if (selectedPosition >= 0 && selectedPosition < residentList.size()) {
                    long residentId = residentList.get(selectedPosition).getId();
                    record.setResidentId(residentId);
                } else {
                    record.setResidentId(1); // 默认值
                }
            } else {
                record.setResidentId(1); // 默认值
            }
            
            record.setBloodType(etBloodType.getText().toString());
            record.setAllergies(etAllergies.getText().toString());
            record.setChronicDiseases(etChronicDiseases.getText().toString());
            record.setSurgeries(etSurgeries.getText().toString());
            if (etMedications != null) record.setMedications(etMedications.getText().toString());
            if (etInsuranceType != null) record.setInsuranceType(etInsuranceType.getText().toString());
            if (etInsuranceNumber != null) record.setInsuranceNumber(etInsuranceNumber.getText().toString());
            if (etLastCheckupDate != null) record.setLastCheckupDate(etLastCheckupDate.getText().toString());
            if (etNotes != null) record.setNotes(etNotes.getText().toString());
            record.setHospital(etHospital.getText().toString());
            record.setDepartment(etDepartment.getText().toString());
            record.setDiagnosis(etDiagnosis.getText().toString());
            record.setTreatment(etTreatment.getText().toString());
            record.setDoctor(etDoctor.getText().toString());
            
            try {
                record.setCost(Double.parseDouble(etCost.getText().toString()));
            } catch (NumberFormatException e) {
                record.setCost(0);
            }
            
            try {
                record.setInsurance(Double.parseDouble(etInsurance.getText().toString()));
            } catch (NumberFormatException e) {
                record.setInsurance(0);
            }
            
            // 设置ownerId为当前用户的ID
            if (currentUser != null) {
                record.setOwnerId(currentUser.getId());
            }

            new Thread(() -> {
                if (isEditMode && medical != null) {
                    medicalDao.update(record);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "医疗记录更新成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                } else {
                    medicalDao.insert(record);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "医疗记录创建成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }
            }).start();
        }
    }

    private boolean validateForm() {
        if (spinnerResident.getSelectedItem() == null) {
            Toast.makeText(this, "请选择居民", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etHospital.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入医院名称", Toast.LENGTH_SHORT).show();
            etHospital.requestFocus();
            return false;
        }

        if (etDepartment.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入科室", Toast.LENGTH_SHORT).show();
            etDepartment.requestFocus();
            return false;
        }

        if (etDiagnosis.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入诊断", Toast.LENGTH_SHORT).show();
            etDiagnosis.requestFocus();
            return false;
        }

        if (etTreatment.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入治疗方案", Toast.LENGTH_SHORT).show();
            etTreatment.requestFocus();
            return false;
        }

        if (etDoctor.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "请输入主治医生", Toast.LENGTH_SHORT).show();
            etDoctor.requestFocus();
            return false;
        }

        String costStr = etCost.getText().toString().trim();
        if (costStr.isEmpty()) {
            Toast.makeText(this, "请输入总费用", Toast.LENGTH_SHORT).show();
            etCost.requestFocus();
            return false;
        }

        try {
            Double.parseDouble(costStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "总费用格式不正确", Toast.LENGTH_SHORT).show();
            etCost.requestFocus();
            return false;
        }

        return true;
    }
}