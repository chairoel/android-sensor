package com.mascill.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mascill.myapplication.ui.theme.MyApplicationTheme
import com.mascill.myapplication.ui.viewmodel.SensorViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AccelerometerScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AccelerometerScreen(
    modifier: Modifier = Modifier,
    viewModel: SensorViewModel = hiltViewModel()
) {
    val data by viewModel.accelerometer.collectAsStateWithLifecycle()

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Accelerometer")
        Text(text = "X: ${"%.2f".format(data.x)}")
        Text(text = "Y: ${"%.2f".format(data.y)}")
        Text(text = "Z: ${"%.2f".format(data.z)}")
    }
}

@Preview(showBackground = true)
@Composable
fun AccelerometerPreview() {
    MyApplicationTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Accelerometer")
            Text(text = "X: 0.00")
            Text(text = "Y: 0.00")
            Text(text = "Z: 0.00")
        }
    }
}
