package utry.data.modular.ccBoard.common.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 队列监控信息业务类
 * @author: WangXinhao
 * @create: 2022-11-10 12:54
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueueMonitorBo {

    /**
     * 时间
     */
    private LocalDateTime time;

    /**
     * 队列号码
     */
    private String queueNumber;

    /**
     * 进入ACD总量
     */
    private Integer totalEnterAcd;

    /**
     * ACD接通总量
     */
    private Integer totalAcdConnected;

    /**
     * ACD未接总量
     */
    private Integer toatlAcdMissed;

    /**
     * ACD总接通率
     */
    private String totalAcdConnectedRate;

    /**
     * 10秒总接通率
     */
    private String tenSecondConnectionRate;

    /**
     * 当前进入ACD数量
     */
    private Integer totalEnterAcdOfNow;

    /**
     * 当前排队人数
     */
    private Integer currentWaitNumber;

    /**
     * 振铃人数
     */
    private Integer currentRingers;

    /**
     * 10秒内接通电话数
     */
    private Integer tenSecondConnection;

    /**
     * 超过10秒内接通电话数
     */
    private Integer exceedTenSecondConnection;

    /**
     * 当前ACD平均分配时长
     */
    private BigDecimal avgCurrentAcdDuration;

    /**
     * 当前平均振铃时长
     */
    private BigDecimal avgCurrentRingDuration;

    /**
     * 当前通话平均时长
     */
    private BigDecimal avgCurrentBillingSeconds;

    /**
     * 忙碌坐席
     */
    private Integer busyAgents;

    /**
     * 签入坐席
     */
    private Integer checkInAgents;

    /**
     * 空闲超时坐席
     */
    private Integer freeTimeOutAgents;

    /**
     * 空闲坐席
     */
    private Integer freeAgents;

    /**
     * 小休坐席
     */
    private Integer restAgents;

    /**
     * 小休超时坐席
     */
    private Integer restTimeOutAgents;

    /**
     * 话后坐席
     */
    private Integer afterAgents;

    /**
     * 话后超时坐席
     */
    private Integer afterTimeOutAgents;

    /**
     * 示忙-忙碌超时坐席
     */
    private Integer busyTimeOutAgents;

    /**
     * 通话中坐席
     */
    private Integer onCallAgents;
}
