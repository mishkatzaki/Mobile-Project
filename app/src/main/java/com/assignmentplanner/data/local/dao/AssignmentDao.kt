package com.assignmentplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.assignmentplanner.data.local.entity.Assignment
import kotlinx.coroutines.flow.Flow

@Dao
interface AssignmentDao {

    @Query("SELECT * FROM assignments")
    fun getAll(): Flow<List<Assignment>>

    @Query("SELECT * FROM assignments WHERE id = :id")
    fun getById(id: Long): Flow<Assignment?>

    @Insert
    suspend fun insert(assignment: Assignment): Long

    @Update
    suspend fun update(assignment: Assignment)

    @Delete
    suspend fun delete(assignment: Assignment)

    @Query("SELECT COUNT(*) FROM assignments WHERE courseId = :courseId")
    suspend fun countByCourse(courseId: Long): Int
}