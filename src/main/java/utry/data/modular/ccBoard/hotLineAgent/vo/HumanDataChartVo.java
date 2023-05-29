package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

/**
 * @program: data
 * @description: 热线项目数据概览-人力数据图表视图
 * @author: WangXinhao
 * @create: 2022-10-21 10:22
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumanDataChartVo {

    /**
     * 时间
     */
    private String time;

    /**
     * 签入坐席数量
     */
    private ChartPointVo checkInAgentNumber;

    /**
     * 10s接通率
     */
    private ChartPointVo tenSecondRate;

    /**
     * 进线量
     */
    private ChartPointVo accessNumber;

    /**
     * 目标10s接通率
     */
    private ChartPointVo tenSecondRateTarget;
}
