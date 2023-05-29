package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.Data;

/**
 * @program: data
 * @description: 热线服务单轴业务类
 * @author: WangXinhao
 * @create: 2022-11-08 14:13
 **/

@Data
public class ServiceOrderDetailAxisBo {

    /**
     * 来电时间
     */
    private String callTime;

    /**
     * ACD排队时间
     */
    private String firstQueueStartTime;

    /**
     * ACD排队到坐席接起时长（振铃时长）
     */
    private String ringTime;

    /**
     * 坐席接起时间
     */
    private String answerTime;

    /**
     * 坐席接起到话后处理时长（通话时长）
     */
    private String billingSeconds;

    /**
     * 话后处理（通话结束时间）
     */
    private String agentHangupTime;
}
