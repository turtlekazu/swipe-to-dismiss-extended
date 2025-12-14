package com.turtlekazu.swipetodismiss.extended.material3

enum class SwipeToDismissTarget {
    Center,

    /** 右に少しずらした位置（メニュー表示など）: +100px */
    SlightRight,

    /** 右に完全にスワイプした状態（削除など）: +Width px */
    Right, // または SwipedRight

    /** 左に少しずらした位置: -100px */
    SlightLeft,

    /** 左に完全にスワイプした状態: -Width px */
    Left // または SwipedLeft
}
