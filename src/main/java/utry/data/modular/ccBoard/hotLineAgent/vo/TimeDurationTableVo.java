package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 人力数据图表-时段在线及工时表格-时间段状态表格视图
 * @author: WangXinhao
 * @create: 2022-10-21 10:36
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeDurationTableVo {

    /**
     * 状态
     */
    private String state;

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

    /**
     * 所属队列id
     */
    private String queueId;

    /**
     * 所属队列名称
     */
    private String queueName;

    /**
     * 当前状态是否超时：0未超时，1超时
     */
    private String currentStateTimeOutFlag;

    /**
     * 姓名
     */
    private String agentName;

    /**
     * 工号
     */
    private String agentId;
}
