package com.turtlekazu.swipetodismiss.extended.material3

import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.SwipeToDismissBoxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier

@Composable
fun SwipeToDismissBoxExtended(
    state: SwipeToDismissBoxStateExtended,
    backgroundContent: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    gesturesEnabled: Boolean = true,
    onDismiss: (SwipeToDismissBoxValueExtended) -> Unit = {},
    content: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier =
            modifier.anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = Orientation.Horizontal,
                enabled = gesturesEnabled && state.settledValue == SwipeToDismissBoxValueExtended.Settled,
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
                        SwipeToDismissBoxValueExtended.Settled at 0f
                        if (enableDismissFromStartToEnd) {
                            SwipeToDismissBoxValueExtended.StartToEnd at width
                        }
                        if (enableDismissFromEndToStart) {
                            SwipeToDismissBoxValueExtended.EndToStart at -width
                        }
                    } to state.targetValue
                },
        )
    }
    LaunchedEffect(state.settledValue, onDismiss) {
        // Only call when state is settled in a dismissed direction.
        if (state.settledValue != SwipeToDismissBoxValueExtended.Settled) {
            onDismiss(state.dismissDirection)
        }
    }
}

@Composable
fun rememberSwipeToDismissBoxStateExtended(
    initialValue: SwipeToDismissBoxValueExtended = SwipeToDismissBoxValueExtended.Settled,
    positionalThreshold: (totalDistance: Float) -> Float =
        SwipeToDismissBoxDefaults.positionalThreshold,
): SwipeToDismissBoxStateExtended {
    return rememberSaveable(
        saver = SwipeToDismissBoxStateExtended.Saver(positionalThreshold = positionalThreshold)
    ) {
        SwipeToDismissBoxStateExtended(initialValue, positionalThreshold)
    }
}
