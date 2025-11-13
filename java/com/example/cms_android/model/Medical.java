package com.example.cms_android.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "medical",
        foreignKeys = @ForeignKey(
                entity = Resident.class,
                parentColumns = "id",
                childColumns = "residentId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = @Index(value = {"residentId"}) // 添加索引以优化外键查询性能
)
public class Medical {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long residentId; // 居民ID
    private String bloodType; // 血型
    private String allergies; // 过敏史
    private String chronicDiseases; // 慢性病史
    private String surgeries; // 手术史
    private String medications; // 常用药物
    private String insuranceType; // 医保类型
    private String insuranceNumber; // 医保号码
    private String lastCheckupDate; // 最后体检日期
    private String notes; // 医疗备注
    // 添加新的字段以匹配UI需求
    private String hospital; // 医院
    private String department; // 科室
    private String diagnosis; // 诊断
    private String treatment; // 治疗方案
    private String doctor; // 主治医生
    private double cost; // 总费用
    private double insurance; // 医保报销

    public Medical() {}

    @Ignore
    public Medical(long residentId, String bloodType, String allergies, 
                  String chronicDiseases, String surgeries, String medications, 
                  String insuranceType, String insuranceNumber, String lastCheckupDate, String notes,
                  String hospital, String department, String diagnosis, String treatment,
                  String doctor, double cost, double insurance) {
        this.residentId = residentId;
        this.bloodType = bloodType;
        this.allergies = allergies;
        this.chronicDiseases = chronicDiseases;
        this.surgeries = surgeries;
        this.medications = medications;
        this.insuranceType = insuranceType;
        this.insuranceNumber = insuranceNumber;
        this.lastCheckupDate = lastCheckupDate;
        this.notes = notes;
        this.hospital = hospital;
        this.department = department;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.doctor = doctor;
        this.cost = cost;
        this.insurance = insurance;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    
    public long getResidentId() { return residentId; }
    public void setResidentId(long residentId) { this.residentId = residentId; }
    
    public String getBloodType() { return bloodType; }
    public void setBloodType(String bloodType) { this.bloodType = bloodType; }
    
    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
    
    public String getChronicDiseases() { return chronicDiseases; }
    public void setChronicDiseases(String chronicDiseases) { this.chronicDiseases = chronicDiseases; }
    
    public String getSurgeries() { return surgeries; }
    public void setSurgeries(String surgeries) { this.surgeries = surgeries; }
    
    public String getMedications() { return medications; }
    public void setMedications(String medications) { this.medications = medications; }
    
    public String getInsuranceType() { return insuranceType; }
    public void setInsuranceType(String insuranceType) { this.insuranceType = insuranceType; }
    
    public String getInsuranceNumber() { return insuranceNumber; }
    public void setInsuranceNumber(String insuranceNumber) { this.insuranceNumber = insuranceNumber; }
    
    public String getLastCheckupDate() { return lastCheckupDate; }
    public void setLastCheckupDate(String lastCheckupDate) { this.lastCheckupDate = lastCheckupDate; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getHospital() { return hospital; }
    public void setHospital(String hospital) { this.hospital = hospital; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    
    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }
    
    public String getDoctor() { return doctor; }
    public void setDoctor(String doctor) { this.doctor = doctor; }
    
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
    
    public double getInsurance() { return insurance; }
    public void setInsurance(double insurance) { this.insurance = insurance; }
}