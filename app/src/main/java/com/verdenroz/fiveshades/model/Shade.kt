package com.verdenroz.fiveshades.model

enum class Shade {
    RED,
    YELLOW,
    BLUE,
    GREEN,
    PURPLE;

    companion object {
        fun next(current: Shade): Shade {
            val values = Shade.entries.toTypedArray()
            val nextIndex = (current.ordinal + 1) % values.size
            return values[nextIndex]
        }

        fun previous(current: Shade): Shade {
            val values = Shade.entries.toTypedArray()
            val prevIndex = (current.ordinal - 1 + values.size) % values.size
            return values[prevIndex]
        }
    }
}