package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 导出条件
 * @author: WangXinhao
 * @create: 2022-11-08 16:47
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HotLineExportConditionDto {

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 队列id
     */
    private List<String> queueId;

    /**
     * 坐席状态
     */
    private String state;

    /**
     * 工号
     */
    private String agentId;

    /**
     * 导出图表标识
     */
    private Integer flag;
}
