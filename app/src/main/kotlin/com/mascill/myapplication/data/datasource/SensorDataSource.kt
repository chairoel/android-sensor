package com.mascill.myapplication.data.datasource

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.mascill.myapplication.data.model.AccelerometerData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SensorDataSource @Inject constructor(
    @ApplicationContext context: Context
) {

    private val sensorManager =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun accelerometer(): Flow<AccelerometerData> = callbackFlow {

        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        if (sensor == null) {
            trySend(AccelerometerData.Unavailable)
            close()
            return@callbackFlow
        }

        val listener = object : SensorEventListener {

            override fun onSensorChanged(event: SensorEvent) {

                trySend(
                    AccelerometerData(
                        x = event.values[0],
                        y = event.values[1],
                        z = event.values[2]
                    )
                )
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        sensorManager.registerListener(
            listener,
            sensor,
            SensorManager.SENSOR_DELAY_UI
        )

        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
}