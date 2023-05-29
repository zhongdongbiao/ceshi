package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

/**
 * @program: data
 * @description: 进线统计图表视图
 * @author: WangXinhao
 * @create: 2022-10-24 13:37
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeStatisticChartVo {

    /**
     * 时间 HH:mm
     */
    private String time;

    /**
     * 接入量/进线量
     */
    private ChartPointVo accessNumber;

    /**
     * 接通量
     */
    private ChartPointVo connectNumber;

    /**
     * 接通率
     */
    private ChartPointVo connectionRate;
}
