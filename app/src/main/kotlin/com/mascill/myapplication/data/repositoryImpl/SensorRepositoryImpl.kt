package com.mascill.myapplication.data.repositoryImpl

import com.mascill.myapplication.data.datasource.SensorDataSource
import com.mascill.myapplication.data.model.AccelerometerData
import com.mascill.myapplication.data.repository.SensorRepository
import kotlinx.coroutines.flow.Flow

class SensorRepositoryImpl(
    private val dataSource: SensorDataSource
) : SensorRepository {

    override fun accelerometer(): Flow<AccelerometerData> {
        return dataSource.accelerometer()
    }
}