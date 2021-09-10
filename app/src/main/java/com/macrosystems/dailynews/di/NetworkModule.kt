package com.macrosystems.dailynews.di

import com.macrosystems.dailynews.data.model.constants.Constants.DISTRICT8_BASE_URL
import com.macrosystems.dailynews.data.network.district8api.District8API
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun providesDistrict8APIInstance(): District8API {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(DISTRICT8_BASE_URL).build()
            .create(District8API::class.java)
    }
}