package view

import entity.player.TokenColor
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Color

/**
 * this files serves collecting repeatedly used assets in one place
 */

/**
 * @param color the color of the token
 *
 * @return a visual of the corresponding color
 */
fun getTokenVisual(color: TokenColor) = when (color) {
    TokenColor.BLACK -> ImageVisual(path = "gui/game/tokenBlack.png")
    TokenColor.BLUE -> ImageVisual(path = "gui/game/tokenBlue.png")
    TokenColor.ORANGE -> ImageVisual(path = "gui/game/tokenOrange.png")
    TokenColor.WHITE -> ImageVisual(path = "gui/game/tokenWhite.png")
}


val PRIMARY_CLR = Color(0, 41, 57)
val SECONDARY_CLR = Color(228, 207, 169)

const val HIGHLIGHT_AVAILABLE = "-fx-border-color: mediumspringgreen;-fx-border-width: 4; -fx-border-radius: 30;" +
        "-fx-effect: dropshadow(gaussian, mediumspringgreen, 30, 0.5, 0, 0);"
const val HIGHLIGHT_HINT = "-fx-border-color: gold;-fx-border-width: 8; -fx-border-radius: 30;" +
        "-fx-effect: dropshadow(gaussian, gold, 30, 0.5, 0, 0);"

val NON_CELL_Visual = ImageVisual("gui/tiles/tileNone.png")