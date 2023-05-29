package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 已受理工单未跟进图表视图
 * @author: WangXinhao
 * @create: 2022-10-24 14:15
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptNotFollowChartVo {

    /**
     * 日期
     */
    private String date;

    /**
     * 数量
     */
    private Integer count;
}
