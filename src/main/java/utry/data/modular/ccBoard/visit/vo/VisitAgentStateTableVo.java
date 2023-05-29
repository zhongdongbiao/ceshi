package utry.data.modular.ccBoard.visit.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @author: zhongodngbiao
 * @create: 2022-10-21 09:34
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitAgentStateTableVo {

    /**
     * 姓名
     */
    private String agentName;

    /**
     * 工号
     */
    private String agentId;

    /**
     * 呼出量
     */
    private Integer breathOut;

    /**
     * 呼通率
     */
    private Integer breathRate;

    /**
     * 呼通量
     */
    private Integer breathNumber;

    /**
     * 工作时长
     */
    private Integer workTime;

    /**
     * 工时利用率
     */
    private String manHourUtilizationRate;

    /**
     * 通话工时利用率
     */
    private String callTimeUtilization;

    /**
     * 单位小时完成量
     */
    private Double completeNumber;

    /**
     * 当前状态
     */
    private String currentState;

    /**
     * 当前状态持续
     */
    private String currentStateStay;

    /**
     * 累积示闲时长
     */
    private String leisureCumulativeDuration;

    /**
     * 累积小休时长
     */
    private String restCumulativeDuration;

    /**
     * 累积话后时长
     */
    private String afterCallCumulativeDuration;

    /**
     * 累积示忙时长
     */
    private String busyCumulativeDuration;

    /**
     * 当前状态是否超时：0未超时，1超时
     */
    private Integer currentStateTimeOutFlag;
}
