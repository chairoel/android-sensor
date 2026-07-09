package com.mascill.myapplication.data.repository

import com.mascill.myapplication.data.model.AccelerometerData
import kotlinx.coroutines.flow.Flow

interface SensorRepository {

    fun accelerometer(): Flow<AccelerometerData>
}