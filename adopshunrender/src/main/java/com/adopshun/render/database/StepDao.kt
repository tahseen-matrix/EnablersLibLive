package com.adopshun.render.database

import androidx.room.*

@Dao
interface StepDao {

    @Query("SELECT * FROM step_data")
    fun getAll(): List<Step>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(step: Step)

    @Delete
    fun delete(step: Step)

    @Query("DELETE FROM step_data")
    fun clearStep()

}