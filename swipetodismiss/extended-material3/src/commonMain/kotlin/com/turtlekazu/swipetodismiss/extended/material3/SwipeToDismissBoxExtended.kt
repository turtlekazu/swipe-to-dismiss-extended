package com.turtlekazu.swipetodismiss.extended.material3

import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun SwipeToDismissBoxExtended(
    state: SwipeToDismissBoxStateExtended,
    backgroundContent: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    gesturesEnabled: Boolean = true,
    onDismiss: (SwipeToDismissBoxValue) -> Unit = {},
    content: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier =
            modifier.anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = Orientation.Horizontal,
                enabled = gesturesEnabled && state.settledValue == SwipeToDismissBoxValue.Settled,
                flingBehavior =
                    if (state.useFlingBehavior)
                        AnchoredDraggableDefaults.flingBehavior(
                            state = state.anchoredDraggableState,
                            positionalThreshold = state.positionalThreshold,
                        )
                    else null,
            ),
        propagateMinConstraints = true,
    ) {
        Row(content = backgroundContent, modifier = Modifier.matchParentSize())
        Row(
            content = content,
            modifier =
                Modifier.draggableAnchorsV2(
                    state.anchoredDraggableState,
                    Orientation.Horizontal
                ) { size,
                    _ ->
                    DraggableAnchors {
                        val width = size.width.toFloat()
                        SwipeToDismissBoxValue.Settled at 0f
                        if (enableDismissFromStartToEnd) {
                            SwipeToDismissBoxValue.StartToEnd at width
                        }
                        if (enableDismissFromEndToStart) {
                            SwipeToDismissBoxValue.EndToStart at -width
                        }
                    } to state.targetValue
                },
        )
    }
    LaunchedEffect(state.settledValue, onDismiss) {
        // Only call when state is settled in a dismissed direction.
        if (state.settledValue != SwipeToDismissBoxValue.Settled) {
            onDismiss(state.dismissDirection)
        }
    }
}

@Composable
fun rememberSwipeToDismissBoxStateExtended(
    initialValue: SwipeToDismissBoxValue = SwipeToDismissBoxValue.Settled,
    positionalThreshold: (totalDistance: Float) -> Float =
        SwipeToDismissBoxDefaults.positionalThreshold,
    velocityThreshold: Dp? = null,
): SwipeToDismissBoxStateExtended {
    val density = LocalDensity.current
    return rememberSaveable(
        saver = SwipeToDismissBoxStateExtended.Saver(
            positionalThreshold = positionalThreshold,
            velocityThreshold = velocityThreshold,
            density
        )
    ) {
        SwipeToDismissBoxStateExtended(
            initialValue,
            density,
            positionalThreshold,
            velocityThreshold,
        )
    }
}
