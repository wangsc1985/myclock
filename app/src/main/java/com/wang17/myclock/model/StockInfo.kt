package com.wang17.myclock.model

import java.math.BigDecimal

class StockInfo {
    var time: String
    var name: String
    var code: String
    var cost: BigDecimal
    var price: BigDecimal
    var increase: BigDecimal
    var exchange: String
    var amount = 0
    var type = 0
    init {
        time=""
        name=""
        code=""
        cost=0.toBigDecimal()
        price = 0.toBigDecimal()
        increase = 0.toBigDecimal()
        exchange=""
    }
}