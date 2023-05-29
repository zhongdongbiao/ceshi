package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: data
 * @description: 日期、队列id查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-24 17:02
 **/

@Data
public class DateQueueIdDto {

    /**
     * 日期
     */
    private String date;

    /**
     * 队列id
     */
    private List<String> queueId;
}
