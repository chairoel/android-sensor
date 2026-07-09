package com.mascill.myapplication.domain.motion

import javax.inject.Inject
import kotlin.math.sqrt

class MotionDetector @Inject constructor() {

    private val window = ArrayDeque<Float>()

    private val movingThreshold = 0.25f
    private val stoppedThreshold = 0.10f

    private val maxSamples = 40

    var isMoving = false
        private set

    fun update(x: Float, y: Float, z: Float): Boolean {
        val magnitude = sqrt(x * x + y * y + z * z)

        if (window.size >= maxSamples) {
            window.removeFirst()
        }

        window.addLast(magnitude)

        val average = window.average().toFloat()

        isMoving = when {
            isMoving && average < stoppedThreshold -> false
            !isMoving && average > movingThreshold -> true
            else -> isMoving
        }

        return isMoving
    }
}
