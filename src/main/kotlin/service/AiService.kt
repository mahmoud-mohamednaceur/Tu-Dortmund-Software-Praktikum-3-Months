package service

import entity.player.Difficulty
import entity.player.Player
import entity.tiles.Tile
import entity.tiles.TileColor


/**
 * Handles and divides AI turns into sub-functions.
 * Called by the [PlayerService] if the new current player is an AI.
 */
class AiService(private val rootService: RootService) : ARefreshingService() {
    /* ============================================ Constant Definitions =========================================== */

    private val MAX_WEIGHT_STEPS = 210
    private val MAX_WEIGHT_TASKS = 40
    
    private val WEIGHT_PER_COMPLETED = 60
    private val WEIGHT_PER_PROGRESS = 5
    private val WEIGHT_PER_STEP = 30


    /* ================================================= Functions ================================================= */

    /**
     * Adds a tile from the given player's grid at given position to given list if possible.
     *
     * @param gridX - Tile X-position in the grid. May be out of bounds.
     * @param gridY - Tile Y-position in the grid. May be out of bounds.
     * @param player - [Player] whose grid is to be accessed.
     * @param list - [ArrayList] of [GameService.GridTile]s to add the tile to.
     */
    private fun addTileSafely(gridX: Int, gridY: Int, player: Player, list: ArrayList<GameService.GridTile>) {
        if (gridX < 0 || gridX > 8 || gridY < 0 || gridY > 8) {
            return
        }

        if (player.displayArea[gridX][gridY] != null) {
            val neighbourTile = GameService.GridTile(
                gridX,
                gridY,
                player.displayArea[gridX][gridY]!!
            )

            if (!list.contains(neighbourTile)) {
                list.add(neighbourTile)
            }
        }
    }

    /**
     * Calculates the weight for given option considering the given player's current tile grid.
     *
     * @param player Reference to a [Player] for access to their tile grid.
     * @param turnOption Option for which to calculate its weight and ideal grid position.
     */
    private fun calculateWeight(player: Player, turnOption: TurnOption) {
        /* Determine weighted grid position from empty (available) fields. */
        val placeableTiles = rootService.gameService.getPlaceableGridTiles(player)
        placeableTiles
            .filter {
                getHasNeighbours(it.gridX, it.gridY, player)
            }
            .forEach {
                val weightAt = calculateWeightOfTasks(turnOption.tile, it.gridX, it.gridY, player)
                if (weightAt >= turnOption.weight) {
                    turnOption.weight = weightAt
                    turnOption.gridX = it.gridX
                    turnOption.gridY = it.gridY
                }
            }

        /* Set center position as target if grid was empty. */
        if (placeableTiles.isEmpty() && player.displayArea[4][4] == null) {
            turnOption.gridX = 4
            turnOption.gridY = 4
        }

        /* Add weight from step count. */
        turnOption.weight += calculateWeightOfSteps(turnOption.tile)
    }

    /**
     * Helper function to calculate the points attributed to the given tile's steps.
     * Fewer steps return more points and thus higher weight. Maximum number of supported steps is seven (7).
     *
     * @param tile [Tile] of which the step-dependent weight needs to be calculated.

     * @return Step weight of given tile, between 0 and 180.
     */
    private fun calculateWeightOfSteps(tile: Tile): Int = (MAX_WEIGHT_STEPS - tile.cost * WEIGHT_PER_STEP)

    /**
     * Helper function to calculate the weight for given tile depending on the number of tasks and their complexity.
     *
     * @param tile The [Tile] of a [TurnOption] whose weight needs to be calculated.
     * @param gridX X-position in the given player's tile grid at which the given tile shall be placed.
     * @param gridY Y-position in the given player's tile grid at which the given tile shall be placed.
     * @param player Reference to the [Player] whose tile grid is to be modified.
     *
     * @return Non-negative task weight of given tile, relative to the number of tasks completing or progressing.
     */
    private fun calculateWeightOfTasks(tile: Tile, gridX: Int, gridY: Int, player: Player): Int {
        /* Total task weight. */
        var taskWeight = 0

        /*
         * Check whether there are any neighbouring tiles requiring this tile. Task completion rewards extra weight.
         */

        /* Get all connected tiles of the same colour and append their neighbours. */
        rootService.gameService.initialiseMarkerGrid(gridX, gridY, player)
        val connectedTiles = rootService.gameService.getConnectedTiles(gridX, gridY, tile.color, player)
        val neighbourTiles = ArrayList<GameService.GridTile>()

        for (gridTile in connectedTiles) {
            addTileSafely(gridTile.gridX - 1, gridTile.gridY, player, neighbourTiles)
            addTileSafely(gridTile.gridX + 1, gridTile.gridY, player, neighbourTiles)
            addTileSafely(gridTile.gridX, gridTile.gridY - 1, player, neighbourTiles)
            addTileSafely(gridTile.gridX, gridTile.gridY + 1, player, neighbourTiles)
        }

        /* Manually append all neighbours (of potentially different colour) relative to given grid position. */
        addTileSafely(gridX - 1, gridY, player, neighbourTiles)
        addTileSafely(gridX + 1, gridY, player, neighbourTiles)
        addTileSafely(gridX, gridY - 1, player, neighbourTiles)
        addTileSafely(gridX, gridY + 1, player, neighbourTiles)

        /* Filter any already-seen tiles and add all others to the list of connected tiles. */
        neighbourTiles.removeIf { connectedTiles.contains(it) }
        connectedTiles.addAll(neighbourTiles)

        /* Check for each of these tiles whether this tile is needed for task progress. Award weight if so. */
        for (gridTile in connectedTiles) {
            for (task in gridTile.tile!!.tasks) {
                /* Skip completed tasks, and tasks which don't require this tile's colour. */
                if (task.isComplete || !task.colors.contains(tile.color)) {
                    continue
                }

                /* Determine if this tile is still required for task progress. */
                val connectedColours = rootService.gameService.getConnectedColors(gridX, gridY, player)
                val requiredColourCount = task.colors.count { it == tile.color }

                if (connectedColours[tile.color]!!.size < requiredColourCount) {
                    /* One tile has smaller impact on larger tasks, hence fewer weight. */
                    taskWeight += (MAX_WEIGHT_TASKS - task.colors.size * WEIGHT_PER_PROGRESS)
                }
                else {
                    continue
                }

                /* The tile is needed for task progress. Check whether it completes this task. */
                var isCompletingTask = true
                for (color in TileColor.values()) {
                    val requiredCount = task.colors.count { it == color }
                    var connectedCount = connectedColours[tile.color]!!.size

                    if (color == tile.color) {
                        connectedCount += 1
                    }

                    if (connectedCount < requiredCount) {
                        isCompletingTask = false
                    }
                }

                /* Award extra weight if this tile completes the task. */
                if (isCompletingTask) {
                    taskWeight += WEIGHT_PER_COMPLETED
                }
            }
        }

        /*
         * Determine which tasks of this tile are completed by pre-existing neighbouring tiles.
         */

        val connectedColours = rootService.gameService.getConnectedColors(gridX, gridY, player)
        for (task in tile.tasks) {
            val taskColours = HashMap<TileColor, Int>()
            for (color in task.colors) {
                taskColours[color]?.plus(1) ?: taskColours.put(color, 1)
            }

            var isTaskComplete = true
            for (colour in TileColor.values()) {
                /* Fail task if the current colour does not have enough neighbouring tiles to complete. */
                if (taskColours.containsKey(colour) && (connectedColours[colour]!!.size < taskColours[colour]!!)) {
                    isTaskComplete = false
                }
            }

            if (isTaskComplete) {
                taskWeight += WEIGHT_PER_COMPLETED
            }
        }

        return taskWeight
    }

    /**
     * Generates a mapping of up to three tiles, available in clockwise order after the meeple.
     */
    fun getAvailableTiles(): ArrayList<TurnOption> {
        val game = rootService.novaLuna.currentGame
        val availableTiles: ArrayList<TurnOption> = ArrayList()

        /* Search the entire field... */
        for (i in 1..11) {
            /* ...starting at the meeple position... */
            val tilePos = (game.meeplePosition + i) % 12
            val tileAt = game.fields[tilePos]

            /* ...picking all encountered tiles... */
            tileAt?.let {
                availableTiles.add(TurnOption(tilePos, tileAt))
            }

            /* ...until at most three tiles have been seen. */
            if (availableTiles.size >= 3) {
                break
            }
        }

        return availableTiles
    }

    /**
     * Determines whether given position in the given player's tile grid has any neighbours.
     *
     * @param gridX Target X-position.
     * @param gridY Target Y-position.
     * @param player Player whose tile grid is queried.
     *
     * @return true if there is at least one neighbour next to given grid position.
     */
    private fun getHasNeighbours(gridX: Int, gridY: Int, player: Player): Boolean {
        return (gridX > 0 && player.displayArea[gridX - 1][gridY] != null) ||
                (gridX < 8 && player.displayArea[gridX + 1][gridY] != null) ||
                (gridY > 0 && player.displayArea[gridX][gridY - 1] != null) ||
                (gridY < 8 && player.displayArea[gridX][gridY + 1] != null)
    }

    /**
     * Generates a data class object holding a suggestion of which tile to pick and where to put it.
     *
     * First calculates all achievable scores for all players for each available tile and subsequently available tiles,
     * marking each tile with integers greater zero. The higher the value, the more likely it will be suggested.
     *
     * @param difficulty Player AI difficulty of the player asking for a suggestion. NO_AI if it is a regular player.
     *
     * @return [TurnOption] data class holding that [Tile] which was considered the best suggestion.
     */
    fun getTurnSuggestion(difficulty: Difficulty): TurnOption {
        /* Get available tiles on the circular field and calculate maximum potential score for each tile. */
        var availableTiles = getAvailableTiles()
        for (turnOption in availableTiles) {
            calculateWeight(rootService.novaLuna.currentGame.currentPlayer, turnOption)
        }

        /* Refill if there are not enough tiles available and if neither option has sufficient weight. */
        if (difficulty == Difficulty.HARD && availableTiles.size < 3) {
            if (availableTiles[0].weight < 240) {
                rootService.playerService.actionRefillTiles()
                availableTiles = getAvailableTiles()

                for (turnOption in availableTiles) {
                    calculateWeight(rootService.novaLuna.currentGame.currentPlayer, turnOption)
                }
            }
        }

        /* Sort in descending order and select an option depending on given AI difficulty. */
        availableTiles.sortByDescending { it.weight }

        var suggestionIndex = when (difficulty) {
            Difficulty.NO_AI -> 0
            Difficulty.EASY -> 2
            Difficulty.MEDIUM -> 1
            Difficulty.HARD -> 0
        }

        if (suggestionIndex >= availableTiles.size) {
            suggestionIndex = availableTiles.size - 1
        }

        return availableTiles[suggestionIndex]
    }


    /* =============================================== Data Classes ================================================ */

    /**
     * Matches contained [Tile] at given field index to a suggested position on the player's tile grid with a weight.
     */
    data class TurnOption(
        val fieldIndex: Int,
        val tile: Tile,
        var weight: Int = 0,
        var gridX: Int = 0,
        var gridY: Int = 0
    )
}
