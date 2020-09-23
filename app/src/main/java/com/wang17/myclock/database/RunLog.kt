package com.wang17.myclock.database

import com.wang17.myclock.model.DateTime
import java.util.*

/**
 * Created by 阿弥陀佛 on 2016/10/28.
 */
class RunLog {
    var id:UUID
    var runTime:DateTime
    var tag:String
    var item:String
    var message:String

    init {
        id = UUID.randomUUID()
        runTime= DateTime()
        tag = DateTime(runTime.timeInMillis).toLongDateTimeString()
        item=""
        message=""
    }

    constructor()

    constructor(item: String, message: String) {
        runTime = DateTime()
        tag = DateTime(runTime.timeInMillis).toLongDateTimeString()
        this.item = item
        this.message = message
    }
}