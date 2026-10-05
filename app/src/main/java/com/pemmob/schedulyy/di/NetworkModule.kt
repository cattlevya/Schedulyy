package com.pemmob.schedulyy.di

import com.pemmob.schedulyy.data.remote.gemini.GeminiRestClientImpl
import com.pemmob.schedulyy.data.remote.gemini.RedactingLoggingInterceptor
import com.pemmob.schedulyy.domain.remote.GeminiClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .addInterceptor(RedactingLoggingInterceptor())
            .build()
    }

    @Provides
    @Singleton
    fun provideGeminiClient(
        impl: GeminiRestClientImpl
    ): GeminiClient = impl
}
