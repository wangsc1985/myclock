package com.wang17.myclock.database

import java.util.*

class Position {
    var id: UUID
    var code: String=""
        get() = field
        set(value) {
            field = value
            if (field.startsWith("6")) {
                exchange = "sh"
            } else {
                exchange = "sz"
            }
        }
    var name: String
    var cost = 0.0
    var type: Int // 0是股票，1是期货多单，2是期货空单
    var amount: Int
    var exchange: String
    var profit: Double

    init {
        id = UUID.randomUUID()
        code = ""
        name = ""
        cost = 0.0
        type = 0
        amount = 0
        exchange = ""
        profit = 0.0
    }
}