package com.itechnowizard.aplitemapapplication.di

import android.app.Application
import androidx.room.Room
import com.itechnowizard.aplitemapapplication.AppDatabase
import com.itechnowizard.aplitemapapplication.dao.UsersDao
import com.itechnowizard.aplitemapapplication.repository.UserRepository
import com.itechnowizard.aplitemapapplication.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    @Singleton
    @Provides
    fun provideUsersRepository(usersDao: UsersDao) =
        UserRepositoryImpl(usersDao) as UserRepository
   }
