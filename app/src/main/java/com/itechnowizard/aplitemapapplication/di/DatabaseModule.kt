package com.itechnowizard.aplitemapapplication.di

import android.app.Application
import androidx.room.Room
import com.itechnowizard.aplitemapapplication.AppDatabase
import com.itechnowizard.aplitemapapplication.dao.UsersDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    @Singleton
    internal fun provideAppDatabase(application: Application) = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "UserDatabase"
    ).allowMainThreadQueries().build()

    @Provides
    internal fun provideUserDao(appDatabase: AppDatabase): UsersDao {
        return appDatabase.userDao()
    }



}
