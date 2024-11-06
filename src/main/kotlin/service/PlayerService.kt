package service

import entity.player.Difficulty
import entity.player.Player
import entity.tiles.Tile


/**
 * Player action handler service.
 */
class PlayerService(private val rootService: RootService) : ARefreshingService() {
    /**
     * Handles AI behaviour for AI-[Player] turns.
     * This essentially just retrieves a hint for the current AI player with given difficulty and executes it.
     *
     * @param difficulty Player AI difficulty of the current player, [Difficulty.NO_AI] if it's a regular player.
     */
    private fun actionAiTurn(difficulty: Difficulty) {
        val turnOption = rootService.aiService.getTurnSuggestion(difficulty)
        onAllRefreshables { onActionAiTurn(turnOption.tile,turnOption.gridX, turnOption.gridY) }
        actionTakeTile(turnOption.fieldIndex, turnOption.tile)
    }

    /**
     * The current [Player] asks for help for their current turn.
     * Retrieves a turn suggestion from the [AiService] and requests the View-layer to display it.
     */
    fun actionGetHint() {
        val suggestedAction = rootService.aiService.getTurnSuggestion(Difficulty.NO_AI)
        onAllRefreshables {
            onActionGetHint(
                suggestedAction.fieldIndex,
                suggestedAction.tile,
                suggestedAction.gridX,
                suggestedAction.gridY
            )
        }
        rootService.novaLuna.currentGame.currentPlayer.setNoLongerHighScoreEligible()
    }

    /**
     * Reloads the current state from game history, discarding any changes in the process.
     */
    fun discardChanges() {
        rootService.novaLuna.currentGame = rootService.novaLuna.history.getCurrentState()
        rootService.novaLuna.currentGame = rootService.novaLuna.currentGame.deepCopy()
        onAllRefreshables { onGameStateChanged() }
    }

    /**
     * Put specified [Tile] at given position in the current [Player]s grid and re-evaluate their tiles' tasks.
     *
     * @param gridX X-coordinate where the tile will be placed.
     * @param gridY Y-coordinate where the tile will be placed.
     * @param tile The [Tile] to place.
     *
     * @throws IllegalArgumentException For invalid, unavailable, or pre-occupied grid positions.
     */
    fun actionPlaceTile(gridX: Int, gridY: Int, tile: Tile) {
        if (gridX < 0 || gridX > 8 || gridY < 0 || gridY > 8) {
            throw IllegalArgumentException("Grid position ($gridX, $gridY) out of bounds")
        }

        val game = rootService.novaLuna.currentGame
        val player = game.currentPlayer
        if (player.displayArea[gridX][gridY] != null) {
            throw IllegalArgumentException("Grid position ($gridX, $gridY) already occupied")
        }

        var isGridEmpty = true
        for (i in 0..8) {
            for (j in 0..8) {
                if (player.displayArea[i][j] != null) {
                    isGridEmpty = false
                }
            }
        }

        if (!isGridEmpty) {
            val placeableGridTiles = rootService.gameService.getPlaceableGridTiles(player)
            if (!placeableGridTiles.contains(GameService.GridTile(gridX, gridY))) {
                throw IllegalArgumentException("Grid position ($gridX, $gridY) unavailable")
            }
        }

        player.displayArea[gridX][gridY] = tile

        for (i in 0..8) {
            for (j in 0..8) {
                if (player.displayArea[i][j] != null) {
                    rootService.gameService.evaluateTasks(i, j)
                }
            }
        }

        onAllRefreshables { onActionPlaceTile(gridX, gridY, tile) }
    }

    /**
     * Redoing a previously undone action.
     */
    fun actionRedo() {
        rootService.novaLuna.currentGame = rootService.novaLuna.history.getNextState()
        rootService.novaLuna.currentGame = rootService.novaLuna.currentGame.deepCopy()
        onAllRefreshables { onGameStateChanged() }
    }

    /**
     * Refill all [Tile]s on the playing field.
     *
     * @throws IllegalStateException If the tiles may not yet be refilled.
     */
    fun actionRefillTiles() {
        val game = rootService.novaLuna.currentGame
        val fields = game.fields
        val meeplePosition = game.meeplePosition

        if (fields.filterNotNull().size > 2) {
            throw IllegalStateException("Tiles may not be refilled yet")
        }

        for (i in 1..game.fields.size) {
            val fieldIndex = (meeplePosition + i) % game.fields.size

            if (fieldIndex == meeplePosition || game.tileStack.isEmpty()) {
                continue
            }

            if (game.fields[fieldIndex] == null) {
                game.fields[fieldIndex] = game.tileStack.drawTile()
            }
        }

        onAllRefreshables { onActionRefillTiles() }
    }

    /**
     * Take the specified [Tile] from specified index on the playing field.
     *
     * @param fieldIndex Index from which to draw a tile.
     * @param tile The [Tile] to take.
     *
     * @throws IllegalArgumentException For invalid field index or mismatched tile.
     */
    fun actionTakeTile(fieldIndex: Int, tile: Tile) {
        val game = rootService.novaLuna.currentGame

        if (fieldIndex < 0 || fieldIndex >= game.fields.size || game.fields[fieldIndex] != tile) {
            throw IllegalArgumentException("Illegal field index $fieldIndex for given tile $tile!")
        }

        val availableTiles = rootService.aiService.getAvailableTiles()
        val isFieldIndexValid = availableTiles.any { it.fieldIndex == fieldIndex }

        if (isFieldIndexValid) {
            game.fields[fieldIndex] = null
            game.meeplePosition = fieldIndex

            val player = game.currentPlayer
            val oldPosition = player.position % game.moonWheel.size
            val newPosition = (player.position + tile.cost) % game.moonWheel.size

            player.position += tile.cost
            game.moonWheel[oldPosition].remove(player)
            game.moonWheel[newPosition].push(player)

            onAllRefreshables { onActionTakeTile(fieldIndex, tile) }
        } else {
            throw IllegalArgumentException("Field index $fieldIndex too far from meeple position")
        }
    }

    /**
     * The current [Player] wishes to undo the last action.
     */
    fun actionUndo() {
        rootService.novaLuna.currentGame = rootService.novaLuna.history.getPreviousState()

        while (rootService.novaLuna.currentGame.currentPlayer.aiDifficulty != Difficulty.NO_AI) {
            if (!rootService.novaLuna.history.hasPreviousState()) {
                break
            }
            rootService.novaLuna.currentGame = rootService.novaLuna.history.getPreviousState()
        }

        rootService.novaLuna.currentGame = rootService.novaLuna.currentGame.deepCopy()
        rootService.novaLuna.currentGame.players.forEach { it.setNoLongerHighScoreEligible() }
        onAllRefreshables { onGameStateChanged() }
    }

    /**
     * Invoked after a [Player] turn ends.
     */
    fun handleTurnFinished() {

        val game = rootService.novaLuna.currentGame
        val hasTokens = game.currentPlayer.tokenCount > 0
        val isFieldAndStackEmpty = game.fields.all { it == null } && game.tileStack.isEmpty()

        if (!hasTokens || isFieldAndStackEmpty) {
            onAllRefreshables { onGameEnded() }
        } else {
            val playersSortedBySteps = game.players.sortedBy { it.position }
            val playerLeastStepCount = playersSortedBySteps[0]
            val playersEqualStepCount = playersSortedBySteps.filter { it.position == playerLeastStepCount.position }

            var selectedPlayer = playerLeastStepCount
            if (playersEqualStepCount.size > 1) {
                for (stack in game.moonWheel) {
                    if (stack.contains(playerLeastStepCount)) {
                        selectedPlayer = stack.peek()
                    }
                }
            }

            if (game.fields.all { it == null }) {
                actionRefillTiles()
            }

            game.currentPlayer = selectedPlayer


            val prevGame = rootService.novaLuna.currentGame
            rootService.novaLuna.history.pushState(prevGame)
            val copyGame = prevGame.deepCopy()
            rootService.novaLuna.currentGame = copyGame

            onAllRefreshables { onActionFinished() }

            if (game.currentPlayer.aiDifficulty != Difficulty.NO_AI) {
                actionAiTurn(game.currentPlayer.aiDifficulty)
            }
        }
    }
}
