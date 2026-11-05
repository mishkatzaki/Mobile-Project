package com.assignmentplanner.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.assignmentplanner.data.local.entity.Assignment
import com.assignmentplanner.data.local.entity.Course
import com.assignmentplanner.data.local.entity.Priority
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var database: AppDatabase
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }



    @Test
    fun insertCourse_returnsGeneratedId() = runTest {
        val course = Course(name = "Database Systems")

        val id = database.courseDao().insert(course)

        assertTrue(id > 0)
    }

    @Test
    fun getAllCourses_returnsInsertedCourses() = runTest {
        database.courseDao().insert(Course(name = "Database Systems"))
        database.courseDao().insert(Course(name = "Software Testing"))

        val courses = database.courseDao().getAll().first()

        assertEquals(2, courses.size)
        assertEquals(
            listOf("Database Systems", "Software Testing"),
            courses.map { it.name }
        )
    }

    @Test
    fun getCourseByName_returnsMatchingCourse() = runTest {
        database.courseDao().insert(Course(name = "Computer Networks"))

        val course = database.courseDao().getByName("Computer Networks")

        assertEquals("Computer Networks", course?.name)
    }

    @Test
    fun getCourseByName_returnsNullWhenMissing() = runTest {
        val course = database.courseDao().getByName("Unknown Course")

        assertNull(course)
    }

    @Test
    fun updateCourse_changesStoredName() = runTest {
        val id = database.courseDao().insert(
            Course(name = "Old Course Name")
        )

        database.courseDao().update(
            Course(id = id, name = "New Course Name")
        )

        val updated = database.courseDao().getByName("New Course Name")

        assertEquals(id, updated?.id)
    }

    @Test
    fun deleteCourse_removesCourse() = runTest {
        val id = database.courseDao().insert(
            Course(name = "Temporary Course")
        )

        database.courseDao().delete(
            Course(id = id, name = "Temporary Course")
        )

        assertNull(
            database.courseDao().getByName("Temporary Course")
        )
    }

    @Test
    fun courseNamesAreCaseInsensitiveAndUnique() = runTest {
        database.courseDao().insert(Course(name = "Mathematics"))

        var duplicateRejected = false

        try {
            database.courseDao().insert(Course(name = "mathematics"))
        } catch (_: Exception) {
            duplicateRejected = true
        }

        assertTrue(
            "The database should reject duplicate names regardless of case",
            duplicateRejected
        )
    }


    @Test
    fun insertAssignment_returnsGeneratedId() = runTest {
        val courseId = database.courseDao().insert(
            Course(name = "Operating Systems")
        )

        val assignment = Assignment(
            courseId = courseId,
            title = "Process Scheduling",
            dueDate = LocalDate.of(2026, 11, 10),
            priority = Priority.HIGH
        )

        val id = database.assignmentDao().insert(assignment)

        assertTrue(id > 0)
    }

    @Test
    fun getAllAssignments_returnsInsertedAssignments() = runTest {
        val courseId = database.courseDao().insert(
            Course(name = "Software Engineering")
        )

        database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "Project Proposal",
                dueDate = LocalDate.of(2026, 11, 12)
            )
        )

        database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "Final Report",
                dueDate = LocalDate.of(2026, 11, 20)
            )
        )

        val assignments = database.assignmentDao().getAll().first()

        assertEquals(2, assignments.size)
        assertTrue(assignments.any { it.title == "Project Proposal" })
        assertTrue(assignments.any { it.title == "Final Report" })
    }

    @Test
    fun getAssignmentById_returnsMatchingAssignment() = runTest {
        val courseId = database.courseDao().insert(
            Course(name = "Artificial Intelligence")
        )

        val id = database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "Search Algorithms",
                dueDate = LocalDate.of(2026, 11, 15)
            )
        )

        val assignment = database.assignmentDao()
            .getById(id)
            .first()

        assertEquals("Search Algorithms", assignment?.title)
        assertEquals(id, assignment?.id)
    }

    @Test
    fun updateAssignment_changesStoredValues() = runTest {
        val courseId = database.courseDao().insert(
            Course(name = "Mobile Development")
        )

        val id = database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "First Version",
                dueDate = LocalDate.of(2026, 11, 10)
            )
        )

        database.assignmentDao().update(
            Assignment(
                id = id,
                courseId = courseId,
                title = "Updated Version",
                description = "Updated assignment",
                dueDate = LocalDate.of(2026, 11, 18),
                priority = Priority.HIGH,
                isCompleted = true
            )
        )

        val updated = database.assignmentDao()
            .getById(id)
            .first()

        assertEquals("Updated Version", updated?.title)
        assertEquals("Updated assignment", updated?.description)
        assertEquals(LocalDate.of(2026, 11, 18), updated?.dueDate)
        assertEquals(Priority.HIGH, updated?.priority)
        assertTrue(updated?.isCompleted == true)
    }

    @Test
    fun deleteAssignment_removesAssignment() = runTest {
        val courseId = database.courseDao().insert(
            Course(name = "Web Development")
        )

        val id = database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "Build a Website",
                dueDate = LocalDate.of(2026, 11, 14)
            )
        )

        val inserted = database.assignmentDao()
            .getById(id)
            .first()

        assertNotEquals(null, inserted)

        database.assignmentDao().delete(inserted!!)

        val deleted = database.assignmentDao()
            .getById(id)
            .first()

        assertNull(deleted)
    }

    @Test
    fun countByCourse_returnsCorrectCount() = runTest {
        val courseId = database.courseDao().insert(
            Course(name = "Data Structures")
        )

        database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "Trees",
                dueDate = LocalDate.of(2026, 11, 10)
            )
        )

        database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "Graphs",
                dueDate = LocalDate.of(2026, 11, 15)
            )
        )

        val count = database.assignmentDao().countByCourse(courseId)

        assertEquals(2, count)
    }


    @Test
    fun assignmentCannotReferenceNonexistentCourse() = runTest {
        var insertionRejected = false

        try {
            database.assignmentDao().insert(
                Assignment(
                    courseId = 9999L,
                    title = "Invalid Assignment",
                    dueDate = LocalDate.of(2026, 11, 10)
                )
            )
        } catch (_: Exception) {
            insertionRejected = true
        }

        assertTrue(
            "The foreign key should reject an assignment with no matching course",
            insertionRejected
        )
    }

    @Test
    fun courseWithAssignmentsCannotBeDeleted() = runTest {
        val courseId = database.courseDao().insert(
            Course(name = "Computer Architecture")
        )

        database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "CPU Design",
                dueDate = LocalDate.of(2026, 11, 16)
            )
        )

        var deletionRejected = false

        try {
            database.courseDao().delete(
                Course(
                    id = courseId,
                    name = "Computer Architecture"
                )
            )
        } catch (_: Exception) {
            deletionRejected = true
        }

        assertTrue(
            "A course with assignments should not be deletable",
            deletionRejected
        )

        assertEquals(
            1,
            database.assignmentDao().countByCourse(courseId)
        )
    }


    @Test
    fun localDateAndPriority_areStoredAndRetrievedCorrectly() = runTest {
        val courseId = database.courseDao().insert(
            Course(name = "Algorithms")
        )

        val dueDate = LocalDate.of(2026, 12, 5)

        val id = database.assignmentDao().insert(
            Assignment(
                courseId = courseId,
                title = "Sorting Algorithms",
                dueDate = dueDate,
                priority = Priority.LOW,
                isCompleted = false
            )
        )

        val result = database.assignmentDao()
            .getById(id)
            .first()

        assertEquals(dueDate, result?.dueDate)
        assertEquals(Priority.LOW, result?.priority)
        assertFalse(result?.isCompleted ?: true)
    }


    @Test
    fun courseFlow_emitsInsertedCourse() = runTest {
        val courseFlow = database.courseDao().getAll()

        assertTrue(courseFlow.first().isEmpty())

        database.courseDao().insert(
            Course(name = "Distributed Systems")
        )

        val updatedCourses = courseFlow.first {
                courses -> courses.any { it.name == "Distributed Systems" }
        }

        assertEquals(1, updatedCourses.size)
    }
}