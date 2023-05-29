package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 回访结果Vo
 * @author zhongdongbiao
 * @date 2022/11/2 11:02
 */
@Data
public class VisitResultVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    private String systemState;

    /**
     * 回访坐席
     */
    private String visitTable;

    /**
     * 坐席编号
     */
    private String agentNumber;

    /**
     * 核算大区
     */
    private String accountingRegional;

    /**
     * 单据号
     */
    private String documentNo;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 单据日期
     */
    private String date;

    /**
     * 服务类别
     */
    private String serviceType;

    /**
     * 服务单号
     */
    private String serviceNumber;

    /**
     * 回访时间
     */
    private String visitTme;

    /**
     * 回访结果描述
     */
    private String completeNote;

    /**
     * 违约结果分类
     */
    private String resultType;
}
