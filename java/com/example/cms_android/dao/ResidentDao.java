package com.example.cms_android.dao;

import androidx.room.*;
import com.example.cms_android.model.Resident;

import java.util.List;

@Dao
public interface ResidentDao {
    @Insert
    long insert(Resident resident);

    @Update
    void update(Resident resident);

    @Delete
    void delete(Resident resident);

    @Query("SELECT * FROM residents ORDER BY name ASC")
    List<Resident> getAllResidents();

    @Query("SELECT * FROM residents WHERE id = :id")
    Resident getResidentById(long id);

    @Query("SELECT * FROM residents WHERE householdId = :householdId ORDER BY name ASC")
    List<Resident> getResidentsByHousehold(long householdId);

    @Query("SELECT * FROM residents WHERE name LIKE :search OR idCard LIKE :search")
    List<Resident> searchResidents(String search);

    @Query("SELECT COUNT(*) FROM residents WHERE householdId = :householdId")
    int getResidentCountByHousehold(long householdId);

    @Query("SELECT * FROM residents WHERE isHouseholder = 1 AND householdId = :householdId")
    Resident getHouseholderByHousehold(long householdId);
}