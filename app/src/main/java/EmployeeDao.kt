package com.example.poctomatapp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface EmployeeDao {

    @Query("SELECT * FROM employees WHERE login = :login AND password = :password LIMIT 1")
    fun login(login: String, password: String): EmployeeEntity?

    @Insert
    fun insert(employee: EmployeeEntity)

    @Query("SELECT COUNT(*) FROM employees")
    fun getCount(): Int
}