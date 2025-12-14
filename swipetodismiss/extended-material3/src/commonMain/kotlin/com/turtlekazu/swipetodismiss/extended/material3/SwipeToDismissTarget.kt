package com.turtlekazu.swipetodismiss.extended.material3

interface SwipeToDismissTarget {
    // 基本的な状態（Settledは必須に近い）
    object Settled : SwipeToDismissTarget

    // 標準的な方向（必要なら定義）
    object StartToEnd : SwipeToDismissTarget
    object EndToStart : SwipeToDismissTarget
}
