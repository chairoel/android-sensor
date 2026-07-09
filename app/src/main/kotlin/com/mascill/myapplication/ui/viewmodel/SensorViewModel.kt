package com.mascill.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mascill.myapplication.data.model.AccelerometerData
import com.mascill.myapplication.data.repository.SensorRepository
import com.mascill.myapplication.domain.motion.MotionDetector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(
    repository: SensorRepository,
    private val motionDetector: MotionDetector
) : ViewModel() {

    val accelerometer: StateFlow<AccelerometerData> = repository
        .accelerometer()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AccelerometerData.Unavailable
        )

    val linearAcceleration: StateFlow<AccelerometerData> = repository
        .linearAcceleration()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AccelerometerData.Unavailable
        )

    val isMoving: StateFlow<Boolean> = linearAcceleration
        .map { data ->
            if (!data.isAvailable) {
                false
            } else {
                motionDetector.update(data.x, data.y, data.z)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )
}
