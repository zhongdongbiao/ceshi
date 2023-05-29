package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 日期区间、坐席id、分页查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-24 17:24
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentIdDateDurationPageDto {

    /**
     * 工号
     */
    private String agentId;

    /**
     * 队列id
     */
    private List<String> queueId;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 分页页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
