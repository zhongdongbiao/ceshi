package utry.data.modular.ccBoard.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 日期区间、队列id、分页查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-24 16:40
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateDurationQueueIdPageDto {

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
