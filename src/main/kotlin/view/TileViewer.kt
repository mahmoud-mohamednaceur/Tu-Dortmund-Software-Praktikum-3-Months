package view

import entity.tiles.Task
import entity.tiles.Tile
import entity.tiles.TileColor
import tools.aqua.bgw.visual.ImageVisual
import java.awt.Font
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.imageio.ImageIO
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Help class to view tile images
 *
 * @param tile the tile which visual is needed
 */
class TileViewer(val tile: Tile) {

    val visual = ImageVisual(
        drawCard(),
        583,
        583,
    )

    /**
     * main function that draws the card and steps
     */
    private fun drawCard(): BufferedImage {
        val baseImage: BufferedImage = ImageIO.read(
            TileViewer::class.java.getResource(
                "/gui/tiles/tile" + when (tile.color) {
                    TileColor.BLUE -> "Blue"
                    TileColor.CYAN -> "Cyan"
                    TileColor.RED -> "Red"
                    TileColor.YELLOW -> "Yellow"
                } + ".png"
            )
        )
        val combined = BufferedImage(583, 583, BufferedImage.TYPE_INT_ARGB)
        val g: Graphics = combined.graphics
        g.drawImage(baseImage, 1, 0, null)
        g.font = Font("Times New Roman", Font.BOLD, 120)
        g.color = PRIMARY_CLR
        g.drawString(tile.cost.toString(), 150, 220)

        tile.tasks.forEachIndexed { index, task ->
            val plateImage: BufferedImage = taskPlate(task)
            val offsetX = (index % 2) * -231
            val offsetY = if (index >= 1) {
                231
            } else {
                0
            }
            g.drawImage(
                plateImage, 291 + offsetX, 60 + offsetY, 291 + 231 + offsetX, 291 + offsetY,
                0, 0, 454, 454, null
            )
        }
        g.dispose()
        return combined
    }

    /**
     * draws task plates
     */
    private fun taskPlate(task: Task): BufferedImage {
        val plateImage: BufferedImage = ImageIO.read(
            TileViewer::class.java.getResource(
                "/gui/tiles/plate" + if (task.isComplete) {
                    "Done.png"
                } else {
                    ".png"
                }
            )
        )
        val combinedPlate = BufferedImage(454, 454, BufferedImage.TYPE_INT_ARGB)
        val plate: Graphics = combinedPlate.graphics
        plate.drawImage(plateImage, 0, 0, null)

        if (!task.isComplete) {
            task.colors.forEachIndexed { index, color ->
                val diamondCount = task.colors.size
                plate.drawImage(
                    taskDiamond(color),
                    160 + getXOffCoord(index, diamondCount), 157 + getYOffCoord(index, diamondCount),
                    294 + getXOffCoord(index, diamondCount), 297 + getYOffCoord(index, diamondCount),
                    0, 0, 192, 202, null
                )
            }
        }
        plate.dispose()
        return combinedPlate
    }

    /**
     * draws those diamonds on a task plate
     */
    private fun taskDiamond(color: TileColor): BufferedImage {
        val imageB: BufferedImage = ImageIO.read(TileViewer::class.java.getResource("/gui/tiles/tasks.png"))
        return imageB.getSubimage(
            color.column, color.row, 192, 202,
        )
    }

    private fun getXOffCoord(index: Int, size: Int): Int {
        if (size == 1)
            return 0
        return (100 * cos(PI / 4 + (index * 2 * PI / size))).toInt()
    }

    private fun getYOffCoord(index: Int, size: Int): Int {
        if (size == 1)
            return 0
        return (100 * sin(PI / 4 + (index * 2 * PI / size))).toInt()
    }
}

private val TileColor.row
    get() = when (this) {
        TileColor.BLUE -> 202
        TileColor.CYAN -> 0
        TileColor.RED -> 0
        TileColor.YELLOW -> 202
    }

private val TileColor.column
    get() = when (this) {
        TileColor.BLUE -> 192
        TileColor.CYAN -> 0
        TileColor.RED -> 192
        TileColor.YELLOW -> 0
    }
