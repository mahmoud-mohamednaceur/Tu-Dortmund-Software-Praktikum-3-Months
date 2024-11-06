package view

import entity.player.Difficulty
import entity.tiles.Tile
import service.RootService
import tools.aqua.bgw.animation.FadeAnimation
import tools.aqua.bgw.animation.MovementAnimation
import tools.aqua.bgw.animation.ParallelAnimation
import tools.aqua.bgw.core.AspectRatio
import tools.aqua.bgw.core.BoardGameApplication
import tools.aqua.bgw.dialog.FileDialogMode
import tools.aqua.bgw.visual.ImageVisual
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


class NovaLunaApplication : BoardGameApplication("Nova Luna", AspectRatio(1.29, 1)), IRefreshable {

    private val rootService = RootService()
    private var playerCount = 0

    private val gameScene = GameScene(rootService)

    private val displayAreaScene = DisplayAreaScene(rootService)

    private val peekScene = PeekScene()

    private val errorScene = ErrorScene().apply {
        backButton.onActivated = {
            this@NovaLunaApplication.hideMenuScene(5)
        }
    }

    private val confirmationScene: ErrorScene = ErrorScene().apply {
        this.label.text = "Karten erfolgreich geladen"

        backButton.width = 140.0
        backButton.posX = 430.0
        backButton.height = 50.0
        backButton.visual = ImageVisual("gui/nameInputScene/buttonStart.png")

        backButton.onActivated = {
            this@NovaLunaApplication.hideMenuScene()
            inputNameScene.initGame()
            rootService.novaLuna.currentGame.withLoadedTiles = false
            showGameScene(gameScene)
        }
    }

    private val startGameScene = StartGameScene().apply {
        if (playerCount == 0) {
            buttonTwoSelected.isVisible = false
            buttonThreeSelected.isVisible = false
            buttonFourSelected.isVisible = false
        }

        startGameButton.onActivated = {
            inputNameScene.initElements()
            this@NovaLunaApplication.showGameScene(inputNameScene)
            if (playerCount == 2) {
                inputNameScene.changeVisibility(3, false)
                inputNameScene.changeVisibility(4, false)
            }
            if (playerCount == 3) {
                inputNameScene.changeVisibility(4, false)
            }
            playerCount = 0
        }

        startGameButton.apply {
            this.isDisabled = (playerCount == 0)
        }

        twoPlayerButton.onActivated = {
            playerCount = 2
            highlightSelection(2)
        }

        threePlayerButton.onActivated = {
            playerCount = 3
            highlightSelection(3)
        }

        fourPlayerButton.onActivated = {
            playerCount = 4
            highlightSelection(4)
        }

        loadGameButton.onActivated = {
            try {
                rootService.fileService.loadGame()
                showGameScene(gameScene)
            } catch (e: IllegalStateException) {
                errorScene.changeText("Spielstand beschädigt")
                this@NovaLunaApplication.showMenuScene(errorScene)
            } catch (e: Exception) {
                errorScene.changeText("Kein Spiel vorhanden")
                this@NovaLunaApplication.showMenuScene(errorScene)
            }
        }

        highScoreButton.onActivated = {
            highscoresScene.update()
            this@NovaLunaApplication.showMenuScene(highscoresScene)
        }
    }

    private val inputNameScene = InputNameScene(rootService).apply {
        val dialog: tools.aqua.bgw.dialog.FileDialog = tools.aqua.bgw.dialog.FileDialog(mode = FileDialogMode.OPEN_FILE)

        startGameButton.apply {
            onActivated = {
                initGame()
                showGameScene(gameScene)
            }
        }

        loadCardButton.apply {
            onActivated = {
                try {
                    val selectedFile = this@NovaLunaApplication.showFileDialog(dialog).get()
                    if (selectedFile.isNotEmpty()) {
                        try {
                            rootService.novaLuna.currentGame.withLoadedTiles = true
                            rootService.fileService.loadTiles(selectedFile[0].path, false)
                            this@NovaLunaApplication.showMenuScene(confirmationScene)
                        } catch (e: ArrayIndexOutOfBoundsException) {
                            rootService.novaLuna.currentGame.withLoadedTiles = true
                            errorScene.changeText("Nur CSV-Dateien erlaubt")
                            this@NovaLunaApplication.showMenuScene(errorScene)
                        }
                    }
                }
                catch (_: NoSuchElementException) {
                    // Silent catch.
                }
            }
        }

        highScoreButton.onActivated = {
            highscoresScene.update()
            this@NovaLunaApplication.showMenuScene(highscoresScene)
        }
    }

    private val pauseMenuScene = PauseMenuScene().apply {
        saveButton.onActivated = {
            rootService.fileService.saveGame()
        }

        continueButton.onActivated = {
            this@NovaLunaApplication.hideMenuScene()
        }

        quitButton.onActivated = {
            hideMenuScene()
            this@NovaLunaApplication.showGameScene(startGameScene)
        }
    }

    private val endGameScene = EndGameScene(rootService).apply {
        exit.onActivated = {
            exit()
        }
        highScoresButton.onActivated = {
            highscoresScene.update()
            showMenuScene(highscoresScene)
        }
    }

    private val highscoresScene = HighscoresScene(rootService).apply {
        backButton.onActivated = { hideMenuScene() }
    }

    init {
        //Adding scenes to service
        listOf(
            this,
            gameScene,
            displayAreaScene,
        ).forEach {
            rootService.gameService.addRefreshable(it)
            rootService.playerService.addRefreshable(it)
        }

        //Setting listeners
        //listeners()

        //Set start Scene for this game
        this.showGameScene(startGameScene)
    }

    /**
     * to wrap up all listeners needed
     */
    private fun listeners() {

        //GameScene Listeners
        gameScene.apply {
            fixedTokensList.forEachIndexed { index, token ->
                token.onMouseClicked = {
                    peekScene.refresh(rootService.novaLuna.currentGame.players[index])
                    showMenuScene(peekScene)
                }
            }

            pauseBtn.onActivated = {
                showMenuScene(pauseMenuScene)
            }

            highScoreBtn.onActivated = {
                showMenuScene(highscoresScene)
            }
        }

        //DisplayAreaScene Listeners
        displayAreaScene.apply {
            highScoreBtn.onActivated = {
                showMenuScene(highscoresScene)
            }
        }

        //PeekScene Listeners
        peekScene.closeBTN.onActivated = {
            hideMenuScene()
        }

    }

    //Implementation of refreshable event functions.
    override fun onActionTakeTile(fieldIndex: Int, tile: Tile) {
        gameScene.apply {
            wheelFields.filterNotNull().forEach { it.componentStyle = "" }
            playAnimation(
                ParallelAnimation(
                    MovementAnimation(
                        wheelFields[fieldIndex]!!,
                        toX = 59,
                        toY = 413,
                        duration = rootService.novaLuna.currentGame.playerTurnDurationSeconds * 500,
                    ),
                    MovementAnimation(
                        meeple,
                        toX = 397 + 258 - 34 + 274 * sin(PI / 6 * fieldIndex),
                        toY = 99 + 258 - 54 - 274 * cos(PI / 6 * fieldIndex),
                        duration = rootService.novaLuna.currentGame.playerTurnDurationSeconds * 500,
                    ),
                    FadeAnimation(
                        wheelFields[fieldIndex]!!,
                        1,
                        0,
                        rootService.novaLuna.currentGame.playerTurnDurationSeconds * 500
                    )
                ).apply {
                    onFinished = {
                        switchToDisplay(tile)
                        gameScene.removeComponents(wheelFields[fieldIndex]!!)
                        wheelFields[fieldIndex] = null
                        unlock()
                    }
                }
            )
        }
    }

    override fun onGameStarted() {
        gameScene.drawFixedTokens()
        gameScene.refresh()
        listeners()
    }

    override fun onGameStateChanged() {
        gameScene.refresh()
        showGameScene(gameScene)
    }

    override fun onActionFinished() {
        gameScene.refresh()
        showGameScene(gameScene)
    }

    override fun onGameEnded() {
        endGameScene.update()
        showGameScene(endGameScene)
    }

    private fun switchToDisplay(tile: Tile) {
        displayAreaScene.chosenTileLabel.visual = TileViewer(tile).visual
        displayAreaScene.chosenTile = tile
        displayAreaScene.refresh()
        showGameScene(displayAreaScene)

        val isCurrentAi = rootService.novaLuna.currentGame.currentPlayer.aiDifficulty != Difficulty.NO_AI
        if (isCurrentAi
            && gameScene.suggestedPos != Pair(null, null)
            && gameScene.suggestedTile != null){
            val posX = gameScene.suggestedPos.first!!
            val posY = gameScene.suggestedPos.second!!
            rootService.playerService.actionPlaceTile(posX, posY, gameScene.suggestedTile!!)
        } else if (gameScene.suggestedTile == tile) {
            val posX = gameScene.suggestedPos.first!!
            val posY = gameScene.suggestedPos.second!!
            displayAreaScene.displayGrid[posX][posY]!!.componentStyle = HIGHLIGHT_HINT
        }
    }
}
