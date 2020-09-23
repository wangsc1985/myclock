package com.wang17.myclock.model

class Commodity {
    // 合约
    var item: String=""
    // 名称
    var name: String=""
    // 交易单位  比如：1000克/手
    var unit: Int=0
    // 最小变动价位 0.05元/克
    var cose: Double=0.0

    constructor(item:String,name:String,unit:Int,cose:Any){
        this.item = item
        this.name=name
        this.unit=unit
        this.cose = cose.toString().toDouble()
    }
}