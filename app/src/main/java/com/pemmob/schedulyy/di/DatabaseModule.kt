package com.pemmob.schedulyy.di

import android.content.Context
import androidx.room.Room
import com.pemmob.schedulyy.data.local.SchedulyyDatabase
import com.pemmob.schedulyy.data.local.dao.ClassSessionDao
import com.pemmob.schedulyy.data.local.dao.CourseDao
import com.pemmob.schedulyy.data.local.dao.DivisionDao
import com.pemmob.schedulyy.data.local.dao.EventDao
import com.pemmob.schedulyy.data.local.dao.ProkerDao
import com.pemmob.schedulyy.data.local.dao.ReminderDao
import com.pemmob.schedulyy.data.local.dao.SemesterDao
import com.pemmob.schedulyy.data.local.dao.SpaceDao
import com.pemmob.schedulyy.data.local.dao.SubTaskDao
import com.pemmob.schedulyy.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SchedulyyDatabase {
        return Room.databaseBuilder(
            context,
            SchedulyyDatabase::class.java,
            SchedulyyDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    fun provideSpaceDao(db: SchedulyyDatabase): SpaceDao = db.spaceDao()

    @Provides
    fun provideSemesterDao(db: SchedulyyDatabase): SemesterDao = db.semesterDao()

    @Provides
    fun provideCourseDao(db: SchedulyyDatabase): CourseDao = db.courseDao()

    @Provides
    fun provideClassSessionDao(db: SchedulyyDatabase): ClassSessionDao = db.classSessionDao()

    @Provides
    fun provideDivisionDao(db: SchedulyyDatabase): DivisionDao = db.divisionDao()

    @Provides
    fun provideProkerDao(db: SchedulyyDatabase): ProkerDao = db.prokerDao()

    @Provides
    fun provideEventDao(db: SchedulyyDatabase): EventDao = db.eventDao()

    @Provides
    fun provideTaskDao(db: SchedulyyDatabase): TaskDao = db.taskDao()

    @Provides
    fun provideSubTaskDao(db: SchedulyyDatabase): SubTaskDao = db.subTaskDao()

    @Provides
    fun provideReminderDao(db: SchedulyyDatabase): ReminderDao = db.reminderDao()
}
