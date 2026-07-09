package com.mascill.myapplication.di

import com.mascill.myapplication.data.repository.SensorRepository
import com.mascill.myapplication.data.repositoryImpl.SensorRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SensorModule {

    @Binds
    @Singleton
    abstract fun bindSensorRepository(
        impl: SensorRepositoryImpl
    ): SensorRepository
}
