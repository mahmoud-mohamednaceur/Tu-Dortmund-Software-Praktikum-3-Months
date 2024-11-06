package entity.game

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Tests the isolated [GameHistory] object for correct behaviour of its functions.
 */
class GameHistoryTest {
    /* =============================================== Preparation ================================================= */

    private var history = GameHistory()
    private var gameA = Game()
    private var gameB = Game()
    private var gameC = Game()

    /**
     * Helper function to (re-)initialise test objects.
     */
    @BeforeEach
    fun prepareTest() {
        history = GameHistory()
        gameA = Game()
        gameB = Game()
        gameC = Game()
    }


    /* ================================================ Functions ================================================== */

    /**
     * Tests [GameHistory.getPreviousState]
     */
    @Test
    fun getPreviousStateTest() {
        // Case 1: No game history yet
        assertThrows<NoSuchElementException> { history.getPreviousState() }

        // Case 2: Still at the beginning
        history.pushState(gameA)
        assertThrows<NoSuchElementException> { history.getPreviousState() }

        // Case 3: (WLOG) 3 GameStates to switch back to them
        history.pushState(gameB)
        history.pushState(gameC)
        assertEquals(gameB, history.getPreviousState())
        assertEquals(gameA, history.getPreviousState())
        assertThrows<NoSuchElementException> { history.getPreviousState() }
    }

    /**
     * Tests [GameHistory.getNextState]
     */
    @Test
    fun getNextStateTest() {
        // Case 1: No Game History yet
        assertThrows<NoSuchElementException> { history.getNextState() }

        // Case 2: (WLOG) 3 GameStates to switch between
        history.pushState(gameA)
        history.pushState(gameB)
        history.pushState(gameC)
        assertThrows<NoSuchElementException> { history.getNextState() }
        assertEquals(gameB, history.getPreviousState())
        assertEquals(gameA, history.getPreviousState())
        assertEquals(gameB, history.getNextState())
        assertEquals(gameC, history.getNextState())
    }

    /**
     * Tests [GameHistory.hasNextState]
     */
    @Test
    fun hasNextStateTest() {
        // Case 1: Empty history
        assertEquals(false, history.hasNextState())

        // Case 2: History with most recent element
        history.pushState(gameA)
        assertEquals(false, history.hasNextState())

        // Case 3: History at older element
        history.pushState(gameB)
        assertEquals(gameA, history.getPreviousState())
        assertEquals(true, history.hasNextState())
    }

    /**
     * Tests [GameHistory.hasPreviousState]
     */
    @Test
    fun hasPreviousStateTest() {
        // Case 1: Empty history
        assertEquals(false, history.hasPreviousState())

        // Case 2: History with one element
        history.pushState(gameA)
        assertEquals(false, history.hasPreviousState())

        // Case 3: History with multiple elements
        history.pushState(gameB)
        assertEquals(true, history.hasPreviousState())

        // Case 4: History at oldest element
        history.pushState(gameC)
        assertEquals(gameB, history.getPreviousState())
        assertEquals(gameA, history.getPreviousState())
        assertEquals(false, history.hasPreviousState())
    }

    /**
     * Tests [GameHistory.pushState]
     */
    @Test
    fun pushStateTest() {
        // Case 1: Pushing to empty History
        history.pushState(gameA)
        history.pushState(gameB)
        assertEquals(gameA, history.getPreviousState())

        // Case 2: Pushing to override an existing State
        history.pushState(gameC)
        assertThrows<NoSuchElementException> { history.getNextState() }
        assertEquals(gameA, history.getPreviousState())
        assertEquals(gameC, history.getNextState())
    }
}
