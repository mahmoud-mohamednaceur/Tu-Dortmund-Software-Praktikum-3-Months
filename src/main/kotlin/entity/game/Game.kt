package entity.game

import com.google.gson.Gson
import entity.player.Player
import entity.player.TokenColor
import entity.tiles.Tile
import entity.tiles.TileStack
import java.util.*

/**
 * Contains current game state data, such as participating players and things on the table.
 */
class Game {
    /* ================================================== Fields =================================================== */

    val INVALID_PLAYER = Player("<NULL>", TokenColor.BLACK)

    val fields: Array<Tile?> = arrayOfNulls(12)
    val players: ArrayList<Player> = arrayListOf()
    val tileStack: TileStack = TileStack()

    var playerTurnDurationSeconds: Int = 4
    var currentPlayer: Player = INVALID_PLAYER
    var meeplePosition: Int = 0
    var moonWheel: Array<Stack<Player>> = Array(24) { Stack<Player>() }
    var withLoadedTiles : Boolean = false


    /* ================================================ Functions ================================================== */

    /**
     * Creates a deep copy of this [Game] using its GSON-representation.
     * Also recreates references to [Player] objects in the [moonWheel] and [currentPlayer] object.
     *
     * @return Deep copy of this game object.
     */
    fun deepCopy(): Game {
        val json = Gson().toJson(this)
        val gameCopy = Gson().fromJson(json, Game::class.java)

        /* Recreate references from the moon wheel. */
        for (i in moonWheel.indices) {
            if (moonWheel[i].isEmpty()) {
                continue
            }
            for (j in moonWheel[i].indices) {
                val player = moonWheel[i][j]
                val playerIndex = players.indexOf(player)
                if (playerIndex < 0) {
                    continue
                }
                gameCopy.moonWheel[i][j] = gameCopy.players[playerIndex]
                if (player == currentPlayer) {
                    gameCopy.currentPlayer = gameCopy.players[playerIndex]
                }
            }
        }
        return gameCopy
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Game

        if (INVALID_PLAYER != other.INVALID_PLAYER) return false
        if (!fields.contentEquals(other.fields)) return false
        if (players != other.players) return false
        if (tileStack != other.tileStack) return false
        if (playerTurnDurationSeconds != other.playerTurnDurationSeconds) return false
        if (currentPlayer != other.currentPlayer) return false
        if (meeplePosition != other.meeplePosition) return false
        if (!moonWheel.contentEquals(other.moonWheel)) return false
        if (withLoadedTiles != other.withLoadedTiles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = INVALID_PLAYER.hashCode()
        result = 31 * result + fields.contentHashCode()
        result = 31 * result + players.hashCode()
        result = 31 * result + tileStack.hashCode()
        result = 31 * result + playerTurnDurationSeconds
        result = 31 * result + currentPlayer.hashCode()
        result = 31 * result + meeplePosition
        result = 31 * result + moonWheel.contentHashCode()
        return result
    }
}
