package com.turtlekazu.swipetodismiss.extended.material3

import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Measurable
import androidx.compose.ui.layout.MeasureResult
import androidx.compose.ui.layout.MeasureScope
import androidx.compose.ui.node.LayoutModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.requireLayoutDirection
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import kotlin.math.roundToInt

@Stable
internal fun <T> Modifier.draggableAnchorsV2(
    state: AnchoredDraggableState<T>,
    orientation: Orientation,
    anchors: (size: IntSize, constraints: Constraints) -> Pair<DraggableAnchors<T>, T>,
) = this then DraggableAnchorsElementV2(state, anchors, orientation)

private class DraggableAnchorsElementV2<T>(
    private val state: AnchoredDraggableState<T>,
    private val anchors:
        (size: IntSize, constraints: Constraints) -> Pair<DraggableAnchors<T>, T>,
    private val orientation: Orientation,
) : ModifierNodeElement<DraggableAnchorsNodeV2<T>>() {

    override fun create() = DraggableAnchorsNodeV2(state, anchors, orientation)

    override fun update(node: DraggableAnchorsNodeV2<T>) {
        node.state = state
        node.anchors = anchors
        node.orientation = orientation
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true

        if (other !is DraggableAnchorsElementV2<*>) return false

        if (state != other.state) return false
        if (anchors !== other.anchors) return false
        if (orientation != other.orientation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + anchors.hashCode()
        result = 31 * result + orientation.hashCode()
        return result
    }

    override fun InspectorInfo.inspectableProperties() {
        debugInspectorInfo {
            properties["state"] = state
            properties["anchors"] = anchors
            properties["orientation"] = orientation
        }
    }
}

private class DraggableAnchorsNodeV2<T>(
    var state: AnchoredDraggableState<T>,
    var anchors: (size: IntSize, constraints: Constraints) -> Pair<DraggableAnchors<T>, T>,
    var orientation: Orientation,
) : Modifier.Node(), LayoutModifierNode {
    private var didLookahead: Boolean = false

    override fun onDetach() {
        didLookahead = false
    }

    private val isReverseDirection: Boolean
        get() =
            requireLayoutDirection() == LayoutDirection.Rtl && orientation == Orientation.Horizontal

    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints,
    ): MeasureResult {
        val placeable = measurable.measure(constraints)
        // If we are in a lookahead pass, we only want to update the anchors here and not in
        // post-lookahead. If there is no lookahead happening (!isLookingAhead && !didLookahead),
        // update the anchors in the main pass.
        if (!isLookingAhead || !didLookahead) {
            val size = IntSize(placeable.width, placeable.height)
            val newAnchorResult = anchors(size, constraints)
            state.updateAnchors(newAnchorResult.first, newAnchorResult.second)
        }
        didLookahead = isLookingAhead || didLookahead
        return layout(placeable.width, placeable.height) {
            // In a lookahead pass, we use the position of the current target as this is where any
            // ongoing animations would move. If the component is in a settled state, lookahead
            // and post-lookahead will converge.
            val offset =
                if (isLookingAhead) {
                    state.anchors.positionOf(state.targetValue)
                } else state.requireOffset()
            val rtlModifier = if (isReverseDirection) -1f else 1f
            val xOffset = if (orientation == Orientation.Horizontal) offset * rtlModifier else 0f
            val yOffset = if (orientation == Orientation.Vertical) offset else 0f
            // Tagging as motion frame of reference placement, meaning the placement
            // contains scrolling. This allows the consumer of this placement offset to
            // differentiate this offset vs. offsets from structural changes. Generally
            // speaking, this signals a preference to directly apply changes rather than
            // animating, to avoid a chasing effect to scrolling.
            withMotionFrameOfReferencePlacement {
                placeable.place(xOffset.roundToInt(), yOffset.roundToInt())
            }
        }
    }
}

internal const val ConfirmValueChangeDeprecated =
    "confirmValueChange is deprecated without replacement. Rather than relying on a callback to " +
        "veto state changes, the anchor set should not include disallowed anchors. See " +
        "androidx.compose.foundation.samples.AnchoredDraggableDynamicAnchorsSample for an " +
        "example of using dynamic anchors over confirmValueChange."
