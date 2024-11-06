package view

import entity.player.Player
import entity.tiles.Tile
import service.RootService
import tools.aqua.bgw.animation.FadeAnimation
import tools.aqua.bgw.animation.MovementAnimation
import tools.aqua.bgw.animation.ParallelAnimation
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.CompoundVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.Visual
import view.component.NovaLunaButton
import java.awt.Color

/**
 * This scene shows the [displayGrid] of a [entity.player]
 *
 * The player places his tiles at his board in his turn
 * @param rootService to interact with service-layer
 */

class DisplayAreaScene(val rootService: RootService) : BoardGameScene(1024, 768), IRefreshable {

    var displayGrid: Array<Array<Label?>> = Array(9) { Array(9) { null } }
    var chosenTile: Tile? = null

    /* =============================================== UI-Elements ================================================ */

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
        text = "12",
        font = Font(size = 50, family = "Roboto", color = Color(0, 41, 57), fontWeight = Font.FontWeight.BOLD)
    )

    private val currentPlayer = Label(
        posX = 38, posY = 240, width = 200, height = 35,
        text = "Maximilian",
        font = Font(size = 30, family = "Roboto", color = Color(0, 41, 57), fontWeight = Font.FontWeight.BOLD)
    )

    private val bgPicture = Label(
        width = 747, height = 768, posX = 277, posY = 0,
        visual = ImageVisual(path = "gui/game/background.png")
    )

    val highScoreBtn = NovaLunaButton(
        posX = 66, posY = 648, width = 141, height = 49,
        visual = ImageVisual(path = "gui/game/buttonHighScore.png")
    )

    val chosenTileLabel = Label(
        posX = 69, posY = 425, width = 139, height = 139,
        visual = Visual.EMPTY
    )

    private val discardBTN = NovaLunaButton(
        posX = 78, posY = 360, width = 117, height = 49,
        visual = ImageVisual(path = "gui/game/buttonDiscard.png")
    ).apply {
        onActivated = {
            rootService.playerService.discardChanges()
        }
    }

    /* ================================================= Functions ================================================= */

    /**
     * Updates the displayed player and tiles
     */
    fun refresh() {
        refreshPlayerInfo()
        refreshPlacedTiles()
    }

    /**
     * The given [Tile] was placed at specified position in the current [Player]'s tile grid.
     */
    override fun onActionPlaceTile(gridX: Int, gridY: Int, tile: Tile) {
        val placedLabel = displayGrid[gridX][gridY]
            .apply {
                this!!.visual = TileViewer(tile).visual
                isDisabled = true
            }
        playAnimation(
            ParallelAnimation(
                MovementAnimation(
                    placedLabel!!,
                    fromX = 80,
                    fromY = 430,
                    duration = rootService.novaLuna.currentGame.playerTurnDurationSeconds * 500
                ),
                FadeAnimation(
                    placedLabel,
                    fromOpacity = 0,
                    toOpacity = 1,
                    duration = rootService.novaLuna.currentGame.playerTurnDurationSeconds * 500
                )
            ).apply {
                onFinished = {
                    rootService.playerService.handleTurnFinished()
                    unlock()
                }
            }
        )
        displayGrid[gridX][gridY]?.visual = TileViewer(tile).visual
    }

    /**
     * Draws an empty grid
     */
    private fun drawGrid() {
        for (col in 0..8) {
            for (raw in 0..8) {
                Label(
                    posX = 312 + 2 + 75 * col, posY = 42 + 2 + 75 * raw, height = 80, width = 80,
                    visual = NON_CELL_Visual,
                ).apply {
                    addComponents(this)
                    displayGrid[col][raw] = this
                    isDisabled = false
                    onMouseClicked = {
                        lock()
                        rootService.playerService.actionPlaceTile(col, raw, chosenTile!!)
                    }
                }
            }
        }
    }

    /**
     * Refreshes all placed and placeable tiles
     */
    private fun refreshPlacedTiles() {
        val currPlayer = rootService.novaLuna.currentGame.currentPlayer
        val placeables = rootService.gameService.getPlaceableGridTiles(currPlayer)

        rootService.novaLuna.currentGame.currentPlayer.displayArea.forEachIndexed { column, arrayOfTiles ->
            arrayOfTiles.forEachIndexed { raw, tile ->

                displayGrid[column][raw]!!.apply {
                    isDisabled = true
                    componentStyle = ""
                    if (tile != null) {
                        visual = TileViewer(tile).visual
                        opacity = 1.0
                    } else {
                        visual = CompoundVisual(NON_CELL_Visual)
                        if (placeables.size > 0) {
                            opacity = 0.2
                        } else {
                            opacity = 1.0
                            isDisabled = false
                        }
                    }
                }
            }
        }

        placeables.forEach {
            displayGrid[it.gridX][it.gridY]!!.apply {
                opacity = 1.0
                isDisabled = false
            }
        }
    }

    /**
     * Refreshes the currentPlayer information (Name, Color, Tokens left)
     */
    private fun refreshPlayerInfo() {
        val currPlayer = rootService.novaLuna.currentGame.currentPlayer
        currentPlayer.text = currPlayer.name
        currentScore.text = currPlayer.tokenCount.toString()
        currentToken.visual = getTokenVisual(currPlayer.tokenColor)
    }

    init {
        background = ColorVisual(SECONDARY_CLR)
        addComponents(
            toolBox,
            logoIcon,
            bgPicture,
            currentToken,
            currentScore,
            currentPlayer,
            highScoreBtn,
            chosenTileLabel,
            discardBTN
        )

        drawGrid()
        refresh()
    }
}