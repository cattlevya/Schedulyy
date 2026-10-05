package com.pemmob.schedulyy.di

import com.pemmob.schedulyy.data.repository.ScheduleRepositoryImpl
import com.pemmob.schedulyy.data.repository.SpaceRepositoryImpl
import com.pemmob.schedulyy.data.repository.TaskRepositoryImpl
import com.pemmob.schedulyy.domain.repository.ScheduleRepository
import com.pemmob.schedulyy.domain.repository.SpaceRepository
import com.pemmob.schedulyy.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        impl: TaskRepositoryImpl
    ): TaskRepository

    @Binds
    @Singleton
    abstract fun bindScheduleRepository(
        impl: ScheduleRepositoryImpl
    ): ScheduleRepository

    @Binds
    @Singleton
    abstract fun bindSpaceRepository(
        impl: SpaceRepositoryImpl
    ): SpaceRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        impl: com.pemmob.schedulyy.data.repository.SettingsRepositoryImpl
    ): com.pemmob.schedulyy.domain.repository.SettingsRepository

    @Binds
    @Singleton
    abstract fun bindCryptoEngine(
        impl: com.pemmob.schedulyy.data.security.TinkCryptoEngine
    ): com.pemmob.schedulyy.data.security.CryptoEngine
}
