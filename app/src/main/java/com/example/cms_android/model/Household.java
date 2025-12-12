package com.example.cms_android.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "households")
public class Household {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String householdNumber; // 户籍编号
    private String address; // 户籍地址
    private String householderName; // 户主姓名
    private String householderIdCard; // 户主身份证号
    private String phoneNumber; // 联系电话
    private String registrationDate; // 登记日期
    private String householdType; // 户籍类型（城镇/农村）
    private int populationCount; // 人口数量
    private String notes; // 备注
    private long ownerId; // 关联的用户ID，用于权限控制

    public Household() {}

    @Ignore
    public Household(String householdNumber, String address, String householderName, 
                    String householderIdCard, String phoneNumber, String registrationDate, 
                    String householdType, int populationCount, String notes, long ownerId) {
        this.householdNumber = householdNumber;
        this.address = address;
        this.householderName = householderName;
        this.householderIdCard = householderIdCard;
        this.phoneNumber = phoneNumber;
        this.registrationDate = registrationDate;
        this.householdType = householdType;
        this.populationCount = populationCount;
        this.notes = notes;
        this.ownerId = ownerId;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public String getHouseholdNumber() { return householdNumber; }
    public void setHouseholdNumber(String householdNumber) { this.householdNumber = householdNumber; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getHouseholderName() { return householderName; }
    public void setHouseholderName(String householderName) { this.householderName = householderName; }
    
    public String getHouseholderIdCard() { return householderIdCard; }
    public void setHouseholderIdCard(String householderIdCard) { this.householderIdCard = householderIdCard; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String registrationDate) { this.registrationDate = registrationDate; }
    
    public String getHouseholdType() { return householdType; }
    public void setHouseholdType(String householdType) { this.householdType = householdType; }
    
    public int getPopulationCount() { return populationCount; }
    public void setPopulationCount(int populationCount) { this.populationCount = populationCount; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public long getOwnerId() { return ownerId; }
    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }
}