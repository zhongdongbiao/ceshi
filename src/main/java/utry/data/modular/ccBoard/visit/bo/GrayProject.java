package utry.data.modular.ccBoard.visit.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhongdongbiao
 * @date 2022/10/31 17:09
 */
@Data
public class GrayProject implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 数量
     */
    private String total;

    /**
     * 不知情
     */
    private String notKnow;

    /**
     * 不知情占比
     */
    private String notKnowRate;

    /**
     * 未完成
     */
    private String noComplete;

    /**
     * 未完成占比
     */
    private String noCompleteRate;

    /**
     * 无人接听
     */
    private String noAnswering;

    /**
     * 无人接听占比
     */
    private String noAnsweringRate;

    /**
     * 停机
     */
    private String downtime;

    /**
     * 停机占比
     */
    private String downtimeRate;

    /**
     * 拒接
     */
    private String reject;

    /**
     * 拒接占比
     */
    private String rejectRate;

    /**
     * 传真
     */
    private String fax;

    /**
     * 传真占比
     */
    private String faxRate;

    /**
     * 拒访
     */
    private String refusedVisit;

    /**
     * 拒访占比
     */
    private String refusedVisitRate;

    /**
     * 改号
     */
    private String gaiHao;

    /**
     * 改号占比
     */
    private String gaiHaoRate;

    /**
     * 关机
     */
    private String turnOff;

    /**
     * 关机占比
     */
    private String turnOffRate;
}
