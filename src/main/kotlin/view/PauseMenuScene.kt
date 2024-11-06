package view

import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.visual.ImageVisual
import view.component.NovaLunaButton


class PauseMenuScene : MenuScene(1024, 768) {

    /* =============================================== UI-Elements ================================================ */

    private val whiteBackgroundLabel: Label = Label(
        width = 720, height = 530, posX = 152, posY = 119,
        visual = ImageVisual("gui/pauseMenuScene/roundedBackground.png")
    )

    private val pauseLabel: Label = Label(
        width = 280, height = 50, posX = 360, posY = 180,
        visual = ImageVisual("gui/pauseMenuScene/pauseLabel.png")
    )

    val saveButton: NovaLunaButton = NovaLunaButton(
        width = 240, height = 67, posX = 385, posY = 299,
        visual = ImageVisual("gui/pauseMenuScene/buttonSaveGame.png")
    )

    val continueButton: NovaLunaButton = NovaLunaButton(
        width = 240, height = 67, posX = 385, posY = 391,
        visual = ImageVisual("gui/pauseMenuScene/buttonResume.png")
    )

    val quitButton: NovaLunaButton = NovaLunaButton(
        width = 240, height = 67, posX = 385, posY = 483,
        visual = ImageVisual("gui/pauseMenuScene/buttonQuit.png")
    )
    
    init {
        opacity = .0
        addComponents(
            whiteBackgroundLabel,
            pauseLabel,
            saveButton,
            continueButton,
            quitButton
        )
    }
}