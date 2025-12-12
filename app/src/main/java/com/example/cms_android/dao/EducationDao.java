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

    @Query("SELECT * FROM education WHERE residentId = :residentId AND ownerId = :ownerId ORDER BY enrollmentDate DESC")
    List<Education> getEducationByResidentAndOwner(long residentId, long ownerId);

    @Query("SELECT * FROM education WHERE id = :id")
    Education getEducationById(long id);

    @Query("SELECT * FROM education WHERE id = :id AND ownerId = :ownerId")
    Education getEducationByIdAndOwner(long id, long ownerId);

    @Query("SELECT * FROM education ORDER BY enrollmentDate DESC")
    List<Education> getAllEducationRecords();

    @Query("SELECT * FROM education WHERE ownerId = :ownerId ORDER BY enrollmentDate DESC")
    List<Education> getAllEducationRecordsByOwner(long ownerId);
}