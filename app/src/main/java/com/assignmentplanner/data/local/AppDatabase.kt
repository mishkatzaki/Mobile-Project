package com.assignmentplanner.data.local

import androidx.room.RoomDatabase

// TODO: Person 5 - add @Database(entities = [Course::class, Assignment::class], version = 1), courseDao()/assignmentDao(), type converters and a singleton builder
abstract class AppDatabase : RoomDatabase()
