package com.wangsc.lib

import java.io.DataOutputStream
import java.io.File
import java.net.Socket

class Client {
    companion object{
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                val dir = File("d:","0")
                println(dir.absolutePath)
                if(!dir.exists()){
                    dir.mkdirs()
                }

//                var s = Socket("192.168.0.107",8000)
//                var os = s.getOutputStream()
//                val dos = DataOutputStream(os)
//                dos.writeInt(3)
//                dos.flush()
//                dos.close()
//                s.close()
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }
}