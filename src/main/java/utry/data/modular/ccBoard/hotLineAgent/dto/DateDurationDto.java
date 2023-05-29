package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.Data;

/**
 * @program: data
 * @description: 日期区间查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-25 13:29
 **/

@Data
public class DateDurationDto {

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;
}
