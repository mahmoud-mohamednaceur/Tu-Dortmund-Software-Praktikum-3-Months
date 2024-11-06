package entity.score

/**
 * Lists winning player scores of all previous games. Also see [PersonalBest].
 */
class HighScoreList {
    val entries: ArrayList<PersonalBest> = ArrayList()

    /**
     * Add given player score to the high score list. This does NOT update the save file on disk.
     *
     * @param score - Player's [PersonalBest] game score to list.
     */
    fun addScore(score: PersonalBest) {
        entries.add(score)
        entries.sortByDescending { it.score }
    }

    /**
     * Overridden toString() method to show the high score list as a formatted list.
     */
    override fun toString(): String {
        var highScoreListString = "Top 10:\n"
        entries.take(10).forEachIndexed { index, personalBest ->
            highScoreListString += "Nr. ${index + 1}: ${personalBest}\n"
        }
        return highScoreListString
    }
}
