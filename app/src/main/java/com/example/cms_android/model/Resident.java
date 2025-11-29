package com.example.cms_android.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "residents",
        foreignKeys = @ForeignKey(
                entity = Household.class,
                parentColumns = "id",
                childColumns = "householdId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = {"householdId"}) // 添加索引
)
public class Resident {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long householdId; // 所属户籍ID
    private String name; // 姓名
    private String idCard; // 身份证号
    private String gender; // 性别
    private String birthDate; // 出生日期
    private String relationship; // 与户主关系
    private String ethnicGroup; // 民族
    private String educationLevel; // 教育程度
    private String occupation; // 职业
    private String maritalStatus; // 婚姻状况
    private String phoneNumber; // 联系电话
    private String healthStatus; // 健康状况
    private String bloodType; // 血型
    private boolean isHouseholder; // 是否为户主
    private String notes; // 备注信息
    private long ownerId; // 关联的用户ID，用于权限控制

    public Resident() {}

    @Ignore
    public Resident(long householdId, String name, String idCard, String gender, 
                   String birthDate, String relationship, String ethnicGroup, 
                   String educationLevel, String occupation, String maritalStatus, 
                   String phoneNumber, String healthStatus, String bloodType, boolean isHouseholder, String notes) {
        this.householdId = householdId;
        this.name = name;
        this.idCard = idCard;
        this.gender = gender;
        this.birthDate = birthDate;
        this.relationship = relationship;
        this.ethnicGroup = ethnicGroup;
        this.educationLevel = educationLevel;
        this.occupation = occupation;
        this.maritalStatus = maritalStatus;
        this.phoneNumber = phoneNumber;
        this.healthStatus = healthStatus;
        this.bloodType = bloodType;
        this.isHouseholder = isHouseholder;
        this.notes = notes;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public long getHouseholdId() { return householdId; }
    public void setHouseholdId(long householdId) { this.householdId = householdId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getIdCard() { return idCard; }
    public void setIdCard(String idCard) { this.idCard = idCard; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    
    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    
    public String getEthnicGroup() { return ethnicGroup; }
    public void setEthnicGroup(String ethnicGroup) { this.ethnicGroup = ethnicGroup; }
    
    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }
    
    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }
    
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    
    public boolean isHouseholder() { return isHouseholder; }
    public void setHouseholder(boolean householder) { isHouseholder = householder; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public long getOwnerId() { return ownerId; }
    public void setOwnerId(long ownerId) { this.ownerId = ownerId; }
}