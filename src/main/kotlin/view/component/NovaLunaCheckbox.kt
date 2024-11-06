package view.component

import tools.aqua.bgw.components.uicomponents.CheckBox
import tools.aqua.bgw.core.*
import tools.aqua.bgw.event.KeyCode
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.Visual

/**
 * Custom checkbox component to support ENTER and SPACE key presses for navigation.
 */
open class NovaLunaCheckbox(
    posX: Number = 0,
    posY: Number = 0,
    width: Number = DEFAULT_CHECKBOX_WIDTH,
    height: Number = DEFAULT_CHECKBOX_HEIGHT,
    text: String = "",
    font: Font = Font(),
    alignment: Alignment = Alignment.CENTER,
    isWrapText: Boolean = false,
    visual: Visual = Visual.EMPTY,
    isChecked: Boolean = false,
    allowIndeterminate: Boolean = false,
    isIndeterminate: Boolean = false
) : CheckBox(
    posX = posX,
    posY = posY,
    width = width,
    height = height,
    text = text,
    font = font,
    alignment = alignment,
    isWrapText = isWrapText,
    visual = visual,
    isChecked = isChecked,
    allowIndeterminate = allowIndeterminate,
    isIndeterminate = isIndeterminate
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
