package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 热线项目数据概览-满意度视图
 * @author: WangXinhao
 * @create: 2022-10-21 09:23
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatisfactionVo {

    /**
     * 今日满意度
     */
    private String dailySatisfaction;

    /**
     * 本周满意度
     */
    private String weeklySatisfaction;

    /**
     * 本月满意度
     */
    private String monthlySatisfaction;
}
