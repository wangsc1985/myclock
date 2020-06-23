package com.wang17.myclock.database.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import com.wang17.myclock.database.BankToDo;
import com.wang17.myclock.database.MarkDay;
import com.wang17.myclock.database.RunLog;
import com.wang17.myclock.database.Setting;
import com.wang17.myclock.model.DateTime;
import com.wang17.myclock.utils._Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by 阿弥陀佛 on 2015/11/18.
 */
public class DataContext {

    private DatabaseHelper dbHelper;
    private Context context;

    public DataContext(Context context) {
        this.context = context;
        dbHelper = new DatabaseHelper(context);
    }


    //region BankToDo


    public void addBankToDo(BankToDo bankToDo) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", bankToDo.getId().toString());
            values.put("dateTime", bankToDo.getDateTime().getTimeInMillis());
            values.put("bankName", bankToDo.getBankName());
            values.put("cardNumber", bankToDo.getCardNumber());
            values.put("money", bankToDo.getMoney());
            //调用方法插入数据
            db.insert("bankToDo", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }


    public void editBankToDo(BankToDo bankToDo) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用update方法更新表中的数据
        ContentValues values = new ContentValues();
        values.put("dateTime", bankToDo.getDateTime().getTimeInMillis());
        values.put("bankName", bankToDo.getBankName());
        values.put("cardNumber", bankToDo.getCardNumber());
        values.put("money", bankToDo.getMoney());


        if (db.update("bankToDo", values, "id=?", new String[]{bankToDo.getId().toString()}) == 0) {
            this.addBankToDo(bankToDo);
        }
        db.close();
    }

    public BankToDo getBankToDo(String bankName, String cardNumber) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("bankToDo", null, "bankName like ? AND cardNumber like ?", new String[]{bankName, cardNumber}, null, null, null);
            //判断游标是否为空
            while (cursor.moveToNext()) {
                BankToDo model = new BankToDo(new DateTime(cursor.getLong(1)), cursor.getString(2), cursor.getString(3), cursor.getDouble(4));
                model.setId(UUID.fromString(cursor.getString(0)));
                cursor.close();
                db.close();
                return model;
            }
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return null;
    }

    public List<BankToDo> getBankToDos() {
        List<BankToDo> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("bankToDo", null, null, null, null, null, "dateTime ASC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                BankToDo model = new BankToDo(new DateTime(cursor.getLong(1)), cursor.getString(2), cursor.getString(3), cursor.getDouble(4));
                model.setId(UUID.fromString(cursor.getString(0)));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public void deleteBankToDo(String bankName, String cardNumber) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            if (cardNumber == null)
                db.delete("bankToDo", "bankName like ? ", new String[]{bankName});
            else
                db.delete("bankToDo", "bankName like ? AND cardNumber like ?", new String[]{bankName, cardNumber});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteBankToDo(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("bankToDo", "id=?", new String[]{id.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteAllBankToDo() {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("bankToDo", null, null);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    //endregion

    //region DayItem
    public void addDayItem(DayItem dayItem) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", dayItem.getId().toString());
            values.put("name", dayItem.getName().toString());
            values.put("summary", dayItem.getSummary());
            values.put("targetInHour", dayItem.getTargetInHour());

            //调用方法插入数据
            db.insert("dayItem", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public DayItem getDayItem(UUID id) {

        List<MarkDay> result = new ArrayList<MarkDay>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("dayItem", null, "id = ?", new String[]{id.toString()}, null, null, null);
            //判断游标是否为空
            while (cursor.moveToNext()) {
                DayItem model = new DayItem(UUID.fromString(cursor.getString(0)));
                model.setName(cursor.getString(1));
                model.setSummary(cursor.getString(2));
                model.setTargetInHour(cursor.getInt(3));
                cursor.close();
                db.close();
                return model;
            }
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return null;
    }

    public List<DayItem> getDayItems(boolean... isNameASC) {
        List<DayItem> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("dayItem", null, null, null, null, null, null);
            if (isNameASC.length == 1) {
                cursor = db.query("dayItem", null, null, null, null, null, isNameASC[0] ? "name ASC" : null);
            }
            //判断游标是否为空
            while (cursor.moveToNext()) {
                DayItem model = new DayItem(UUID.fromString(cursor.getString(0)));
                model.setName(cursor.getString(1));
                model.setSummary(cursor.getString(2));
                model.setTargetInHour(cursor.getInt(3));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }


    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public void editDayItem(DayItem dayItem) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //使用update方法更新表中的数据
            ContentValues values = new ContentValues();
            values.put("name", dayItem.getName().toString());
            values.put("summary", dayItem.getSummary());
            values.put("targetInHour", dayItem.getTargetInHour());

            db.update("dayItem", values, "id=?", new String[]{dayItem.getId().toString()});
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    /**
     * 删除指定的record
     *
     * @param id
     */
    public void deleteDayItem(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("dayItem", "id=?", new String[]{id.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
    //endregion

    //region MarkDay


    /**
     * 增加一条SexualDay
     *
     * @param markDay 记录对象
     */
    public void addMarkDay(MarkDay markDay) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", markDay.getId().toString());
            values.put("dateTime", markDay.getDateTime().getTimeInMillis());
            values.put("item", markDay.getItem().toString());
            values.put("summary", markDay.getSummary());

            //调用方法插入数据
            db.insert("markDay", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public MarkDay getLastMarkDay(UUID itemId) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("markDay", null, "item like ?", new String[]{itemId.toString()}, null, null, "DateTime  DESC");
            //判断游标是否为空

            if (cursor.moveToNext()) {
                MarkDay model = new MarkDay(UUID.fromString(cursor.getString(0)));
                model.setDateTime(new DateTime(cursor.getLong(1)));
                model.setItem(UUID.fromString(cursor.getString(2)));
                model.setSummary(cursor.getString(3));
                cursor.close();
                db.close();
                return model;
            }
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return null;
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public List<MarkDay> getMarkDays(UUID item, boolean isTimeDesc) {

        List<MarkDay> result = new ArrayList<MarkDay>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("markDay", null, "item like ?", new String[]{item.toString()}, null, null, isTimeDesc ? "DateTime DESC" : null);
            //判断游标是否为空
            while (cursor.moveToNext()) {
                MarkDay model = new MarkDay(UUID.fromString(cursor.getString(0)));
                model.setDateTime(new DateTime(cursor.getLong(1)));
                model.setItem(UUID.fromString(cursor.getString(2)));
                model.setSummary(cursor.getString(3));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public void editMarkDay(MarkDay markDay) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //使用update方法更新表中的数据
            ContentValues values = new ContentValues();
            values.put("dateTime", markDay.getDateTime().getTimeInMillis());
            values.put("item", markDay.getItem().toString());
            values.put("summary", markDay.getSummary());

            db.update("markDay", values, "id=?", new String[]{markDay.getId().toString()});
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }


    /**
     * 得到所有SexualDay
     *
     * @return
     */
    public void hideMarkDay(UUID item, DateTime dateTime) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL("update markDay set summary='hide' where item='" + item + "' and dateTime<=" + dateTime.getTimeInMillis());
            Log.e("wangsc", "update markDay set summary='hide' where item='" + item + "' and dateTime<=" + dateTime.getTimeInMillis());
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    /**
     * 删除指定的record
     *
     * @param itemId
     */
    public void deleteMarkDayBy(UUID itemId) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("markDay", "item=?", new String[]{itemId.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    /**
     * 删除指定的record
     *
     * @param id
     */
    public void deleteMarkDay(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("markDay", "id=?", new String[]{id.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
    //endregion

    //region RunLog
    public List<RunLog> getRunLogs() {
        List<RunLog> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, null, null, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public List<RunLog> getRunLogsByEquals(String item) {
        List<RunLog> result = new ArrayList<>();
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, "item like ?", new String[]{item}, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public List<RunLog> getRunLogsByLike(String[] itemLike) {
        List<RunLog> result = new ArrayList<>();
        String where = "";
        for (int i = 0; i < itemLike.length; i++) {
            where += " item like  ? ";
            if (i < itemLike.length - 1) {
                where += "OR";
            }
        }
        String[] whereArg = new String[itemLike.length];
        for (int i = 0; i < itemLike.length; i++) {
            whereArg[i] = "%" + itemLike[i] + "%";
        }

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //查询获得游标
            Cursor cursor = db.query("runLog", null, where, whereArg, null, null, "runTime DESC");
            //判断游标是否为空
            while (cursor.moveToNext()) {
                RunLog model = new RunLog(UUID.fromString(cursor.getString(0)));
                model.setRunTime(new DateTime(cursor.getLong(1)));
                model.setTag(cursor.getString(2));
                model.setItem(cursor.getString(3));
                model.setMessage(cursor.getString(4));
                result.add(model);
            }
            cursor.close();
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
        return result;
    }

    public void addRunLog(RunLog runLog) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", runLog.getId().toString());
            values.put("runTime", runLog.getRunTime().getTimeInMillis());
            values.put("tag", runLog.getTag());
            values.put("item", runLog.getItem());
            values.put("message", runLog.getMessage());
            //调用方法插入数据
            db.insert("runLog", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void addRunLog(String item, String message) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //使用insert方法向表中插入数据
            ContentValues values = new ContentValues();
            values.put("id", UUID.randomUUID().toString());
            values.put("runTime", System.currentTimeMillis());
            values.put("tag", new DateTime(System.currentTimeMillis()).toLongDateTimeString());
            values.put("item", item);
            values.put("message", message);
            //调用方法插入数据
            db.insert("runLog", "id", values);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void updateRunLog(RunLog runLog) {

        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            //使用update方法更新表中的数据
            ContentValues values = new ContentValues();
            values.put("runTime", runLog.getRunTime().getTimeInMillis());
            values.put("tag", runLog.getTag());
            values.put("item", runLog.getItem());
            values.put("message", runLog.getMessage());

            db.update("runLog", values, "id=?", new String[]{runLog.getId().toString()});
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void clearRunLog() {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", null, null);
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLogByEquals(String item) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "item like ?", new String[]{"%" + item + "%"});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLogByLike(String itemLike) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "item like ?", new String[]{"%" + itemLike + "%"});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }

    public void deleteRunLog(UUID id) {
        try {
            //获取数据库对象
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.delete("runLog", "id = ?", new String[]{id.toString()});
            //关闭SQLiteDatabase对象
            db.close();
        } catch (Exception e) {
            _Utils.printException(context, e);
        }
    }
    //endregion

    //region Setting
    public Setting getSetting(Object name) {

        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("setting", null, "name=?", new String[]{name.toString()}, null, null, null);
        //判断游标是否为空
        while (cursor.moveToNext()) {
            Setting setting = new Setting(name.toString(), cursor.getString(1),cursor.getInt(2));
            cursor.close();
            db.close();
            return setting;
        }
        return null;
    }

    public Setting getSetting(Object name, Object defaultValue) {
        Setting setting = getSetting(name);
        if (setting == null) {
            this.addSetting(name, defaultValue);
            setting = new Setting(name.toString(), defaultValue.toString(),100);
            return setting;
        }
        return setting;
    }

    /**
     * 修改制定key配置，如果不存在则创建。
     *
     * @param name
     * @param value
     */
    public void editSetting(Object name, Object value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用update方法更新表中的数据
        ContentValues values = new ContentValues();
        values.put("value", value.toString());
        if (db.update("setting", values, "name=?", new String[]{name.toString()}) == 0) {
            this.addSetting(name, value.toString());
        }
        db.close();
    }
    public void editSettingLevel(Object name, int level) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用update方法更新表中的数据
        ContentValues values = new ContentValues();
        values.put("level", level+"");
        db.update("setting", values, "name=?", new String[]{name.toString()});
        db.close();
    }

    public void deleteSetting(Object name) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", "name=?", new String[]{name.toString()});
//        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND name="+name;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public void deleteSetting(String name) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", "name=?", new String[]{name});
//        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND name="+name;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public void addSetting(Object name, Object value) {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        //使用insert方法向表中插入数据
        ContentValues values = new ContentValues();
        values.put("name", name.toString());
        values.put("value", value.toString());
        //调用方法插入数据
        db.insert("setting", "name", values);
        //关闭SQLiteDatabase对象
        db.close();
    }

    public List<Setting> getSettings() {
        List<Setting> result = new ArrayList<>();
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //查询获得游标
        Cursor cursor = db.query("setting", null, null, null, null, null, "level,name");
        //判断游标是否为空
        while (cursor.moveToNext()) {
            Setting setting = new Setting(cursor.getString(0), cursor.getString(1),cursor.getInt(2));
            result.add(setting);
        }
        cursor.close();
        db.close();
        return result;
    }

    public void clearSetting() {
        //获取数据库对象
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("setting", null, null);
        //        String sql = "DELETE FROM setting WHERE userId="+userId.toString()+" AND key="+key;
//        addLog(new Log(sql,userId),db);
        //关闭SQLiteDatabase对象
        db.close();
    }
    //endregion
}
