package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 客户评价、客户评价分析图表视图
 * @author: WangXinhao
 * @create: 2022-10-24 14:17
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEvaluateChartVo {

    /**
     * 名称
     */
    private String name;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 占百分比
     */
    private String rate;
}
