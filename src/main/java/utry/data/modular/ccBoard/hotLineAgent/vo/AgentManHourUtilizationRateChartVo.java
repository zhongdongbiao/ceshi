package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

/**
 * @program: data
 * @description: 坐席工时利用率图表视图
 * @author: WangXinhao
 * @create: 2022-10-24 13:43
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentManHourUtilizationRateChartVo {

    /**
     * 时间 HH:mm
     */
    private String time;

    /**
     * 单位小时接入量/呼出量
     */
    private ChartPointVo hourlyCallNumber;

    /**
     * 工时利用率
     */
    private ChartPointVo manHourUtilizationRate;
}
