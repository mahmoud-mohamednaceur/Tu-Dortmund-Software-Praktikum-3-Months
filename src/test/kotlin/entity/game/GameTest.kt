package entity.game

import entity.player.Player
import entity.player.TokenColor
import entity.tiles.Task
import entity.tiles.Tile
import entity.tiles.TileColor
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

/**
 * Tests functions in [Game].
 */
class GameTest {
    /* =============================================== Preparation ================================================= */

    private var game = Game()

    private var player1 = Player("Player1", TokenColor.BLACK)
    private var player2 = Player("Player2", TokenColor.BLUE)
    private var player3 = Player("Player3", TokenColor.ORANGE)
    private var player4 = Player("Player4", TokenColor.WHITE)

    private var tileBlue = Tile(TileColor.BLUE, 1)
    private var tileCyan = Tile(TileColor.CYAN, 3, listOf(Task(listOf(TileColor.YELLOW))))
    private var tileRed = Tile(TileColor.RED, 5, listOf(Task(listOf(TileColor.BLUE))))

    /**
     * Helper function to (re-)initialise test objects.
     */
    @BeforeEach
    fun prepareTest() {
        game = Game()
        game.fields[11] = tileBlue
        game.players.add(player1)
        game.players.add(player2)
        game.players.add(player3)
        game.players.add(player4)

        for (i in 1..67) {
            game.tileStack.putTile(tileCyan)
        }

        game.tileStack.putTile(tileRed)
        game.currentPlayer = player3
        game.moonWheel[23].push(player4)
    }

    /* ================================================ Functions ================================================== */

    /**
     * Tests [Game.deepCopy]
     */
    @Test
    fun deepCopyTest() {
        val gameCopy = game.deepCopy()

        // Case 1: game.fields
        assertEquals(12, gameCopy.fields.size)
        for (i in 0..10) {
            assertEquals(null, gameCopy.fields[i])
        }
        assertEquals(tileBlue, gameCopy.fields[11])

        // Case 2: game.players
        assertEquals(4, gameCopy.players.size)
        assertEquals(player1, gameCopy.players[0])
        assertEquals(player2, gameCopy.players[1])

        // Case 3: game.tileStack and game.currentPlayer
        assertEquals(68, gameCopy.tileStack.size())
        assertEquals(tileRed, gameCopy.tileStack.drawTile())
        for (i in 1..67) {
            assertEquals(tileCyan, gameCopy.tileStack.drawTile())
        }
        assertEquals(player3, gameCopy.currentPlayer)

        // Case 5: game.moonWheel
        assertEquals(24, gameCopy.moonWheel.size)
        assertEquals(1, gameCopy.moonWheel[23].size)
        assertEquals(player4, gameCopy.moonWheel[23][0])

        for (i in 0..22) {
            assertEquals(true, gameCopy.moonWheel[i].isEmpty())
        }

        // Case 6: Other fields
        assertEquals(game.playerTurnDurationSeconds, gameCopy.playerTurnDurationSeconds)
        assertEquals(game.meeplePosition, gameCopy.meeplePosition)
    }
}
