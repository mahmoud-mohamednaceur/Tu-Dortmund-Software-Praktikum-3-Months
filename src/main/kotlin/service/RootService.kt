package service

import entity.game.NovaLuna


/**
 * Root access to relevant game services.
 */
class RootService {
    /* Entity-layer */
    val novaLuna = NovaLuna()

    /* Service-layer */
    val aiService = AiService(this)
    val fileService = FileService(this)
    val gameService = GameService(this)
    val playerService = PlayerService(this)
}
