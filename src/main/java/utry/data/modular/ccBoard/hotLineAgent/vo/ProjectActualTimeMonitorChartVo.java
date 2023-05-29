package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 热线项目实时监控图表
 * @author: WangXinhao
 * @create: 2022-11-25 14:36
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectActualTimeMonitorChartVo {

    /**
     * 接入量图表
     */
    private List<DateValueChartVo> accessNumberChart;

    /**
     * 接通率图表
     */
    private List<DateValueChartVo> connectionRateChart;

    /**
     * 10s率图表
     */
    private List<DateValueChartVo> tenSecondRateChart;

    /**
     * 平均排队时间图表
     */
    private List<DateValueChartVo> avgQueueDurationChart;

    /**
     * 平均通话时长图表
     */
    private List<DateValueChartVo> avgCallInDurationChart;
}
