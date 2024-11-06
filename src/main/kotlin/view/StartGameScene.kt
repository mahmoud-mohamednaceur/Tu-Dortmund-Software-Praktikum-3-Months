package view

import tools.aqua.bgw.components.uicomponents.Label
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import view.component.NovaLunaButton

/**
 * This scene is the starting point of the application
 *
 * It allows the player to determine the number of players,
 * show the HighScore-List or load an existing Game
 */

class StartGameScene : BoardGameScene(1024, 768) {

    /* =============================================== UI-Elements ================================================ */

    private val whiteBackgroundLabel: Label = Label(
        width = 842, height = 469, posX = 91, posY = 139,
        visual = ImageVisual("gui/roundedBackground.png")
    )

    private val welcomeLabel: Label = Label(
        width = 488, height = 82, posX = 186, posY = 227,
        visual = ImageVisual("gui/startGameScene/welcomeTitle.png")
    )

    val highScoreButton: NovaLunaButton = NovaLunaButton(
        width = 141, height = 49, posX = 792, posY = 68,
        visual = ImageVisual("gui/buttonHighScore.png")
    )

    val loadGameButton: NovaLunaButton = NovaLunaButton(
        width = 198, height = 61, posX = 413, posY = 650,
        visual = ImageVisual("gui/nameInputScene/buttonLoadGame.png")
    )

    private val startGameButtonGround: Label = Label(
        width = 215, height = 220, posX = 639, posY = 298,
        visual = ImageVisual("gui/bigStartButtonB.png")
    )

    val startGameButton: NovaLunaButton = NovaLunaButton(
        width = 160, height = 160, posX = 666, posY = 328,
        visual = ImageVisual("gui/bigStartButtonT.png")
    )

    private val novaLunaImage: Label = Label(
        width = 166, height = 79, posX = 429, posY = 31,
        visual = ImageVisual("gui/logo.png")
    )

    val buttonTwoSelected: Label = Label(
        width = 82.25, height = 123.75, posX = 186.53, posY = 378.07,
        visual = ImageVisual("gui/startGameScene/selectedBackground.png")
    )

    val twoPlayerButton: NovaLunaButton = NovaLunaButton(
        width = 51.67, height = 100.13, posX = 201.82, posY = 389.88,
        visual = ImageVisual("gui/startGameScene/b2.png")
    )

    val buttonThreeSelected: Label = Label(
        width = 82.25, height = 123.75, posX = 292.53, posY = 378.07,
        visual = ImageVisual("gui/startGameScene/selectedBackground.png")
    )

    val threePlayerButton: NovaLunaButton = NovaLunaButton(
        width = 51.67, height = 100.13, posX = 307.82, posY = 389.88,
        visual = ImageVisual("gui/startGameScene/b3.png")
    )

    val buttonFourSelected: Label = Label(
        width = 82.25, height = 123.75, posX = 398.53, posY = 378.07,
        visual = ImageVisual("gui/startGameScene/selectedBackground.png")
    )

    val fourPlayerButton: NovaLunaButton = NovaLunaButton(
        width = 51.67, height = 100.13, posX = 413.82, posY = 389.88,
        visual = ImageVisual("gui/startGameScene/b4.png")
    )

    /* ================================================= Functions ================================================= */

    /**
     * Sets a grey background behind the selected button and disables the start-Button
     *
     * @param player to know which button to highlight
     */
    fun highlightSelection(player: Int) {
        startGameButton.isDisabled = false
        when (player) {
            2 -> {
                buttonTwoSelected.isVisible = true
                buttonThreeSelected.isVisible = false
                buttonFourSelected.isVisible = false
            }
            3 -> {
                buttonTwoSelected.isVisible = false
                buttonThreeSelected.isVisible = true
                buttonFourSelected.isVisible = false
            }
            4 -> {
                buttonTwoSelected.isVisible = false
                buttonThreeSelected.isVisible = false
                buttonFourSelected.isVisible = true
            }
            else -> throw IllegalArgumentException("Not a valid Number of Players")
        }
    }

    init {
        background = ColorVisual(228, 207, 169)

        //Rounded Background after klick
        startGameButton.componentStyle =
            "-fx-alignment:bottom-center;-fx-text-alignment:center;-fx-background-radius:100%;"

        addComponents(
            whiteBackgroundLabel,
            startGameButtonGround,
            startGameButton,
            welcomeLabel,
            highScoreButton,
            loadGameButton,
            buttonTwoSelected,
            twoPlayerButton,
            buttonThreeSelected,
            threePlayerButton,
            buttonFourSelected,
            fourPlayerButton,
            novaLunaImage
        )
    }
}