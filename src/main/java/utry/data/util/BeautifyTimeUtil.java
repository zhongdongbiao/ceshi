package utry.data.util;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BeautifyTimeUtil {
    public static String beautifyTime(double second) {
        if (second <= 0){
            return "0";
        }
        final String[] units = new String[]{"秒", "分钟", "小时"};
        int digitGroups = (int) (Math.log10(second) / Math.log10(60));
        String value = "0";
        if(digitGroups<3) {
            value = new DecimalFormat("#,##0.#").format(second / Math.pow(60, digitGroups)) + "" + units[digitGroups];
        }else if(digitGroups >= 3) {
            //如果超过了小时的表达范围则，则转换为天，小时，分，秒格式

            value = secondToDate(second);
        }

        return value;
    }

    public static String secondToDate(double second) {
        Long time =  new Long(new Double(second).longValue());
        String strTime = null;
        Long days = time / (60 * 60 * 24);
        Long hours = (time % (60 * 60 * 24)) / (60 * 60);
        Long minutes = (time % (60 * 60)) / 60;
        if (days > 0) {
            strTime = days + "天" + hours + "小时" + minutes + "分钟";
        } else if (hours > 0) {
            strTime = hours + "小时" + minutes + "分钟";
        } else if (minutes > 0) {
            strTime = minutes + "分钟";
        }
        return strTime;
    }

    public static String secondToHour(double second) {
        Long time =  new Long(new Double(second).longValue());
        String strTime = null;
        Long hours = (time / (60 * 60));
        Long minutes = (time % (60 * 60)) / 60;
        if (hours > 0) {
            strTime = hours + "小时" + minutes + "分钟";
        } else if (minutes > 0) {
            strTime = minutes + "分钟";
        }
        return strTime;
    }

    public static String HourToDate(double second) {
        Long time =  new Long(new Double(second).longValue());
        String strTime = null;
        Long days = time / (60 * 60 * 24);
        Long hours = (time % (60 * 60 * 24)) / (60 * 60);
        if (days > 0) {
            strTime = days + "天";
        } else if (hours > 0) {
            strTime = hours + "小时";
        }
        return strTime;
    }
}