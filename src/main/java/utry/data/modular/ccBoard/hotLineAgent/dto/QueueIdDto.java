package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: data
 * @description: 队列id查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-25 11:24
 **/

@Data
public class QueueIdDto {

    /**
     * 队列id
     */
    private List<String> queueId;
}
