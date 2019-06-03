package ru.hse.supertux3.multiplayer

import io.grpc.stub.StreamObserver
import ru.hse.supertux3.SuperTux3Grpc
import ru.hse.supertux3.SuperTux3Proto
import java.io.IOException

/**
 * Service for SuperTux3 server
 */
class SuperTux3Service : SuperTux3Grpc.SuperTux3ImplBase() {

    private val games = mutableMapOf<String, Game>()

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
        responseObserver.onNext(SuperTux3Proto.CreateGameResponse.getDefaultInstance())
        responseObserver.onCompleted()
    }

    override fun startGame(
        request: SuperTux3Proto.StartGameRequest,
        responseObserver: StreamObserver<SuperTux3Proto.StartGameResponse>
    ) = withGame(request.gameId, responseObserver) { game ->
        val userId = game.start()
        responseObserver.onNext(
            SuperTux3Proto.StartGameResponse.newBuilder()
                .setLevel(game.level.toProto())
                .setUserId(userId)
                .build()
        )
        responseObserver.onCompleted()
    }

    override fun joinGame(
        request: SuperTux3Proto.JoinGameRequest,
        responseObserver: StreamObserver<SuperTux3Proto.JoinGameResponse>
    ) = withGame(request.gameId, responseObserver) { game ->
        val userId = game.join()
        responseObserver.onNext(
            SuperTux3Proto.JoinGameResponse.newBuilder()
                .setLevel(game.level.toProto())
                .setUserId(userId)
                .build()
        )
        responseObserver.onCompleted()
    }

    override fun isMyTurn(
        request: SuperTux3Proto.IsMyTurnRequest,
        responseObserver: StreamObserver<SuperTux3Proto.IsMyTurnResponse>
    ) = withGame(request.gameId, responseObserver) { game ->
        val isMyTurn = game.isMyTurn(request.userId)
        responseObserver.onNext(
            SuperTux3Proto.IsMyTurnResponse.newBuilder()
                .setMyTurn(isMyTurn)
                .build()
        )
        responseObserver.onCompleted()
    }

    override fun getUpdate(
        request: SuperTux3Proto.GetUpdateRequest,
        responseObserver: StreamObserver<SuperTux3Proto.GetUpdateResponse>
    ) = withGame(request.gameId, responseObserver) { game ->
        val cells = game.getUpdate()
        val turn = SuperTux3Proto.Turn.newBuilder()
            .addAllCells(cells.map { it.toProto() })
            .build()
        responseObserver.onNext(
            SuperTux3Proto.GetUpdateResponse.newBuilder()
                .setTurn(turn)
                .build()
        )
        responseObserver.onCompleted()
    }


    override fun makeTurn(
        request: SuperTux3Proto.MakeTurnRequest,
        responseObserver: StreamObserver<SuperTux3Proto.MakeTurnResponse>
    ) = withGame(request.gameId, responseObserver) { game ->
        val cells = game.makeTurn(request.userId, request.command)
        val turn = SuperTux3Proto.Turn.newBuilder()
            .addAllCells(cells.map { it.toProto() })
            .build()
        responseObserver.onNext(
            SuperTux3Proto.MakeTurnResponse.newBuilder()
                .setTurn(turn)
                .build()
        )
        responseObserver.onCompleted()
    }


    private fun withGame(
        gameId: String, responseObserver: StreamObserver<*>,
        exec: (Game) -> Unit
    ) {
        val game = games[gameId]
        if (game != null) {
            exec(game)
        } else {
            responseObserver.onError(IOException("Game not exists"))
        }
    }
}