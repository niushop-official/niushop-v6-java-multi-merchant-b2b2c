package com.niu.core.common.utils.date;


import cn.hutool.core.date.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 针对hutool二次封装时间方便调用
 */
public class DateUtils {

    /**
     * 时间戳转化成时间格式
     *
     * @param timestamp
     * @return
     */
    public static String timestampToString(long timestamp) {
        //转化成毫秒
        if(timestamp == 0L)
        {
            return "";
        }
        long minTimestamp = timestamp * 1000;
        Date date = DateUtil.date(minTimestamp);
        return DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String timestampToString(long timestamp, String format) {
        //转化成毫秒
        if(timestamp == 0L)
        {
            return "";
        }
        long minTimestamp = timestamp * 1000;
        Date date = DateUtil.date(minTimestamp);
        return DateUtil.format(date, format);
    }

    /**
     * 时间字符串转化成时间戳
     *
     * @param date
     * @return
     */
    public static Long StringToTimestamp(String date) {

        if (date.equals("") || date.equals("0")) {
            return 0L;
        }
        java.util.Date dateTime = DateUtil.parse(date);
        // 获取时间戳
        return dateTime.getTime() / 1000;
    }

    /**
     * 获取当前时间戳
     *
     * @return long
     */
    public static long currTime() {

        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public static String currDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    /**
     * 获取凌晨时间戳
     *
     * @return
     */
    public static long currInitTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(date.getTime());
        return StringToTimestamp(today);
    }

    /**
     * 获取凌晨时间
     *
     * @return
     */
    public static String currInitDate() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = simpleDateFormat.format(date.getTime());
        return today;
    }

    /**
     * 当前时间加几天
     *
     * @param dayNum
     * @return
     */
    public static String getDateAddDay(Integer dayNum) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, dayNum);
        Date resultDate = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(resultDate);
    }

    /**
     * 当前时间加几个月
     *
     * @param month
     * @return
     */
    public static String getDateAddMonth(Integer month) {
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);
        Date resultDate = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(resultDate);
    }


    /**
     * 当天时间的开始结束时间
     *
     * @param currDate
     * @return
     */
    public static String[] getStartEndByDay(String currDate) {
        String[] dates = new String[2];
        Date startDate = DateUtil.parse(currDate, "yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.DATE, 1);
        Date endDate = calendar.getTime();
        dates[0] = DateUtil.format(startDate, "yyyy-MM-dd");
        dates[1] = DateUtil.format(endDate, "yyyy-MM-dd HH:mm:ss");
        return dates;
    }

    /**
     * 获取当天的格式化时间
     *
     * @return
     */
    public static String getFormatTodayString() {
        return DateUtil.format(new Date(), "yyyy-MM-dd");
    }

    /**
     * 获取昨天的格式化时间
     *
     * @return
     */
    public static String getFormatYesterdayString() {
        long timestamp = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        return DateUtil.format(new Date(timestamp), "yyyy-MM-dd");
    }


    /**
     * 输出的结果是：今天开始的时间beginTime：2018-08-16 00:00:00
     * 今天结束的时间endTime：  2018-08-16 23:59:59
     */
    public static String[] getStartEndByToday(Date time) {
        String[] result = new String[2];
        Calendar statCalendar = Calendar.getInstance();
        statCalendar.setTime(time);
        statCalendar.set(Calendar.HOUR_OF_DAY, 0);
        statCalendar.set(Calendar.MINUTE, 0);
        statCalendar.set(Calendar.SECOND, 0);
        statCalendar.set(Calendar.MILLISECOND, 0);


        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(time);
        endCalendar.set(Calendar.HOUR_OF_DAY, 23);
        endCalendar.set(Calendar.MINUTE, 59);
        endCalendar.set(Calendar.SECOND, 59);
        endCalendar.set(Calendar.MILLISECOND, 999);

        result[0] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(statCalendar.getTime());
        result[1] = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(endCalendar.getTime());
        return result;
    }
    public static String timestampToRelativeTime(long timestamp) {
        if (timestamp == 0L) {
            return "";
        }
        
        long currentTime = System.currentTimeMillis() / 1000;
        long diff = currentTime - timestamp;
        
        // 小于1分钟，显示"刚刚"
        if (diff < 60) {
            return "刚刚";
        }
        
        // 小于1小时，显示"X分钟前"
        if (diff < 3600) {
            long minutes = diff / 60;
            return minutes + "分钟前";
        }
        
        // 小于24小时，显示"X小时前"
        if (diff < 86400) {
            long hours = diff / 3600;
            return hours + "小时前";
        }
        
        // 小于48小时，显示"昨天"
        if (diff < 172800) {
            return "昨天";
        }
        
        // 超过48小时，显示"MM-dd HH:mm"格式
        long minTimestamp = timestamp * 1000;
        Date date = DateUtil.date(minTimestamp);
        return DateUtil.format(date, "MM-dd HH:mm");
    }

}
