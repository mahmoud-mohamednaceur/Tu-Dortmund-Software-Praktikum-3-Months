package view

import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ImageVisual
import view.component.NovaLunaButton

/**
 * This scene allows to show a pop-up with given information
 *
 * The scene shows the given text and can be closed with a button
 */
class ErrorScene : MenuScene(1024, 768) {

    //Placeholder-String for dynamic error-messages
    var text: String = "Fehler beim Laden des Spiels"

    /* =============================================== UI-Elements ================================================ */

    private val whiteBackgroundLabel: Label = Label(
        width = 600, height = 150, posX = 212, posY = 119,
        visual = ImageVisual("gui/pauseMenuScene/roundedBackground.png")
    )

    var label: Label = Label(
        width = 280, height = 20, posX = 360, posY = 140,
        text = "", font = Font(fontWeight = Font.FontWeight.BOLD), alignment = Alignment.CENTER
    )

    val backButton: NovaLunaButton = NovaLunaButton(
        width = 80, height = 40, posX = 465, posY = 180,
        visual = ImageVisual("gui/game/buttonDiscard.png")
    )

    /* ================================================= Functions ================================================= */

    fun changeText(newText: String) {
        label.text = newText
    }

    init {
        opacity = .0
        addComponents(
            whiteBackgroundLabel,
            label,
            backButton
        )
    }
}