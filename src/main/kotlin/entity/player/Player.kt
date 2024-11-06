package entity.player

import entity.tiles.Tile


/**
 * Player object, participating in a Nova Luna game. This class holds player data for AI and non-AI players.
 *
 * @param name - This player's name string.
 * @param tokenColor - Chosen [TokenColor] for this player's token.
 */
data class Player(val name: String, val tokenColor: TokenColor, val aiDifficulty: Difficulty = Difficulty.NO_AI) {
    /* ================================================== Fields =================================================== */

    var displayArea: Array<Array<Tile?>> = Array(9) { Array(9) { null } }
    var isHighScoreEligible: Boolean = true
    var position: Int = 0
    var tokenCount: Int = 20


    /* ================================================ Functions ================================================== */

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Player

        if (name != other.name) return false
        if (tokenColor != other.tokenColor) return false
        if (aiDifficulty != other.aiDifficulty) return false
        if (isHighScoreEligible != other.isHighScoreEligible) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + tokenColor.hashCode()
        result = 31 * result + aiDifficulty.hashCode()
        result = 31 * result + isHighScoreEligible.hashCode()
        return result
    }

    /**
     * Change the status of a Player from Highscore eligible to not eligible.
     */
    fun setNoLongerHighScoreEligible() {
        isHighScoreEligible = false
    }

    /**
     * Decrease the amount of tokens available for a player by 1
     */
    fun tokenCountDecrease() {
        if (tokenCount > 0)
            tokenCount--
        else
            throw IllegalStateException("Token count already zero")
    }

    override fun toString(): String {
        return "Player(${this.name})"
    }
}
