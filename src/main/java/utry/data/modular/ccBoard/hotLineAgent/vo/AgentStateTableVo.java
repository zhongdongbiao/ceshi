package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线项目数据概览-在线坐席-坐席状态表格视图
 * @author: WangXinhao
 * @create: 2022-10-21 09:34
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentStateTableVo {

    /**
     * 姓名
     */
    private String agentName;

    /**
     * 工号
     */
    private String agentId;

    /**
     * 接通量
     */
    private Integer connectNumber;

    /**
     * 单位小时接通量
     */
    private Integer hourlyConnectNumber;

    /**
     * 工时利用率
     */
    private String manHourUtilizationRate;

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
