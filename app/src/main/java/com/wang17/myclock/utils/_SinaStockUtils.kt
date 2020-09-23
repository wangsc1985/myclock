package com.wang17.myclock.utils

import android.util.Log
import com.wang17.myclock.database.Position
import com.wang17.myclock.model.Commodity
import com.wang17.myclock.model.StockInfo
import com.wang17.myclock.utils._Utils.runlog2file
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*

object _SinaStockUtils {
    private fun parseItem(code: String?): String {
        val item = StringBuffer()
        for (i in 0 until code!!.length) {
            val c = code[i]
            if (!Character.isDigit(c)) {
                item.append(c)
            } else {
                break
            }
        }
        return item.toString()
    }

    fun findCommodity(code: String?): Commodity? {
        for (commodity in _Session.commoditys) {
            if (commodity.item.toLowerCase() == parseItem(code).toLowerCase()) {
                return commodity
            }
        }
        return null
    }

    private fun e(log: Any) {
        Log.e("wangsc", log.toString())
    }
    fun getStockInfoList(stocks: List<Position>, onLoadStockInfoListListener: OnLoadStockInfoListListener) {
        Thread(Runnable {
            var isStock = true
            var totalProfit = 0.0
            var totalAmount = 0.0
            var time = ""
            val stockInfoList: MutableList<StockInfo> = ArrayList()
            try {
                for (stock in stocks) {
//                        final Stock stock = stock;

                    val url = "https://hq.sinajs.cn/list=" + stock.exchange + stock.code
                    val client = OkHttpClient()

                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val body = response.body()!!.string()
                        val result = body.substring(body.indexOf("\"")).replace("\"", "").split(",".toRegex()).toTypedArray()
                        val info = StockInfo()
                        var profit = 0.0
                        if (stock.type == 0) {
                            /**
                             * 股票列表
                             */
                            isStock = true
                            val open = result[2].toDouble()
                            info.name = result[0]
                            info.price = result[3].toDouble()
                            info.increase = (info.price - open) / open
                            info.time = result[31]
                            profit = (info.price - stock.cost) / stock.cost
                            totalProfit += profit * stock.amount * stock.cost * 100
                            totalAmount += stock.amount * stock.cost * 100
                        } else {
                            /**
                             * 期货列表
                             */
                            isStock = false
                            val open = result[2].toDouble()
                            info.name = result[0]
                            info.price = result[8].toDouble()
                            val yesterdayClose = result[5].toDouble()
                            info.increase = info.price - yesterdayClose
                            info.time = result[1]
                            val commodity = findCommodity(stock.code)
                            profit = stock.type * (info.price - stock.cost) * stock.amount * commodity!!.unit
                            totalProfit += profit
                        }
                        time = info.time
                        info.type = stock.type
                        info.code = stock.code
                        info.cost = stock.cost
                        info.exchange = stock.exchange
                        info.amount = stock.amount
                        stockInfoList.add(info)
                    } else {
                        runlog2file("获取数据失败...")
                        return@Runnable
                    }
                }
                var averageProfit = 0.0
                averageProfit = if (isStock) {
                    totalProfit / totalAmount
                } else {
                    totalProfit
                }
                if (onLoadStockInfoListListener != null) {
                    if (stocks.size != 0) onLoadStockInfoListListener.onLoadFinished(stockInfoList, totalProfit, averageProfit, time) else onLoadStockInfoListListener.onLoadFinished(stockInfoList, 0.0, 0.0, time)
                }
                //                    return stockInfoList;
            } catch (e: Exception) {
                _Utils.error2file("SinaStockUtils.getStockInfoList  error: ", e.message)
                Log.e("wangsc", e.message)
            }
        }).start()
    }

    fun getStockInfo(info: StockInfo, onLoadStockInfoListener: OnLoadStockInfoListener) {
        Thread {
            var time = ""
            try {
                val url = "https://hq.sinajs.cn/list=" + info.exchange + info.code
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body()!!.string()
                    val result = body.substring(body.indexOf("\"")).replace("\"", "").split(",".toRegex()).toTypedArray()
                    val open = result[2].toDouble()
                    info.name = result[0]
                    info.price = result[3].toDouble()
                    info.increase = (info.price - open) / open
                    info.time = result[31]
                    time = info.time
                }
                onLoadStockInfoListener.onLoadFinished(info, time)
                //                    return stockInfoList;
            } catch (e: Exception) {
                _Utils.error2file("SinaStockUtils.getStockInfo  error: ", e.message)
                Log.e("wangsc", e.message)
            }
        }.start()
    }

    interface OnLoadStockInfoListener {
        fun onLoadFinished(infoList: StockInfo, time: String)
    }

    interface OnLoadStockInfoListListener {
        fun onLoadFinished(infoList: List<StockInfo>, totalProfit: Double, averageProfit: Double, time: String)
    }
}