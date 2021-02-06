package com.wang17.myclock.utils

import android.os.Environment
import com.wang17.myclock.model.Commodity
import com.wang17.myclock.model.DateTime
import com.wang17.myclock.model.SSLSocketFactoryCompat
import okhttp3.OkHttpClient
import java.io.File
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.util.*
import javax.net.ssl.SSLSocketFactory

/**
 * Created by 阿弥陀佛 on 2016/10/28.
 */
object _Session {
    val ROOT_DIR:File
    get() {
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/0/myclock")
        if(!dir.exists()){
            dir.mkdirs()
        }
        return dir
    }

    var TALLY_MUSIC_NAMES= _Utils.getFilesWithSuffix(ROOT_DIR.path, ".mp3")
    var commoditys: MutableList<Commodity> = ArrayList()

    fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
        try {
            val factory: SSLSocketFactory = SSLSocketFactoryCompat()
            builder.sslSocketFactory(factory)
        } catch (e: KeyManagementException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return builder.build()
    }

    val okHttpClient:OkHttpClient
    init {
        try {
            okHttpClient= createOkHttpClient()
            /**
             * 加载念佛音乐列表
             */

            var dir = File(Environment.getExternalStorageDirectory().toString() + "/0/myclock")
            if (!dir.exists()) {
                dir.mkdirs()
            }

            Arrays.sort(TALLY_MUSIC_NAMES)
            /**
             * unit*cose：一个点位多少钱/手。
             */

            commoditys.add(Commodity("ts", "二债", 200, 0.005))
            commoditys.add(Commodity("tf", "五债", 100, 0.005))
            commoditys.add(Commodity("t", "十债", 10, 0.005))
            commoditys.add(Commodity("au", "金", 1000, 0.05))
            commoditys.add(Commodity("ag", "银", 15, 1))
            commoditys.add(Commodity("cu", "铜", 5, 10))
            commoditys.add(Commodity("al", "铝", 5, 5))
            commoditys.add(Commodity("zn", "锌", 5, 5))
            commoditys.add(Commodity("pb", "铅", 5, 5))
            commoditys.add(Commodity("ni", "镍", 1, 10))
            commoditys.add(Commodity("sn", "锡", 1, 10))
            commoditys.add(Commodity("j", "焦炭", 100, 0.5))
            commoditys.add(Commodity("jm", "焦煤", 60, 0.5))
            commoditys.add(Commodity("zc", "郑煤", 100, 0.2))
            commoditys.add(Commodity("i", "铁矿", 100, 0.5))
            commoditys.add(Commodity("wr", "线材", 10, 1))
            commoditys.add(Commodity("rb", "螺纹", 10, 1))
            commoditys.add(Commodity("hc", "热卷", 10, 1))
            commoditys.add(Commodity("sf", "硅铁", 5, 2))
            commoditys.add(Commodity("sm", "锰硅", 5, 2))
            commoditys.add(Commodity("fg", "玻璃", 20, 1))
            commoditys.add(Commodity("fu", "燃油", 10, 1))
            commoditys.add(Commodity("sc", "原油", 1000, 0.1))
            commoditys.add(Commodity("ru", "橡胶", 10, 5))
            commoditys.add(Commodity("l", "塑料", 5, 5))
            commoditys.add(Commodity("ta", "PTA", 5, 2))
            commoditys.add(Commodity("v", "PVC", 5, 5))
            commoditys.add(Commodity("pp", "PP", 5, 1))
            commoditys.add(Commodity("ma", "郑醇", 10, 1))
            commoditys.add(Commodity("bu", "沥青", 10, 2))
            commoditys.add(Commodity("a", "豆一", 10, 1))
            commoditys.add(Commodity("b", "豆二", 10, 1))
            commoditys.add(Commodity("c", "玉米", 10, 1))
            commoditys.add(Commodity("wh", "郑麦", 20, 1))
            commoditys.add(Commodity("ri", "早稻", 20, 1))
            commoditys.add(Commodity("lr", "晚稻", 20, 1))
            commoditys.add(Commodity("jr", "粳稻", 20, 1))
            commoditys.add(Commodity("rs", "菜籽", 10, 1))
            commoditys.add(Commodity("ml", "豆粕", 10, 1))
            commoditys.add(Commodity("rm", "菜粕", 10, 1))
            commoditys.add(Commodity("y", "豆油", 10, 2))
            commoditys.add(Commodity("oi", "郑油", 10, 1))
            commoditys.add(Commodity("p", "棕榈", 10, 2))
            commoditys.add(Commodity("cf", "郑棉", 5, 5))
            commoditys.add(Commodity("sr", "白糖", 10, 1))
            commoditys.add(Commodity("cy", "棉纱", 5, 5))
            commoditys.add(Commodity("jd", "鸡蛋", 5, 1))
            commoditys.add(Commodity("ap", "苹果", 10, 1))
            commoditys.add(Commodity("cs", "淀粉", 10, 1))
            commoditys.add(Commodity("fb", "纤板", 500, 0.05))
            commoditys.add(Commodity("bb", "胶板", 500, 0.05))
        } catch (e: Exception) {
            throw e
        }
    }
}