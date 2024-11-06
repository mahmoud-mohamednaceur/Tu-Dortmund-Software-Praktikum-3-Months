package entity.player

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

/**
 * Tests [Player] functions for correct behaviour.
 */
class PlayerTest {
    /* =============================================== Preparation ================================================= */

    private var player = Player("Player", TokenColor.BLACK)

    /**
     * Helper function to (re-)initialise test objects.
     */
    @BeforeEach
    fun prepareTest() {
        player = Player("Player", TokenColor.BLACK)
    }


    /* ================================================ Functions ================================================== */

    /**
     * Tests [Player.tokenCountDecrease]
     */
    @Test
    fun tokenCountDecreaseTest() {
        // Case 1: Isolated decrease
        player.tokenCountDecrease()
        assertEquals(player.tokenCount, 19)

        // Case 2: Decrease until empty
        while (player.tokenCount > 0) {
            player.tokenCountDecrease()
        }
        assertThrows<IllegalStateException> { player.tokenCountDecrease() }
    }

    /**
     * Tests [Player.setNoLongerHighScoreEligible]
     */
    @Test
    fun setNoLongerHighScoreEligibleTest() {
        assertEquals(true, player.isHighScoreEligible)
        player.setNoLongerHighScoreEligible()
        assertEquals(false, player.isHighScoreEligible)
    }
}
