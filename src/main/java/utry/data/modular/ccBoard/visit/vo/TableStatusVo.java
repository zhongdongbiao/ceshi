package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 *坐席状态
 *
 * @author zhongdongbiao
 * @date 2022/10/24 10:30
 */
@Data
public class TableStatusVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 姓名
     */
    private String name;

    /**
     * 工号
     */
    private String workNumber;

    /**
     * 当日呼出量
     */
    private String callOutNumber;

    /**
     * 呼出量
     */
    private String breatheNumber;

    /**
     * 呼通率
     */
    private String callPassband;

    /**
     * 呼通量
     */
    private String callFlux;

    /**
     * 工时利用率
     */
    private String workingHoursRate;

    /**
     * 通话工时利用率
     */
    private String callTimeRate;

    /**
     * 单位小时完成量
     */
    private String hourlyComplete;

    /**
     * 当前状态
     */
    private String currentState;

    /**
     * 当前状态持续时间
     */
    private String currentStateDuration;

    /**
     * 累计示闲时长
     */
    private String spareTime;

    /**
     * 累计话后时长
     */
    private String afterTime;

    /**
     * 累计小休时长
     */
    private String breakTime;

}
