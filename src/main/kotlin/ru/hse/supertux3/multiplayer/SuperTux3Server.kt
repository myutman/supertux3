package ru.hse.supertux3.multiplayer

import io.grpc.Server
import io.grpc.ServerBuilder

class SuperTux3Server(port: Int, service: SuperTux3Service) {
    private val server: Server = ServerBuilder.forPort(port).addService(service).build()
    fun start() {
        this.server.start()
        Runtime.getRuntime().addShutdownHook(object : Thread() {
            override fun run() {
                this@SuperTux3Server.stop()
            }
        })
        this.server.awaitTermination()
    }

    private fun stop() {
        this.server.shutdown()
    }
}