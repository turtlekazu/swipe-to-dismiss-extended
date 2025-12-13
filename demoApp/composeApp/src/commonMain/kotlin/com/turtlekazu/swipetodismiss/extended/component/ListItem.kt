package com.turtlekazu.swipetodismiss.extended.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.turtlekazu.swipetodismiss.extended.material3.SwipeToDismissBoxExtended
import com.turtlekazu.swipetodismiss.extended.material3.SwipeToDismissBoxValueExtended
import com.turtlekazu.swipetodismiss.extended.material3.rememberSwipeToDismissBoxStateExtended
import kotlinx.coroutines.launch

@Composable
fun ListItem(
    text: String,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxStateExtended(
        positionalThreshold = { totalDistance ->
            totalDistance * 0.1f
        }
    )

    SwipeToDismissBoxExtended(
        state = dismissState,
        backgroundContent = {
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValueExtended.EndToStart,
                SwipeToDismissBoxValueExtended.EndToStartMiddle,
                    -> {
                    DismissBackground()
                }

                else -> Unit
            }

        },
        onDismiss = {
            when (it) {
                SwipeToDismissBoxValueExtended.EndToStart -> {
                    onDelete()
                }

                else -> Unit
            }
            scope.launch { dismissState.reset() }
        },
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = modifier
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = modifier.padding(vertical = 12.dp, horizontal = 24.dp)
            )
        }
    }
}

@Composable
fun ListItemAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        Text(
            text = "Add Item",
            color = MaterialTheme.colorScheme.onSecondary,
            modifier = modifier.padding(vertical = 12.dp, horizontal = 24.dp)
        )
    }
}

@Composable
private fun DismissBackground(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.error,
                shape = RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.CenterEnd
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Remove item",
            tint = MaterialTheme.colorScheme.onError,
            modifier = Modifier.padding(end = 20.dp)
        )
    }
}