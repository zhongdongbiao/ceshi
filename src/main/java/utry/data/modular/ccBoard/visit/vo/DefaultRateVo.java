package utry.data.modular.ccBoard.visit.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

import java.io.Serializable;

/**
 * 违约率
 * @author zhongdongbiao
 * @date 2022/11/3 15:53
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DefaultRateVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 回访完成违约率
     */
    private ChartPointVo completeDefaultRate;

    /**
     * 回访审核违约率
     */
    private ChartPointVo auditDefaultRate;
}
