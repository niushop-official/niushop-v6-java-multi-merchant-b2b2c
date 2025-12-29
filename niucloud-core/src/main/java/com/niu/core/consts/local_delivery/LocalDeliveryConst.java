package com.niu.core.consts.local_delivery;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class LocalDeliveryConst {
    // 星期常量定义
    public static final int WEEK_MONDAY = 1;
    public static final int WEEK_TUESDAY = 2;
    public static final int WEEK_WEDNESDAY = 3;
    public static final int WEEK_THURSDAY = 4;
    public static final int WEEK_FRIDAY = 5;
    public static final int WEEK_SATURDAY = 6;
    public static final int WEEK_SUNDAY = 0;

    // 时间间隔常量定义
    public static final int TIME_INTERVAL_30 = 30;
    public static final int TIME_INTERVAL_60 = 60;
    public static final int TIME_INTERVAL_90 = 90;
    public static final int TIME_INTERVAL_120 = 120;

    // 星期数据映射表
    private static final Map<Integer, WeekDay> WEEK_LIST;

    static {
        WEEK_LIST = Map.of(
                    WEEK_MONDAY, new WeekDay("周一", WEEK_MONDAY),
                    WEEK_TUESDAY, new WeekDay("周二", WEEK_TUESDAY),
                    WEEK_WEDNESDAY, new WeekDay("周三", WEEK_WEDNESDAY),
                    WEEK_THURSDAY, new WeekDay("周四", WEEK_THURSDAY),
                    WEEK_FRIDAY, new WeekDay("周五", WEEK_FRIDAY),
                    WEEK_SATURDAY, new WeekDay("周六", WEEK_SATURDAY),
                    WEEK_SUNDAY, new WeekDay("周日", WEEK_SUNDAY)
        );
    }

    /**
     * 获取周列表
     *
     * @param week 星期类型，如果为空则返回全部列表
     * @return 星期数据对象或映射表
     */
    public static Object getWeekList(Integer week) {
        if (week != null) {
            return WEEK_LIST.get(week);
        }
        return WEEK_LIST;
    }

    /**
     * 获取时间间隔列表
     *
     * @param timeInterval 时间间隔类型，如果为空则返回全部列表
     * @return 时间间隔数据对象或映射表
     */
    public static Object getTimeIntervalList(Integer timeInterval) {
        Map<Integer, TimeInterval> timeIntervalMap = createTimeIntervalMap();

        if (timeInterval != null) {
            return timeIntervalMap.get(timeInterval);
        }
        return timeIntervalMap;
    }

    /**
     * 创建时间间隔映射表
     */
    private static Map<Integer, TimeInterval> createTimeIntervalMap() {
        return Map.of(TIME_INTERVAL_30, new TimeInterval(TIME_INTERVAL_30, "30分钟"), TIME_INTERVAL_60, new TimeInterval(TIME_INTERVAL_60, "60分钟"), TIME_INTERVAL_90, new TimeInterval(TIME_INTERVAL_90, "90分钟"), TIME_INTERVAL_120, new TimeInterval(TIME_INTERVAL_120, "120分钟"));
    }

    /**
     * 星期数据类
     */
    @Data
    public static class WeekDay {
        private final String name;
        private final int type;

        public WeekDay(String name, int type) {
            this.name = name;
            this.type = type;
        }
    }

    /**
     * 时间间隔数据类
     */
    @Data
    @Builder
    public static class TimeInterval {
        private final int type;
        private final String name;

        public TimeInterval(int type, String name) {
            this.type = type;
            this.name = name;
        }
    }
}