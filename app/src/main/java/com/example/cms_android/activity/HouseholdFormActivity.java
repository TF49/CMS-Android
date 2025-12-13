package com.example.cms_android.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.cms_android.R;
import com.example.cms_android.database.AppDatabase;
import com.example.cms_android.dao.HouseholdDao;
import com.example.cms_android.model.Household;
import com.example.cms_android.model.User;
import com.example.cms_android.utils.SharedPreferencesManager;
import com.example.cms_android.utils.PermissionManager;

public class HouseholdFormActivity extends AppCompatActivity
{
    private EditText etHouseholdNumber, etAddress, etHouseholderName, etHouseholderIdCard, etPhoneNumber, etRegistrationDate, etHouseholdType, etPopulationCount, etNotes;
    private Button btnSave, btnCancel;
    private HouseholdDao householdDao;
    private Household currentHousehold;
    private SharedPreferencesManager sharedPreferencesManager;
    private User currentUser;
    
    // 标识是否为编辑模式
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_household_form);

        householdDao = AppDatabase.getDatabase(this).householdDao();
        sharedPreferencesManager = new SharedPreferencesManager(this);
        currentUser = sharedPreferencesManager.getCurrentUser();

        initializeViews();
        setupClickListeners();
        
        // 检查是否是编辑模式
        long householdId = getIntent().getLongExtra("HOUSEHOLD_ID", -1);
        if (householdId != -1)
        {
            isEditMode = true;
            loadHouseholdData(householdId);
        }
    }

    private void initializeViews()
    {
        etHouseholdNumber = findViewById(R.id.et_household_number);
        etAddress = findViewById(R.id.et_address);
        etHouseholderName = findViewById(R.id.et_householder_name);
        etHouseholderIdCard = findViewById(R.id.et_householder_id_card);
        etPhoneNumber = findViewById(R.id.et_phone_number);
        etRegistrationDate = findViewById(R.id.et_registration_date);
        etHouseholdType = findViewById(R.id.et_household_type);
        etPopulationCount = findViewById(R.id.et_population_count);
        etNotes = findViewById(R.id.et_notes);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
    }

    private void setupClickListeners()
    {
        btnSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                saveHousehold();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });
        
        // 为工具栏的返回按钮设置监听器
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null)
        {
            toolbar.setNavigationOnClickListener(v -> finish());
        }
    }

    private void loadHouseholdData(long householdId)
    {
        new AsyncTask<Long, Void, Household>()
        {
            @Override
            protected Household doInBackground(Long... ids)
            {
                return householdDao.getHouseholdById(ids[0]);
            }

            @Override
            protected void onPostExecute(Household household)
            {
                if (household != null)
                {
                    currentHousehold = household;
                    populateFormFields(household);
                }
                else
                {
                    Toast.makeText(HouseholdFormActivity.this, "加载户籍信息失败", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }.execute(householdId);
    }

    private void populateFormFields(Household household)
    {
        etHouseholdNumber.setText(household.getHouseholdNumber());
        etAddress.setText(household.getAddress());
        etHouseholderName.setText(household.getHouseholderName());
        etHouseholderIdCard.setText(household.getHouseholderIdCard());
        etPhoneNumber.setText(household.getPhoneNumber());
        etRegistrationDate.setText(household.getRegistrationDate());
        etHouseholdType.setText(household.getHouseholdType());
        etPopulationCount.setText(String.valueOf(household.getPopulationCount()));
        etNotes.setText(household.getNotes());
    }

    private void saveHousehold()
    {
        String householdNumber = etHouseholdNumber.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String householderName = etHouseholderName.getText().toString().trim();
        String householderIdCard = etHouseholderIdCard.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();
        String registrationDate = etRegistrationDate.getText().toString().trim();
        String householdType = etHouseholdType.getText().toString().trim();
        String populationCountStr = etPopulationCount.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        // 验证输入
        if (householdNumber.isEmpty() || address.isEmpty() || householderName.isEmpty() || 
            householderIdCard.isEmpty() || phoneNumber.isEmpty() || registrationDate.isEmpty() || 
            householdType.isEmpty() || populationCountStr.isEmpty())
        {
            Toast.makeText(this, "请填写所有必填字段", Toast.LENGTH_SHORT).show();
            return;
        }

        int populationCount;
        try
        {
            populationCount = Integer.parseInt(populationCountStr);
        }
        catch (NumberFormatException e)
        {
            Toast.makeText(this, "人口数量必须是数字", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isEditMode && currentHousehold != null)
        {
            // 验证编辑权限
            if (!PermissionManager.canModifyHousehold(currentUser, currentHousehold))
            {
                Toast.makeText(this, "您没有权限编辑此户籍信息", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // 编辑模式，更新现有记录
            currentHousehold.setHouseholdNumber(householdNumber);
            currentHousehold.setAddress(address);
            currentHousehold.setHouseholderName(householderName);
            currentHousehold.setHouseholderIdCard(householderIdCard);
            currentHousehold.setPhoneNumber(phoneNumber);
            currentHousehold.setRegistrationDate(registrationDate);
            currentHousehold.setHouseholdType(householdType);
            currentHousehold.setPopulationCount(populationCount);
            currentHousehold.setNotes(notes);

            updateHousehold(currentHousehold);
        }
        else
        {
            // 新增模式，创建新记录
            final Household household = new Household(householdNumber, address, householderName, 
                    householderIdCard, phoneNumber, registrationDate, householdType, populationCount, notes, currentUser.getId());

            insertHousehold(household);
        }
    }

    private void insertHousehold(final Household household)
    {
        new AsyncTask<Void, Void, Long>()
        {
            @Override
            protected Long doInBackground(Void... voids) {
                return householdDao.insert(household);
            }

            @Override
            protected void onPostExecute(Long result)
            {
                if (result > 0)
                {
                    Toast.makeText(HouseholdFormActivity.this, "户籍添加成功", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                else
                {
                    Toast.makeText(HouseholdFormActivity.this, "添加失败", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void updateHousehold(final Household household)
    {
        new AsyncTask<Void, Void, Integer>()
        {
            @Override
            protected Integer doInBackground(Void... voids)
            {
                householdDao.update(household);
                return 1; // 返回一个标识表示更新成功
            }

            @Override
            protected void onPostExecute(Integer result)
            {
                Toast.makeText(HouseholdFormActivity.this, "户籍信息更新成功", Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                finish();
            }
        }.execute();
    }
}