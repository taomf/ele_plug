package com.ygzy.eles.camera.utils;

import android.os.SystemClock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 说明：日期工具类
 */

public final class DateUtils {

    /**
     * 禁止实例化
     */
    private DateUtils() {
    }

    /**
     * 格式：【yyyy】
     */
    public static final String FORMAT_YYYY_1 = "yyyy";

    /**
     * 格式：【yyyy年】
     */
    public static final String FORMAT_YYYY_2 = "yyyy年";

    /**
     * 格式：【MM】
     */
    public static final String FORMAT_MM_1 = "MM";

    /**
     * 格式：【MM月】
     */
    public static final String FORMAT_MM_2 = "MM月";

    /**
     * 格式：【dd】
     */
    public static final String FORMAT_DD_1 = "dd";

    /**
     * 格式：【dd日】
     */
    public static final String FORMAT_DD_2 = "dd日";

    /**
     * 格式：【HH】
     */
    public static final String FORMAT_HH_1 = "HH";

    /**
     * 格式：【HH时】
     */
    public static final String FORMAT_HH_2 = "HH时";

    /**
     * 格式：【mm】
     */
    public static final String FORMAT_MM_3 = "mm";

    /**
     * 格式：【mm分】
     */
    public static final String FORMAT_MM_4 = "mm分";

    /**
     * 格式：【ss】
     */
    public static final String FORMAT_SS_1 = "ss";

    /**
     * 格式：【ss秒】
     */
    public static final String FORMAT_SS_2 = "ss秒";

    /**
     * 格式：【HH:mm】
     */
    public static final String FORMAT_HH_MM_1 = "HH:mm";

    /**
     * 格式：【HH时mm分】
     */
    public static final String FORMAT_HH_MM_2 = "HH时mm分";

    /**
     * 格式：【HH:mm:ss】
     */
    public static final String FORMAT_HH_MM_SS_1 = "HH:mm:ss";

    /**
     * 格式：【HH时mm分ss秒】
     */
    public static final String FORMAT_HH_MM_SS_2 = "HH时mm分ss秒";

    /**
     * 格式：【yyyy-MM-dd】
     */
    public static final String FORMAT_YYYY_MM_DD_1 = "yyyy-MM-dd";

    /**
     * 格式：【yyyy/MM/dd】
     */
    public static final String FORMAT_YYYY_MM_DD_2 = "yyyy/MM/dd";

    /**
     * 格式：【yyyy年MM月dd日】
     */
    public static final String FORMAT_YYYY_MM_DD_3 = "yyyy年MM月dd日";

    /**
     * 格式：【yyyy-MM-dd HH:mm:ss】
     */
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS_1 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 格式：【yyyy/MM/dd HH:mm:ss】
     */
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS_2 = "yyyy/MM/dd HH:mm:ss";

    /**
     * 格式：【yyyy年MM月dd日 HH时mm分ss秒】
     */
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS_3 = "yyyy年MM月dd日 HH时mm分ss秒";

    /**
     * 格式：【yyyyMMddHHmmss】
     */
    public static final String FORMAT_YYYY_MM_DD_HH_MM_SS_4 = "yyyyMMddHHmmss";

    /**
     * 格式：【yyyyMMddHHmmss】
     */
    public static final String[] WEEKDAYS = {"","星期日","星期一","星期二","星期三","星期四","星期五","星期六"};

    private static final SimpleDateFormat getSDF(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    private static final Calendar getCalendar() {
        return Calendar.getInstance();
    }

    /**
     * 说明：当前时间转数字
     *
     * @param format
     *            时间格式
     * @return
     */
    public static final long getNowTimeNum(String format) {
        try {
            return Long.parseLong(getNowTime(format));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 说明：获取Date对象
     * @return Date
     */
    public static final Date getNowTimeDate() {
        return new Date();
    }

    /**
     * 说明：获取当前时间（格式毫秒）
     * @return
     */
    public static final long getMillisecond(){
        return getNowTimeDate().getTime();
    }

    /**
     * 说明：现在时间格式化后的形式
     *
     * @param format
     *            时间格式
     * @return 格式化后的时间
     */
    public static final String getNowTime(String format) {
        SimpleDateFormat sdf = getSDF(format);
        return sdf.format(new Date());
    }

    /**
     * 说明：获取Unix时间戳（10位）1970年1月1日-当前时间 经过的秒数
     * @return
     */
    public static final long getUnixTime(){
        return SystemClock.uptimeMillis()/1000;
    }

    /**
     * 说明：字符串转Date
     *
     * @param oldDate
     *            要转的时间
     * @param format
     *            时间格式
     * @return
     */
    public static final Date getStrToDate(String oldDate, String format) {
        SimpleDateFormat sdf = getSDF(format);
        Date date = null;
        try {
            date = sdf.parse(oldDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 说明：一种格式时间转另一种格式时间
     *
     * @param oldDate
     *            要转的时间
     * @param oldFormat
     *            旧时间格式
     * @param newFormat
     *            新时间格式
     * @return
     */
    public static final String getStrToStr(String oldDate, String oldFormat,
                                           String newFormat) {
        Date date = getStrToDate(oldDate, oldFormat);
        SimpleDateFormat sdf = getSDF(newFormat);
        return sdf.format(date);
    }

    /**
     * 说明：将字符串时间转Long类型
     *
     * @param date
     *            要转的时间
     * @param format
     *            时间格式
     * @return
     */
    public static final long getStrToLong(String date, String format){
        return getStrToDate(date, format).getTime();
    }

    /**
     * 说明：获取当前时间，默认时间格式【yyyy-MM-dd HH:mm:ss】
     *
     * @return
     */
    public static final String getNowTime() {
        return getNowTime(FORMAT_YYYY_MM_DD_HH_MM_SS_1);
    }
    /**
     * 说明：获取当前时间，默认时间格式【yyyyMMddHHmmss】
     *
     * @return
     */
    public static final String getNowTimeSend(){
        return getNowTime(FORMAT_YYYY_MM_DD_HH_MM_SS_4);
    }

    /**
     * 说明：获取星期【星期一】
     * @return
     */
    public static int  getWeekDay(){
        Calendar calendar = getCalendar();
        calendar.setTime(getNowTimeDate());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static String getWeekDayS(){
        int weekDay = getWeekDay();
        String s = "星期";
        switch (weekDay){
            case 1:
                s+="日";
                break;
            case 2:
                s+="一";
                break;
            case 3:
                s+="二";
                break;
            case 4:
                s+="三";
                break;
            case 5:
                s+="四";
                break;
            case 6:
                s+="五";
                break;
            case 7:
                s+="六";
                break;
        }
        return s;
    }

    /**
     * 说明：获取年份
     *
     * @return 例如【2015】
     */
    public static final String getYear() {
        return getNowTime(FORMAT_YYYY_1);
    }

    /**
     * 说明：获取月份
     *
     * @return 例如【02】
     */
    public static final String getMonth() {
        return getNowTime(FORMAT_MM_1);
    }

    /**
     * 说明：获取第几日
     *
     * @return 例如【11】
     */
    public static final String getDay() {
        return getNowTime(FORMAT_DD_1);
    }

    /**
     * 说明：获取两个时间间隔(单位：天)
     *
     * @param firstDate
     *            第一个时间
     * @param secondDate
     *            第一个时间
     * @param format
     *            对应时间格式
     * @return 返回时间间隔的天数
     */
    public static final long getDaySpace(String firstDate, String secondDate,
                                         String format) {
        return getSecondSpace(firstDate, secondDate, format) / (24 * 60 * 60);
    }

    /**
     * 说明：获取两个时间间隔(单位：小时)
     *
     * @param firstDate
     *            第一个时间
     * @param secondDate
     *            第一个时间
     * @param format
     *            对应时间格式
     * @return 返回时间间隔的小时数
     */
    public static final long getHourSpace(String firstDate, String secondDate,
                                          String format) {
        return getSecondSpace(firstDate, secondDate, format) / (60 * 60);
    }

    /**
     * 说明：获取两个时间间隔（单位：秒）
     *
     * @param firstDate
     *            第一个时间
     * @param secondDate
     *            第一个时间
     * @param format
     *            对应时间格式
     * @return 返回时间间隔的秒数
     */
    public static final long getSecondSpace(String firstDate,
                                            String secondDate, String format) {
        SimpleDateFormat sdf = getSDF(format);
        long day = 0;
        try {
            Date date1 = sdf.parse(firstDate);
            Date date2 = sdf.parse(secondDate);
            day = (date2.getTime() - date1.getTime()) / 1000;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return day;
    }

    /**
     * 说明：判断年份是否为闰年
     *
     * @param year
     * @return
     */
    public static final boolean isLeapYear(int year) {
        boolean isLeapYear = false;
        if (year <= 0) {
            isLeapYear = false;
        } else {
            isLeapYear = (!(year % 100 == 0) && (year % 4 == 0))
                    || ((year % 100 == 0) && (year % 400 == 0));
        }
        return isLeapYear;
    }

    /**
     * 说明：将long类型转为时间字符串
     *
     * @param l
     *             类型时间
     * @param format
     *            时间格式
     * @return
     */
    public static final String getLongToStr(long l, String format) {
        Date date = new Date(l);
        SimpleDateFormat sdf = getSDF(format);
        return sdf.format(date);
    }

    public static final String currentTimeMillis() {
        return System.currentTimeMillis()+"";
    }

    /**
     * 说明：根据不同时区，转换时间 2015年7月31日
     */
    public static final Date transformTime(Date date, TimeZone oldZone,
                                           TimeZone newZone) {
        Date finalDate = null;
        if (date != null) {
            int timeOffset = oldZone.getOffset(date.getTime())
                    - newZone.getOffset(date.getTime());
            finalDate = new Date(date.getTime() - timeOffset);
        }
        return finalDate;
    }

    /**
     * 说明：判断用户的设备时区是否为东八区（中国） 2015年7月31日
     * @return
     */
    public static boolean isInEasternEightZones() {
        boolean defaultVaule = true;
        if (TimeZone.getDefault() == TimeZone.getTimeZone("GMT+08"))
            defaultVaule = true;
        else
            defaultVaule = false;
        return defaultVaule;
    }

    /**
     * 说明： 根据日历的规则，为给定的日历字段添加或减去指定的时间量
     * @param date 指定时间
     * @param field 日历字段（年或月或日）
     * @param amount 时间量
     * @param format 返回时间格式
     * @return 返回指定的时间格式的字符串
     */
    public static String add(Date date, int field, int amount, String format){
        Calendar calendar = getCalendar();
        SimpleDateFormat sdf = getSDF(format);
        calendar.setTime(date);
        calendar.add(field, amount);
        return sdf.format(calendar.getTime());
    }

    /**
     * 说明：判断src时间是否在start-end区间
     * @param src  指定时间
     * @param start  前范围
     * @param end  后范围
     * @param format  时间格式要统一
     * @return【true：在区间，false：不在区间】
     */
    public static boolean between(String src, String start, String end, String format){
        Calendar srcCal = getCalendar();
        Calendar startCal = getCalendar();
        Calendar endCal = getCalendar();
        srcCal.setTime(getStrToDate(src, format));
        startCal.setTime(getStrToDate(start, format));
        endCal.setTime(getStrToDate(end, format));
        if (srcCal.after(startCal) && srcCal.before(endCal)) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 说明：d1与d2比较
     * @param d1 时间1
     * @param d2 时间2
     * @param format 时间格式
     * @return 【当d1小于d2 返回小于0，当d2大于d1 返回大于0，当d1等于d2 返回0】
     */
    public static int compareTo(String d1, String d2, String format){
        Calendar d1Cal = getCalendar();
        Calendar d2Cal = getCalendar();
        d1Cal.setTime(getStrToDate(d1, format));
        d2Cal.setTime(getStrToDate(d2, format));
        return d1Cal.compareTo(d2Cal);
    }
}
