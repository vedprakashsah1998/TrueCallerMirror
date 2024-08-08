package com.infinity8.truecallermirror.di

import android.content.Context
import androidx.room.Room
import com.infinity8.truecallermirror.appDb
import com.infinity8.truecallermirror.dao.CallDao
import com.infinity8.truecallermirror.dao.ContactDao
import com.infinity8.truecallermirror.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideCallLogDao(appDatabase: AppDatabase): CallDao = appDatabase.callListDao()

    @Provides
    fun provideContactsDao(appDatabase: AppDatabase): ContactDao = appDatabase.contactList()


    @Singleton
    @Provides
    fun provideMyDB(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            appDb
        ).fallbackToDestructiveMigration().allowMainThreadQueries().build()
}