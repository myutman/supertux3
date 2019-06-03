package ru.hse.supertux3.multiplayer

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import io.grpc.Server
import io.grpc.ServerBuilder

/**
 * Grpc server for SuperTux3 game, it uses SuperTux3Service
 */
class SuperTux3Server(port: Int, service: SuperTux3Service) {
    private val server: Server = ServerBuilder.forPort(port).addService(service).build()
    fun start() {
        this.server.start()
        println("Server started")
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

fun main(args: Array<String>) = mainBody {
    val parser = ArgParser(args)
    val arg = SuperTux3ServerArgs(parser)
    val port = arg.port
    val server = SuperTux3Server(port, SuperTux3Service())
    println("Starting server on port $port")
    server.start()
}

class SuperTux3ServerArgs(parser: ArgParser) {
    val port: Int by parser.storing(
        "--port",
        help = "port to listen on (default 9805)"
    ) { toInt() }.default { 9805 }
}