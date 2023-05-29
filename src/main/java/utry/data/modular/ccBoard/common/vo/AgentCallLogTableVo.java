package utry.data.modular.ccBoard.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线项目数据概览-在线坐席-坐席状态表格-通话记录表格视图
 * @author: WangXinhao
 * @create: 2022-10-21 10:16
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentCallLogTableVo {

    /**
     * 通话id
     */
    private Long id;

    /**
     * 10s率达标：1是；0否
     */
    private String tenSecondRateFlag;

    /**
     * 队列id
     */
    private String queueId;

    /**
     * 部门/队列
     */
    private String queueName;

    /**
     * 姓名
     */
    private String agentName;

    /**
     * 工号
     */
    private String agentId;

    /**
     * 主叫号码
     */
    private String source;

    /**
     * 呼叫类型
     */
    private String callType;

    /**
     * 呼叫状态
     */
    private String callState;

    /**
     * 振铃时长
     */
    private String ringDuration;

    /**
     * 通话时长
     */
    private String callDuration;

    /**
     * 挂断方
     */
    private String hangUpParty;

    /**
     * 呼叫时间
     */
    private String callDateTime;

    /**
     * 满意度
     */
    private String satisfaction;
}
