package entity.game

import entity.score.HighScoreList


/**
 * Accessor object holding the high score list as well as the current game with its history.
 */
class NovaLuna {
    var currentGame = Game()
    var highScoreList = HighScoreList()
    val history = GameHistory()
}
