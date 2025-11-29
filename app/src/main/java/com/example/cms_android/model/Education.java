package com.example.cms_android.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "education",
        foreignKeys = @ForeignKey(
                entity = Resident.class,
                parentColumns = "id",
                childColumns = "residentId",
                onDelete = ForeignKey.CASCADE
        ),
        indices = {@Index("residentId")} // 添加这一行以创建索引
)
public class Education {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long residentId; // 居民ID
    private String educationLevel; // 教育程度
    private String schoolName; // 学校名称
    private String major; // 专业
    private String enrollmentDate; // 入学日期
    private String graduationDate; // 毕业日期
    private String degree; // 学位
    private String status; // 状态
    private boolean isCurrent; // 是否在读
    private String notes; // 备注

    // Room 使用的无参构造函数
    public Education() {}


    // 用于创建对象的全参构造函数
    @Ignore
    public Education(long residentId, String educationLevel, String schoolName,
                     String major, String enrollmentDate, String graduationDate,
                     String degree, String status, boolean isCurrent, String notes) {
        this.residentId = residentId;
        this.educationLevel = educationLevel;
        this.schoolName = schoolName;
        this.major = major;
        this.enrollmentDate = enrollmentDate;
        this.graduationDate = graduationDate;
        this.degree = degree;
        this.status = status;
        this.isCurrent = isCurrent;
        this.notes = notes;
    }

    // Getters and Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getResidentId() { return residentId; }
    public void setResidentId(long residentId) { this.residentId = residentId; }

    public String getEducationLevel() { return educationLevel; }
    public void setEducationLevel(String educationLevel) { this.educationLevel = educationLevel; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(String enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getGraduationDate() { return graduationDate; }
    public void setGraduationDate(String graduationDate) { this.graduationDate = graduationDate; }

    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isCurrent() { return isCurrent; }
    public void setCurrent(boolean current) { isCurrent = current; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}