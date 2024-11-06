package service

import entity.game.Game
import entity.player.Difficulty
import entity.player.Player
import entity.player.TokenColor
import entity.tiles.Task
import entity.tiles.Tile
import entity.tiles.TileColor

/**
 * Handles initialisation and updates of the current [Game] state.
 */
class GameService(private val rootService: RootService) : ARefreshingService() {
    /* ================================================== Fields =================================================== */

    /** Helper-object for [getConnectedTiles]. */
    private val markerGrid = Array(9) { Array(9) { 0 } }


    /* ================================================= Functions ================================================= */

    /**
     * Tests if specified grid position is available and adds it to given list if so.
     */
    private fun addPlaceableGridTile(gridX: Int, gridY: Int, player: Player, availableTiles: ArrayList<GridTile>) {
        if (gridX < 0 || gridX > 8 || gridY < 0 || gridY > 8 || player.displayArea[gridX][gridY] != null) {
            return
        }

        val gridTile = GridTile(gridX, gridY)
        if (!availableTiles.contains(gridTile)) {
            availableTiles.add(gridTile)
        }
    }

    /**
     * Calculates the given player's point score.
     * Points are calculated as the other players' total token count divided by the number of other players.
     *
     * @param player [Player] whose points are to be calculated.
     * @return Player score for given player.
     */
    fun calculatePoints(player: Player): Int {
        val game = rootService.novaLuna.currentGame
        val points = game.players.filter { it != player }.sumOf { it.tokenCount }
        return points / (game.players.size - 1)
    }

    /**
     *  Creates a new [Player] and add them to the game.
     *
     *  @param name Name of the player.
     *  @param color The player's token color.
     *  @param aiDifficulty Difficulty leve if this is an AI player, or [Difficulty.NO_AI] if this is a human player.
     */
    fun createPlayer(name: String, color: TokenColor, aiDifficulty: Difficulty) {
        rootService.novaLuna.currentGame.players.add(Player(name, color, aiDifficulty))
    }

    /**
     * Determines if the [Task]s of a [Tile] are completed.
     *
     * @param gridX The X-position of the tile to be evaluated.
     * @param gridY The Y-position of the tile to be evaluated.
     */
    fun evaluateTasks(gridX: Int, gridY: Int) {
        val game = rootService.novaLuna.currentGame
        val player = game.currentPlayer

        /* Testing each incomplete task for completion. */
        for (task in player.displayArea[gridX][gridY]!!.tasks) {

            if (task.isComplete) {
                continue
            }

            /* Turn the task into an Int-format. */
            val taskColors = Array(TileColor.values().size) { 0 }
            for (color in task.colors) {
                taskColors[color.ordinal] += 1
            }

            initialiseMarkerGrid(gridX, gridY, player)

            /* Determine whether the task is solved. */
            var solved = true
            for (color in TileColor.values()) {
                if (!task.colors.contains(color)) {
                    continue
                }
                val resultArray = getConnectedTiles(gridX, gridY, color, player)
                if (resultArray.size < taskColors[color.ordinal]) {
                    solved = false
                }
            }

            if (solved) {
                task.isComplete = true
                if (player.tokenCount > 0) {
                    player.tokenCount--
                }
            }
        }
    }

    /**
     * Calculate all neighbouring [GridTile]s for each [TileColor] at given [Player] tile grid.
     *
     * @param gridX Tile grid X-position, relative to which connected tiles need to be calculated.
     * @param gridY Tile grid Y-position, relative to which connected tiles need to be calculated.
     * @param player The [Player] whose tile grid is referenced for calculation.
     */
    fun getConnectedColors(gridX: Int, gridY: Int, player: Player): HashMap<TileColor, ArrayList<GridTile>> {
        val result = HashMap<TileColor, ArrayList<GridTile>>()
        for (color in TileColor.values()) {
            result[color] = getConnectedTiles(gridX, gridY, color, player)
        }
        return result
    }

    /**
     * Helper function to [getConnectedTiles] which tries to pick the [GridTile] at given position if applicable.
     */
    private fun getConnectedTile(
        gridX: Int,
        gridY: Int,
        tileColor: TileColor,
        player: Player,
        tiles: ArrayList<GridTile>
    ) {
        if (gridX < 0 || gridX > 8 || gridY < 0 || gridY > 8 || markerGrid[gridX][gridY] == 0) {
            return
        }

        val tileAtGrid = player.displayArea[gridX][gridY]
        if (tileAtGrid == null || tileAtGrid.color != tileColor) {
            return
        }

        markerGrid[gridX][gridY] = 0
        tiles.add(GridTile(gridX, gridY, tileAtGrid))
        tiles.addAll(getConnectedTiles(gridX, gridY, tileColor, player))
    }

    /**
     * Determines how many [Tile]s of given color are connected to the given grid Position.
     *
     * @param gridX X-position of the [Tile].
     * @param gridY Y-position of the [Tile].
     * @param tileColor The [TileColor] we are looking for.
     * @param player The [Player] whose tile grid is to be considered.
     *
     * @return A list of equally-colored [Tile]s connected to the specified grid position.
     */
    fun getConnectedTiles(gridX: Int, gridY: Int, tileColor: TileColor, player: Player): ArrayList<GridTile> {
        val tiles = ArrayList<GridTile>()
        getConnectedTile(gridX - 1, gridY, tileColor, player, tiles)
        getConnectedTile(gridX + 1, gridY, tileColor, player, tiles)
        getConnectedTile(gridX, gridY - 1, tileColor, player, tiles)
        getConnectedTile(gridX, gridY + 1, tileColor, player, tiles)
        return tiles
    }

    /**
     * Returns an array list of grid positions at which a new tile may be placed at.
     * Only considers those tiles neighbouring an occupied tile.
     *
     * @param player The [Player] whose 9x9 display area is to be considered.
     *
     * @return [ArrayList] containing positions of available [GridTile]s, may be empty.
     */
    fun getPlaceableGridTiles(player: Player): ArrayList<GridTile> {
        val placeableTiles = ArrayList<GridTile>()
        for (gridX in 0..8) {
            for (gridY in 0..8) {
                if (player.displayArea[gridX][gridY] == null) {
                    continue
                }

                addPlaceableGridTile(gridX + 1, gridY, player, placeableTiles)
                addPlaceableGridTile(gridX - 1, gridY, player, placeableTiles)
                addPlaceableGridTile(gridX, gridY + 1, player, placeableTiles)
                addPlaceableGridTile(gridX, gridY - 1, player, placeableTiles)
            }
        }
        return placeableTiles
    }

    /**
     * Initialises the current game.
     *
     * Game initialisation involves following steps, in order:
     *  - Initialises token count for all players.
     *  - Loads default tile set.
     *  - Populates field with tiles from loaded tile stack.
     *  - Put players onto moon wheel in (shuffled) order.
     *
     * @param shufflePlayerOrder Shuffles the player order if true, uses default player order otherwise.
     * @param isFirstGame If true, then a reduced number of tokens is used.
     * @param aiTurnTime Number of seconds per AI turn, between four (4) and eight (8).
     */
    fun initialiseGame(shufflePlayerOrder: Boolean, isFirstGame: Boolean, aiTurnTime: Int) {
        val game = rootService.novaLuna.currentGame
        require(game.players.size in 2..4) { "Invalid player count! (${game.players.size})" }
        require(aiTurnTime in 4..8) { "Illegal AI turn duration! ($aiTurnTime)" }

        /* Initialise non-default token count. */
        val tokenCount = if (isFirstGame)
            when (game.players.size) {
                3 -> 17
                4 -> 15
                else -> 20
            }
        else 20
        game.players.forEach { it.tokenCount = tokenCount }

        /* Load (custom) tiles into tile stack. */
        if (!(rootService.novaLuna.currentGame.withLoadedTiles)) {
            rootService.fileService.loadTiles("defaultTiles.csv", true)
            game.tileStack.tiles.shuffle()
        }

        /* Populate playing field with tiles. */
        for (i in 0 until game.fields.size) {
            if (i != game.meeplePosition) {
                game.fields[i] = game.tileStack.drawTile()
            }
        }

        /* Put players in (random) order onto the moon wheel board and set current player. */
        val playerList = if (shufflePlayerOrder) game.players.shuffled() else game.players
        for (i in playerList.size - 1 downTo 0) {
            game.moonWheel[0].push(playerList[i])
        }

        game.currentPlayer = playerList[0]
        onAllRefreshables { onGameStarted() }
        rootService.playerService.handleTurnFinished()
    }

    /**
     * Marks all available tiles as unseen (color-coded) and resets the specified position's entry.
     *
     * @param gridX Seen X-position in given player's tile grid.
     * @param gridY Seen Y-position in given player's tile grid.
     * @param player Reference used to access a [Player]s tile grid (=display area).
     */
    fun initialiseMarkerGrid(gridX: Int, gridY: Int, player: Player) {
        if (gridX < 0 || gridX > 8 || gridY < 0 || gridY > 8) {
            return
        }
        for (i in 0..8) {
            for (j in 0..8) {
                if (player.displayArea[i][j] != null) {
                    markerGrid[i][j] = player.displayArea[i][j]!!.color.ordinal + 1
                } else {
                    markerGrid[i][j] = 0
                }
            }
        }
        markerGrid[gridX][gridY] = 0
    }


    /* =============================================== Data Classes ================================================ */

    /**
     * Bundles X- and Y-positions of the specified [Tile] in a player's tile grid.
     */
    data class GridTile(
        val gridX: Int,
        val gridY: Int,
        val tile: Tile? = null
    )
}
