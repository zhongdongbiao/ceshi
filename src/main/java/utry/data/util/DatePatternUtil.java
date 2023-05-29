package utry.data.util;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * @program: data
 * @description: 日期格式化类，提供常用的日期格式化对象
 * @author: WangXinhao
 * @create: 2022-10-31 16:18
 **/

public class DatePatternUtil {

    public static final String NORM_MINUTE_PATTERN = "HH:mm";

    public static final DateTimeFormatter NORM_MINUTE_FORMATTER = createFormatter(NORM_MINUTE_PATTERN);

    public static final String NORM_DAY_PATTERN = "MM-dd";

    public static final DateTimeFormatter NORM_DAY_FORMATTER = createFormatter(NORM_DAY_PATTERN);

    public static DateTimeFormatter createFormatter(String pattern) {
        return DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
                .withZone(ZoneId.systemDefault());
    }
}
