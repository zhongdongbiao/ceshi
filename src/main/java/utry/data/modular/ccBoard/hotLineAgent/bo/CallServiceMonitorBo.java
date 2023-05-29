package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线呼叫业务监控视图
 * @author: WangXinhao
 * @create: 2022-10-21 09:13
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CallServiceMonitorBo {

    /**
     * 来电数量
     */
    private Integer totalEnterAcdOfNow;

    /**
     * ACD排队数量
     */
    private Integer currentWaitNumber;

    /**
     * ACD排队到分配坐席平均耗时
     */
    private Double avgCurrentAcdDuration;

    /**
     * 分配坐席数量
     */
    private Integer allocateAgentNumber;

    /**
     * 平均振铃时长（分配坐席到坐席接起平均耗时）
     */
    private Double avgCurrentRingDuration;

    /**
     * 分配坐席到坐席接起超时数量（坐席接起时间-分配时间 > 2.5s人数）
     */
    private Integer exceedTenSecondConnection;

    /**
     * 坐席接起数量
     */
    private Integer onCallAgents;

    /**
     * 平均通话时长（坐席接起到话后处理平均耗时）
     */
    private Double avgCurrentBillingSeconds;

    /**
     * 话后处理数量
     */
    private Integer afterAgents;

    /**
     * 话后处理超时（话后处理 > 20s人数）
     */
    private Integer afterTimeOutAgents;
}
