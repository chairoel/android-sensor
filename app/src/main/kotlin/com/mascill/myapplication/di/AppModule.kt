package com.mascill.myapplication.di

import android.content.Context
import com.mascill.myapplication.data.datasource.SensorDataSource
import com.mascill.myapplication.data.repository.SensorRepository
import com.mascill.myapplication.data.repositoryImpl.SensorRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SensorModule {

    @Provides
    @Singleton
    fun provideSensorDataSource(
        @ApplicationContext context: Context
    ) = SensorDataSource(context)

    @Provides
    @Singleton
    fun provideSensorRepository(
        dataSource: SensorDataSource
    ): SensorRepository {
        return SensorRepositoryImpl(dataSource)
    }
}