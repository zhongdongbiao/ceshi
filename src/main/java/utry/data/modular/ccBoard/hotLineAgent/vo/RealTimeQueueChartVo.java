package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 实时排队图表
 * @author: WangXinhao
 * @create: 2022-10-24 13:20
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealTimeQueueChartVo {

    /**
     * 时间 HH:mm:ss
     */
    private String time;

    /**
     * 队列
     */
    private List<RealTimeQueueVo> queue;
}
