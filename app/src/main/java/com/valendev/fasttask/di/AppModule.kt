package com.valendev.fasttask.di

import android.app.Application
import android.content.Context
import com.valendev.fasttask.context.PreferenceManager
import com.valendev.fasttask.data.dao.CategoryDao
import com.valendev.fasttask.data.dao.TaskDao
import com.valendev.fasttask.data.dao.UserDao
import com.valendev.fasttask.data.db.AppDatabase
import com.valendev.fasttask.data.repository.AppRepository
import com.valendev.fasttask.data.repository.CategoryRepository
import com.valendev.fasttask.data.repository.TaskRepository
import com.valendev.fasttask.data.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(application: Application): AppDatabase{
        return AppDatabase.getDatabase(application)
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao{
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository{
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun providePreferenceManager(@ApplicationContext context: Context): PreferenceManager{
        return PreferenceManager(context)
    }

    @Provides
    @Singleton
    fun provideCategoryDao(appDatabase: AppDatabase): CategoryDao{
        return appDatabase.categoryDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(appDatabase: AppDatabase): TaskDao{
        return appDatabase.taskDao()
    }

    @Provides
    @Singleton
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository{
        return CategoryRepository(categoryDao)
    }

    @Provides
    @Singleton
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository{
        return TaskRepository(taskDao)
    }

    @Provides
    @Singleton
    fun provideAppRepository(categoryRepository: CategoryRepository, taskRepository: TaskRepository, userRepository: UserRepository): AppRepository{
        return AppRepository(categoryRepository, taskRepository, userRepository)
    }
}