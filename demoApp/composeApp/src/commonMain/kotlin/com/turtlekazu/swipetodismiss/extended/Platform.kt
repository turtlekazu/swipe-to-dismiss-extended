package com.turtlekazu.swipetodismiss.extended

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform