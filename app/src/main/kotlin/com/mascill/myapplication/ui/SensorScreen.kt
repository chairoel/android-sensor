package com.mascill.myapplication.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mascill.myapplication.ui.viewmodel.SensorViewModel

@Composable
fun SensorScreen(
    viewModel: SensorViewModel = hiltViewModel()
) {

    val sensor by viewModel.accelerometer.collectAsState()

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text("Accelerometer")

        Spacer(modifier = Modifier.height(16.dp))

        Text("X = ${sensor.x}")
        Text("Y = ${sensor.y}")
        Text("Z = ${sensor.z}")
    }
}