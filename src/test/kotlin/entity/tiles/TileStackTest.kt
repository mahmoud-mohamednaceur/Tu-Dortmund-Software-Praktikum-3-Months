package entity.tiles

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/**
 *
 */
class TileStackTest {
    /* =============================================== Preparation ================================================= */

    private var tileStack = TileStack()
    private var tileBlue = Tile(TileColor.BLUE, 1)
    private var tileCyan = Tile(TileColor.CYAN, 3)

    /**
     * Helper function to (re-)initialise test objects.
     */
    @BeforeEach
    fun prepareTest() {
        tileStack = TileStack()
        tileBlue = Tile(TileColor.BLUE, 1)
        tileCyan = Tile(TileColor.CYAN, 3)
    }


    /* ================================================ Functions ================================================== */


    /**
     * Tests [TileStack.drawTile]
     */
    @Test
    fun drawTileTest() {
        // Case 1: Not enough cards in stack should throw an exception
        assertFailsWith<NoSuchElementException> { tileStack.drawTile() }

        // Case 2: Drawing from filled tile stack
        for (i in 1..67) {
            tileStack.putTile(tileCyan)
        }
        tileStack.putTile(tileBlue)
        assertEquals(68, tileStack.size())
        assertEquals(tileBlue, tileStack.drawTile())
        assertEquals(67, tileStack.size())
        assertEquals(tileCyan, tileStack.drawTile())
    }

    /**
     * Tests [TileStack.isEmpty]
     */
    @Test
    fun isEmptyTest() {
        // Case 1: Call on empty TileStack
        assertEquals(true, tileStack.isEmpty())

        // Case 2: Call on non-empty TileStack
        tileStack.putTile(tileCyan)
        assertEquals(false, tileStack.isEmpty())
    }

    /**
     * Tests [TileStack.putTile]
     */
    @Test
    fun putTileTest() {
        assertEquals(true, tileStack.isEmpty())
        assertThrows<NoSuchElementException> { tileStack.drawTile() }
        tileStack.putTile(tileBlue)
        assertEquals(tileBlue, tileStack.drawTile())
    }

    /**
     * Tests [TileStack.size]
     */
    @Test
    fun sizeTest() {
        assertEquals(true, tileStack.isEmpty())
        assertEquals(0, tileStack.size())
        tileStack.putTile(tileCyan)
        assertEquals(1, tileStack.size())
    }
}
