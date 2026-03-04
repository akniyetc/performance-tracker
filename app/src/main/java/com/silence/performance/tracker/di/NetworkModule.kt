package com.silence.performance.tracker.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.silence.performance.tracker.content.GifApi
import com.silence.performance.tracker.content.PhotosApi
import com.silence.performance.tracker.content.TracingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
  private const val TIMEOUT = 60L

  @Provides
  @Singleton
  @PhotoRetrofit
  fun provideRetrofit(
    okHttpClient: OkHttpClient,
    json: Json,
  ): Retrofit {
    val contentType = "application/json".toMediaType()
    return Retrofit.Builder()
      .baseUrl("https://api.pexels.com/")
      .client(okHttpClient)
      .addConverterFactory(json.asConverterFactory(contentType))
      .build()
  }

  @Provides
  @Singleton
  @GifRetrofit
  fun provideGifRetrofit(
    @PhotoRetrofit retrofit: Retrofit,
  ): Retrofit {
    return retrofit.newBuilder().baseUrl("https://api.giphy.com/").build()
  }

  @OptIn(ExperimentalSerializationApi::class)
  @Provides
  @Singleton
  fun provideJson(): Json = Json {
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
  }

  @Provides
  @Singleton
  fun provideOkhttp(): OkHttpClient {
    val builder = OkHttpClient.Builder()

    with(builder) {
      addInterceptor(TracingInterceptor())
    }

    return builder
      .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
      .readTimeout(TIMEOUT, TimeUnit.SECONDS)
      .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
      .callTimeout(TIMEOUT, TimeUnit.SECONDS)
      .build()
  }

  @Provides
  @Singleton
  fun providePhotoApi(
    @PhotoRetrofit retrofit: Retrofit,
  ): PhotosApi = retrofit.create<PhotosApi>()

  @Provides
  @Singleton
  fun provideGifApi(
    @GifRetrofit retrofit: Retrofit,
  ): GifApi = retrofit.create<GifApi>()
}

@Qualifier
@MustBeDocumented
annotation class PhotoRetrofit

@Qualifier
@MustBeDocumented
annotation class GifRetrofit