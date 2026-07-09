package com.mascill.myapplication.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
    val accelerometer by viewModel.accelerometer.collectAsStateWithLifecycle()
    val linearAcceleration by viewModel.linearAcceleration.collectAsStateWithLifecycle()

    SensorContent(
        accelerometer = accelerometer,
        linearAcceleration = linearAcceleration,
        modifier = modifier
    )
}

@Composable
private fun SensorContent(
    accelerometer: AccelerometerData,
    linearAcceleration: AccelerometerData,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Center
    ) {
        SensorSection(
            title = "Accelerometer",
            data = accelerometer
        )

        Spacer(modifier = Modifier.width(32.dp))

        SensorSection(
            title = "Linear Acceleration",
            data = linearAcceleration
        )
    }
}

@Composable
private fun SensorSection(
    title: String,
    data: AccelerometerData
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = title)

        Spacer(modifier = Modifier.height(8.dp))

        if (!data.isAvailable) {
            Text(text = "$title tidak tersedia di perangkat ini")
        } else {
            Text(text = "X: ${"%.2f".format(data.x)}")
            Text(text = "Y: ${"%.2f".format(data.y)}")
            Text(text = "Z: ${"%.2f".format(data.z)}")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorScreenPreview() {
    MyApplicationTheme {
        SensorContent(
            accelerometer = AccelerometerData(0.1f, 0.2f, 9.8f),
            linearAcceleration = AccelerometerData(0.1f, 0.2f, 0.0f)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorUnavailablePreview() {
    MyApplicationTheme {
        SensorContent(
            accelerometer = AccelerometerData.Unavailable,
            linearAcceleration = AccelerometerData.Unavailable
        )
    }
}
