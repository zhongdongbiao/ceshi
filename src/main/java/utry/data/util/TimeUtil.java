package utry.data.util;

import com.alibaba.fastjson.JSONObject;
import utry.core.common.BusinessException;
import utry.data.constant.AggregateTypeConstant;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhongdongbiao
 * @date 2022/4/20 9:43
 */
public class TimeUtil {

    //根据日期取得星期几
    public static String getWeek(Date date){
        String[] weeks = {"sun","mon","tue","wed","thurs","fri","sta"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if(week_index<0){
            week_index = 0;
        }
        return weeks[week_index];

    }

    /**
     * 获取两个日期之间的日期
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static List<Date> getDistanceDate(String startTime, String endTime) {
        List<Date> dates = new ArrayList<>();
        Date startDateTime = null;
        Date endDateTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startDateTime = sdf.parse(startTime);
            endDateTime = sdf.parse(endTime);
        } catch (BusinessException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long differentDays = endDateTime.getTime() - startDateTime.getTime();
        int targetDays = ((int) (differentDays / 86400000L)) + 1;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < targetDays; i++) {
            cal.setTime(startDateTime);
            cal.add(Calendar.DATE, i);
            dates.add(cal.getTime());
        }
        return dates;
    }

    /**
     * 获取两个日期之间的日期
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return
     */
    public static List<Date> getDistanceDateByDate(String startTime, String endTime) {
        List<Date> dates = new ArrayList<>();
        Date startDateTime = null;
        Date endDateTime = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            startDateTime = sdf.parse(startTime);
            endDateTime = sdf.parse(endTime);
        } catch (BusinessException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long differentDays = endDateTime.getTime() - startDateTime.getTime();
        int targetDays = ((int) (differentDays / 86400000L)) + 1;
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < targetDays; i++) {
            cal.setTime(startDateTime);
            cal.add(Calendar.DATE, i);
            dates.add(cal.getTime());
        }
        return dates;
    }

    /**
     * 两数相除保留两位小数
     * @param dividend
     * @param divisor
     * @return
     */
    public static Double getRate(int dividend,int divisor){
        if(divisor!=0){
            double f =  (double) dividend / divisor *100;
            DecimalFormat decimal = new DecimalFormat("0.00");
            decimal.setRoundingMode(RoundingMode.HALF_UP);
            return Double.parseDouble(decimal.format(f));
        }
        return null;
    }

    /**
     * 两数相除保留四位小数
     * @param dividend
     * @param divisor
     * @return
     */
    public static String getFourRate(int dividend,int divisor){
        if(divisor!=0){
            double f =  (double) dividend / divisor;
            BigDecimal bd = new BigDecimal(f);
            return bd.setScale(4, BigDecimal.ROUND_HALF_UP).toString();
        }
        return null;
    }

    /**
     * 两数相除保留四位小数
     * @param dividend
     * @param divisor
     * @return
     */
    public static String getFourRate(double dividend,double divisor){
        if(divisor!=0){
            double f =  (double) dividend / divisor;
            BigDecimal bd = new BigDecimal(f);
            return bd.setScale(4, BigDecimal.ROUND_HALF_UP).toString();
        }
        return null;
    }

    /**
     * 两数相除保留两位小数
     * @param dividend
     * @param divisor
     * @return
     */
    public static Double getDouble(int dividend,int divisor){
        if(divisor!=0){
            double f =  (double) dividend / divisor;
            DecimalFormat decimal = new DecimalFormat("0.00");
            decimal.setRoundingMode(RoundingMode.HALF_UP);
            return Double.parseDouble(decimal.format(f));
        }
        return null;
    }


    /**
     * 两数相除保留两位小数
     * @param dividend
     * @param divisor
     * @return
     */
    public static Double getDouble(Double dividend,Double divisor){
        if(divisor!=0){
            double f =  (double) dividend / divisor;
            DecimalFormat decimal = new DecimalFormat("0.00");
            decimal.setRoundingMode(RoundingMode.HALF_UP);
            return Double.parseDouble(decimal.format(f));
        }
        return null;
    }

    /**
     * 两数相除保留两位小数
     * @param dividend
     * @param divisor
     * @return
     */
    public static Double getRate(double dividend,double divisor){
        if(divisor!=0){
            double f =  dividend / divisor *100;
            DecimalFormat decimal = new DecimalFormat("0.00");
            decimal.setRoundingMode(RoundingMode.UP);
            return Double.parseDouble(decimal.format(f));
        }
        return null;
    }

    /**
     * 根据当前日期获得所在周的日期区间
     */
    public static List<Date> getTimeInterval(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
        if(1 == dayWeek){
            cal.add(Calendar.DAY_OF_MONTH,-1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        String imptimeBegin = format.format(cal.getTime());
        cal.add(Calendar.DATE,6);
        String imptimeEnd = format.format(cal.getTime());
        List<Date> distanceDate = getDistanceDate(imptimeBegin, imptimeEnd);
        return distanceDate;
    }

    /**
     * 根据当前日期获得下周的日期区间
     */
    public static List<Date> getNextTimeInterval(Date date){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        calendar1.setTime(date);
        calendar2.setTime(date);
        int dayOfWeek = calendar1.get(Calendar.DAY_OF_WEEK) - 1;
        if(dayOfWeek <= 0){
            dayOfWeek = 7;
        }
        int offset1 = 1 - dayOfWeek;
        int offset2 = 7 - dayOfWeek;
        calendar1.add(Calendar.DATE, offset1 + 7);
        calendar2.add(Calendar.DATE, offset2 + 7);
        // last Monday
        String lastBeginDate = format.format(calendar1.getTime());
        // last Sunday
        String lastEndDate =format.format(calendar2.getTime());
        List<Date> distanceDate = getDistanceDate(lastBeginDate, lastEndDate);
        return distanceDate;
    }

    /**
     * 根据天数以及指定日期获取相隔多少天的天数 + 加 - 减
     * @param startTime
     * @param days
     * @return
     */
    public static Date getDateByDistance(Date startTime,Integer days){
        Calendar ca = Calendar.getInstance();
        ca.setTime(startTime);
        ca.add(Calendar.DATE, days);
        return ca.getTime();
    }

    /**
     * 通过request请求 获取对象
     * @param request request请求
     * @param object    需要转换的对象
     * @return
     */
    public static Object requestToObject(HttpServletRequest request, Object object){
        if(request==null){
            throw new BusinessException("参数为空");
        }
        String result="";
        try {
            InputStream in = request.getInputStream();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int n;
            while ((n = in.read(bytes)) != -1) {
                out.write(bytes, 0, n);
            }
            bytes = out.toByteArray();
            result = new String(bytes, "utf-8");
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("参数异常");
        }
        String replace = result.replace("\\", "\\\\");
        char[] temp = replace.toCharArray();
        int n = temp.length;
        try{
            JSONObject js = JSONObject.parseObject(replace);
        }catch (Exception e){
            for (int i = 0; i < n; i++) {
                if (temp[i] == ':' && temp[i + 1] == '"') {
                    for (int j = i + 2; j < n; j++) {
                        if (temp[j] == '"') {
                            if ((temp[j + 1] != ',' && temp[j + 1] != '}') || (temp[j + 1] == ',' && temp[j + 2] != '"')) {
                                temp[j] = '”';
                            } else if (temp[j + 1] == ',' || temp[j + 1] == '}') {
                                break;
                            }
                        }
                    }
                }
            }
        }
        String s = new String(temp);
        try{
            JSONObject js = JSONObject.parseObject(s);
            Object object1 = js.toJavaObject(object.getClass());
            return object1;
        }catch (Exception e){
            return RetResponse.makeRsp(500,"Json转换异常，请转换为Json格式再进行传输！谢谢！");
        }
    }

    /**获取两个时间节点之间的月份列表**/
    public static List<String> getMonthBetween(String minDate, String maxDate){
        ArrayList<String> result = new ArrayList<String>();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

            Calendar min = Calendar.getInstance();
            Calendar max = Calendar.getInstance();
            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);

            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);

            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static List<String> getStartAndEnd(String date){
        List<String> list = new ArrayList<>();
        LocalDate localDate = LocalDate.parse(date);
        LocalDate firstDayOfMonth = localDate.with(TemporalAdjusters.firstDayOfMonth());
        list.add(firstDayOfMonth.toString());
        LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
        list.add(lastDayOfMonth.toString());
        return list;
    }

    /**
     * 获取该月第一天
     * @param date 日期
     * @return 该月第一天
     */
    public static String getFirstDayOfMonth(String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDate firstDayOfMonth = localDate.with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfMonth.toString();
    }

    /**
     * 获取该月最后一天
     * @param date 日期
     * @return 该月最后一天
     */
    public static String getLastDayOfMonth(String date) {
        LocalDate localDate = LocalDate.parse(date);
        LocalDate lastDayOfMonth = localDate.with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfMonth.toString();
    }

    /**
     * 获取该周开始日期
     *
     * @param date 日期
     * @return 该周开始日期
     */
    public static String getFirstDayOfWeek(String date) {
        return getStartOrEndDayOfWeek(date, true);
    }

    /**
     * 获取该周结束日期
     *
     * @param date 日期
     * @return 该周结束日期
     */
    public static String getLastDayOfWeek(String date) {
        return getStartOrEndDayOfWeek(date, false);
    }

    /**
     * 获取该周开始或结束日期
     *
     * @param date 日期
     * @param isFirst true表示开始日期；false表示结束日期
     * @return 该周开始或结束日期
     */
    private static String getStartOrEndDayOfWeek(String date, boolean isFirst) {
        LocalDate localDate = LocalDate.parse(date);
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        int value = dayOfWeek.getValue();
        if (isFirst) {
            return localDate.minusDays(value - 1).toString();
        } else {
            return localDate.plusDays(7 - value).toString();
        }
    }

    /**
     * 获取两个时间内的每一天时间 yyyy-MM-dd
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    public static List<String> findDaysStr(String startDate, String endDate, String aggregateType) {
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);
        List<String> result = null;
        if (AggregateTypeConstant.DAILY_AGGREGATION.equals(aggregateType)) {
            // 按日聚合
            long betweenDays = startLocalDate.until(endLocalDate, ChronoUnit.DAYS) + 1;
            result = new ArrayList<>();
            for (long i = 0; i < betweenDays; i++) {
                LocalDate plusDays = startLocalDate.plusDays(i);
                result.add(plusDays.toString());
            }
        }
        if (AggregateTypeConstant.MONTHLY_AGGREGATION.equals(aggregateType)) {
            // 按月聚合
            LocalDate startFirstDayOfMonth = startLocalDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endFirstDayOfMonth = endLocalDate.with(TemporalAdjusters.firstDayOfMonth());
            long betweenMonths = startFirstDayOfMonth.until(endFirstDayOfMonth, ChronoUnit.MONTHS) + 1;
            result = new ArrayList<>();
            for (long i = 0; i < betweenMonths; i++) {
                LocalDate plusMonths = startFirstDayOfMonth.plusMonths(i);
                result.add(plusMonths.toString());
            }
        }
        return result;
    }

    /**
     * 两时间节点间间隔
     *
     * @param begin         开始时间
     * @param end           结束时间
     * @param intervalUnit  间隔单位
     * @param intervalValue 间隔值
     * @return 分割出的时间节点
     */
    public static List<LocalDateTime> groupByInterval(LocalDateTime begin, LocalDateTime end, TemporalUnit intervalUnit, long intervalValue) {
        long intervalTotalAmount = begin.until(end, intervalUnit);
        BigDecimal num = BigDecimal.valueOf(intervalTotalAmount).divide(BigDecimal.valueOf(intervalValue), RoundingMode.HALF_UP);
        return Stream.iterate(begin, seed -> seed.plus(intervalValue, intervalUnit)).limit(num.longValue() + 1).collect(Collectors.toList());
    }

    /**
     * 向前获取最近的一个半点
     * @return 09:28返回09:00     09:58返回09:30
     */
    public static LocalTime getLastHalfHour() {
        LocalTime now = LocalTime.now();
        if (now.getMinute() > 30) {
            return now.withMinute(30).withSecond(0).withNano(0);
        }
        return now.withMinute(0).withSecond(0).withNano(0);
    }

    /**
     * 获取距离后一个半点秒数
     * @return 秒
     */
    public static long getAfterHalfHourSecond() {
        LocalTime now = LocalTime.now();
        LocalTime afterHalfHour;
        if (now.getMinute() >= 30) {
            afterHalfHour = now.withHour(now.getHour() + 1).withMinute(0).withSecond(0).withNano(0);
        } else {
            afterHalfHour = now.withMinute(30).withSecond(0).withNano(0);
        }
        return Duration.between(now, afterHalfHour).getSeconds();
    }

    /**
     * 秒转 HH:mm:ss
     * @param time 秒
     * @return HH:mm:ss
     */
    public static String secondTransform(String time) {
        int second = Integer.parseInt(time);
        int hh = second / 3600;
        int mm = (second % 3600) / 60;
        int ss = (second % 3600) % 60;
        return (hh < 10 ? ("0" + hh) : hh) + ":" + (mm < 10 ? ("0" + mm) : mm) + ":" + (ss < 10 ? ("0" + ss) : ss);
    }

    /**
     * 秒转 mm:ss
     *
     * @param second 秒
     * @return mm:ss
     */
    public static String secondTransformMinSecond(int second) {
        int mm = (second % 3600) / 60;
        int ss = (second % 3600) % 60;
        return (mm < 10 ? ("0" + mm) : mm) + ":" + (ss < 10 ? ("0" + ss) : ss);
    }

    /**
     * HH:mm:ss 转 秒
     *
     * @param time HH:mm:ss
     * @return 秒
     */
    public static int hourMinSecTransformSecond(String time) {
        String[] split = time.split(":");
        int hour = Integer.parseInt(split[0]);
        int min = Integer.parseInt(split[1]);
        int second = Integer.parseInt(split[2]);
        return hour * 3600 + min * 60 + second;
    }
}
