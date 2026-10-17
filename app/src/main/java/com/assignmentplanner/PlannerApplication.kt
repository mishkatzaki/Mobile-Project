package com.assignmentplanner

import android.app.Application
import com.assignmentplanner.data.local.AppDatabase
import com.assignmentplanner.data.repository.PlannerRepository
import com.assignmentplanner.data.repository.RoomPlannerRepository

class PlannerApplication : Application() {
    val repository: PlannerRepository by lazy {
        val db = AppDatabase.get(this)
        RoomPlannerRepository(db.courseDao(), db.assignmentDao())
    }
}