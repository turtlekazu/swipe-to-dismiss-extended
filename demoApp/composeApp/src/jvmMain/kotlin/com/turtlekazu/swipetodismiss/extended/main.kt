package com.turtlekazu.swipetodismiss.extended

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "swipe-to-dismiss-extended",
    ) {
        App()
    }
}