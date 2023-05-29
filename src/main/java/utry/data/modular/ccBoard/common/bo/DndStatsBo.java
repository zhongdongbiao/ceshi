package utry.data.modular.ccBoard.common.bo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @program: data
 * @description: 状态信息业务类
 * @author: WangXinhao
 * @create: 2022-11-03 11:30
 **/

@Data
public class DndStatsBo {

    /**
     * 队列id
     */
    private String queueId;

    /**
     * 坐席id
     */
    private String agentId;

    /**
     * 坐席姓名
     */
    private String agentName;

    /**
     * 开始时间
     */
    private LocalDateTime startDateTime;

    /**
     * 结束时间
     */
    private LocalDateTime endDateTime;

    /**
     * 操作时长
     */
    private Integer duration;

    /**
     * 操作类型
     */
    private String operationType;
}
