package com.turtlekazu.swipetodismiss.extended.material3

import androidx.annotation.FloatRange
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class SwipeToDismissBoxStateExtended {
    constructor(
        initialValue: SwipeToDismissTarget,
        density: Density,
        positionalThreshold: (totalDistance: Float) -> Float,
        velocityThreshold: Dp? = null,
    ) {
        this.anchoredDraggableState = AnchoredDraggableState(
            initialValue = initialValue,
            confirmValueChange = { true },
            velocityThreshold = {
                with(density) {
                    (velocityThreshold ?: DismissVelocityThreshold).toPx()
                }
            },
            positionalThreshold = positionalThreshold,
            snapAnimationSpec = AnchoredDraggableDefaults.SnapAnimationSpec,
            decayAnimationSpec = AnchoredDraggableDefaults.DecayAnimationSpec,
        )
        this.positionalThreshold = positionalThreshold
    }

    internal val anchoredDraggableState: AnchoredDraggableState<SwipeToDismissTarget>

    internal lateinit var positionalThreshold: (Float) -> Float

    internal val useFlingBehavior: Boolean
        get() = ::positionalThreshold.isInitialized

    internal val offset: Float
        get() = anchoredDraggableState.offset

    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    val currentValue: SwipeToDismissTarget
        get() = anchoredDraggableState.currentValue

    val targetValue: SwipeToDismissTarget
        get() = anchoredDraggableState.targetValue

    val settledValue: SwipeToDismissTarget
        get() = anchoredDraggableState.settledValue

    @get:FloatRange(from = 0.0, to = 1.0)
    @Suppress("Deprecation")
    val progress: Float
        get() = anchoredDraggableState.progress

    val dismissDirection: SwipeToDismissTarget
        get() =
            when {
                offset == 0f || offset.isNaN() -> SwipeToDismissTarget.Settled
                offset > 0f -> SwipeToDismissTarget.StartToEnd
                else -> SwipeToDismissTarget.EndToStart
            }

    suspend fun snapTo(targetValue: SwipeToDismissTarget) {
        anchoredDraggableState.snapTo(targetValue)
    }

    suspend fun reset() =
        anchoredDraggableState.animateTo(targetValue = SwipeToDismissTarget.Settled)

    suspend fun dismiss(direction: SwipeToDismissTarget) {
        anchoredDraggableState.animateTo(targetValue = direction)
    }

    companion object {
        fun Saver(
            positionalThreshold: (totalDistance: Float) -> Float,
            velocityThreshold: Dp? = null,
            density: Density
        ) =
            Saver<SwipeToDismissBoxStateExtended, SwipeToDismissTarget>(
                save = { it.currentValue },
                restore = {
                    SwipeToDismissBoxStateExtended(
                        it,
                        density,
                        positionalThreshold,
                        velocityThreshold ?: DismissVelocityThreshold,
                    )
                },
            )
    }
}

private val DismissVelocityThreshold = 125.dp