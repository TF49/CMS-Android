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

    @Query("SELECT * FROM households WHERE ownerId = :ownerId ORDER BY householdNumber ASC")
    List<Household> getHouseholdsByOwner(long ownerId);

    @Query("SELECT * FROM households WHERE id = :id")
    Household getHouseholdById(long id);

    @Query("SELECT * FROM households WHERE id = :id AND ownerId = :ownerId")
    Household getHouseholdByIdAndOwner(long id, long ownerId);

    @Query("SELECT * FROM households WHERE householdNumber LIKE :search OR householderName LIKE :search OR address LIKE :search")
    List<Household> searchHouseholds(String search);

    @Query("SELECT * FROM households WHERE (householdNumber LIKE :search OR householderName LIKE :search OR address LIKE :search) AND ownerId = :ownerId")
    List<Household> searchHouseholdsByOwner(String search, long ownerId);

    @Query("SELECT COUNT(*) FROM households")
    int getHouseholdCount();

    @Query("SELECT COUNT(*) FROM households WHERE ownerId = :ownerId")
    int getHouseholdCountByOwner(long ownerId);

    @Query("SELECT * FROM households WHERE householdNumber = :householdNumber")
    Household getHouseholdByNumber(String householdNumber);

    @Query("SELECT * FROM households WHERE householdNumber = :householdNumber AND ownerId = :ownerId")
    Household getHouseholdByNumberAndOwner(String householdNumber, long ownerId);

    @Query("SELECT * FROM households WHERE address LIKE '%' || :address || '%'")
    List<Household> getHouseholdsByAddress(String address);

    @Query("SELECT * FROM households WHERE address LIKE '%' || :address || '%' AND ownerId = :ownerId")
    List<Household> getHouseholdsByAddressAndOwner(String address, long ownerId);

    @Query("SELECT * FROM households WHERE householderName LIKE '%' || :name || '%'")
    List<Household> getHouseholdsByHouseholderName(String name);

    @Query("SELECT * FROM households WHERE householderName LIKE '%' || :name || '%' AND ownerId = :ownerId")
    List<Household> getHouseholdsByHouseholderNameAndOwner(String name, long ownerId);
}