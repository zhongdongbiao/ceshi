package utry.data.modular.ccBoard.visit.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

import java.io.Serializable;

/**
 * 回访满意率Vo
 * @author zhongdongbiao
 * @date 2022/11/3 15:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SatisfactionRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 满意率
     */
    private ChartPointVo satisfactionRate;
}
