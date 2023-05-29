package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 坐席分时段通话时长
 * @author: WangXinhao
 * @create: 2022-11-08 13:38
 **/

@Data
public class AgentIntervalCallDurationBo {

    /**
     * 时间节点
     */
    private LocalDateTime time;

    /**
     * 坐席id
     */
    private String agentId;

    /**
     * 通话时长
     */
    private Integer callDuration;
}
