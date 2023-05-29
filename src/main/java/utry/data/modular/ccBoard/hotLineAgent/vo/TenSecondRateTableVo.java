package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 10s接通率表格视图
 * @author: WangXinhao
 * @create: 2022-10-21 09:08
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TenSecondRateTableVo {
    /**
     * 队列id
     */
    private String queueId;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 当前排队数
     */
    private Integer currentQueueNumber;

    /**
     * 接入量
     */
    private Integer accessNumber;

    /**
     * 接通量
     */
    private Integer connectNumber;

    /**
     * 接通率
     */
    private String connectionRate;

    /**
     * 10s接通率
     */
    private String tenSecondRate;

    /**
     * 坐席组平均坐席
     */
    private String avgQueueAgent;

    /**
     * 呼入平均通话时间
     */
    private String avgCallInDuration;

    /**
     * 呼出平均通话时间
     */
    private String avgCallOutDuration;

    /**
     * 工时利用率
     */
    private String manHourUtilizationRate;

    /**
     * 通话工时利用率
     */
    private String callManHourUtilizationRate;

    /**
     * 签入坐席
     */
    private Integer checkInAgentNumber;

    /**
     * 技能组空闲坐席
     */
    private Integer freeAgentNumber;

    /**
     * 示忙坐席
     */
    private Integer busyAgentNumber;
}
