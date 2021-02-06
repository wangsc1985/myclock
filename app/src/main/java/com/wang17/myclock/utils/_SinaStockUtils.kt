package com.wang17.myclock.utils

import android.util.Log
import com.wang17.myclock.database.Position
import com.wang17.myclock.model.Commodity
import com.wang17.myclock.model.StockInfo
import com.wang17.myclock.utils._Utils.runlog2file
import okhttp3.OkHttpClient
import okhttp3.Request
import java.math.BigDecimal
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

    @JvmStatic
    @Throws(Exception::class)
    fun getStockInfoList(positions: List<Position>, onLoadStockInfoListListener: OnLoadStockInfoListListener?) {
        Thread(Runnable {
            var isStock = true
            var totalProfit = 0.toBigDecimal()
            var totalCostFund = 0.toBigDecimal()
            var time = ""
            val stockInfoList: MutableList<StockInfo> = ArrayList()
            try {
                for (position in positions) {
//                        final Stock stock = stock;
                    val url = "https://hq.sinajs.cn/list=" + position.exchange + position.code
//                    e(url)
                    val client = _Session.okHttpClient
                    val request = Request.Builder().url(url).build()
                    val response = client.newCall(request).execute()
                    if (response.isSuccessful) {
                        val body = response.body()!!.string()
                        val result = body.substring(body.indexOf("\"")).replace("\"", "").split(",".toRegex()).toTypedArray()
                        val info = StockInfo()
                        var profit = 0.toBigDecimal()
                        if (position.type == 0) {
                            /**
                             * 股票列表
                             */
                            isStock = true
                            val open = result[2].toDouble()
                            info.name = result[0]
                            info.price = result[3].toBigDecimal()

                            val fee = TradeUtils.commission(info.price, position.amount) + TradeUtils.tax(-1, info.price, position.amount) + TradeUtils.transferFee(info.price, position.amount)
                            info.increase =(info.price - position.cost) / position.cost
                            info.time = result[31]
                            profit = (info.price - position.cost)*(position.amount*100).toBigDecimal() -fee
                            totalProfit += profit
                            totalCostFund += position.cost * (position.amount  * 100).toBigDecimal()
                        } else {
                            /**
                             * 期货列表
                             */
                            isStock = false
                            val open = result[2].toBigDecimal()
                            info.name = result[0]
                            info.price = result[8].toBigDecimal()
                            val yesterdayClose = result[5].toBigDecimal()
                            info.increase = info.price - yesterdayClose
                            info.time = result[1]
                            val commodity = findCommodity(position.code)
                            profit =(info.price - position.cost) * (position.type *  position.amount * commodity!!.unit).toBigDecimal()
                            totalProfit += profit
                        }
                        time = info.time
                        info.type = position.type
                        info.code = position.code
                        info.cost = position.cost
                        info.exchange = position.exchange
                        info.amount = position.amount
                        stockInfoList.add(info)
                    } else {
                        _Utils.log2file("err","获取数据失败...","")
                        return@Runnable
                    }
                }
                var totalAverageIncrease = if (isStock) {
                    totalProfit / totalCostFund
                } else {
                    totalProfit
                }
//                e("ffffffffffffffffffffffffffffffffffffff $onLoadStockInfoListListener")
                if (onLoadStockInfoListListener != null) {
                    if (positions.size != 0)
                        onLoadStockInfoListListener.onLoadFinished(stockInfoList, totalProfit, totalAverageIncrease, time)
                    else
                        onLoadStockInfoListListener.onLoadFinished(stockInfoList, 0.toBigDecimal(), 0.toBigDecimal(), time)
                }
            } catch (e: Exception) {
                e(e.message!!)
                _Utils.log2file("err","获取数据失败...",e.message!!)
            }
        }).start()
    }

    fun getStockInfo(info: StockInfo, onLoadStockInfoListener: OnLoadStockInfoListener) {
        Thread {
            var time = ""
            try {
                val url = "https://hq.sinajs.cn/list=" + info.exchange + info.code
                val client = _Session.okHttpClient
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val body = response.body()!!.string()
                    val result = body.substring(body.indexOf("\"")).replace("\"", "").split(",".toRegex()).toTypedArray()
                    val open = result[2].toBigDecimal()
                    info.name = result[0]
                    info.price = result[3].toBigDecimal()
                    info.increase = (info.price - open) / open
                    info.time = result[31]
                    time = info.time
                }
                onLoadStockInfoListener.onLoadFinished(info, time)
                //                    return stockInfoList;
            } catch (e: Exception) {
//                _Utils.error2file("SinaStockUtils.getStockInfo  error: ", e.message)
                Log.e("wangsc", e.message)
            }
        }.start()
    }

    interface OnLoadStockInfoListener {
        fun onLoadFinished(infoList: StockInfo, time: String)
    }

    interface OnLoadStockInfoListListener {
        fun onLoadFinished(infoList: MutableList<StockInfo>, totalProfit: BigDecimal, averageProfit: BigDecimal, time: String)
    }

}