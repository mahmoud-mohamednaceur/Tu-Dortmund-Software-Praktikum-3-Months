package entity.score

import java.text.SimpleDateFormat
import java.util.*

/**
 * A winning player's game result with relevant data. Element of the [HighScoreList].
 */
class PersonalBest(val name: String, val score: Int, val timestamp: Long) {
    override fun toString(): String {
        return "$name scored $score on ${
            SimpleDateFormat("dd/MM/yyyy HH:mm").format(Date(timestamp))
        }"
    }
}
