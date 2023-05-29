package utry.data.modular.technicalQuality.utils;

import utry.data.modular.technicalQuality.dto.LineChartDTO;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Description 时间范围公共类
 * @Author zh
 * @Date 2022/4/27 16:32
 */
public class LineChartDateUtil {

    /**
     * 获取当前日期上一季度 开始时间
     *
     * @return
     */
    public static String getStartQuarter(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTime(date);
        startCalendar.set(Calendar.MONTH, ((int) startCalendar.get(Calendar.MONTH) / 3 - 1) * 3);
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
        setMinTime(startCalendar);
        return sd.format(startCalendar.getTime());
    }

    /**
     * 获取当前日期上一季度 结束时间
     *
     * @return
     */
    public static String getLastQuarter(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTime(date);
        endCalendar.set(Calendar.MONTH, ((int) endCalendar.get(Calendar.MONTH) / 3 - 1) * 3 + 2);
        endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setMaxTime(endCalendar);
        return sd.format(endCalendar.getTime());
    }

    /**
     * 计算某日期所在季度开始日期
     * 季度划分：1、2、3， 4、5、6， 7、8、9， 10、11、12
     */
    public static String getSeasonEndDate (Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, (month + 3) / 3 * 3);
        calendar.set(Calendar.DATE, 1);
        return sd.format(new Date(calendar.getTime().getTime() - 24 * 60 * 60 *1000));
    }
    /**
     * 计算某日期所在季度结束日期
     * 季度划分：1、2、3， 4、5、6， 7、8、9， 10、11、12
     */
    public static String getSeasonStartDate (Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(Calendar.MONTH, month / 3 * 3);
        calendar.set(Calendar.DATE, 1);
        return sd.format(calendar.getTime());
    }

    /**
     * 最小时间
     *
     * @param calendar
     */
    private static void setMinTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    /**
     * 最大时间
     *
     * @param calendar
     */
    private static void setMaxTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, calendar.getActualMaximum(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.getActualMaximum(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, calendar.getActualMaximum(Calendar.SECOND));
        calendar.set(Calendar.MILLISECOND, calendar.getActualMaximum(Calendar.MILLISECOND));
    }

    /**
     * @Description 过去6个月 (半年)
     **/
    public static List<String> pastHalfYear(Date date){
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -5);
        String beginDate = sd.format(date).substring(0,7);
        String endDate = sd.format(c.getTime()).substring(0, 7);
        dateList.add(beginDate);
        dateList.add(endDate);
        return dateList;
    }

    /**
     * @Description 过去12个月 (一年)
     **/
    public static List<String> pastYear(Date date){
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, -11);
        c.set(Calendar.DAY_OF_MONTH, 1);
        String beginDate = sd.format(c.getTime()).substring(0,7);
        String endDate = sd.format(date).substring(0,7);
        dateList.add(beginDate);
        dateList.add(endDate);
        return dateList;
    }

    /**
     * 获得今年的起始日期
     * @param date
     * @return
     */
    public static String currentYearFirst(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_YEAR, 1);
        return sd.format(c.getTime());
    }

    /**
     * 获得今年的结束日期
     * @param date
     * @return
     */
    public static String currentYearLast(Date date) {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(c.get(Calendar.YEAR),11,31);
        return sd.format(c.getTime());
    }

    /**
     * 近三天日期
     * @return
     */
    public static List<String> threeDayBefore() {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -2);
        String beginDate = sdf.format(calendar.getTime());
        String endDate = sdf.format(new Date());
        dateList.add(beginDate);
        dateList.add(endDate);
        return dateList;
    }

    /**
     * 近七天日期
     * @return
     */
    public static List<String> SevenDayBefore() {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -6);
        String beginDate = sdf.format(calendar.getTime());
        String endDate = sdf.format(new Date());
        dateList.add(beginDate);
        dateList.add(endDate);
        return dateList;
    }

    /**
     * 近30天日期
     * @return
     */
    public static List<String> MonthBefore() {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        calendar.add(Calendar.DATE, -29);
        String beginDate = sdf.format(calendar.getTime());
        String endDate = sdf.format(new Date());
        dateList.add(beginDate);
        dateList.add(endDate);
        return dateList;
    }

    /**
     * 本周日期
     * @return
     */
    public static List<String> weekRange() {
        List<String> dateList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String beginDate = sdf.format(cal.getTime());
        String endDate = sdf.format(new Date());
        dateList.add(beginDate);
        dateList.add(endDate);
        return dateList;
    }

    /**
     * 获取本月的第一天
     */
    public static String getMonthFirstDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH,1);
        calendar.add(Calendar.MONTH,0);
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     * 获取本月的最后一天
     */
    public static String getMonthLastDay(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     * 获取某月的第一天
     * @param year
     * @param month
     * @return
     */
    public static String getFirstDayOfMonth(int year,int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR,year);
        //设置月份
        cal.set(Calendar.MONTH, month);
        //获取某月最小天数
        int firstDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最小天数
        cal.set(Calendar.DAY_OF_MONTH, firstDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDayOfMonth = sdf.format(cal.getTime());
        return firstDayOfMonth;
    }

    /**
     * 获取某月的最后一天
     */
    public static String getLastDayOfMonth(int year,int month) {
        Calendar cal = Calendar.getInstance();
        //设置年份
        cal.set(Calendar.YEAR, year);
        //设置月份
        cal.set(Calendar.MONTH, month);
        //获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        //设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        //格式化日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String lastDayOfMonth = sdf.format(cal.getTime());
        return lastDayOfMonth;
    }

    /**
     *  获取两个日期之间的所有日期 (年月日)
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<LineChartDTO> getBetweenDate(String startTime, String endTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 声明保存日期集合
        List<LineChartDTO> list = new ArrayList();
        try {
            // 转化成日期类型
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime()<=endDate.getTime()){
                LineChartDTO lineChartDTO = new LineChartDTO();
                lineChartDTO.setTime(sdf.format(startDate));
                lineChartDTO.setRepairRate(null);
                // 把日期添加到集合
                list.add(lineChartDTO);
                // 设置日期
                calendar.setTime(startDate);
                //把日期增加一天
                calendar.add(Calendar.DATE, 1);
                // 获取增加后的日期
                startDate=calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**获取两月之间的所有月份*/
    public static List<LineChartDTO> getBetweenMonth(String minDate, String maxDate){
        ArrayList<LineChartDTO> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月

        try {
            // 转化成日期类型
            Date startDate = sdf.parse(minDate);
            Date endDate = sdf.parse(maxDate);

            //用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime()<=endDate.getTime()){
                LineChartDTO lineChartDTO = new LineChartDTO();
                lineChartDTO.setTime(sdf.format(startDate));
                lineChartDTO.setRepairRate(null);
                // 把日期添加到集合
                result.add(lineChartDTO);
                // 设置日期
                calendar.setTime(startDate);
                //把日期增加一天
                calendar.add(Calendar.MONTH, 1);
                // 获取增加后的日期
                startDate=calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

}
