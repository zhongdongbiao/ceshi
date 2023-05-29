package utry.data.util;

import cn.hutool.core.date.DatePattern;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @program: data
 * @description: 数据工具类
 * @author: WangXinhao
 * @create: 2022-11-02 16:51
 **/

public class DataUtil {

    /**
     * 求交集
     *
     * @param list1
     * @param list2
     * @return
     */
    public static List<String> getIntersection(List<String> list1, List<String> list2) {
        List<String> result = new ArrayList<>();
        HashSet<String> hashSet = new HashSet<>(list1);
        for (String s : list2) {
            if (hashSet.contains(s)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * 限制跨天查询
     *
     * @param clazz 类
     * @param cls   实体类
     */
    public static <U> void limitCrossDayQuery(Class<U> clazz, U cls) {
        String getStartDateString = "getStartDate";
        String getEndDateString = "getEndDate";
        String setStartDateString = "setStartDate";
        String setEndDateString = "setEndDate";
        try {
            Method getStartDateMethod = clazz.getMethod(getStartDateString);
            Method getEndDateMethod = clazz.getMethod(getEndDateString);
            LocalDate startLocalDate = LocalDate.parse(getStartDateMethod.invoke(cls).toString());
            LocalDate endLocalDate = LocalDate.parse(getEndDateMethod.invoke(cls).toString());
            LocalDate nowLocalDate = LocalDate.now();
            if (Period.between(startLocalDate, endLocalDate).getDays() > 0 || !endLocalDate.isEqual(nowLocalDate)) {
                String now = nowLocalDate.toString();
                clazz.getMethod(setStartDateString, String.class).invoke(cls, now);
                clazz.getMethod(setEndDateString, String.class).invoke(cls, now);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static <U> void limitCrossDayQueryLocalDateTime(Class<U> clazz, U cls) {
        String getStartDateTimeString = "getStartDateTime";
        String getEndDateTimeString = "getEndDateTime";
        String setStartDateTimeString = "setStartDateTime";
        String setEndDateTimeString = "setEndDateTime";
        try {
            // 获取入参
            Method getStartDateMethod = clazz.getMethod(getStartDateTimeString);
            Method getEndDateMethod = clazz.getMethod(getEndDateTimeString);
            LocalDateTime startLocalDateTime = LocalDateTime.parse(getStartDateMethod.invoke(cls).toString(), DatePattern.NORM_DATETIME_FORMATTER);
            LocalDateTime endLocalDateTime = LocalDateTime.parse(getEndDateMethod.invoke(cls).toString(), DatePattern.NORM_DATETIME_FORMATTER);
            // 存 HH:mm:ss
            LocalTime startTime = startLocalDateTime.toLocalTime();
            LocalTime endTime = endLocalDateTime.toLocalTime();
            // 日期用于判断是否跨天
            LocalDate startLocalDate = startLocalDateTime.toLocalDate();
            LocalDate endLocalDate = endLocalDateTime.toLocalDate();
            LocalDate nowLocalDate = LocalDate.now();
            if (Period.between(startLocalDate, endLocalDate).getDays() > 0 || !endLocalDate.isEqual(nowLocalDate)) {
                clazz.getMethod(setStartDateTimeString, String.class).invoke(cls, nowLocalDate.atTime(startTime).format(DatePattern.NORM_DATETIME_FORMATTER));
                clazz.getMethod(setEndDateTimeString, String.class).invoke(cls, nowLocalDate.atTime(endTime).format(DatePattern.NORM_DATETIME_FORMATTER));
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
