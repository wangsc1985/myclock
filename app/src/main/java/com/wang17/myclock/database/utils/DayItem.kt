package com.wang17.myclock.database.utils

import java.util.*

/**
 * Created by Administrator on 2017/10/11.
 */
class DayItem {
    var id: UUID= UUID.randomUUID()
    var name: String = ""
    var summary: String = ""
    var targetInHour = 0

    constructor(id: UUID) {
        this.id = id
    }

    constructor(name: String, summary: String, targetInHour: Int) {
        this.name = name
        this.summary = summary
        this.targetInHour = targetInHour
    }
}