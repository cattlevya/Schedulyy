package com.pemmob.schedulyy.di

import com.pemmob.schedulyy.data.local.dao.SpaceDao
import com.pemmob.schedulyy.data.local.entity.SpaceEntity
import com.pemmob.schedulyy.domain.model.SpaceType
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @ApplicationScope
    @Singleton
    @Provides
    fun provideApplicationScope(): CoroutineScope =
        CoroutineScope(SupervisorJob() + Dispatchers.IO)

    @Singleton
    @Provides
    fun provideSpaceSeeder(
        spaceDao: SpaceDao,
        @ApplicationScope scope: CoroutineScope
    ): SpaceSeeder = SpaceSeeder(spaceDao, scope).also { it.seed() }
}

/** Memastikan Space default (Pribadi, Kuliah) tersedia sejak DB pertama kali dibuat. */
class SpaceSeeder(
    private val spaceDao: SpaceDao,
    private val scope: CoroutineScope
) {
    fun seed() {
        scope.launch {
            val existing = spaceDao.getSpaceById(1L)
            if (existing == null) {
                spaceDao.insertAll(
                    listOf(
                        SpaceEntity(
                            id = 1L,
                            type = SpaceType.PRIBADI,
                            name = "Pribadi",
                            colorKey = "pastelButter",
                            iconKey = "person"
                        ),
                        SpaceEntity(
                            id = 2L,
                            type = SpaceType.KULIAH,
                            name = "Kuliah",
                            colorKey = "pastelSky",
                            iconKey = "school"
                        )
                    )
                )
            }
        }
    }
}
