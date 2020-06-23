package com.wang17.myclock.model;


import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by 阿弥陀佛 on 2015/6/24.
 */
public class DateTime extends GregorianCalendar {

    public DateTime() {
        this.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//        this.setTimeInMillis(System.currentTimeMillis());
    }

    public DateTime(long millinseconds) {
        this.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        this.setTimeInMillis(millinseconds);
    }

    public DateTime(int year, int month, int day) {
        this.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        this.set(year, month, day, 0, 0, 0);
        this.set(Calendar.MILLISECOND, 0);
    }

    public DateTime(int year, int month, int day, int hour, int minute, int second) {
        this.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        this.set(year, month, day, hour, minute, second);
        this.set(Calendar.MILLISECOND, 0);
    }
    public DateTime(int hour, int minute) {
        this.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        this.set(Calendar.HOUR_OF_DAY, hour);
        this.set(Calendar.MINUTE, minute);
        this.set(Calendar.SECOND,0);
        this.set(Calendar.MILLISECOND, 0);
    }

    public static DateTime getToday() {
        DateTime today = new DateTime();
        return today.getDate();
    }

    /**
     * 返回一个时、分、秒、毫秒置零的此DateTime副本。
     *
     * @return
     */
    public DateTime getDate() {
        return new DateTime(this.get(YEAR), this.get(MONTH), this.get(DAY_OF_MONTH));
    }

    public DateTime addMonths(int months) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(MONTH, months);
        return dateTime;
    }

    public DateTime addDays(int days) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(DAY_OF_MONTH, days);
        return dateTime;
    }

    public DateTime addHours(int hours) {
        DateTime dateTime = (DateTime) this.clone();
        dateTime.add(HOUR_OF_DAY, hours);
        return dateTime;
    }

    public int getYear() {
        return this.get(YEAR);
    }

    public int getMonth() {
        return this.get(MONTH);
    }

    public int getDay() {
        return this.get(DAY_OF_MONTH);
    }

    public int getHour() {
        return this.get(HOUR_OF_DAY);
    }

    public int getMinite() {
        return this.get(MINUTE);
    }

    public int getSecond() {
        return this.get(SECOND);
    }

    public String getMonthStr() {
        int tt = this.getMonth() + 1;
        return tt < 10 ? "0" + tt : "" + tt;
    }

    public String getDayStr() {
        int tt = this.getDay();
        return tt < 10 ? "0" + tt : "" + tt;
    }

    public String getHourStr() {
        int tt = this.getHour();
        return tt < 10 ? "0" + tt : "" + tt;
    }

    public String getMiniteStr() {
        int tt = this.getMinite();
        return tt < 10 ? "0" + tt : "" + tt;
    }

    public String getSecondStr() {
        int tt = this.getSecond();
        return tt < 10 ? "0" + tt : "" + tt;
    }

    /**
     * 格式：****@/**@/**
     *
     * @return
     */
    public String toShortDateString() {
        return _String.concat(this.getYear(), "/", this.getMonthStr(), "/", this.getDayStr());
    }
    /**
     * 格式：****年**月**日
     *
     * @return
     */
    public String toShortDateString3() {
        return _String.concat(this.getYear(), "年", this.getMonthStr(), "月", this.getDayStr(),"日");
    }

    /**
     * 格式：**@/**
     *
     * @return
     */
    public String toShortDateString1() {
        return _String.concat( this.getMonthStr(), "/", this.getDayStr());
    }
    /**
     * 格式：**@/**  **:**
     *
     * @return
     */
    public String toLongDateString2() {
        return _String.concat( this.getMonthStr(), "/", this.getDayStr()," ",this.getHourStr(), ":", this.getMiniteStr());
    }

    /**
     * 格式：****@/**@/**  **:**:**
     *
     * @return
     */
    public String toLongDateTimeString() {
        return _String.concat(toShortDateString(), "  ", toTimeString());
    }
    /**
     * 格式：****@/**@/**  **:**
     *
     * @return
     */
    public String toLongDateTimeString1() {
        return _String.concat(toShortDateString(), "  ",this.getHourStr(), ":", this.getMiniteStr());
    }

    /**
     * 格式：**:**:**
     *
     * @return
     */
    public String toTimeString() {
        return _String.concat(this.getHourStr(), ":", this.getMiniteStr(), ":", this.getSecondStr());
    }

    /**
     * 格式：**:**
     *
     * @return
     */
    public String toShortTimeString() {
        return _String.concat(this.getHourStr(), ":", this.getMiniteStr());
    }

    /**
     * 格式：*天*小时*分钟*秒
     *
     * @return
     */
//    public static String toSpanStringSecond(long timeInMillis) {
//        int second = (int) (timeInMillis / 1000 % 60);
//        int minite = (int) (timeInMillis / 60000 % 60);
//        int hour = (int) (timeInMillis / 60000 / 60 % 24);
//        int day = (int) (timeInMillis / 60000 / 60 / 24);
//        if (day == 0 && hour == 0 && minite == 0) {
//            return second + "秒";
//        }
//        return _String.concat(day > 0 ? day + "天" : "", hour > 0 ? hour + "小时" : "", minite > 0 ? minite + "分钟" : "", second > 0 ? second + "秒" : "");
//    }

    /**
     * 格式：*天*小时*分钟
     *
     * @return
     */
//    public static String toSpanStringMin(long timeInMillis) {
//        int minite = (int) (timeInMillis / 60000 % 60);
//        int hour = (int) (timeInMillis / 60000 / 60 % 24);
//        int day = (int) (timeInMillis / 60000 / 60 / 24);
//        if (day == 0 && hour == 0) {
//            return minite + "分钟";
//        }
//        return _String.concat(day > 0 ? day + "天" : "", hour > 0 ? hour + "小时" : "", minite > 0 ? minite + "分钟" : "");
//    }

    /**
     * 格式：*天*小时*分钟*秒
     *
     * @param timeInMillis
     * @param startTag     开始标志 1：秒；2：分；3：时；4：天
     * @param endTag       开始标志 1：秒；2：分；3：时；4：天
     * @return
     */
    public static String toSpanString2(long timeInMillis, int startTag, int endTag) throws Exception {
        if (startTag < endTag)
            throw new Exception("开始标志必须大于等于结束标志");

        String resutl = "";
        int day = (int) (timeInMillis / 60000 / 60 / 24);
        int hour = (int) (timeInMillis / 60000 / 60 % 24);
        if (startTag == 3)
            hour += day * 24;
        int minite = (int) (timeInMillis / 60000 % 60);
        if (startTag == 2)
            minite += hour * 60;
        int second = (int) (timeInMillis / 1000 % 60);
        if (startTag == 1)
            second += minite * 60;
        switch (startTag) {
            case 4:
                resutl += day > 0 ? day + "天" : "";
                if (endTag == 4) {
                    if (day == 0) {
                        return day + "天";
                    }
                    break;
                }
            case 3:
                String sHour = hour > 9 ? "" + hour : "0" + hour;
                resutl += hour > 0 ? sHour + "小时" : "";
                if (endTag == 3) {
                    if (day == 0 && hour == 0) {
                        return sHour + "小时";
                    }
                    break;
                }
            case 2:
                String sMinite = minite > 9 ? "" + minite : "0" + minite;
                resutl += minite > 0 ? sMinite + "分钟" : "";
                if (endTag == 2) {
                    if (day == 0 && hour == 0 && minite == 0) {
                        return sMinite + "分钟";
                    }
                    break;
                }
            case 1:
                String sSecond = second > 9 ? "" + second : "0" + second;
                resutl += second > 0 ? sSecond + "秒" : "";

                if (day == 0 && hour == 0 && minite == 0 && second == 0) {
                    return sSecond + "秒";
                }
        }
        return resutl;
    }

    /**
     * 格式：*天*小时*分钟*秒
     *
     * @param timeInMillis
     * @param startTag     开始标志 1：秒；2：分；3：时；4：天
     * @param endTag       开始标志 1：秒；2：分；3：时；4：天
     * @return
     */
    public static String toSpanString(long timeInMillis, int startTag, int endTag) throws Exception {
        if (startTag < endTag)
            throw new Exception("开始标志必须大于等于结束标志");

        String resutl = "";
        int day = (int) (timeInMillis / 60000 / 60 / 24);
        int hour = (int) (timeInMillis / 60000 / 60 % 24);
        if (startTag == 3)
            hour += day * 24;
        int minite = (int) (timeInMillis / 60000 % 60);
        if (startTag == 2)
            minite += hour * 60;
        int second = (int) (timeInMillis / 1000 % 60);
        if (startTag == 1)
            second += minite * 60;
        switch (startTag) {
            case 4:
                resutl += day > 0 ? day + "天" : "";
                if (endTag == 4) {
                    if (day == 0) {
                        return day + "天";
                    }
                    break;
                }
            case 3:
                resutl += hour > 0 ? hour + "小时" : "";
                if (endTag == 3) {
                    if (day == 0 && hour == 0) {
                        return hour + "小时";
                    }
                    break;
                }
            case 2:
                resutl += minite > 0 ? minite + "分钟" : "";
                if (endTag == 2) {
                    if (day == 0 && hour == 0 && minite == 0) {
                        return minite + "分钟";
                    }
                    break;
                }
            case 1:
                resutl += second > 0 ? second + "秒" : "";

                if (day == 0 && hour == 0 && minite == 0 && second == 0) {
                    return second + "秒";
                }
        }
        return resutl;
    }

    /**
     * 格式：最大显示xx:xx:xx  最小显示xx:xx
     *
     * @param timeInMillis
     * @return
     */
    public static String toSpanString(long timeInMillis) {

        String resutl = "";
        int hour = (int) (timeInMillis / 60000 / 60);
        int minite = (int) (timeInMillis / 60000 % 60);
        int second = (int) (timeInMillis / 1000 % 60);
        if (hour == 0) {
            return minite + ":" + (second < 10 ? "0" + second : second + "");
        } else {
            return hour + ":" + (minite < 10 ? "0" + minite : minite + "") + ":" + (second < 10 ? "0" + second : second + "");
        }
    }

    /**
     * 格式：*天*小时
     *
     * @param timeInHours
     * @return
     */
    public static String toSpanString1(int timeInHours) {

        String resutl = "";
        int day = (int) (timeInHours / 24);
        int hour = (int) (timeInHours % 24);
        resutl += day > 0 ? day + "天" : "";
        resutl += hour > 0 ? hour + "小时" : "";
        if (day == 0 && hour == 0)
            resutl = hour + "小时";
        return resutl;
    }



    /**
     * date2比date1多的天数
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int dayOffset(DateTime date1, DateTime date2) {


        long date1l = date1.getTimeInMillis()-date1.getHour()*3600000-date1.getMinite()*60000-date1.getSecond()*1000;
        long date2l = date2.getTimeInMillis()-date2.getHour()*3600000-date2.getMinite()*60000-date2.getSecond()*1000;
        long date1Days = date1l/ (3600 * 1000 * 24);
        long date2Days = date2l / (3600 * 1000 * 24);

        return (int) (date2Days - date1Days);


//        int day1 = date1.get(Calendar.DAY_OF_YEAR);
//        int day2 = date2.get(Calendar.DAY_OF_YEAR);
//
//        int year1 = date1.get(Calendar.YEAR);
//        int year2 = date2.get(Calendar.YEAR);
//        if (year1 != year2) {  //同一年
//            int timeDistance = 0;
//            for (int i = year1; i < year2; i++) {
//                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) {  //闰年
//                    timeDistance += 366;
//                } else {  //不是闰年
//
//                    timeDistance += 365;
//                }
//            }
//            return timeDistance + (day2 - day1);
//        } else { //不同年
//            return day2 - day1;
//        }
    }

    /**
     * date2比date1多的周数
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static int calcWeekOffset(DateTime startTime, DateTime endTime) {
        int dayOfWeek = startTime.get(Calendar.DAY_OF_WEEK);
        dayOfWeek = dayOfWeek - 1;
        if (dayOfWeek == 0) dayOfWeek = 7;

        int dayOffset = dayOffset(startTime, endTime);

        int weekOffset = dayOffset / 7;
        int a;
        if (dayOffset > 0) {
            a = (dayOffset % 7 + dayOfWeek > 7) ? 1 : 0;
        } else {
            a = (dayOfWeek + dayOffset % 7 < 1) ? -1 : 0;
        }
        weekOffset = weekOffset + a;
        return weekOffset;
    }

    public String getWeekDayStr() {
        switch (this.get(DAY_OF_WEEK)-1){
            case 1:return "周一";
            case 2:return "周二";
            case 3:return "周三";
            case 4:return "周四";
            case 5:return "周五";
            case 6:return "周六";
            case 0:return "周日";
        }
        return "";
    }
}
