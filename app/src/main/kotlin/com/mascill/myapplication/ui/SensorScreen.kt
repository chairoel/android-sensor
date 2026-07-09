package com.mascill.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mascill.myapplication.data.model.AccelerometerData
import com.mascill.myapplication.ui.theme.MyApplicationTheme
import com.mascill.myapplication.ui.viewmodel.SensorViewModel

@Composable
fun SensorScreen(
    modifier: Modifier = Modifier,
    viewModel: SensorViewModel = hiltViewModel()
) {
    val sensor by viewModel.accelerometer.collectAsStateWithLifecycle()

    SensorContent(
        sensor = sensor,
        modifier = modifier
    )
}

@Composable
private fun SensorContent(
    sensor: AccelerometerData,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Accelerometer")

        Spacer(modifier = Modifier.height(16.dp))

        if (!sensor.isAvailable) {
            Text(text = "Accelerometer tidak tersedia di perangkat ini")
        } else {
            Text(text = "X: ${"%.2f".format(sensor.x)}")
            Text(text = "Y: ${"%.2f".format(sensor.y)}")
            Text(text = "Z: ${"%.2f".format(sensor.z)}")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorScreenPreview() {
    MyApplicationTheme {
        SensorContent(sensor = AccelerometerData(1.2f, -0.5f, 9.8f))
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorUnavailablePreview() {
    MyApplicationTheme {
        SensorContent(sensor = AccelerometerData.Unavailable)
    }
}
