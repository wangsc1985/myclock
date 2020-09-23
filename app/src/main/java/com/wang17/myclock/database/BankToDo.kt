package com.wang17.myclock.database

import com.wang17.myclock.model.DateTime
import java.util.*

class BankToDo {
    var id: UUID
    var dateTime: DateTime
    var bankName: String
    var cardNumber: String
    var money: Double

    init {
        id = UUID.randomUUID()
        dateTime = DateTime()
        bankName = ""
        cardNumber = ""
        money =0.0
    }

    constructor()

    constructor(id:UUID,dateTime: DateTime,bankName:String,cardNumber:String,money:Double){
        this.id = id
        this.dateTime = dateTime
        this.bankName = bankName
        this.cardNumber = cardNumber
        this.money =money
    }
}
