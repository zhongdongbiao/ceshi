package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 日期数值图表视图
 * @author: WangXinhao
 * @create: 2022-11-25 09:25
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DateValueChartVo {

    /**
     * 日期
     */
    private String date;

    /**
     * 数值
     */
    private String value;
}
