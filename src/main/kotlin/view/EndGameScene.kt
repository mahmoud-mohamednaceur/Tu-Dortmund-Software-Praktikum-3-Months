package view

import entity.player.Difficulty
import entity.player.TokenColor
import entity.score.PersonalBest
import service.RootService
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import view.component.NovaLunaButton
import java.util.*

class EndGameScene(private val rootService: RootService) : BoardGameScene(1024, 768), IRefreshable {

    /* =============================================== UI-Elements ================================================ */

    val exit = NovaLunaButton(
        width = 159, height = 61, posX = 432, posY = 653,
        visual = ImageVisual(path = "gui/endgameScene/buttonExit.png")
    )

    val highScoresButton = NovaLunaButton(
        width = 141, height = 49, posX = 792, posY = 50,
        visual = ImageVisual(path = "gui/endgameScene/buttonHighScore.png")
    )

    private val novaLunaIcon = Label(
        width = 166, height = 79, posX = 429, posY = 21,
        visual = ImageVisual(path = "gui/logo.png")
    )

    private val backgroundRectangle = Label(
        width = 842, height = 469, posX = 91, posY = 119,
        visual = ImageVisual(path = "gui/endgameScene/rectangle.png")
    )

    private val winnerIcon = Label(
        width = 97, height = 97, posX = 463, posY = 260,
        visual = ImageVisual(path = "gui/endgameScene/winner.png")
    )

    private val winnerText = Label(
        width = 312, height = 120, posX = 356, posY = 361,
        text = "Der Gewinner ist: \n",
        font = Font(size = 40, family = "Roboto")
    )

    private val winnerName = Label(
        width = 312, height = 60, posX = 356, posY = 441,
        text = "", font = Font(size = 40, family = "Roboto")
    )

    private val winnerScore = Label(
        width = 200, posX = 437, posY = 500,
        text = "Punkte:  ",
        font = Font(size = 40, family = "Roboto")
    )

    private val groupIcon = Label(
        width = 190, height = 60, posX = 417, posY = 165,
        visual = ImageVisual(path = "gui/endgameScene/group.png")
    )

    //Player 1
    private val p1icon = Label(
        width = 63, height = 59, posX = 132, posY = 275,
        visual = getTokenVisual(TokenColor.ORANGE)
    )

    private val p1name = Label(
        posX = 132, posY = 342,
        text = "player1",
        font = Font(size = 20, family = "Roboto", fontWeight = Font.FontWeight.BOLD)
    )

    private val p1score = Label(
        posX = 203, posY = 282,
        text = "0",
        font = Font(size = 35, family = "Roboto", fontWeight = Font.FontWeight.BOLD)
    )

    //Player 2
    private val p2icon = Label(
        width = 63, height = 58, posX = 755, posY = 275,
        visual = getTokenVisual(TokenColor.BLACK)
    )

    private val p2name = Label(
        posX = 755, posY = 342,
        text = "player2",
        font = Font(size = 20, family = "Roboto", fontWeight = Font.FontWeight.BOLD)
    )

    private val p2score = Label(
        posX = 820, posY = 289,
        text = "0",
        font = Font(size = 35, family = "Roboto", fontWeight = Font.FontWeight.BOLD)
    )

    //Player 3
    private val p3icon = Label(
        width = 63, height = 57, posX = 132, posY = 423,
        visual = getTokenVisual(TokenColor.BLUE)
    ).apply {
        this.isVisible = false
    }

    private val p3name = Label(
        posX = 132, posY = 489,
        text = "player3",
        font = Font(size = 20, family = "Roboto", fontWeight = Font.FontWeight.BOLD)
    ).apply {
        this.isVisible = false
    }

    private val p3score = Label(
        posX = 203, posY = 429,
        text = "0",
        font = Font(size = 35, family = "Roboto", fontWeight = Font.FontWeight.BOLD)
    ).apply {
        this.isVisible = false
    }

    //Player 4
    private val p4icon = Label(
        width = 63, height = 59, posX = 755, posY = 423,
        visual = getTokenVisual(TokenColor.WHITE)
    ).apply {
        this.isVisible = false
    }

    private val p4name = Label(
        text = "player4", posX = 755, posY = 489,
        font = Font(size = 20, family = "Roboto", fontWeight = Font.FontWeight.BOLD)
    ).apply {
        this.isVisible = false
    }

    private val p4score = Label(
        posX = 824, posY = 429,
        text = "0",
        font = Font(size = 35, family = "Roboto", fontWeight = Font.FontWeight.BOLD)
    ).apply {
        this.isVisible = false
    }

    /* ================================================= Functions ================================================= */

    fun update() {
        val game = rootService.novaLuna.currentGame
        val players = game.players

        if (players.isEmpty()) throw IllegalArgumentException("Error, not enough players.")
        if (players.size >= 2) {
            p1name.text = players[0].name
            p1score.text = players[0].tokenCount.toString()

            p2name.text = players[1].name
            p2score.text = players[1].tokenCount.toString()
        }
        if (players.size >= 3) {
            p3name.text = players[2].name
            p3score.text = players[2].tokenCount.toString()

            p3name.isVisible = true
            p3icon.isVisible = true
            p3score.isVisible = true
        }
        if (players.size == 4) {
            p4name.text = players[3].name
            p4score.text = players[3].tokenCount.toString()

            p4name.isVisible = true
            p4icon.isVisible = true
            p4score.isVisible = true
        }

        val winner = rootService.novaLuna.currentGame.players.sortedBy { it.tokenCount }[0]
        val score = rootService.gameService.calculatePoints(winner)

        if (winner.isHighScoreEligible && winner.aiDifficulty == Difficulty.NO_AI) {
            rootService.novaLuna.highScoreList.addScore(PersonalBest(winner.name, score, Date().time))
            rootService.fileService.saveHighScores()
        }

        winnerName.text = winner.name
        winnerScore.text += score.toString()
    }

    init {
        background = ColorVisual(228, 207, 169)
        addComponents(
            backgroundRectangle, novaLunaIcon, exit, highScoresButton, winnerIcon,
            p1icon, p2icon, p3icon, p4icon, winnerText, winnerScore, groupIcon,
            p1score, p1name, p2score, p2name, p3score, p3name, p4score, p4name, winnerName
        )
    }

}
