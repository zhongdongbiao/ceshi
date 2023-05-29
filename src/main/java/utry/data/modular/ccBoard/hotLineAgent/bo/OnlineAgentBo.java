package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.Data;

/**
 * @program: data
 * @description: 在线坐席业务类
 * @author: WangXinhao
 * @create: 2022-11-10 15:02
 **/

@Data
public class OnlineAgentBo {

    /**
     * 在线坐席
     */
    private Integer checkInAgents;

    /**
     * 空闲坐席
     */
    private Integer freeAgents;

    /**
     * 空闲超时坐席
     */
    private Integer freeTimeOutAgents;

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
     * 忙碌坐席
     */
    private Integer busyAgents;

    /**
     * 示忙-忙碌超时坐席
     */
    private Integer busyTimeOutAgents;

    /**
     * 通话中坐席
     */
    private Integer onCallAgents;
}
