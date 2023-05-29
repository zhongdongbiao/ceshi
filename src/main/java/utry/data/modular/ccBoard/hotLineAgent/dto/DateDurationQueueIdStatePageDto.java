package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: data
 * @description: 日期区间、队列id、状态、分页查询条件
 * @author: WangXinhao
 * @create: 2022-10-27 09:31
 **/

@Data
public class DateDurationQueueIdStatePageDto {

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
     * 状态
     */
    private String state;

    /**
     * 分页页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
