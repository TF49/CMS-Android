package com.example.cms_android.dao;

import androidx.room.*;
import com.example.cms_android.model.Education;

import java.util.List;

@Dao
public interface EducationDao {
    @Insert
    long insert(Education education);

    @Update
    void update(Education education);

    @Delete
    void delete(Education education);

    @Query("SELECT * FROM education WHERE residentId = :residentId ORDER BY enrollmentDate DESC")
    List<Education> getEducationByResident(long residentId);

    @Query("SELECT * FROM education WHERE id = :id")
    Education getEducationById(long id);

    @Query("SELECT * FROM education ORDER BY enrollmentDate DESC")
    List<Education> getAllEducationRecords();
}