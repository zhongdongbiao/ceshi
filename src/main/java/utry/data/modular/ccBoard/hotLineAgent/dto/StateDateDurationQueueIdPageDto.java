package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 坐席状态、日期区间、队列id、分页查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-11-08 11:15
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StateDateDurationQueueIdPageDto {

    /**
     * 坐席状态
     */
    private String state;

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
     * 分页页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
