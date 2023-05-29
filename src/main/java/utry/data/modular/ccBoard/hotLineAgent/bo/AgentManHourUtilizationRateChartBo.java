package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

/**
 * @program: data
 * @description: 坐席工时利用率图表导出业务类
 * @author: WangXinhao
 * @create: 2022-11-10 18:50
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentManHourUtilizationRateChartBo {

    /**
     * 时间 yyyy-MM-dd HH:mm:ss
     */
    private String time;

    /**
     * 单位小时接入量/呼出量
     */
    private String hourlyCallNumber;

    /**
     * 工时利用率
     */
    private String manHourUtilizationRate;
}
