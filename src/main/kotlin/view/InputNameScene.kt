package view

import entity.game.Game
import entity.player.Difficulty
import entity.player.TokenColor
import service.RootService
import tools.aqua.bgw.components.uicomponents.*
import tools.aqua.bgw.core.Alignment
import tools.aqua.bgw.core.BoardGameScene
import tools.aqua.bgw.util.Font
import tools.aqua.bgw.visual.ColorVisual
import tools.aqua.bgw.visual.ImageVisual
import view.component.NovaLunaButton
import view.component.NovaLunaCheckbox

/**
 * This scene allows the user to configure the game
 *
 * The player can define the player names and order and settings for potential AI players.
 * He can also define Tile-Settings(first game) and load predefined tile stacks.
 * @param rootService to interact with service-layer
 */

class InputNameScene(private val rootService: RootService) : BoardGameScene(1024, 768) {

    private val checkBoxSelection = mutableListOf("LEICHT", "MITTEL", "SCHWER")
    
    /* =============================================== UI-Elements ================================================ */

    private val whiteBackgroundLabel: Label = Label(
        width = 842, height = 469, posX = 91, posY = 139,
        visual = ImageVisual("gui/roundedBackground.png")
    )

    private val welcomeLabel: Label = Label(
        width = 478, height = 82, posX = 186, posY = 187,
        visual = ImageVisual("gui/startGameScene/welcomeTitle.png")
    )

    private val novaLunaImage: Label = Label(
        width = 166, height = 79, posX = 429, posY = 31,
        visual = ImageVisual("gui/logo.png")
    )

    val highScoreButton: NovaLunaButton = NovaLunaButton(
        width = 141, height = 49, posX = 792, posY = 68,
        visual = ImageVisual("gui/buttonHighScore.png")
    )

    private val startGameButtonGround: Label = Label(
        width = 215, height = 220, posX = 639, posY = 298,
        visual = ImageVisual("gui/bigStartButtonB.png")
    )

    val startGameButton: NovaLunaButton = NovaLunaButton(
        width = 160, height = 160, posX = 666, posY = 328,
        visual = ImageVisual("gui/bigStartButtonT.png")
    )

    private val toggleFirstGame: ToggleButton = ToggleButton(
        width = 35, height = 20, posX = 410, posY = 545
    )

    private val toggleRandom: ToggleButton = ToggleButton(
        width = 35, height = 20, posX = 238, posY = 545
    )

    val loadCardButton: NovaLunaButton = NovaLunaButton(
        width = 180, height = 49, posX = 91, posY = 632,
        visual = ImageVisual("gui/nameInputScene/buttonLoadTiles.png")
    )

    private val kiLabel: Label = Label(
        width = 30, height = 24, posX = 438, posY = 292,
        text = "KI?", font = Font(size = 15, fontWeight = Font.FontWeight.BOLD)
    )

    private val randomLabel: Label = Label(
        width = 110, height = 20, posX = 288, posY = 565,
        text = "Randomisieren", font = Font(size = 15)
    )

    private val firstGameLabel: Label = Label(
        width = 100, height = 20, posX = 455, posY = 565,
        text = "Erstes Spiel", font = Font(size = 15)
    )

    private val timeLabel: Label = Label(
        width = 55,
        height = 20,
        posX = 600,
        posY = 565,
        text = "${rootService.novaLuna.currentGame.playerTurnDurationSeconds} Sek.",
        font = Font(size = 15),
        alignment = Alignment.CENTER
    )

    private val plusStepper: NovaLunaButton = NovaLunaButton(
        width = 20, height = 20, posX = 655, posY = 565,
        text = "+", font = Font(size = 15, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        onActivated = {
            if (rootService.novaLuna.currentGame.playerTurnDurationSeconds < 9) {
                rootService.novaLuna.currentGame.playerTurnDurationSeconds++
            }
            timeLabel.text = "${rootService.novaLuna.currentGame.playerTurnDurationSeconds} Sek."
        }
    }

    private val minusStepper: NovaLunaButton = NovaLunaButton(
        width = 20, height = 20, posX = 575, posY = 565,
        text = "-", font = Font(size = 15, fontWeight = Font.FontWeight.BOLD)
    ).apply {
        onActivated = {
            if (rootService.novaLuna.currentGame.playerTurnDurationSeconds > 4) {
                rootService.novaLuna.currentGame.playerTurnDurationSeconds--
            }
            timeLabel.text = "${rootService.novaLuna.currentGame.playerTurnDurationSeconds} Sek."
        }
    }

    private val colorOne: Label = Label(
        width = 50, height = 47, posX = 170, posY = 313,
        visual = getTokenVisual(TokenColor.ORANGE)
    )

    private val playerOneTF: TextField = TextField(
        width = 185, height = 34, posX = 230, posY = 319,
        prompt = "Name Eingeben .."
    ).apply {
        onKeyTyped = {
            startGameButton.isDisabled = !checkValidInput()
            loadCardButton.isDisabled = !checkValidInput()
        }
    }

    private val isKiOne: NovaLunaCheckbox = NovaLunaCheckbox(
        width = 24, posX = 442, posY = 322
    ).apply {
        onActivated = {
            evaluateCheckBox(this, kiDiffOne)
        }
    }

    private val kiDiffOne: ComboBox<String> = ComboBox<String>(
        width = 115, height = 28, posX = 484, posY = 320, prompt = "NoAI"
    ).apply {
        this.items = checkBoxSelection
    }

    private val colorTwo: Label = Label(
        width = 50, height = 47, posX = 170, posY = 365,
        visual = getTokenVisual(TokenColor.BLACK)
    )

    private val playerTwoTF: TextField = TextField(
        width = 185, height = 34, posX = 230, posY = 371,
        prompt = "Name Eingeben .."
    ).apply {
        onKeyTyped = {
            startGameButton.isDisabled = !checkValidInput()
            loadCardButton.isDisabled = !checkValidInput()
        }
    }

    private val isKiTwo: NovaLunaCheckbox = NovaLunaCheckbox(
        width = 24, posX = 442, posY = 375
    ).apply {
        onActivated = {
            evaluateCheckBox(this, kiDiffTwo)
        }
    }

    private val kiDiffTwo: ComboBox<String> = ComboBox<String>(
        width = 115, height = 28, posX = 484, posY = 375, prompt = "NoAI"
    ).apply {
        this.items = checkBoxSelection
    }

    private val colorThree: Label = Label(
        width = 50, height = 47, posX = 170, posY = 417,
        visual = getTokenVisual(TokenColor.BLUE)
    )

    private val playerThreeTF: TextField = TextField(
        width = 185, height = 34, posX = 230, posY = 423,
        prompt = "Name Eingeben .."
    ).apply {
        onKeyTyped = {
            startGameButton.isDisabled = !checkValidInput()
            loadCardButton.isDisabled = !checkValidInput()
        }
    }

    private val isKiThree: NovaLunaCheckbox = NovaLunaCheckbox(
        width = 24, posX = 442, posY = 426
    ).apply {
        onActivated = {
            evaluateCheckBox(this, kiDiffThree)
        }
    }

    private val kiDiffThree: ComboBox<String> = ComboBox<String>(
        width = 115, height = 28, posX = 484, posY = 429, prompt = "NoAI"
    ).apply {
        this.items = checkBoxSelection
    }

    private val colorFour: Label = Label(
        width = 50, height = 47, posX = 170, posY = 467,
        visual = getTokenVisual(TokenColor.WHITE)
    )

    private val playerFourTF: TextField = TextField(
        width = 185, height = 34, posX = 230, posY = 475,
        prompt = "Name Eingeben .."
    ).apply {
        onKeyTyped = {
            startGameButton.isDisabled = !checkValidInput()
            loadCardButton.isDisabled = !checkValidInput()
        }
    }

    private val isKiFour: NovaLunaCheckbox = NovaLunaCheckbox(
        width = 24, posX = 442, posY = 478
    ).apply {
        onActivated = {
            evaluateCheckBox(this, kiDiffFour)
        }
    }

    private val kiDiffFour: ComboBox<String> = ComboBox<String>(
        width = 115, height = 28, posX = 484, posY = 477, prompt = "NoAI"
    ).apply {
        this.items = checkBoxSelection
    }

    private val listOfNameFields = mutableListOf(playerOneTF, playerTwoTF, playerThreeTF, playerFourTF)
    private val listOfIsKIFields = mutableListOf(isKiOne, isKiTwo, isKiThree, isKiFour)
    private val listOfKIDiffs = mutableListOf(kiDiffOne, kiDiffTwo, kiDiffThree, kiDiffFour)
    private val listOfTokenColor = mutableListOf(TokenColor.ORANGE, TokenColor.BLACK, TokenColor.BLUE, TokenColor.WHITE)

    /* ================================================= Functions ================================================= */

    /**
     * prevents not allowed use of ui-components
     */
    fun initElements() {
        listOfNameFields.forEach { it.text = "" }
        rootService.novaLuna.currentGame = Game()
        rootService.novaLuna.history.clearStates()

        startGameButton.isDisabled = true
        loadCardButton.isDisabled = true
        kiDiffOne.isDisabled = true
        kiDiffOne.isVisible = false
        kiDiffTwo.isDisabled = true
        kiDiffTwo.isVisible = false
        kiDiffThree.isDisabled = true
        kiDiffThree.isVisible = false
        kiDiffFour.isDisabled = true
        kiDiffFour.isVisible = false
    }

    /**
     * Initialize the game with the input from the user
     */
    fun initGame() {
        val gameService = rootService.gameService
        for (i in 0..3) {
            if (listOfNameFields[i].isVisible) {
                gameService.createPlayer(
                    listOfNameFields[i].text,
                    listOfTokenColor[i], getDifficulty(listOfIsKIFields[i], listOfKIDiffs[i])
                )
            }
        }
        gameService.initialiseGame(
            toggleRandom.isSelected,
            toggleFirstGame.isSelected,
            rootService.novaLuna.currentGame.playerTurnDurationSeconds
        )
    }


    /**
     * returns the selected [Difficulty]
     */
    private fun getDifficulty(checkBox: CheckBox, comboBox: ComboBox<String>): Difficulty {
        return if ((checkBox.checked) && (comboBox.isVisible)) {
            when (comboBox.selectedItem) {
                "LEICHT" -> Difficulty.EASY
                "MITTEL" -> Difficulty.MEDIUM
                "SCHWER" -> Difficulty.HARD
                else -> Difficulty.NO_AI
            }
        } else {
            Difficulty.NO_AI
        }

    }

    /**
     * checks for valid player names. The names must have a length from 1 to 12 and same names are not allowed
     */
    private fun checkValidInput(): Boolean {
        val listOfNames = mutableListOf<String>()
        for (tf in listOfNameFields) {
            if (tf.isVisible) {
                if ((tf.text.isNotEmpty()) && (tf.text.length <= 12)) {
                    listOfNames.add(tf.text)
                } else {
                    return false
                }
            }
        }
        return listOfNames.distinct() == listOfNames
    }

    /**
     * sets selection of given comboBox on "LEICHT" after users checked the given checkBox
     */
    private fun evaluateCheckBox(checkBox: CheckBox, comboBox: ComboBox<String>) {
        if (checkBox.checked) {
            comboBox.isVisible = true
            comboBox.isDisabled = false
            comboBox.selectedItem = "LEICHT"
        } else {
            comboBox.isVisible = false
            comboBox.isDisabled = true
        }
    }

    /**
     * changes the visibility of the ui-row number to the given value
     *
     * @param row to specify the row in which the visibility of all ui-components should be changed
     * @param value to specify the visibility of the row
     */
    fun changeVisibility(row: Int, value: Boolean) {
        if (row == 3) {
            colorThree.isVisible = value
            playerThreeTF.isVisible = value
            isKiThree.isVisible = value
            kiDiffThree.isVisible = value
        }
        if (row == 4) {
            colorFour.isVisible = value
            playerFourTF.isVisible = value
            isKiFour.isVisible = value
            kiDiffFour.isVisible = value
        }
    }

    init {
        background = ColorVisual(228, 207, 169)

        //Rounded Background after klick
        startGameButton.componentStyle =
            "-fx-alignment:bottom-center;-fx-text-alignment:center;-fx-background-radius:100%;"

        addComponents(
            whiteBackgroundLabel,
            welcomeLabel,
            novaLunaImage,
            highScoreButton,
            startGameButtonGround,
            startGameButton,
            toggleRandom,
            toggleFirstGame,
            loadCardButton,
            kiLabel,
            randomLabel,
            firstGameLabel,
            timeLabel,
            plusStepper,
            minusStepper,

            colorOne,
            playerOneTF,
            isKiOne,
            kiDiffOne,

            colorTwo,
            playerTwoTF,
            isKiTwo,
            kiDiffTwo,

            colorThree,
            playerThreeTF,
            isKiThree,
            kiDiffThree,

            colorFour,
            playerFourTF,
            isKiFour,
            kiDiffFour
        )
    }
}