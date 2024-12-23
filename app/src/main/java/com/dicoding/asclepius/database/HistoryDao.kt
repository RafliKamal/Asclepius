package com.dicoding.asclepius.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(history: History)
    @Update
    fun update(history: History)
    @Delete
    fun delete(history: History)
    @Query("SELECT * from history ORDER BY id ASC")
    fun getAllHistory(): LiveData<List<History>>
}