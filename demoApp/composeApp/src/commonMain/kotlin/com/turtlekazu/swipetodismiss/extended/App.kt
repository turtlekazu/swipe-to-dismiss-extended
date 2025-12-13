package com.turtlekazu.swipetodismiss.extended

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.turtlekazu.swipetodismiss.extended.component.ListItem
import com.turtlekazu.swipetodismiss.extended.component.ListItemAddButton
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
@Preview
fun App() {
    MaterialTheme {
        val listItems = mutableStateListOf(
            SampleItem("Item 1"),
            SampleItem("Item 2"),
            SampleItem("Item 3"),
        )

        Box(
            modifier = Modifier
                .safeDrawingPadding()
                .fillMaxSize()
        ) {
            val state = rememberLazyListState()
            LazyColumn(
                state = state,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 30.dp)
            ) {
                items(listItems, key = { it.uuid }) {
                    ListItem(
                        text = it.text,
                        onDelete = {
                            listItems.remove(it)
                        }
                    )
                }

                item {
                    ListItemAddButton(
                        onClick = {
                            listItems.add(SampleItem("Item ${listItems.size + 1}"))
                        }
                    )
                }
            }
        }
    }
}

data class SampleItem @OptIn(ExperimentalUuidApi::class) constructor(
    val text: String,
    val uuid: String = Uuid.random().toString()
)