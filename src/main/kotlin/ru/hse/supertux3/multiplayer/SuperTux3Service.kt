package ru.hse.supertux3.multiplayer

import io.grpc.stub.StreamObserver
import ru.hse.supertux3.SuperTux3Grpc
import ru.hse.supertux3.SuperTux3Proto
import java.io.IOException

class SuperTux3Service: SuperTux3Grpc.SuperTux3ImplBase() {

    val games = mutableMapOf<String, Game>()

    override fun createGame(
        request: SuperTux3Proto.CreateGameRequest,
        responseObserver: StreamObserver<SuperTux3Proto.CreateGameResponse>
    ) {
        if (request.gameId in games) {
            responseObserver.onError(IOException("Game already exists"))
            return
        }
        val game = Game(request.gameId)
        games[request.gameId] = game
        game.addUser()
        responseObserver.onNext(SuperTux3Proto.CreateGameResponse.getDefaultInstance())
        responseObserver.onCompleted()
    }

    override fun startGame(
        request: SuperTux3Proto.StartGameRequest,
        responseObserver: StreamObserver<SuperTux3Proto.StartGameResponse>
    ) {
        super.startGame(request, responseObserver)
    }

    override fun joinGame(
        request: SuperTux3Proto.JoinGameRequest,
        responseObserver: StreamObserver<SuperTux3Proto.JoinGameResponse>
    ) {
        super.joinGame(request, responseObserver)
    }

    override fun getUpdate(
        request: SuperTux3Proto.GetUpdateRequest,
        responseObserver: StreamObserver<SuperTux3Proto.GetUpdateResponse>
    ) {

    }

    override fun isMyTurn(
        request: SuperTux3Proto.IsMyTurnRequest,
        responseObserver: StreamObserver<SuperTux3Proto.IsMyTurnResponse>
    ) {
        super.isMyTurn(request, responseObserver)
    }



    override fun makeTurn(
        request: SuperTux3Proto.MakeTurnRequest,
        responseObserver: StreamObserver<SuperTux3Proto.MakeTurnResponse>
    ) {
        super.makeTurn(request, responseObserver)
    }

}