package com.wangsc.lib

import java.io.DataInputStream
import java.net.ServerSocket
import java.util.*
import java.util.concurrent.CountDownLatch

class Server {
    companion object{

        private lateinit var serverSocket: ServerSocket
        fun startSocket() {
            Thread {
                try {
                    serverSocket = ServerSocket(8000)
//                    latch.countDown()
                    while (true) {
                        var s = serverSocket.accept()
                        var dis = DataInputStream(s.getInputStream())
                        when (dis.readInt()) {
                            0->{
                                println("000000000000000000000000000")
                            }
                        }
                        dis.close();
                        s.close()
                    }
                } catch (e: Exception) {
                }
            }.start()
        }


        @JvmStatic
        fun main(args: Array<String>) {
            try {
                startSocket()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}