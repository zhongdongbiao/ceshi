package utry.data.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.core.util.CronExpression;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import utry.core.cloud.caller.CallerParam;
import utry.core.cloud.caller.IServiceCaller;
import utry.core.common.LoginInfoParams;
import utry.core.log.UtryLogger;
import utry.core.log.UtryLoggerFactory;
import utry.core.util.ApplicationContextUtil;
import utry.data.enums.SiteCodeEnum;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: DJ
 * @Date: 2021/3/8 10:39
 */
public class TimeTaskUtil {

    private static final UtryLogger LOGGER = UtryLoggerFactory.getLogger(TimeTaskUtil.class);
    private static IServiceCaller serviceCaller;


    static {
        serviceCaller = (IServiceCaller) ApplicationContextUtil.getBean("serviceCaller");
    }

    /**
     * 每天指定时间点循环执行
     *
     * @param taskId    任务id
     * @param taskName  任务名称
     * @param subsiteId 子站编码
     * @param service   服务名称
     * @param dataMap   回调参数
     * @param time      指定时间点，如"11:59:59"
     */
    public static void executeTaskEveryDayByTime(String taskId, String taskName, String subsiteId, String service, Map<String, Object> dataMap, String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date formatDate = null;
        try {
            formatDate = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(formatDate);
        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer minute = calendar.get(Calendar.MINUTE);
        Integer second = calendar.get(Calendar.SECOND);

        StringBuffer sb = new StringBuffer();
        sb.append(second).append(" ");
        sb.append(minute).append(" ");
        sb.append(hour).append(" ");
        sb.append("*").append(" ");
        sb.append("*").append(" ");
        sb.append("?").append(" ");
        executeTaskByCronExpression(taskId, taskName, subsiteId, service, dataMap, sb.toString());
    }

    /**
     * 间隔多少秒循环执行
     *
     * @param taskId    任务id
     * @param taskName  任务名称
     * @param subsiteId 子站编码
     * @param service   服务名称
     * @param dataMap   回调参数
     * @param second    间隔时间（单位：秒）
     */
    public static void executeTaskBySecond(String taskId, String taskName, String subsiteId, String service, Map<String, Object> dataMap, String second) {
        String cronExpression = "*/second * * * * ?".replace("second", second);
        executeTaskByCronExpression(taskId, taskName, subsiteId, service, dataMap, cronExpression);
    }

    /**
     * 间隔多少分钟循环执行
     *
     * @param taskId    任务id
     * @param taskName  任务名称
     * @param subsiteId 子站编码
     * @param service   服务名称
     * @param dataMap   回调参数
     * @param minute    间隔时间（单位：分钟）
     */
    public static void executeTaskByMinutes(String taskId, String taskName, String subsiteId, String service, Map<String, Object> dataMap, String minute) {
        String cronExpression = "0 */minute * * * ?".replace("minute", minute);
        executeTaskByCronExpression(taskId, taskName, subsiteId, service, dataMap, cronExpression);
    }

    /**
     * 指定时间执行
     *
     * @param taskId    任务id
     * @param taskName  任务名称
     * @param subsiteId 子站编码
     * @param service   服务名称
     * @param dataMap   回调参数
     * @param date      时间（24小时制，精确到秒）
     */
    public static void executeTaskOnlyOnce(String taskId, String taskName, String subsiteId, String service, Map<String, Object> dataMap, String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date formatDate = null;
        try {
            formatDate = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        executeTaskOnlyOnce(taskId, taskName, subsiteId, service, dataMap, formatDate);
    }

    /**
     * 指定时间执行
     *
     * @param taskId    任务id
     * @param taskName  任务名称
     * @param subsiteId 子站编码
     * @param service   服务名称
     * @param dataMap   回调参数
     * @param date      时间（24小时制，精确到秒）
     */
    public static void executeTaskOnlyOnce(String taskId, String taskName, String subsiteId, String service, Map<String, Object> dataMap, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        Integer year = calendar.get(Calendar.YEAR);
        Integer month = calendar.get(Calendar.MONTH) + 1;//月份是从0开始，故+1
        Integer day = calendar.get(Calendar.DATE);

        Integer hour = calendar.get(Calendar.HOUR_OF_DAY);
        Integer minute = calendar.get(Calendar.MINUTE);
        Integer second = calendar.get(Calendar.SECOND);

        StringBuffer sb = new StringBuffer();
        sb.append(second).append(" ");
        sb.append(minute).append(" ");
        sb.append(hour).append(" ");
        sb.append(day).append(" ");
        sb.append(month).append(" ");
        sb.append("?").append(" ");
        sb.append(year).append(" ");
        executeTaskByCronExpression(taskId, taskName, subsiteId, service, dataMap, sb.toString());
    }


    //    经典案例：
//    "*/5 * * * * ?"每隔5秒执行一次
//    "0 */1 * * * ?"每隔1分钟执行一次：
//    "30 10 * * * ?"每小时的10分30秒触发任务
//    "30 10 1 * * ?"每天1点10分30秒触发任务
//    "30 10 1 20 * ?"每月20号1点10分30秒触发任务
//    "30 10 1 20 10 ? *"每年10月20号1点10分30秒触发任务
//    "30 10 1 20 10 ? 2011"2011年10月20号1点10分30秒触发任务
//    "30 10 1 ? 10 * 2011"2011年10月每天1点10分30秒触发任务
//    "30 10 1 ? 10 SUN 2011"2011年10月每周日1点10分30秒触发任务
//    "15,30,45 * * * * ?"每15秒，30秒，45秒时触发任务
//    "15-45 * * * * ?"15到45秒内，每秒都触发任务
//    "15/5 * * * * ?"每分钟的每15秒开始触发，每隔5秒触发一次
//    "15-30/5 * * * * ?"每分钟的15秒到30秒之间开始触发，每隔5秒触发一次
//    "0 0/3 * * * ?"每小时的第0分0秒开始，每三分钟触发一次
//    "0 15 10 ? * MON-FRI"星期一到星期五的10点15分0秒触发任务
//    "0 15 10 L * ?"每个月最后一天的10点15分0秒触发任务
//    "0 15 10 LW * ?"每个月最后一个工作日的10点15分0秒触发任务
//    "0 15 10 ? * 5L"每个月最后一个星期四的10点15分0秒触发任务
//    "0 15 10 ? * 5#3"每个月第三周的星期四的10点15分0秒触发任务

    /**
     * 按自定义规则执行
     *
     * @param taskId         任务id
     * @param taskName       任务名称
     * @param subsiteId      子站编码
     * @param service        服务名称
     * @param dataMap        回调参数
     * @param cronExpression 执行规则表达式
     */
    public static void executeTaskByCronExpression(String taskId, String taskName, String subsiteId, String service, Map<String, Object> dataMap, String cronExpression) {
        if (StringUtils.isEmpty(taskId)) {
            LOGGER.error("taskId不能为空");
            return;
        }
        if (StringUtils.isEmpty(taskName)) {
            LOGGER.error("taskName不能为空");
            return;
        }
        if (StringUtils.isEmpty(subsiteId)) {
            LOGGER.error("subsiteId不能为空");
            return;
        }
        if (StringUtils.isEmpty(service)) {
            LOGGER.error("method不能为空");
            return;
        }
        if (StringUtils.isEmpty(cronExpression)) {
            LOGGER.error("cronExpression不能为空");
            return;
        }
        if (!CronExpression.isValidExpression(cronExpression)) {
            LOGGER.error("创建定时任务[" + taskName + "]失败,表达式不合法");
            return;
        }
        if (dataMap == null || dataMap.size() < 0) {
            dataMap = new HashMap<>();
        }
        dataMap.put("taskId", taskId);
        dataMap.put("accountId", LoginInfoParams.getAccountID());
        dataMap.put("loginName", LoginInfoParams.getLoginName());
        dataMap.put("realName", LoginInfoParams.getRealName());
        dataMap.put("isTenant", true);

        JSONObject params = new JSONObject();
        params.put("taskId", taskId);
        params.put("taskName", taskName);
        params.put("taskCron", cronExpression);
        params.put("taskSite", subsiteId);
        params.put("beanLocal", service);
        params.put("taskParameter", dataMap);

        //远程调用task子站
        CallerParam callerParam = new CallerParam(SiteCodeEnum.TASK.code(), "/api/task/add");
        callerParam.addParameter("task", params.toString());
        callerParam.setHttpMethod(HttpMethod.POST);
        JSONObject response = serviceCaller.call(callerParam, new ParameterizedTypeReference<JSONObject>() {
        });

    }

    /**
     * 删除定时任务
     *
     * @param taskId 任务id
     */
    public static void removeTask(String taskId) {
        //远程调用task子站
        CallerParam callerParam = new CallerParam(SiteCodeEnum.TASK.code(), "/api/task/del");
        callerParam.addParameter("taskid", taskId);
        callerParam.setHttpMethod(HttpMethod.GET);
        JSONObject response = serviceCaller.call(callerParam, new ParameterizedTypeReference<JSONObject>() {
        });
    }

}
