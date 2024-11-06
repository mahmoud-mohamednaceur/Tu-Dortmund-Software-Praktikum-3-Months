package entity.tiles


/**
 * Field tile with a value of [cost], offering specified tasks for completion. Colored in specified color.
 *
 * @param color - This tile's color, determining which tasks of nearby tiles this tile may fulfill.
 * @param cost - Number of steps the player may move after completing this tile.
 * @param tasks - List of up to three [Task] objects which can be completed on this tile.
 */
data class Tile(val color: TileColor, val cost: Int, val tasks: List<Task> = listOf()) {
    override fun equals(other: Any?): Boolean {
        return this.color == (other as Tile).color && this.cost == other.cost && this.tasks == other.tasks
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + cost
        result = 31 * result + tasks.hashCode()
        return result
    }

    override fun toString(): String {
        return if (tasks.isNotEmpty()) {
            "Tile(${color.toString().lowercase()}, $cost, $tasks)"
        } else {
            "Tile(${color.toString().lowercase()}, $cost, No tasks)"
        }
    }
}
