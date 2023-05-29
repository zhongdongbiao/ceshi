package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;
import utry.data.modular.ccBoard.common.vo.ChartPointVo;

import java.io.Serializable;

/**
 * 未完成回访项目Vo
 *
 * @author zhongdongbiao
 * @date 2022/10/24 14:16
 */
@Data
public class NotCompleteVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 类型
     */
    private String type;

    /**
     * 待完成
     */
    private ChartPointVo pending;

    /**
     * 超三天未完成
     */
    private ChartPointVo threeNotComplete;

    /**
     * 未完成总量
     */
    private String notCompleteTotal;

    /**
     * 未完成率
     */
    private ChartPointVo rate;

}
