package com.turtlekazu.swipetodismiss.extended.material3

import androidx.annotation.FloatRange
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.turtlekazu.swipetodismiss.extended.material3.SwipeToDismissBoxStateExtended.Companion.Saver

enum class SwipeToDismissBoxValueExtended {
    /** Can be dismissed by swiping in the reading direction. */
    StartToEnd,

    StartToEndMiddle,

    /** Can be dismissed by swiping in the reverse of the reading direction. */
    EndToStart,

    EndToStartMiddle,

    /** Cannot currently be dismissed. */
    Settled,
}

class SwipeToDismissBoxStateExtended {
    /**
     * State of the [SwipeToDismissBox] composable.
     *
     * @param initialValue The initial value of the state.
     * @param positionalThreshold The positional threshold to be used when calculating the target
     *   state while a swipe is in progress and when settling after the swipe ends. This is the
     *   distance from the start of a transition. It will be, depending on the direction of the
     *   interaction, added or subtracted from/to the origin offset. It should always be a positive
     *   value.
     */
    constructor(
        initialValue: SwipeToDismissBoxValueExtended,
        positionalThreshold: (totalDistance: Float) -> Float,
    ) {
        this.anchoredDraggableState = AnchoredDraggableState(initialValue)
        this.positionalThreshold = positionalThreshold
    }

    /**
     * State of the [SwipeToDismissBox] composable.
     *
     * @param initialValue The initial value of the state.
     * @param density The density that this state can use to convert values to and from dp.
     * @param confirmValueChange Optional callback invoked to confirm or veto a pending state
     *   change.
     * @param positionalThreshold The positional threshold to be used when calculating the target
     *   state while a swipe is in progress and when settling after the swipe ends. This is the
     *   distance from the start of a transition. It will be, depending on the direction of the
     *   interaction, added or subtracted from/to the origin offset. It should always be a positive
     *   value.
     */
    @Deprecated(
        message = ConfirmValueChangeDeprecated,
        level = DeprecationLevel.WARNING,
        replaceWith =
            ReplaceWith("SwipeToDismissBoxStateExtended(initialValue, density, positionalThreshold)"),
    )
    @Suppress("Deprecation")
    constructor(
        initialValue: SwipeToDismissBoxValueExtended,
        density: Density,
        confirmValueChange: (SwipeToDismissBoxValueExtended) -> Boolean = { true },
        positionalThreshold: (totalDistance: Float) -> Float,
    ) {
        this.anchoredDraggableState =
            AnchoredDraggableState(
                initialValue = initialValue,
                confirmValueChange = confirmValueChange,
                velocityThreshold = { with(density) { DismissVelocityThreshold.toPx() } },
                positionalThreshold = positionalThreshold,
                snapAnimationSpec = AnchoredDraggableDefaults.SnapAnimationSpec,
                decayAnimationSpec = AnchoredDraggableDefaults.DecayAnimationSpec,
            )
    }

    internal val anchoredDraggableState: AnchoredDraggableState<SwipeToDismissBoxValueExtended>

    internal lateinit var positionalThreshold: (Float) -> Float

    internal val useFlingBehavior: Boolean
        get() = ::positionalThreshold.isInitialized

    internal val offset: Float
        get() = anchoredDraggableState.offset

    /**
     * Require the current offset.
     *
     * @throws IllegalStateException If the offset has not been initialized yet
     */
    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    /** The current state value of the [SwipeToDismissBoxState]. */
    val currentValue: SwipeToDismissBoxValueExtended
        get() = anchoredDraggableState.currentValue

    /**
     * The target state. This is the closest state to the current offset (taking into account
     * positional thresholds). If no interactions like animations or drags are in progress, this
     * will be the current state.
     */
    val targetValue: SwipeToDismissBoxValueExtended
        get() = anchoredDraggableState.targetValue

    /**
     * The value the [SwipeToDismissBoxState] is currently settled at. When progressing through
     * multiple anchors, e.g. A -> B -> C, settledValue will stay the same until settled at an
     * anchor, while currentValue will update to the closest anchor.
     */
    val settledValue: SwipeToDismissBoxValueExtended
        get() = anchoredDraggableState.settledValue

    /**
     * The fraction of the progress going from currentValue to targetValue, within [0f..1f] bounds.
     */
    @get:FloatRange(from = 0.0, to = 1.0)
    @Suppress("Deprecation")
    val progress: Float
        get() = anchoredDraggableState.progress

    /**
     * The direction (if any) in which the composable has been or is being dismissed.
     *
     * Use this to change the background of the [SwipeToDismissBox] if you want different actions on
     * each side.
     */
    val dismissDirection: SwipeToDismissBoxValueExtended
        get() =
            when {
                offset == 0f || offset.isNaN() -> SwipeToDismissBoxValueExtended.Settled
                offset > 0f -> SwipeToDismissBoxValueExtended.StartToEnd
                else -> SwipeToDismissBoxValueExtended.EndToStart
            }

    /**
     * Set the state without any animation and suspend until it's set
     *
     * @param targetValue The new target value
     */
    suspend fun snapTo(targetValue: SwipeToDismissBoxValueExtended) {
        anchoredDraggableState.snapTo(targetValue)
    }

    /**
     * Reset the component to the default position with animation and suspend until it if fully
     * reset or animation has been cancelled. This method will throw [CancellationException] if the
     * animation is interrupted
     *
     * @return the reason the reset animation ended
     */
    suspend fun reset() =
        anchoredDraggableState.animateTo(targetValue = SwipeToDismissBoxValueExtended.Settled)

    /**
     * Dismiss the component in the given [direction], with an animation and suspend. This method
     * will throw [CancellationException] if the animation is interrupted
     *
     * @param direction The dismiss direction.
     */
    suspend fun dismiss(direction: SwipeToDismissBoxValueExtended) {
        anchoredDraggableState.animateTo(targetValue = direction)
    }

    companion object {

        /** [Saver] implementation for [SwipeToDismissBoxState]. */
        @Suppress("Deprecation")
        @Deprecated(
            message = ConfirmValueChangeDeprecated,
            level = DeprecationLevel.WARNING,
            replaceWith = ReplaceWith("Saver(positionalThreshold, density)"),
        )
        fun Saver(
            confirmValueChange: (SwipeToDismissBoxValueExtended) -> Boolean,
            positionalThreshold: (totalDistance: Float) -> Float,
            density: Density,
        ) =
            Saver<SwipeToDismissBoxStateExtended, SwipeToDismissBoxValueExtended>(
                save = { it.currentValue },
                restore = {
                    SwipeToDismissBoxStateExtended(
                        it,
                        density,
                        confirmValueChange,
                        positionalThreshold
                    )
                },
            )

        /** The default [Saver] implementation for [SwipeToDismissBoxStateExtended]. */
        fun Saver(positionalThreshold: (totalDistance: Float) -> Float) =
            Saver<SwipeToDismissBoxStateExtended, SwipeToDismissBoxValueExtended>(
                save = { it.currentValue },
                restore = { SwipeToDismissBoxStateExtended(it, positionalThreshold) },
            )
    }
}

private val DismissVelocityThreshold = 125.dp