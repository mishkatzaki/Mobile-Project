package com.assignmentplanner.data.local.dao

import com.assignmentplanner.data.local.entity.Course
import kotlinx.coroutines.flow.Flow

// TODO: Person 5 - add @Dao and the Room annotations/queries for each function
interface CourseDao {
    fun getAll(): Flow<List<Course>>
    suspend fun getByName(name: String): Course?
    suspend fun insert(course: Course): Long
    suspend fun update(course: Course)
    suspend fun delete(course: Course)
}
