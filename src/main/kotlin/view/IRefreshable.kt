package view

import entity.game.Game
import entity.player.Player
import entity.tiles.Tile


/**
 * Interface of refreshable event functions.
 * These functions are invoked after their respective action was executed, in order to refresh the view layer's state.
 */
interface IRefreshable {
    /* ================================================= Functions ================================================= */

    /**
     * [Player] action (and hence, their turn) ended.
     */
    fun onActionFinished() {}

    /**
     * A [Player] asked for a hint during their current turn.
     *
     * @param fieldIndex Index of the suggested tile to take.
     * @param tile Reference to the [Tile] that was suggested to take.
     * @param gridX X-position on the player's tile grid where the suggested tile should be put.
     * @param gridY Y-position on the player's tile grid where the suggested tile should be put.
     */
    fun onActionGetHint(fieldIndex: Int, tile: Tile, gridX: Int, gridY: Int) {}

    /**
     * An AI-Player sends its choices to be played by View.
     *
     * @param tile Reference to the [Tile] that was suggested to take.
     * @param gridX X-position on the player's tile grid where the suggested tile should be put.
     * @param gridY Y-position on the player's tile grid where the suggested tile should be put.
     */
    fun onActionAiTurn(tile: Tile, gridX: Int, gridY: Int) {}


    /**
     * The given [Tile] was placed at specified position in the current [Player]'s tile grid.
     */
    fun onActionPlaceTile(gridX: Int, gridY: Int, tile: Tile) {}

    /**
     * [Tile]s on the circular playing field have been refilled.
     * At this point, it is unknown whether the action was triggered by a [Player] or automatically.
     */
    fun onActionRefillTiles() {}

    /**
     * Specified [Tile] was taken off the playing field from given field index.
     */
    fun onActionTakeTile(fieldIndex: Int, tile: Tile) {}

    /**
     * The [Game] terminated after either a [Player] placed all their tokens or if there are no [Tile]s left to draw.
     */
    fun onGameEnded() {}

    /**
     * Current [Game] was initialised and is now starting.
     */
    fun onGameStarted() {}

    /**
     * There was a change in [Game] history after undo or redo.
     */
    fun onGameStateChanged() {}
}