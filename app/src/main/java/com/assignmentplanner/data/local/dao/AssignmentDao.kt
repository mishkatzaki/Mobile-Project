package com.assignmentplanner.data.local.dao

import com.assignmentplanner.data.local.entity.Assignment
import kotlinx.coroutines.flow.Flow

// TODO: Person 5 - add @Dao and the Room annotations/queries for each function
interface AssignmentDao {
    fun getAll(): Flow<List<Assignment>>
    fun getById(id: Long): Flow<Assignment?>
    suspend fun insert(assignment: Assignment): Long
    suspend fun update(assignment: Assignment)
    suspend fun delete(assignment: Assignment)
    suspend fun countByCourse(courseId: Long): Int
}
