package com.mascill.myapplication.data.repositoryImpl

import com.mascill.myapplication.data.datasource.SensorDataSource
import com.mascill.myapplication.data.model.AccelerometerData
import com.mascill.myapplication.data.repository.SensorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorRepositoryImpl @Inject constructor(
    private val dataSource: SensorDataSource
) : SensorRepository {

    override fun accelerometer(): Flow<AccelerometerData> {
        return dataSource.accelerometer()
    }

    override fun linearAcceleration(): Flow<AccelerometerData> {
        return dataSource.linearAcceleration()
    }
}