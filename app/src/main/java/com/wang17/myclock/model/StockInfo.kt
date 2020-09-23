package com.wang17.myclock.model

class StockInfo {
    var time:String
    var name:String
    var code:String
    var cost:Double
    var price:Double
    var increase:Double
    var exchange:String
    var amount:Int
    var type:Int

    init {
        time=""
        name=""
        code=""
        cost=0.0
        price=0.0
        increase=0.0
        exchange=""
        amount=0
        type=0
    }
}