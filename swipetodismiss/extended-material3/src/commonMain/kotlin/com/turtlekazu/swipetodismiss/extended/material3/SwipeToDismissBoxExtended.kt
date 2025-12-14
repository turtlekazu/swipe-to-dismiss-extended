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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import kotlin.math.abs
import kotlin.math.min

@Composable
fun SwipeToDismissBoxExtended(
    state: SwipeToDismissBoxStateExtended,
    backgroundContent: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    enableDismissFromStartToEnd: Boolean = true,
    enableDismissFromEndToStart: Boolean = true,
    gesturesEnabled: Boolean = true,
    onDismiss: (SwipeToDismissTarget) -> Unit = {},
    content: @Composable RowScope.() -> Unit,
) {
    Box(
        modifier =
            modifier.anchoredDraggable(
                state = state.anchoredDraggableState,
                orientation = Orientation.Horizontal,
                enabled = gesturesEnabled && state.settledValue == SwipeToDismissTarget.Center,
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
                        // width未満になるようにする。
                        val positionalThreshold = min(
                            abs(state.positionalThreshold(width)),
                            width
                        ) - 1f
                        val gap = width - positionalThreshold
                        SwipeToDismissTarget.Center at 0f
                        if (enableDismissFromStartToEnd) {
                            SwipeToDismissTarget.Right at width
                            SwipeToDismissTarget.SlightRight at (width - gap)
                        }
                        if (enableDismissFromEndToStart) {
                            SwipeToDismissTarget.Left at -width
                            SwipeToDismissTarget.SlightLeft at -(width - gap)
                        }
                    } to state.targetValue
                },
        )
    }
    LaunchedEffect(state.settledValue, onDismiss) {
        if (state.settledValue != SwipeToDismissTarget.Center) {
            onDismiss(state.dismissDirection)
        }
    }
}

@Composable
fun rememberSwipeToDismissBoxStateExtended(
    initialValue: SwipeToDismissTarget = SwipeToDismissTarget.Center,
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
