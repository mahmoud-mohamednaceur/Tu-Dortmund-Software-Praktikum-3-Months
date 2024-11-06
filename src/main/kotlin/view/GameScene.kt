package view

import entity.player.Difficulty
import entity.player.Player
import entity.tiles.Tile
import service.RootService
import tools.aqua.bgw.components.container.LinearLayout
import tools.aqua.bgw.components.gamecomponentviews.TokenView
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.Visual
import view.component.NovaLunaButton
import kotlin.math.*

/**
 * This scene shows the main game board
 *
 * Here is where the game takes place.
 * @param rootService to interact with service-layer
 */

class GameScene(var rootService: RootService) : BoardGameScene(1024, 768), IRefreshable {

    var suggestedTile: Tile? = null
    var suggestedPos: Pair<Int?, Int?> = Pair(null, null)
    val wheelFields: Array<Label?> = Array(12) { null }

    /* =============================================== UI-Elements ================================================ */

    private val gameTokens = mutableListOf<TokenView>()

    private val aiIcon = Label(
        posX = 58, posY = 194, width = 41, height = 41,
        visual = ImageVisual(path = "gui/game/ai.png")
    )

    private val toolBox = Label(
        width = 277, height = 768, posX = 0, posY = 0,
        visual = ColorVisual.WHITE
    )

    private val logoIcon = Label(
        width = 166, height = 89, posX = 54, posY = 24,
        visual = ImageVisual(path = "gui/game/logo.png")
    )

    private val currentToken = Label(
        posX = 58, posY = 156, width = 82, height = 76,
        visual = Visual.EMPTY
    )

    private val currentScore = Label(
        posX = 140, posY = 161, width = 58, height = 59,
        text = "0",
        font = Font(size = 50, family = "Roboto", color = PRIMARY_CLR, fontWeight = Font.FontWeight.BOLD)
    )

    private val currentName = Label(
        posX = 38, posY = 240, width = 200, height = 35,
        text = "-",
        font = Font(size = 30, family = "Roboto", color = PRIMARY_CLR, fontWeight = Font.FontWeight.BOLD)
    )

    private val bgPicture = Label(
        width = 747, height = 768, posX = 277, posY = 0,
        visual = ImageVisual(path = "gui/game/background.png")
    )

    private val wheel = Label(
        width = 516, height = 516, posX = 397, posY = 99,
        visual = ImageVisual(path = "gui/game/wheel.png")
    )

    val pauseBtn = NovaLunaButton(
        posX = 341, posY = 50, width = 42, height = 48,
        visual = ImageVisual(path = "gui/game/buttonPause.png")
    )

    val fixedTokensList: LinearLayout<TokenView> = LinearLayout(
        posX = 525, posY = 695, width = 252, height = 42,
        spacing = 24,
        alignment = Alignment.CENTER
    )

    val highScoreBtn = NovaLunaButton(
        posX = 66, posY = 648, width = 141, height = 49,
        visual = ImageVisual(path = "gui/game/buttonHighScore.png")
    )

    private val fillTilesBtn = NovaLunaButton(
        posX = 59, posY = 413, width = 159, height = 187,
        visual = ImageVisual(path = "gui/game/buttonRefillTiles.png")
    ).apply {
        onActivated = {
            rootService.playerService.actionRefillTiles()
        }
    }

    private val undoBTN = NovaLunaButton(
        posX = 62, posY = 347, width = 42, height = 48,
        visual = ImageVisual(path = "gui/game/buttonsUndoRedo.png", offsetX = 0, offsetY = 0, width = 126, height = 144)
    ).apply {
        onActivated = {
            rootService.playerService.actionUndo()
        }
    }

    private val tippBTN = NovaLunaButton(
        posX = 123, posY = 347, width = 31, height = 48,
        visual = ImageVisual(path = "gui/game/buttonsUndoRedo.png", offsetX = 126, offsetY = 0, width = 93, height = 144)
    ).apply {
        onActivated = {
            rootService.playerService.actionGetHint()
        }
    }

    private val redoBTN = NovaLunaButton(
        posX = 173, posY = 347, width = 42, height = 48,
        visual = ImageVisual(path = "gui/game/buttonsUndoRedo.png", offsetX = 219, offsetY = 0, width = 126, height = 144)
    ).apply {
        onActivated = {
            rootService.playerService.actionRedo()
        }
    }

    val meeple = Label(
        posX = 621, posY = 29, height = 94, width = 74,
        visual = ImageVisual("gui/game/meeple.png", -1, -1)
    )

    /* ================================================= Functions ================================================= */

    /**
     * A [Player] asked for a hint during their current turn.
     *
     * @param fieldIndex Index of the suggested tile to take.
     * @param tile Reference to the [Tile] that was suggested to take.
     * @param gridX X-position on the player's tile grid where the suggested tile should be put.
     * @param gridY Y-position on the player's tile grid where the suggested tile should be put.
     */
    override fun onActionGetHint(fieldIndex: Int, tile: Tile, gridX: Int, gridY: Int) {
        wheelFields[fieldIndex]?.componentStyle = HIGHLIGHT_HINT
        suggestedTile = tile
        suggestedPos = Pair(gridX, gridY)
    }

    /**
     * [Tile]s on the circular playing field have been refilled.
     * At this point, it is unknown whether the action was triggered by a [Player] or automatically.
     */
    override fun onActionRefillTiles() {
        refreshWheel()
    }

    /**
     * An AI-Player sends its choices to be played by View.
     *
     * @param tile Reference to the [Tile] that was suggested to take.
     * @param gridX X-position on the player's tile grid where the suggested tile should be put.
     * @param gridY Y-position on the player's tile grid where the suggested tile should be put.
     */
    override fun onActionAiTurn(tile: Tile, gridX: Int, gridY: Int) {
        suggestedTile = tile
        suggestedPos = Pair(gridX, gridY)
    }

    /**
     * Refreshed all relevant components before showing the scene
     */
    fun refresh() {
        refreshUtilityBtns()
        refreshWheel()
        refreshPlayerInfo()
        moveMeepleTo(rootService.novaLuna.currentGame.meeplePosition)
        refreshGameTokens()
    }

    /**
     * Refreshed game tokens that move on the moonWheel
     */
    private fun refreshGameTokens() {
        gameTokens.forEach { removeComponents(it) }
        drawGameTokens()
    }

    /**
     * Moves the meeple
     *
     * @param index target position on the wheel
     */
    private fun moveMeepleTo(index: Int) {
        meeple.posX = 397 + 258 - 34 + 274 * sin(PI / 6 * index)
        meeple.posY = 99 + 258 - 54 - 274 * cos(PI / 6 * index)
    }

    /**
     * Draw game tokens that move on the moonWheel
     */
    private fun drawGameTokens() {
        rootService.novaLuna.currentGame.moonWheel.forEachIndexed { idx, it ->
            if (it.empty())
                return@forEachIndexed
            it.forEachIndexed { index, player ->
                TokenView(
                    posX = 397 + 258 - 25 + 170 * (sin(PI / 12 * idx)),
                    posY = 99 + 258 - 25 - 170 * (cos(PI / 12 * idx)) - 15 * index,
                    height = 47,
                    width = 50,
                    visual = getTokenVisual(player.tokenColor),
                ).apply {
                    gameTokens.add(this)
                    addComponents(this)
                }
            }
        }
    }

    /**
     * Draw game tokens that are fixed at the bottom of the screen that serve checking other players DisplayArea
     */
    fun drawFixedTokens() {
        fixedTokensList.clear()
        rootService.novaLuna.currentGame.players.forEach {
            TokenView(
                visual = getTokenVisual(it.tokenColor)
            ).apply {
                fixedTokensList.add(this)
            }
        }
    }

    /**
     * Refreshes the currentPlayer information (Name, Color, Tokens left)
     */
    private fun refreshPlayerInfo() {
        val currPlayer = rootService.novaLuna.currentGame.currentPlayer
        currentName.text = currPlayer.name
        currentScore.text = currPlayer.tokenCount.toString()
        currentToken.visual = getTokenVisual(currPlayer.tokenColor)
        aiIcon.isVisible = currPlayer.aiDifficulty != Difficulty.NO_AI
    }

    /**
     * Refreshes utility buttons such as Undo, Redo, Refill
     */
    private fun refreshUtilityBtns() {
        if (rootService.novaLuna.history.hasPreviousState()) {
            undoBTN.opacity = 1.0
            undoBTN.isDisabled = false
        } else {
            undoBTN.opacity = 0.1
            undoBTN.isDisabled = true
        }

        if (rootService.novaLuna.history.hasNextState()) {
            redoBTN.opacity = 1.0
            redoBTN.isDisabled = false
        } else {
            redoBTN.opacity = 0.1
            redoBTN.isDisabled = true
        }

        if (rootService.novaLuna.currentGame.tileStack.isEmpty() ||
            rootService.novaLuna.currentGame.fields.filterNotNull().size > 3) {
            fillTilesBtn.opacity = 0.1
            fillTilesBtn.isDisabled = true
        } else {
            fillTilesBtn.opacity = 1.0
            fillTilesBtn.isDisabled = false
        }
    }

    /**
     * Refreshes all Tiles placed on the moonWheel
     */
    private fun refreshWheel() {
        suggestedTile = null
        suggestedPos = Pair(null, null)
        rootService.novaLuna.currentGame.fields.forEachIndexed { index, tile ->

            if (wheelFields[index] != null)
                removeComponents(wheelFields[index]!!)

            if (tile == null)
                return@forEachIndexed

            val tileCard = Label(
                posX = 397 + 258 - 55 + 267 * sin(PI / 6 * index),
                posY = 99 + 258 - 56 - 267 * cos(PI / 6 * index),
                height = 110,
                width = 110,
                visual = TileViewer(tile).visual,
            )
            tileCard.apply {
                wheelFields[index] = this
                this.rotate(((index % 3.0) / 2).roundToInt() * 30 * (-1.0).pow(index % 3 + 1))
                this@GameScene.addComponents(this)

                isDisabled = true
                onMouseClicked = {
                    lock()
                    rootService.playerService.actionTakeTile(index, tile)
                }
            }
        }

        //enable legal tiles to pick
        val currentGame = rootService.novaLuna.currentGame
        val currentPlayerHuman = currentGame.currentPlayer.aiDifficulty == Difficulty.NO_AI
        val meeplePosition = currentGame.meeplePosition
        var wheelPointer = (meeplePosition + 1) % 12
        var enabledCount = 0

        while ((wheelPointer - meeplePosition) % 12 != 0 && enabledCount < 3) {
            if (wheelFields[wheelPointer] != null) {
                wheelFields[wheelPointer]?.isDisabled = false
                if (currentPlayerHuman) {
                    wheelFields[wheelPointer]?.componentStyle = HIGHLIGHT_AVAILABLE
                }
                enabledCount++
            }
            wheelPointer = (wheelPointer + 1) % 12
        }
    }

    init {
        background = ColorVisual(SECONDARY_CLR)
        addComponents(
            toolBox,
            logoIcon,
            bgPicture,
            wheel,
            fixedTokensList,
            pauseBtn,
            currentToken,
            currentScore,
            currentName,
            highScoreBtn,
            fillTilesBtn,
            undoBTN, tippBTN, redoBTN,
            meeple,
            aiIcon,
        )

        refresh()
        drawFixedTokens()
    }
}