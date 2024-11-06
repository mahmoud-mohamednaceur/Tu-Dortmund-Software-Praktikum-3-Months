package entity.score

import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals

/**
 * Testing the combination of [HighScoreList] and [PersonalBest].
 */
class HighScoreListTest {
    /* =============================================== Preparation ================================================= */

    private var highScoreList = HighScoreList()
    private var score1 = PersonalBest("Player1", 42, Date().time)
    private var score2 = PersonalBest("Player2", 69, Date().time)


    /* ================================================ Functions ================================================== */

    /**
     * Tests only the entity part of the high score list.
     */
    @Test
    fun addScoreTest() {
        // Case 1: Empty, uninitialised list.
        assert(highScoreList.entries.isEmpty())

        // Case 2: Two scores in descending order.
        highScoreList.addScore(score1)
        highScoreList.addScore(score2)
        assertEquals(2, highScoreList.entries.size)
        assertEquals(score1, highScoreList.entries[1])
        assertEquals(score2, highScoreList.entries[0])
        assert(highScoreList.entries[0].score > highScoreList.entries[1].score)
    }
}
