package utry.data.modular.ccBoard.common.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 坐席监控信息表业务类
 * @author: WangXinhao
 * @create: 2022-11-10 13:32
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentMonitorBo {

    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 坐席分机号码
     */
    private String extension;

    /**
     * 坐席真实姓名
     */
    private String realName;

    /**
     * 所属队列号码
     */
    private String agentFromQueue;

    /**
     * 当前状态
     */
    private String currentState;

    /**
     * 当前状态持续时长
     */
    private String currentStateDuration;

    /**
     * 呼入数
     */
    private Integer incomingCalls;

    /**
     * 呼入接通数
     */
    private Integer incomingCallConnections;

    /**
     * 呼入时长
     */
    private String incomingCallDuration;

    /**
     * 平均呼入时长
     */
    private String avgIncomingCallDuration;

    /**
     * 呼入振铃时长
     */
    private String incomingRingDuration;

    /**
     * 呼出数
     */
    private Integer callOutCount;

    /**
     * 呼出接通数
     */
    private Integer callOutConnections;

    /**
     * 呼出时长
     */
    private String callOutDuration;

    /**
     * 平均呼出时长
     */
    private String avgCallOutDuration;

    /**
     * 平均呼出振铃时长
     */
    private String avgCallOutRingDuration;

    /**
     * 通话总数（呼入接通+呼出接通）
     */
    private Integer totalCalls;

    /**
     * 示闲时长
     */
    private String freeDuration;

    /**
     * 话后处理时长
     */
    private String afterDuration;

    /**
     * 小休-如厕时长
     */
    private String toiletDuration;

    /**
     * 小休-培训时长
     */
    private String trainDuration;

    /**
     * 小休-休息时长
     */
    private String restDuration;

    /**
     * 小休-就餐时长
     */
    private String eatDuration;

    /**
     * 小休-值日时长
     */
    private String dutyDuration;

    /**
     * 示忙-忙碌时长
     */
    private String busyDuration;
}
