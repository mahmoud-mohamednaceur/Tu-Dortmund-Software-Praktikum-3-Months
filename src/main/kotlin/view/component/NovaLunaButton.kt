package view.component

import tools.aqua.bgw.components.uicomponents.Button
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.DEFAULT_BUTTON_HEIGHT
import tools.aqua.bgw.core.DEFAULT_BUTTON_WIDTH
import tools.aqua.bgw.event.KeyCode
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.Visual

/**
 * Custom button component to support ENTER and SPACE key presses for navigation.
 */
open class NovaLunaButton(
    posX: Number = 0,
    posY: Number = 0,
    width: Number = DEFAULT_BUTTON_WIDTH,
    height: Number = DEFAULT_BUTTON_HEIGHT,
    text: String = "",
    font: Font = Font(),
    alignment: Alignment = Alignment.CENTER,
    isWrapText: Boolean = false,
    visual: Visual = ColorVisual.WHITE
) : Button(
    posX = posX,
    posY = posY,
    width = width,
    height = height,
    text = text,
    font = font,
    alignment = alignment,
    isWrapText = isWrapText,
    visual = visual
) {
    init {
        onKeyReleased = {
            if (it.keyCode == KeyCode.ENTER || it.keyCode == KeyCode.SPACE) {
                onActivated?.invoke()
            }
        }
        onMouseClicked = {
            onActivated?.invoke()
        }
    }

    var onActivated: (() -> Unit)? = null
}
