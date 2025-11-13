package com.example.cms_android.dao;

import androidx.room.*;
import com.example.cms_android.model.Medical;

import java.util.List;

@Dao
public interface MedicalDao {
    @Insert
    long insert(Medical medical);

    @Update
    void update(Medical medical);

    @Delete
    void delete(Medical medical);

    @Query("SELECT * FROM medical WHERE residentId = :residentId")
    Medical getMedicalByResident(long residentId);

    @Query("SELECT * FROM medical WHERE id = :id")
    Medical getMedicalById(long id);

    @Query("SELECT * FROM medical ORDER BY lastCheckupDate DESC")
    List<Medical> getAllMedicalRecords();
}