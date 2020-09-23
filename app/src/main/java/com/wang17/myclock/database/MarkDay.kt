package com.wang17.myclock.database

import com.wang17.myclock.model.DateTime
import java.util.*

/**
 * Created by 阿弥陀佛 on 2016/10/17.
 */
class MarkDay {
    var id:UUID
    var dateTime:DateTime
    var item:UUID
    var summary: String

    init{
        this.id= UUID.randomUUID()
        this.dateTime = DateTime()
        this.item=UUID.randomUUID()
        this.summary = ""
    }

    constructor()

    constructor(dateTime: DateTime, itemId: UUID, summary: String) {
        this.dateTime = dateTime
        item = itemId
        this.summary = summary
    }
}