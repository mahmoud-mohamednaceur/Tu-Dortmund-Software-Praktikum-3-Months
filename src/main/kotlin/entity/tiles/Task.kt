package entity.tiles


/**
 * One game tile's task, requiring the specified set of colors nearby for completion.
 *
 * @param colors - List containing one to four tile colors of any composition required for task completion.
 */
data class Task(val colors: List<TileColor>) {
    /* ================================================== Fields =================================================== */

    var isComplete = false


    /* ================================================ Functions ================================================== */

    override fun equals(other: Any?): Boolean {
        return this.colors == (other as Task).colors
    }

    override fun hashCode(): Int {
        var result = colors.hashCode()
        result = 31 * result + isComplete.hashCode()
        return result
    }

    override fun toString(): String {
        return if (isComplete) {
            "CompleteTask $colors"
        } else {
            "IncompleteTask $colors"
        }
    }
}
