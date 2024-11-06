package entity.tiles

import entity.player.TokenColor


/**
 * Color types for Nova Luna field tiles. Not to be confused with [TokenColor]!
 */
enum class TileColor {
    BLUE,
    CYAN,
    RED,
    YELLOW;

    override fun toString() = when (this) {
        BLUE -> "blue"
        CYAN -> "cyan"
        RED -> "red"
        YELLOW -> "yellow"
    }

    companion object {
        /**
         * A helper method to return a [TileColor] entity from a char.
         * @returns the enum, corresponding to the char.
         */
        fun fromChar(colorName: Char): TileColor {
            return when {
                colorName.lowercaseChar() == 'b' -> {
                    BLUE
                }
                colorName.lowercaseChar() == 'r' -> {
                    RED
                }
                colorName.lowercaseChar() == 'y' -> {
                    YELLOW
                }
                colorName.lowercaseChar() == 'c' -> {
                    CYAN
                }
                else -> throw IllegalArgumentException("Unsupported tile colour. Please purchase an extension pack.")
            }
        }
    }
}
