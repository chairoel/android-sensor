package com.mascill.myapplication.data.model

data class AccelerometerData(
    val x: Float,
    val y: Float,
    val z: Float,
    val isAvailable: Boolean = true
) {
    companion object {
        val Unavailable = AccelerometerData(0f, 0f, 0f, isAvailable = false)
    }
}
