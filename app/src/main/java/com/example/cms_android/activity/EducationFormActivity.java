package com.example.cms_android.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.example.cms_android.dao.EducationDao;
import com.example.cms_android.dao.ResidentDao;
import com.example.cms_android.model.Education;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EducationFormActivity extends AppCompatActivity
{
    private Button btnSave;
    private Spinner spinnerResident;
    private EditText etSchoolName;
    private EditText etEducationLevel;
    private EditText etMajor;
    private EditText etEnrollmentDate;
    private EditText etGraduationDate;
    private EditText etStatus;
    private EditText etNotes;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;
    private Education education;
    private boolean isEditMode = false;
    private EducationDao educationDao;
    private ResidentDao residentDao;
    private long educationId = -1;
    private List<com.example.cms_android.model.Resident> residentList; // 添加居民列表
    private SharedPreferencesManager sharedPreferencesManager;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education_form);

        educationDao = AppDatabase.getDatabase(this).educationDao();
        residentDao = AppDatabase.getDatabase(this).residentDao();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        currentUser = sharedPreferencesManager.getCurrentUser();

        //初始化视图
        initViews();

        //设置日期选择器
        setupDatePickers();

        //设置居民选择
        setupResidentSpinner();

        //设置点击监听器
        setupClickListeners();

        //加载教育记录数据
        loadEducationData();
    }

    private void initViews()
    {
        btnSave = findViewById(R.id.btn_save);
        spinnerResident = findViewById(R.id.spinner_resident);
        etSchoolName = findViewById(R.id.et_school_name);
        etEducationLevel = findViewById(R.id.et_education_level);
        etMajor = findViewById(R.id.et_major);
        etEnrollmentDate = findViewById(R.id.et_enrollment_date);
        etGraduationDate = findViewById(R.id.et_graduation_date);
        etStatus = findViewById(R.id.et_status);
        etNotes = findViewById(R.id.et_notes);

        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    }

    private void setupDatePickers()
    {
        etEnrollmentDate.setOnClickListener(v -> showDatePicker(etEnrollmentDate));
        etGraduationDate.setOnClickListener(v -> showDatePicker(etGraduationDate));
    }

    private void showDatePicker(final EditText editText)
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        editText.setText(dateFormatter.format(calendar.getTime()));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void setupResidentSpinner()
    {
        new Thread(() ->
        {
            residentList = residentDao.getResidentsByOwner(currentUser.getId()); // 只获取当前用户的居民
            runOnUiThread(() ->
            {
                List<String> residentNames = new ArrayList<>();
                for (com.example.cms_android.model.Resident resident : residentList)
                {
                    residentNames.add(resident.getName());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, residentNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerResident.setAdapter(adapter);
                
                // 如果是编辑模式，设置选中项
                if (isEditMode && education != null)
                {
                    for (int i = 0; i < residentList.size(); i++)
                    {
                        if (residentList.get(i).getId() == education.getResidentId())
                        {
                            spinnerResident.setSelection(i);
                            break;
                        }
                    }
                }
            });
        }).start();
    }

    private void setupClickListeners()
    {
        // 为工具栏的返回按钮设置监听器
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null)
        {
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        btnSave.setOnClickListener(v -> saveEducationRecord());

        // 添加取消按钮的点击监听
        findViewById(R.id.btn_cancel).setOnClickListener(v -> finish());
    }

    private void loadEducationData()
    {
        // 检查是否传递了教育记录ID用于编辑
        if (getIntent().hasExtra("education_id"))
        {
            isEditMode = true;
            educationId = getIntent().getLongExtra("education_id", -1);
            
            // 模拟加载教育记录数据
            loadEducationFromDatabase();
        }
    }

    private void loadEducationFromDatabase()
    {
        new Thread(() ->
        {
            education = educationDao.getEducationById(educationId);
            if (education != null)
            {
                runOnUiThread(this::populateFormData);
            }
        }).start();
    }

    private void populateFormData()
    {
        if (education != null)
        {
            // 设置居民选择（这里简化处理，实际应该根据residentId查找居民）
            
            etSchoolName.setText(education.getSchoolName());
            etEducationLevel.setText(education.getEducationLevel());
            etMajor.setText(education.getMajor());
            etEnrollmentDate.setText(education.getEnrollmentDate());
            etGraduationDate.setText(education.getGraduationDate());
            etStatus.setText(education.getStatus());
            etNotes.setText(education.getNotes());
        }
    }

    private void saveEducationRecord()
    {
        if (validateForm())
        {
            Education record = new Education();
            if (isEditMode && education != null)
            {
                record.setId(education.getId());
            }
            
            // 正确设置居民ID
            if (residentList != null && !residentList.isEmpty())
            {
                int selectedPosition = spinnerResident.getSelectedItemPosition();
                if (selectedPosition >= 0 && selectedPosition < residentList.size())
                {
                    long residentId = residentList.get(selectedPosition).getId();
                    record.setResidentId(residentId);
                }
                else
                {
                    record.setResidentId(1); // 默认值
                }
            }
            else
            {
                record.setResidentId(1); // 默认值
            }
            
            record.setSchoolName(etSchoolName.getText().toString());
            record.setEducationLevel(etEducationLevel.getText().toString());
            record.setMajor(etMajor.getText().toString());
            record.setEnrollmentDate(etEnrollmentDate.getText().toString());
            record.setGraduationDate(etGraduationDate.getText().toString());
            record.setStatus(etStatus.getText().toString());
            record.setNotes(etNotes.getText().toString());
            // 设置ownerId为当前用户的ID
            if (currentUser != null)
            {
                record.setOwnerId(currentUser.getId());
            }

            new Thread(() ->
            {
                if (isEditMode && education != null)
                {
                    educationDao.update(record);
                    runOnUiThread(() ->
                    {
                        Toast.makeText(this, "教育记录更新成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }
                else
                {
                    educationDao.insert(record);
                    runOnUiThread(() ->
                    {
                        Toast.makeText(this, "教育记录创建成功", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    });
                }
            }).start();
        }
    }

    private boolean validateForm()
    {
        if (spinnerResident.getSelectedItem() == null)
        {
            Toast.makeText(this, "请选择居民", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (etSchoolName.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "请输入学校名称", Toast.LENGTH_SHORT).show();
            etSchoolName.requestFocus();
            return false;
        }

        if (etEducationLevel.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "请输入教育程度", Toast.LENGTH_SHORT).show();
            etEducationLevel.requestFocus();
            return false;
        }

        if (etEnrollmentDate.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this, "请选择入学日期", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}