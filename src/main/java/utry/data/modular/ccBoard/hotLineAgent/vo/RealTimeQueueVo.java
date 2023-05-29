package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 实时排队图表-队列排队数量、占百分比视图
 * @author: WangXinhao
 * @create: 2022-10-24 13:23
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealTimeQueueVo {

    /**
     * 队列id
     */
    private String id;

    /**
     * 队列名称
     */
    private String name;

    /**
     * 排队数量
     */
    private Integer count;

    /**
     * 队列10秒接通率
     */
    //private String tenSecondRate;
}
