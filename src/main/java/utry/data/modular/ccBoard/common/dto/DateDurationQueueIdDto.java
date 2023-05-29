package utry.data.modular.ccBoard.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 日期区间、队列id查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-24 14:27
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateDurationQueueIdDto {

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

}
