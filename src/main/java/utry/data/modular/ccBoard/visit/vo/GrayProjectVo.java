package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;
import utry.data.modular.ccBoard.common.vo.ChartPointTwoValueVo;

import java.io.Serializable;

/**
 * @author zhongdongbiao
 * @date 2022/10/31 17:09
 */
@Data
public class GrayProjectVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 总数量
     */
    private String total;

    /**
     * 不知情
     */
    private ChartPointTwoValueVo notKnow;

    /**
     * 未完成
     */
    private ChartPointTwoValueVo noComplete;

    /**
     * 无人接听
     */
    private ChartPointTwoValueVo noAnswering;

    /**
     * 停机
     */
    private ChartPointTwoValueVo downtime;

    /**
     * 拒接
     */
    private ChartPointTwoValueVo reject;

    /**
     * 传真
     */
    private ChartPointTwoValueVo fax;

    /**
     * 拒访
     */
    private ChartPointTwoValueVo refusedVisit;

    /**
     * 改号
     */
    private ChartPointTwoValueVo gaiHao;

    /**
     * 关机
     */
    private ChartPointTwoValueVo turnOff;

}
