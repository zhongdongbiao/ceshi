package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线项目数据概览-人力数据图表业务类
 * @author: WangXinhao
 * @create: 2022-11-01 09:59
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HumanDataChartBo {

    /**
     * 时间
     */
    private String time;

    /**
     * 签入坐席数量
     */
    private Integer checkInAgentNumber;

    /**
     * 10s接通率
     */
    private String tenSecondRate;

    /**
     * 进线量
     */
    private Integer accessNumber;

    /**
     * 目标10s接通率
     */
    private String tenSecondRateTarget;
}
