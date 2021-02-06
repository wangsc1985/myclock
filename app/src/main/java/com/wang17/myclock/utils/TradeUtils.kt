package com.wang17.myclock.utils

import com.wang17.myclock.toMyDecimal
import java.math.BigDecimal

object TradeUtils {

    /**
     * 印花税  卖出时千1
     * type price amount
     * @param money
     */
    fun tax(type:Int, price: BigDecimal, amount:Int): BigDecimal {
        if (type == 1) {
            return 0.toBigDecimal()
        } else {
            var money = price * (amount * 100).toBigDecimal()
            return money / 1000.toBigDecimal()
        }
    }

    /**
     * 佣金  万3  最低5元
     * price amount
     */
    fun commission(price:BigDecimal,amount:Int): BigDecimal {
        var money = price * (amount * 100).toBigDecimal()
        money = money * 3.toMyDecimal() / 10000.toBigDecimal()
//        e("commission : ${money}  less than 5.0 ? ${money < 5.toBigDecimal()}")
        return if (money < 5.toBigDecimal()) 5.0.toBigDecimal() else money
    }


    /**
     * 过户费   十万2
     * price amount
     */
    fun transferFee(price:BigDecimal,amount:Int): BigDecimal {
        var money = price *( amount * 100).toBigDecimal()
        return money * 2.toMyDecimal() / 100000.toBigDecimal()
    }
}