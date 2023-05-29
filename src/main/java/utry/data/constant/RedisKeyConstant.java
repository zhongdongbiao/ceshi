package utry.data.constant;

/**
 * @program: data
 * @description: redis key常量
 * @author: WangXinhao
 * @create: 2022-06-08 11:15
 **/

public class RedisKeyConstant {

    /**
     * 部品库存表生成id的redis key
     * 格式：generateid:业务名
     */
    public static final String GENERATE_ID_PART_DRAWING_STOCK = "generateid:partdrawingstock";

    /**
     * 生成id的前缀
     */
    public static final String GENERATE_ID_DATE_PREFIX = "generateid:dateprefix";

    /**
     * 部品库存分表后缀
     */
    public static final String SHARDING_SUFFIX_PART_DRAWING_STOCK = "sharding:suffix:partdrawingstock";

    /**
     * 人力数据图表
     */
    public static final String HUMAN_DATA_CHART = "cc:humanDataChart";

    /**
     * 实时排队图表
     */
    public static final String REAL_TIME_QUEUE_CHART = "cc:realTimeQueueChart";

    /**
     * 进线统计图表
     */
    public static final String INCOME_STATISTIC_CHART = "cc:incomeStatisticChart";

    /**
     * 量级数据图表
     */
    public static final String MAGNITUDE_DATA_CHART = "cc:magnitudeDataChart";

    /**
     * 坐席工时利用率图表
     */
    public static final String AGENT_MAN_HOUR_UTILIZATION_RATE_CHART = "cc:agentManHourUtilizationRateChart";
}
