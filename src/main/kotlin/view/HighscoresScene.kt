package view

import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import view.component.NovaLunaButton
import java.io.IOException

class HighscoresScene(private val rootService: RootService) : MenuScene(1024, 768), IRefreshable {

    /* =============================================== UI-Elements ================================================ */

    private val novaLunaIcon = Label(
        width = 166, height = 79, posX = 429, posY = 21,
        visual = ImageVisual(path = "gui/logo.png")
    )

    private val listIcon = Label(
        width = 215, height = 60, posX = 91, posY = 29,
        visual = ImageVisual(path = "gui/highscoresScene/list.png")
    )

    val backButton = NovaLunaButton(
        width = 135, height = 61, posX = 445, posY = 690,
        visual = ImageVisual(path = "gui/highscoresScene/buttonBack.png")
    )

    private val rectangle = Label(
        width = 842, height = 582, posX = 91, posY = 100,
        visual = ImageVisual(path = "gui/highscoresScene/rectangle.png")
    )

    private val winner = Label(
        width = 63, height = 57, posX = 140, posY = 150,
        visual = ImageVisual(path = "gui/highscoresScene/crown.png")
    )


    private val scores = Label(
        width = 842, height = 582, posX = 120, posY = 135, alignment = Alignment.TOP_CENTER,
        font = Font(family = "Roboto", size = 30, fontWeight = Font.FontWeight.BOLD)
    )

    /* ================================================= Functions ================================================= */

    fun update() {
        try {
            rootService.fileService.loadHighScores()
            scores.text = rootService.novaLuna.highScoreList.toString()
        }
        catch (_: IOException) {
            scores.text = "No high scores available."
        }
    }

    override fun onGameEnded() {
        update()
    }

    override fun onGameStarted() {
        update()
    }

    init {
        scores.text = rootService.novaLuna.highScoreList.toString()
        background = ColorVisual(228, 207, 169)
        addComponents(novaLunaIcon, listIcon, backButton, rectangle, winner, scores)
    }
}
