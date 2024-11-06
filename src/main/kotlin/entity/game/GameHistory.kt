package entity.game

import java.util.*
import kotlin.NoSuchElementException


/**
 * Manages a history of [Game] copies, each saving game state after certain events for un- and redoing.
 */
class GameHistory
{
    /* ================================================== Fields =================================================== */

    /** The (i+1)-th index of the current game state in this history's list of [gameStates]. Zero for empty history. */
    private var currentState : Int = 0
    /** Actual game history, containing game objects. */
    private var gameStates : ArrayList<Game> = ArrayList()


    /* ================================================= Functions ================================================= */

    /**
     * Return the previous game object in this history.
     *
     * @return Next [Game] in current history, or null if there are no more elements behind.
     * @throws NoSuchElementException If there is no previous game state available.
     */
    fun getPreviousState() : Game
    {
        if (gameStates.isEmpty() || currentState <= 1)
            throw NoSuchElementException("No previous game state available")
        else
            return gameStates[--currentState - 1]
    }

    /**
     * Returns the next game object in the current history.
     *
     * @return Next [Game] in current history, or null if there are no more elements ahead.
     * @throws NoSuchElementException If there was no next state available.
     */
    fun getNextState() : Game
    {
        if (gameStates.isEmpty() || currentState >= gameStates.size)
            throw NoSuchElementException("No more game states available")
        else
            return gameStates[currentState++]
    }

    /**
     * True if the current [Game] has a next state.
     */
    fun hasNextState() = currentState < gameStates.size

    /**
     * True if the current [Game] has a previous state.
     */
    fun hasPreviousState() = currentState > 1

    /**
     * Push given game object onto the history, discarding any following states obtained from [getNextState].
     *
     * @param gameState The previous [Game] object to push onto the history stack.
     */
    fun pushState(gameState : Game)
    {
        while (currentState < gameStates.size)
            gameStates.removeLast()

        gameStates.add(gameState)
        currentState++
    }

    fun getCurrentState() = gameStates[currentState-1]

    /**
     * Clears all saved states
     */
    fun clearStates(){
        currentState = 0
        gameStates.clear()
    }

}
