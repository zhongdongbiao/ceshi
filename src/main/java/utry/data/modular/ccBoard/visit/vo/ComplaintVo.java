package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

import java.io.Serializable;

/**
 * 已完成回访项目
 * @author zhongdongbiao
 * @date 2022/10/31 16:08
 */
@Data
public class ComplaintVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 申诉率
     */
    private ChartPointVo complaintRate;

    /**
     * 申诉不通过率
     */
    private ChartPointVo noComplaintRate;

}
