package entity.tiles

/**
 * Contains a stack of differing [Tile] objects. Empty on creation.
 */
class TileStack {
    /* ================================================== Fields =================================================== */

    var tiles = ArrayDeque<Tile>(68)


    /* ================================================= Functions ================================================= */

    /**
     * Try to draw exactly one [Tile] from this stack or throw an exception if this stack is empty.
     *
     * @throws NoSuchElementException If the stack was empty.
     * @return topmost [Tile] from this stack
     */
    fun drawTile(): Tile {
        if (!isEmpty())
            return tiles.removeLast()
        else
            throw NoSuchElementException("Not enough Tiles to draw again!")
    }

    /**
     * Overridden to compare tile stack contents.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TileStack

        return (tiles == other.tiles)
    }

    /**
     * Returns the hash code of tile stack contents.
     */
    override fun hashCode(): Int = tiles.hashCode()

    /**
     * Determines whether this stack has any [Tile]s left.
     *
     * @return true if there is at least one tile left, false otherwise.
     */
    fun isEmpty() = tiles.isEmpty()

    /**
     * Puts given tile on top of the tile stack.
     */
    fun putTile(tile : Tile) = tiles.addLast(tile)

    /**
     * Returns the number of tiles in this stack.
     */
    fun size() = tiles.size
}
