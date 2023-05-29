package utry.data.modular.ccBoard.hotLineAgent.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 进线统计图表业务类
 * @author: WangXinhao
 * @create: 2022-11-02 16:10
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncomeStatisticChartBo {

    /**
     * 时间 yyyy-MM-dd HH:mm:ss
     */
    private String time;

    /**
     * 接入量/进线量
     */
    private Integer accessNumber;

    /**
     * 接通量
     */
    private Integer connectNumber;

    /**
     * 接通率
     */
    private String connectionRate;
}
