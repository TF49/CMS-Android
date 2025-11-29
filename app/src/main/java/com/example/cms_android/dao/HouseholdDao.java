package com.example.cms_android.dao;

import androidx.room.*;
import com.example.cms_android.model.Household;

import java.util.List;

@Dao
public interface HouseholdDao {
    @Insert
    long insert(Household household);

    @Update
    void update(Household household);

    @Delete
    void delete(Household household);

    @Query("SELECT * FROM households ORDER BY householdNumber ASC")
    List<Household> getAllHouseholds();

    @Query("SELECT * FROM households WHERE id = :id")
    Household getHouseholdById(long id);

    @Query("SELECT * FROM households WHERE householdNumber LIKE :search OR householderName LIKE :search OR address LIKE :search")
    List<Household> searchHouseholds(String search);

    @Query("SELECT COUNT(*) FROM households")
    int getHouseholdCount();

    @Query("SELECT * FROM households WHERE householdNumber = :householdNumber")
    Household getHouseholdByNumber(String householdNumber);

    @Query("SELECT * FROM households WHERE address LIKE '%' || :address || '%'")
    List<Household> getHouseholdsByAddress(String address);

    @Query("SELECT * FROM households WHERE householderName LIKE '%' || :name || '%'")
    List<Household> getHouseholdsByHouseholderName(String name);
}