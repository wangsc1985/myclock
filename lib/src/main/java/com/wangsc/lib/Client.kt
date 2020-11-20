package com.wangsc.lib

import java.io.DataOutputStream
import java.net.Socket

class Client {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                var s = Socket("127.0.0.1",8000)
                var os = s.getOutputStream()
                val dos = DataOutputStream(os)
                dos.writeInt(1)
                dos.flush()
                dos.close()
                s.close()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}