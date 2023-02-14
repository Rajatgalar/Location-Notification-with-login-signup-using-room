package com.itechnowizard.aplitemapapplication

import androidx.room.Database
import androidx.room.RoomDatabase
import com.itechnowizard.aplitemapapplication.dao.UsersDao
import com.itechnowizard.aplitemapapplication.model.Users

@Database(entities = [Users::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

        abstract fun userDao(): UsersDao
}