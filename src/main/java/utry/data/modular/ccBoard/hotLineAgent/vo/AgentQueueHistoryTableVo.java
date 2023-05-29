package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线项目数据概览-在线坐席-坐席状态表格-队列历史表格视图
 * @author: WangXinhao
 * @create: 2022-10-21 10:13
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentQueueHistoryTableVo {

    /**
     * 部门/队列
     */
    private String queueName;

    /**
     * 开始日期时间
     */
    private String startDateTime;

    /**
     * 结束日期时间
     */
    private String endDateTime;

    /**
     * 历时
     */
    private Integer duration;
}
