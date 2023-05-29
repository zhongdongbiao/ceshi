package utry.data.constant;

/**
 * @program: data
 * @description: cc看板常量
 * @author: WangXinhao
 * @create: 2022-11-03 14:24
 **/

public class CcBoardConstant {

    /**
     * 判断坐席签入时间（之前是15分分钟）
     */
    public static final int CHECK_IN_PASS_LINE_SECOND = 0;

    public static final String IN = "in";

    public static final String OUT = "out";

    /**
     * 导出打勾
     */
    public static final String TICK = "✔";

    /**
     * 导出打叉
     */
    public static final String CROSS = "×";

    /**
     * 理论接起量 = 一线人力 * 4
     */
    public static final int FRONT_LINE_MANPOWER_MULTI_VALUE = 4;

    /**
     * 工时利用率状态字典项
     */
    public static final String CC_WORK_STATUS = "CC_WORK_STATUS";

    /**
     * 单位小时接通量、单位小时接入量状态字典项
     */
    public static final String CC_SERVICE_STATUS = "CC_SERVICE_STATUS";

    /**
     * 热线CPH分母状态
     */
    public static final String CC_IN_CPH_STATUS = "CC_IN_CPH_STATUS";

    /**
     * 回访CPH分母状态
     */
    public static final String CC_OUT_CPH_STATUS = "CC_OUT_CPH_STATUS";
}
