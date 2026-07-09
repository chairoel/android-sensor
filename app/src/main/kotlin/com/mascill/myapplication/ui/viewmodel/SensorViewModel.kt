package com.mascill.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mascill.myapplication.data.model.AccelerometerData
import com.mascill.myapplication.data.repository.SensorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SensorViewModel @Inject constructor(
    repository: SensorRepository
) : ViewModel() {

    val accelerometer: StateFlow<AccelerometerData> = repository
        .accelerometer()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AccelerometerData.Unavailable
        )
}
