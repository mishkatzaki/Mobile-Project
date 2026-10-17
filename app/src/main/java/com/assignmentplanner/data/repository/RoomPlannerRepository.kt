package com.assignmentplanner.data.repository


import com.assignmentplanner.data.local.dao.AssignmentDao
import com.assignmentplanner.data.local.dao.CourseDao
import com.assignmentplanner.data.local.entity.Assignment
import com.assignmentplanner.data.local.entity.Course
import kotlinx.coroutines.flow.Flow

class RoomPlannerRepository(
    private val courseDao: CourseDao,
    private val assignmentDao: AssignmentDao
) : PlannerRepository {

    // ---- Assignments ----
    override fun getAssignments(): Flow<List<Assignment>> = assignmentDao.getAll()

    override fun getAssignment(id: Long): Flow<Assignment?> = assignmentDao.getById(id)

    override suspend fun insertAssignment(assignment: Assignment): Long =
        assignmentDao.insert(assignment)

    override suspend fun updateAssignment(assignment: Assignment) =
        assignmentDao.update(assignment)

    override suspend fun deleteAssignment(assignment: Assignment) =
        assignmentDao.delete(assignment)

    // ---- Courses ----
    override fun getCourses(): Flow<List<Course>> = courseDao.getAll()

    override suspend fun insertCourse(course: Course): Long = courseDao.insert(course)

    override suspend fun updateCourse(course: Course) = courseDao.update(course)

    override suspend fun deleteCourse(course: Course) = courseDao.delete(course)

    override suspend fun courseHasAssignments(courseId: Long): Boolean =
        assignmentDao.countByCourse(courseId) > 0
}