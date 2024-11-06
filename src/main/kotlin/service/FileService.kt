package service

import com.google.gson.Gson
import com.opencsv.CSVParserBuilder
import com.opencsv.CSVReaderBuilder
import com.opencsv.CSVWriter
import entity.game.Game
import entity.player.Player
import entity.score.PersonalBest
import entity.tiles.Task
import entity.tiles.Tile
import entity.tiles.TileColor
import org.apache.commons.lang3.ObjectUtils
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayDeque


/**
 * A class that manages the Read-Write operations on the disk.
 * Used to save and load high scores, as well as saving and loading a game.
 */
class FileService(var rootService: RootService) : ARefreshingService() {

    private val saveFileName = "src/main/resources/save.json"
    private val highScoreCsvHeader = arrayOf("Rank", "Name", "Score", "Date")
    private val highScoreFileName = "src/main/resources/highScores.csv"

    /**
     * Loads the highest scores from a csv file.
     * @returns true, if the operation was successful.
     */
    fun loadHighScores(): Boolean {
        rootService.novaLuna.highScoreList.entries.clear()
        try {
            val csvReader = CSVReaderBuilder(FileReader(highScoreFileName))
                .withCSVParser(CSVParserBuilder().withSeparator(',').build())
                .build()

            csvReader.readNext() // CSV Header is unused when reading
            var line: Array<String>? = csvReader.readNext()

            while (line != null) {
                val name: String = line[1]
                val score: Int = line[2].toInt()
                val date = line[3].toLong()
                val personalBest = PersonalBest(name, score, date)
                rootService.novaLuna.highScoreList.addScore(personalBest)
                line = csvReader.readNext()
            }
            csvReader.close()
            return true
        } catch (_: FileNotFoundException) {
            println("No save file found")
            return false
        }
    }

    /**
     * Saves the current high score [PersonalBest] entries to the disk in a csv file.
     * @returns true, if the operation was successful.
     */
    fun saveHighScores(): Boolean {
        try {
            val tempHighScoreList = rootService.novaLuna.highScoreList.entries
            tempHighScoreList.sortByDescending { it.score }
            val writer = FileWriter(highScoreFileName)
            val csvWriter = CSVWriter(
                writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.NO_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END
            )
            csvWriter.writeNext(highScoreCsvHeader)

            tempHighScoreList.forEachIndexed { index, tempPersonalBest ->
                csvWriter.writeNext(
                    arrayOf(
                        (index + 1).toString(),
                        tempPersonalBest.name,
                        tempPersonalBest.score.toString(),
                        tempPersonalBest.timestamp.toString()
                    )
                )
            }
            csvWriter.close()
        }
        catch (_: Exception) {
            return false
        }
        return true
    }

    /**
     * Loads a predefined set of tiles from a csv file.
     *
     * @param filePath to the load tiles file.
     * @param isClassResource true to load from within this compilation unit.
     *
     * @throws FileNotFoundException when no tile file is present.
     * @returns the loaded tiles from the csv file.
     */
    fun loadTiles(filePath: String, isClassResource: Boolean): ArrayDeque<Tile> {
        val tiles: ArrayDeque<Tile> = ArrayDeque()
        val inputStream = if (isClassResource)
            FileService::class.java.classLoader.getResource(filePath).openStream()
        else FileInputStream(File(filePath))

        val csvReader = CSVReaderBuilder(InputStreamReader(inputStream))
            .withCSVParser(CSVParserBuilder().withSeparator(',').build())
            .build()

        csvReader.readNext() // CSV Header is unused when reading
        var line: Array<String>? = csvReader.readNext()

        while (line != null) {
            val color = TileColor.fromChar(line[1].first())
            val cost = line[2]
            val tasksToImport = arrayOf(line[3], line[4], line[5])
            val tileTasks = ArrayList<Task>()

            tasksToImport.forEach { task ->
                if (task.isNotEmpty()) {
                    val taskColors = ArrayList<TileColor>()
                    task.forEach { taskColor ->
                        taskColors.add(TileColor.fromChar(taskColor))
                    }
                    tileTasks.add(
                        Task(taskColors)
                    )
                }
            }
            tiles.add(Tile(color, cost.toInt(), tileTasks))
            line = csvReader.readNext()
        }
        csvReader.close()
        tiles.reverse()
        rootService.novaLuna.currentGame.tileStack.tiles = tiles
        return tiles
    }

    /**
     * Saves the current state of the game to the disk in a .json file.
     * The json structure is as follows:
     * {
     *      game: {},
     *      tileStack: Tile[],
     *      players: Player[],
     *      fields: Tile[]
     * }
     *
     * Game objects have the following structure:
     * {
     *      currentPlayer: {},
     *      meeplePosition: Int,
     *      playerPositionList: []
     * }
     *
     * TileStack and fields have the same structure.
     * The difference is that fields MAY contain empty objects.
     */
    fun saveGame() {
        val game = rootService.novaLuna.currentGame
        require(game.players.size in 2..4)
        require(!game.tileStack.isEmpty())

        val currentGameSaveObject = GameDataSaveObject(game.currentPlayer, game.meeplePosition, game.moonWheel)
        val map: MutableMap<String, Any> = HashMap()
        map["players"] = game.players
        map["game"] = currentGameSaveObject
        map["tileStack"] = game.tileStack.tiles
        map["fields"] = game.fields

        val writer: Writer = FileWriter(saveFileName)
        Gson().toJson(map, writer)
        writer.close()
    }

    /**
     * The load game from a save file functionality.
     * Refer to [saveGame] for the file structure.
     */
    fun loadGame() {
        val players = ArrayList<Player>()
        val gson = Gson()
        var gameSave: GameDataSaveObject? = null
        val tiles: ArrayList<Tile> = arrayListOf()
        val fields: ArrayList<Tile> = arrayListOf()
        val reader: Reader = Files.newBufferedReader(Paths.get(saveFileName))
        val map: Map<*, *> = gson.fromJson(reader, MutableMap::class.java)
        for ((key, value) in map) {
            when (key) {
                "players" -> {
                    val parsedPlayerObject: String = gson.toJson(value)
                    val playerSaveObjectArray: Array<Player> =
                        gson.fromJson(parsedPlayerObject, Array<Player>::class.java)
                    playerSaveObjectArray.forEach {
                        val player = Player(it.name, it.tokenColor, it.aiDifficulty)
                        player.tokenCount = it.tokenCount
                        player.displayArea = it.displayArea
                        player.position = it.position
                        player.isHighScoreEligible = it.isHighScoreEligible
                        players.add(player)
                    }
                }
                "game" -> {
                    val parsed = gson.fromJson(gson.toJson(value), GameDataSaveObject::class.java)
                    gameSave = GameDataSaveObject(
                        parsed.currentPlayer,
                        parsed.meeplePosition,
                        parsed.playerPositions
                    )
                }
                "tileStack" -> {
                    val tileStackParsed: Array<Tile> =
                        gson.fromJson(gson.toJson(value), Array<Tile>::class.java)
                    tiles.addAll(tileStackParsed)
                }
                "fields" -> {
                    val fieldsParsed: Array<Tile> =
                        gson.fromJson(gson.toJson(value), Array<Tile>::class.java)
                    fields.addAll(fieldsParsed)
                }
            }
        }

        if (gameSave != null) {
            val loadedGame = Game()
            loadedGame.players.addAll(players)
            loadedGame.currentPlayer = gameSave.currentPlayer
            loadedGame.meeplePosition = gameSave.meeplePosition
            loadedGame.moonWheel = gameSave.playerPositions
            loadedGame.tileStack.tiles = ArrayDeque(tiles)

            for (i in 0 until loadedGame.fields.size) {
                loadedGame.fields[i] = fields[i]
            }

            rootService.novaLuna.currentGame = loadedGame
        }
        else {
            throw IllegalStateException("Save game file corrupted or incomplete")
        }

        reader.close()
    }

    /**
     * A data class that defines the root game data object.
     */
    data class GameDataSaveObject(
        var currentPlayer: Player,
        val meeplePosition: Int,
        val playerPositions: Array<Stack<Player>>
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GameDataSaveObject

            if (currentPlayer != other.currentPlayer) return false
            if (meeplePosition != other.meeplePosition) return false
            if (!playerPositions.contentEquals(other.playerPositions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = currentPlayer.hashCode()
            result = 31 * result + meeplePosition
            result = 31 * result + playerPositions.contentHashCode()
            return result
        }
    }
}
