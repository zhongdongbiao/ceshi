package utry.data.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    private static final String[] weeks = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static Logger logger = LoggerFactory.getLogger(DateTimeUtil.class);

    /**
     * yyyyMMdd与yyyy-MM-dd hh:mm:ss 转换
     *
     * @param value
     * @return
     */
    public static String formatString(String value) {
        String sReturn = "";
        if (value == null || "".equals(value)) {
            return sReturn;
        }
        //长度为14格式转换成yyyy-mm-dd hh:mm:ss
        if (value.length() == 14) {
            sReturn = value.substring(0, 4) + "-" + value.substring(4, 6) + "-" + value.substring(6, 8) + " "
                    + value.substring(8, 10) + ":" + value.substring(10, 12) + ":" + value.substring(12, 14);
            return sReturn;
        }
        //长度为19格式转换成yyyymmddhhmmss
        if (value.length() == 19) {
            sReturn = value.substring(0, 4) + value.substring(5, 7) + value.substring(8, 10) + value.substring(11, 13)
                    + value.substring(14, 16) + value.substring(17, 19);
            return sReturn;
        }
        //长度为10格式转换成yyyymmhh
        if (value.length() == 10) {
            sReturn = value.substring(0, 4) + value.substring(5, 7) + value.substring(8, 10);
        }
        //长度为8格式转化成yyyy-mm-dd
        if (value.length() == 8) {
            sReturn = value.substring(0, 4) + "-" + value.substring(4, 6) + "-" + value.substring(6, 8);
        }
        return sReturn;
    }

    /**
     * 获得所需要的日期格式
     *
     * @param date
     * @param format yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date getFormatDateFromString(String date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date formatDate = null;
        try {
            formatDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate;
    }

    /**
     * 获得所需要的日期格式 date+time
     *
     * @param date   只有 yyyy-MM-dd
     * @param format yyyy-MM-dd HH:mm:ss
     * @param time   HH:mm:ss
     * @return
     */
    public static Date getFormatDateFromString(String date, String format, String time) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date formatDate = null;
        try {
            formatDate = formatter.parse(date + " " + time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return formatDate;
    }

    /**
     * 获得所需要的日期格式
     *
     * @param date
     * @param format
     * @return
     */
    public static String getFormatStringDate(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    /**
     * long转日期
     *
     * @param lo
     * @return
     */
    public static String longToDate(long lo) {
        Date date = new Date(lo);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    /**
     * 跟当前时间进行比较
     *
     * @param date
     * @param format
     * @return
     */
    public static int compareNowDate(Date date, String format) {
        Date now = new Date();
        int result = 0;
        if (getFormatDate(date, format).getTime() > getFormatDate(now, format).getTime()){
            result = 1;
        }
        return result;
    }

    /**
     * 获得所需要的日期格式
     *
     * @param date
     * @param format yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date getFormatDate(Date date, String format) {
        DateFormat df = new SimpleDateFormat(format);
        Date formatDate = null;
        try {
            formatDate = df.parse(getFormatStringDate(date, format));
        } catch (Exception e) {
            logger.info("日期转换出错" + e.getMessage());
        }
        return formatDate;
    }

    /**
     * 获得本月的第一天
     *
     * @return
     */
    public static Date getMonthOfFirst() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(calendar.HOUR_OF_DAY, 00);
        calendar.set(calendar.MINUTE, 00);
        calendar.set(calendar.SECOND, 00);
        return calendar.getTime();
    }

    /**
     * 获得下月的第一天
     *
     * @return
     */
    public static Date getMonthOfEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(calendar.HOUR_OF_DAY, 00);
        calendar.set(calendar.MINUTE, 00);
        calendar.set(calendar.SECOND, 00);
        return calendar.getTime();
    }

    /**
     * 获得该周的周一
     *
     * @return
     */
    public static Date getWeekOfFirst() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.HOUR_OF_DAY, 00);
        calendar.set(calendar.MINUTE, 00);
        calendar.set(calendar.SECOND, 00);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.add(calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 获得下周的周一
     *
     * @return
     */
    public static Date getWeekOfEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.HOUR_OF_DAY, 00);
        calendar.set(calendar.MINUTE, 00);
        calendar.set(calendar.SECOND, 00);
        calendar.set(Calendar.DAY_OF_WEEK, 1);
        calendar.add(calendar.DAY_OF_MONTH, 8);
        return calendar.getTime();
    }

    /**
     * 获得当天时间
     *
     * @return
     */
    public static Date getToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.HOUR_OF_DAY, 00);
        calendar.set(calendar.MINUTE, 00);
        calendar.set(calendar.SECOND, 00);
        return calendar.getTime();
    }

    /**
     * 获取明天日期
     *
     * @return
     */
    public static Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(calendar.DAY_OF_MONTH, 1);
        calendar.set(calendar.HOUR_OF_DAY, 00);
        calendar.set(calendar.MINUTE, 00);
        calendar.set(calendar.SECOND, 00);
        return calendar.getTime();
    }

    /**
     * 获取一天的开始
     *
     * @param now
     * @return
     */
    public static Date getDayBegin(Date now) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.HOUR_OF_DAY, 00);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        return c.getTime();
    }

    /**
     * 获取一天的结束
     *
     * @param now
     * @return
     */
    public static Date getDayEnd(Date now) {
        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * 获取传入时间对应的星期一的日期
     *
     * @param now
     * @return
     */
    public static Date getWeekBegin(Date now) {

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.DAY_OF_WEEK, c.getActualMinimum(Calendar.DAY_OF_WEEK));
        // 国内一周以星期一作为开始
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 00);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        return c.getTime();
    }

    /**
     * 获取传入时间对应的星期天的日期时间
     *
     * @param now
     * @return
     */
    public static Date getWeekEnd(Date now) {

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.DAY_OF_WEEK, c.getActualMaximum(Calendar.DAY_OF_WEEK));
        // 国内一周以星期天作为结束
        c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * 获取传入时间对应的月初日期
     *
     * @param now
     * @return
     */
    public static Date getMonthBegin(Date now) {

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 00);
        c.set(Calendar.MINUTE, 00);
        c.set(Calendar.SECOND, 00);
        return c.getTime();
    }

    /**
     * 获取传入时间对应的月末日期
     *
     * @param now
     * @return
     */
    public static Date getMonthEnd(Date now) {

        Calendar c = Calendar.getInstance();
        c.setTime(now);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        return c.getTime();
    }

    /**
     * 获取某个时间后几天的时间
     *
     * @param sourceTime 时间
     * @param days       x天后提交
     * @return
     */
    public static Date getAfterDateTime(Date sourceTime, int days) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceTime);
        calendar.add(calendar.DATE, days);

        return calendar.getTime();
    }

    /**
     * 获得两者的时间差
     *
     * @param start
     * @param end
     * @return
     */
    public static long getTimeDifference(Date start, Date end) {
        return end.getTime() - start.getTime();
    }

    public static int getTimeDifference1(Date start, Date end) {
        long result = end.getTime() - start.getTime();
        if (result > 0) {
            return 1;
        }
        if (result == 0) {
            return 0;
        }
        return -1;
    }

    public static String dateToStr(Date dateDate, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        String dateString = formatter.format(dateDate);
        return dateString;
    }

    public static Date strToDateLong(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }

    /**
     * 获取某个时间前几天的时间
     *
     * @param sourceTime
     * @param days
     * @return
     */
    public static Date getBeforeDateTime(Date sourceTime, int days) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(sourceTime);
        calendar.add(Calendar.DATE, -days);
        return calendar.getTime();
    }

    /**
     * 获取当前日期为星期几
     *
     * @param value
     * @return
     */
    public static String getCurrentWeek(String value) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        date = sdf.parse(value);
        Calendar calender = Calendar.getInstance();
        calender.setTime(date);
        int weekIndex = calender.get(Calendar.DAY_OF_WEEK) - 1 < 0 ? 0 : calender.get(Calendar.DAY_OF_WEEK) - 1;
        return weeks[weekIndex];
    }

    /**
     * 获取今日最后的时间
     * @return
     */
    public static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public static LocalTime getTodayEndTimeLocalTime() {
        return LocalTime.of(23, 59, 59, 999999999);
    }

    /**
     * 今天的日期格式: 20190101
     * @return
     */
    public static String getTodayFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    /**
     * 获取现在到今天最后时间的差值
     * @return
     */
    public static Duration getDValueNowToTodayEndTime() {
        LocalTime now = LocalTime.now();
        return Duration.between(now, getTodayEndTimeLocalTime());
    }

        public static void main(String[] args) {
    }
}
