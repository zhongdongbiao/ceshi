package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 坐席工时利用率标识视图
 * @author: WangXinhao
 * @create: 2022-10-24 13:46
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentManHourUtilizationRateTotalVo {

    /**
     * 总体CPH
     */
    private String totalCPH;

    /**
     * 总体工时利用率
     */
    private String totalManHourUtilizationRate;
}
