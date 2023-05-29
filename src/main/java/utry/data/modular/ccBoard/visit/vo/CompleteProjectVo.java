package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;
import utry.data.modular.ccBoard.common.vo.ChartPointTwoValueVo;

import java.io.Serializable;

/**
 * 已完成回访项目Vo
 * @author zhongdongbiao
 * @date 2022/10/31 16:08
 */
@Data
public class CompleteProjectVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    private String abscissa;

    /**
     * 总数量
     */
    private String total;

    /**
     * 完成数量
     */
    private ChartPointTwoValueVo complete;

    /**
     * 灰色数量
     */
    private ChartPointTwoValueVo gray;

    /**
     * 违约
     */
    private ChartPointTwoValueVo defaultCount;

}
