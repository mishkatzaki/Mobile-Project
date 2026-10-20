package com.assignmentplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.assignmentplanner.data.local.entity.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY name")
    fun getAll(): Flow<List<Course>>

    @Query("SELECT * FROM courses WHERE name = :name LIMIT 1")
    suspend fun getByName(name: String): Course?

    @Insert
    suspend fun insert(course: Course): Long

    @Update
    suspend fun update(course: Course)

    @Delete
    suspend fun delete(course: Course)
}