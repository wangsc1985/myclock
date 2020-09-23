package com.wang17.myclock.database.utils

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.wang17.myclock.database.BankToDo
import com.wang17.myclock.database.MarkDay
import com.wang17.myclock.database.RunLog
import com.wang17.myclock.database.Setting
import com.wang17.myclock.model.DateTime
import com.wang17.myclock.utils._Utils
import java.util.*

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
class DataContext(private val context: Context?) {
    private val dbHelper: DatabaseHelper

    //region BankToDo
    fun addBankToDo(bankToDo: BankToDo) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            //使用insert方法向表中插入数据
            val values = ContentValues()
            values.put("id", bankToDo.id.toString())
            values.put("dateTime", bankToDo.dateTime.timeInMillis)
            values.put("bankName", bankToDo.bankName)
            values.put("cardNumber", bankToDo.cardNumber)
            values.put("money", bankToDo.money)
            //调用方法插入数据
            db.insert("bankToDo", "id", values)
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun editBankToDo(bankToDo: BankToDo) {
        //获取数据库对象
        val db = dbHelper.writableDatabase
        //使用update方法更新表中的数据
        val values = ContentValues()
        values.put("dateTime", bankToDo.dateTime.timeInMillis)
        values.put("bankName", bankToDo.bankName)
        values.put("cardNumber", bankToDo.cardNumber)
        values.put("money", bankToDo.money)
        if (db.update("bankToDo", values, "id=?", arrayOf(bankToDo.id.toString())) == 0) {
            addBankToDo(bankToDo)
        }
        db.close()
    }

    fun getBankToDo(bankName: String, cardNumber: String): BankToDo? {
        try {
            //获取数据库对象
            val db = dbHelper.readableDatabase
            //查询获得游标
            val cursor = db.query("bankToDo", null, "bankName like ? AND cardNumber like ?", arrayOf(bankName, cardNumber), null, null, null)
            //判断游标是否为空
            while (cursor.moveToNext()) {
                val model = BankToDo( UUID.fromString(cursor.getString(0)),DateTime(cursor.getLong(1)),cursor.getString(2),cursor.getString(3),cursor.getDouble(4))
                cursor.close()
                db.close()
                return model
            }
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
        return null
    }

    //获取数据库对象
    val bankToDos: List<BankToDo>
        get() {
            val result: MutableList<BankToDo> = ArrayList()
            try {
                //获取数据库对象
                val db = dbHelper.readableDatabase
                //查询获得游标
                val cursor = db.query("bankToDo", null, null, null, null, null, "dateTime ASC")
                //判断游标是否为空
                while (cursor.moveToNext()) {
                    val model = BankToDo(UUID.fromString(cursor.getString(0)),DateTime(cursor.getLong(1)), cursor.getString(2), cursor.getString(3), cursor.getDouble(4))
                    result.add(model)
                }
                cursor.close()
                db.close()
            } catch (e: Exception) {
                _Utils.printException(context, e)
            }
            return result
        }

    fun deleteBankToDo(bankName: String, cardNumber: String?) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            if (cardNumber == null) db.delete("bankToDo", "bankName like ? ", arrayOf(bankName)) else db.delete("bankToDo", "bankName like ? AND cardNumber like ?", arrayOf(bankName, cardNumber))
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun deleteBankToDo(id: UUID) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("bankToDo", "id=?", arrayOf(id.toString()))
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun deleteAllBankToDo() {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("bankToDo", null, null)
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    //endregion
    //region DayItem
    fun addDayItem(dayItem: DayItem) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            //使用insert方法向表中插入数据
            val values = ContentValues()
            values.put("id", dayItem.id.toString())
            values.put("name", dayItem.name.toString())
            values.put("summary", dayItem.summary)
            values.put("targetInHour", dayItem.targetInHour)

            //调用方法插入数据
            db.insert("dayItem", "id", values)
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    fun getDayItem(id: UUID): DayItem? {
        val result: List<MarkDay> = ArrayList()
        try {
            //获取数据库对象
            val db = dbHelper.readableDatabase
            //查询获得游标
            val cursor = db.query("dayItem", null, "id = ?", arrayOf(id.toString()), null, null, null)
            //判断游标是否为空
            while (cursor.moveToNext()) {
                val model = DayItem(UUID.fromString(cursor.getString(0)))
                model.name = cursor.getString(1)
                model.summary = cursor.getString(2)
                model.targetInHour = cursor.getInt(3)
                cursor.close()
                db.close()
                return model
            }
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
        return null
    }

    fun getDayItems(vararg isNameASC: Boolean): List<DayItem> {
        val result: MutableList<DayItem> = ArrayList()
        try {
            //获取数据库对象
            val db = dbHelper.readableDatabase
            //查询获得游标
            var cursor = db.query("dayItem", null, null, null, null, null, null)
            if (isNameASC.size == 1) {
                cursor = db.query("dayItem", null, null, null, null, null, if (isNameASC[0]) "name ASC" else null)
            }
            //判断游标是否为空
            while (cursor.moveToNext()) {
                val model = DayItem(UUID.fromString(cursor.getString(0)))
                model.name = cursor.getString(1)
                model.summary = cursor.getString(2)
                model.targetInHour = cursor.getInt(3)
                result.add(model)
            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
        return result
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    fun editDayItem(dayItem: DayItem) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase

            //使用update方法更新表中的数据
            val values = ContentValues()
            values.put("name", dayItem.name.toString())
            values.put("summary", dayItem.summary)
            values.put("targetInHour", dayItem.targetInHour)
            db.update("dayItem", values, "id=?", arrayOf(dayItem.id.toString()))
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    /**
     * 删除指定的record
     *
     * @param id
     */
    fun deleteDayItem(id: UUID) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("dayItem", "id=?", arrayOf(id.toString()))
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }
    //endregion
    //region MarkDay
    /**
     * 增加一条SexualDay
     *
     * @param markDay 记录对象
     */
    fun addMarkDay(markDay: MarkDay) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            //使用insert方法向表中插入数据
            val values = ContentValues()
            values.put("id", markDay.id.toString())
            values.put("dateTime", markDay.dateTime.timeInMillis)
            values.put("item", markDay.item.toString())
            values.put("summary", markDay.summary)

            //调用方法插入数据
            db.insert("markDay", "id", values)
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    fun getLastMarkDay(itemId: UUID): MarkDay? {
        try {
            //获取数据库对象
            val db = dbHelper.readableDatabase
            //查询获得游标
            val cursor = db.query("markDay", null, "item like ?", arrayOf(itemId.toString()), null, null, "DateTime  DESC")
            //判断游标是否为空
            if (cursor.moveToNext()) {
                val model = MarkDay()
                model.id=UUID.fromString(cursor.getString(0))
                model.dateTime = DateTime(cursor.getLong(1))
                model.item = UUID.fromString(cursor.getString(2))
                model.summary = cursor.getString(3)
                cursor.close()
                db.close()
                return model
            }
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
        return null
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    fun getMarkDays(item: UUID, isTimeDesc: Boolean): List<MarkDay> {
        val result: MutableList<MarkDay> = ArrayList()
        try {
            //获取数据库对象
            val db = dbHelper.readableDatabase
            //查询获得游标
            val cursor = db.query("markDay", null, "item like ?", arrayOf(item.toString()), null, null, if (isTimeDesc) "DateTime DESC" else null)
            //判断游标是否为空
            while (cursor.moveToNext()) {
                val model = MarkDay()
                model.id=UUID.fromString(cursor.getString(0))
                model.dateTime = DateTime(cursor.getLong(1))
                model.item = UUID.fromString(cursor.getString(2))
                model.summary = cursor.getString(3)
                result.add(model)
            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
        return result
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    fun editMarkDay(markDay: MarkDay) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase

            //使用update方法更新表中的数据
            val values = ContentValues()
            values.put("dateTime", markDay.dateTime.timeInMillis)
            values.put("item", markDay.item.toString())
            values.put("summary", markDay.summary)
            db.update("markDay", values, "id=?", arrayOf(markDay.id.toString()))
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    fun hideMarkDay(item: UUID, dateTime: DateTime) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.execSQL("update markDay set summary='hide' where item='" + item + "' and dateTime<=" + dateTime.timeInMillis)
            Log.e("wangsc", "update markDay set summary='hide' where item='" + item + "' and dateTime<=" + dateTime.timeInMillis)
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    /**
     * 删除指定的record
     *
     * @param itemId
     */
    fun deleteMarkDayBy(itemId: UUID) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("markDay", "item=?", arrayOf(itemId.toString()))
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    /**
     * 删除指定的record
     *
     * @param id
     */
    fun deleteMarkDay(id: UUID) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("markDay", "id=?", arrayOf(id.toString()))
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }//获取数据库对象

    //查询获得游标
    //判断游标是否为空
    //endregion
    //region RunLog
    val runLogs: List<RunLog>
        get() {
            val result: MutableList<RunLog> = ArrayList()
            try {
                //获取数据库对象
                val db = dbHelper.readableDatabase
                //查询获得游标
                val cursor = db.query("runLog", null, null, null, null, null, "runTime DESC")
                //判断游标是否为空
                while (cursor.moveToNext()) {
                    val model = RunLog()
                    model.id=UUID.fromString(cursor.getString(0))
                    model.runTime = DateTime(cursor.getLong(1))
                    model.tag = cursor.getString(2)
                    model.item = cursor.getString(3)
                    model.message = cursor.getString(4)
                    result.add(model)
                }
                cursor.close()
                db.close()
            } catch (e: Exception) {
                _Utils.printException(context, e)
            }
            return result
        }

    fun getRunLogsByEquals(item: String): List<RunLog> {
        val result: MutableList<RunLog> = ArrayList()
        try {
            //获取数据库对象
            val db = dbHelper.readableDatabase
            //查询获得游标
            val cursor = db.query("runLog", null, "item like ?", arrayOf(item), null, null, "runTime DESC")
            //判断游标是否为空
            while (cursor.moveToNext()) {
                val model = RunLog()
                model.id=UUID.fromString(cursor.getString(0))
                model.runTime = DateTime(cursor.getLong(1))
                model.tag = cursor.getString(2)
                model.item = cursor.getString(3)
                model.message = cursor.getString(4)
                result.add(model)
            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
        return result
    }

    fun getRunLogsByLike(itemLike: Array<String>): List<RunLog> {
        val result: MutableList<RunLog> = ArrayList()
        var where = ""
        for (i in itemLike.indices) {
            where += " item like  ? "
            if (i < itemLike.size - 1) {
                where += "OR"
            }
        }
        val whereArg = arrayOfNulls<String>(itemLike.size)
        for (i in itemLike.indices) {
            whereArg[i] = "%" + itemLike[i] + "%"
        }
        try {
            //获取数据库对象
            val db = dbHelper.readableDatabase
            //查询获得游标
            val cursor = db.query("runLog", null, where, whereArg, null, null, "runTime DESC")
            //判断游标是否为空
            while (cursor.moveToNext()) {
                val model = RunLog()
                model.id=UUID.fromString(cursor.getString(0))
                model.runTime = DateTime(cursor.getLong(1))
                model.tag = cursor.getString(2)
                model.item = cursor.getString(3)
                model.message = cursor.getString(4)
                result.add(model)
            }
            cursor.close()
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
        return result
    }

    fun addRunLog(runLog: RunLog) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            //使用insert方法向表中插入数据
            val values = ContentValues()
            values.put("id", runLog.id.toString())
            values.put("runTime", runLog.runTime.timeInMillis)
            values.put("tag", runLog.tag)
            values.put("item", runLog.item)
            values.put("message", runLog.message)
            //调用方法插入数据
            db.insert("runLog", "id", values)
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun addRunLog(item: String?, message: String?) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            //使用insert方法向表中插入数据
            val values = ContentValues()
            values.put("id", UUID.randomUUID().toString())
            values.put("runTime", System.currentTimeMillis())
            values.put("tag", DateTime(System.currentTimeMillis()).toLongDateTimeString())
            values.put("item", item)
            values.put("message", message)
            //调用方法插入数据
            db.insert("runLog", "id", values)
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun updateRunLog(runLog: RunLog) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase

            //使用update方法更新表中的数据
            val values = ContentValues()
            values.put("runTime", runLog.runTime.timeInMillis)
            values.put("tag", runLog.tag)
            values.put("item", runLog.item)
            values.put("message", runLog.message)
            db.update("runLog", values, "id=?", arrayOf(runLog.id.toString()))
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun clearRunLog() {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("runLog", null, null)
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun deleteRunLogByEquals(item: String) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("runLog", "item like ?", arrayOf("%$item%"))
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun deleteRunLogByLike(itemLike: String) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("runLog", "item like ?", arrayOf("%$itemLike%"))
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    fun deleteRunLog(id: UUID) {
        try {
            //获取数据库对象
            val db = dbHelper.writableDatabase
            db.delete("runLog", "id = ?", arrayOf(id.toString()))
            //关闭SQLiteDatabase对象
            db.close()
        } catch (e: Exception) {
            _Utils.printException(context, e)
        }
    }

    //endregion
    //region Setting
    fun getSetting(name: Any): Setting? {

        //获取数据库对象
        val db = dbHelper.readableDatabase
        //查询获得游标
        val cursor = db.query("setting", null, "name=?", arrayOf(name.toString()), null, null, null)
        //判断游标是否为空
        while (cursor.moveToNext()) {
            val setting = Setting(name.toString(), cursor.getString(1))
            cursor.close()
            db.close()
            return setting
        }
        return null
    }

    fun getSetting(name: Any, defaultValue: Any): Setting {
        var setting = getSetting(name)
        if (setting == null) {
            addSetting(name, defaultValue)
            setting = Setting(name, defaultValue)
            return setting
        }
        return setting
    }

    private fun e(string: Any) {
        Log.e("wangsc",string.toString())
    }

    /**
     * 修改制定key配置，如果不存在则创建。
     *
     * @param name
     * @param value
     */
    fun editSetting(name: Any, value: Any) {
        //获取数据库对象
        val db = dbHelper.writableDatabase
        //使用update方法更新表中的数据
        val values = ContentValues()
        values.put("value", value.toString())
        if (db.update("setting", values, "name=?", arrayOf(name.toString())) == 0) {
            addSetting(name, value.toString())
        }
        db.close()
    }

    fun editSettingLevel(name: Any, level: Int) {
        //获取数据库对象
        val db = dbHelper.writableDatabase
        //使用update方法更新表中的数据
        val values = ContentValues()
        values.put("level", level.toString() + "")
        db.update("setting", values, "name=?", arrayOf(name.toString()))
        db.close()
    }

    fun deleteSetting(name: Any) {
        //获取数据库对象
        val db = dbHelper.writableDatabase
        db.delete("setting", "name=?", arrayOf(name.toString()))
        //        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND name="+name;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close()
    }

    fun deleteSetting(name: String) {
        //获取数据库对象
        val db = dbHelper.writableDatabase
        db.delete("setting", "name=?", arrayOf(name))
        //        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND name="+name;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close()
    }

    fun addSetting(name: Any, value: Any) {
        //获取数据库对象
        val db = dbHelper.writableDatabase
        //使用insert方法向表中插入数据
        val values = ContentValues()
        values.put("name", name.toString())
        values.put("value", value.toString())
        //调用方法插入数据
        db.insert("setting", "name", values)
        //关闭SQLiteDatabase对象
        db.close()
    }

    //获取数据库对象
    val settings: List<Setting>
        get() {
            val result: MutableList<Setting> = ArrayList()
            //获取数据库对象
            val db = dbHelper.readableDatabase
            //查询获得游标
            val cursor = db.query("setting", null, null, null, null, null, "level,name")
            //判断游标是否为空
            while (cursor.moveToNext()) {
                val setting = Setting(cursor.getString(0), cursor.getString(1))
                result.add(setting)
            }
            cursor.close()
            db.close()
            return result
        }

    fun clearSetting() {
        //获取数据库对象
        val db = dbHelper.writableDatabase
        db.delete("setting", null, null)
        //        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND key="+key;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close()
    } //endregion

    init {
        dbHelper = DatabaseHelper(context)
    }
}