package com.itechnowizard.aplitemapapplication.di

import android.app.Application
import androidx.room.Room
import com.itechnowizard.aplitemapapplication.AppDatabase
import com.itechnowizard.aplitemapapplication.dao.UsersDao
import com.itechnowizard.aplitemapapplication.repository.UserRepository
import com.itechnowizard.aplitemapapplication.usecase.UsersUseCase
import com.itechnowizard.aplitemapapplication.usecase.UsersUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    @Singleton
    fun provideUsersUseCase(userRepository: UserRepository) =
        UsersUseCaseImpl(userRepository) as UsersUseCase

}
