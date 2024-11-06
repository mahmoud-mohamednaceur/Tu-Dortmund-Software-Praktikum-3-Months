package view

import entity.player.Player
import entity.tiles.Tile
import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.MenuScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import tools.aqua.bgw.visual.Visual
import view.component.NovaLunaButton

/**
 * This scene shows the displayGrid of a [entity.player]
 */
class PeekScene : MenuScene(747, 768, ColorVisual(PRIMARY_CLR)) {

    /* =============================================== UI-Elements ================================================ */

    private val bgPicture = Label(
        width = 747, height = 768, posX = 0, posY = 0,
        visual = ImageVisual(path = "gui/game/background.png")
    )

    private val playersToken = Label(
        posX = 100, posY = 723, height = 23, width = 25,
        visual = Visual.EMPTY
    )

    private val playersName = Label(
        posX = 130, posY = 725, height = 23, width = 300,
        text = "NA",
        alignment = Alignment.TOP_LEFT,
        font = Font(size = 15, family = "Roboto", color = PRIMARY_CLR, fontWeight = Font.FontWeight.BOLD)
    )

    val closeBTN = NovaLunaButton(
        posX = 750, posY = 20, height = 50, width = 50,
        visual = ImageVisual("gui/game/buttonClose.png")
    )

    private var toBeDeleted = mutableListOf<Label>()

    /* ================================================= Functions ================================================= */

    /**
     * Updates the displayed player and tiles
     */
    fun refresh(player: Player) {
        toBeDeleted.forEach {
            removeComponents(it)
        }
        toBeDeleted.clear()
        drawGridCells(player.displayArea)

        playersToken.visual = getTokenVisual(player.tokenColor)

        playersName.text = player.name
    }

    /**
     * Draws an empty grid
     */
    private fun drawGrid() {
        for (col in 0..8) {
            for (raw in 0..8) {
                Label(
                    posX = 35 + 2 + 75 * col, posY = 42 + 2 + 75 * raw, height = 80, width = 80,
                    visual = NON_CELL_Visual,
                ).apply {
                    addComponents(this)
                    isDisabled = true
                }
            }
        }
    }

    /**
     * Refreshes all cells
     */
    private fun drawGridCells(displayArea: Array<Array<Tile?>>) {
        displayArea.forEachIndexed { column, arrayOfTiles ->
            arrayOfTiles.forEachIndexed { raw, tile ->
                if (tile != null) {
                    Label(
                        posX = 35 + 2 + 75 * column,
                        posY = 42 + 2 + 75 * raw,
                        height = 80,
                        width = 80,
                        visual = TileViewer(tile).visual
                    ).apply {
                        addComponents(this)
                        toBeDeleted.add(this)
                        isDisabled = true
                    }
                }
            }
        }
    }

    init {
        addComponents(
            bgPicture,
            playersToken,
            playersName,
            closeBTN
        )

        drawGrid()
    }
}