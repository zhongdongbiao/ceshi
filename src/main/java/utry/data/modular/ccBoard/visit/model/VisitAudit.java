package utry.data.modular.ccBoard.visit.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 服务回访记录审核数据
 *
 * @author zhongdongbiao
 * @date 2022/10/24 13:23
 */
@Data
public class VisitAudit implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系统状态
     */
    private String systemState;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 服务类别
     */
    private String serviceType;

    /**
     * 服务单号
     */
    private String serviceNumber;

    /**
     * 服务店编号
     */
    private String storeNumber;

    /**
     * 服务店名称
     */
    private String storeName;

    /**
     * 服务店级别
     */
    private String storeLevel;

    /**
     * 工程师编号
     */
    private String engineerId;

    /**
     * 上门时间
     */
    private String doortTime;

    /**
     * 完成时间
     */
    private String completeTime;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品系列
     */
    private String productSeries;

    /**
     * 产品型号
     */
    private String productModel;

    /**
     * 机器编号
     */
    private String machineNumber;

    /**
     * 回访备注
     */
    private String visitNote;

    /**
     * 回访时间
     */
    private String visitTme;

    /**
     * 回访坐席
     */
    private String visitTable;

    /**
     * 回访完成
     */
    private String visitComplete;

    /**
     * 回访结果代码
     */
    private String completeCode;

    /**
     * 回访结果描述
     */
    private String completeNote;

    /**
     * 拨打次数
     */
    private String dialNumber;

    /**
     * 违约代码
     */
    private String defaultCode;

    /**
     * 违约描述
     */
    private String defaultDescription;

    /**
     * 赔偿金额
     */
    private String money;

    /**
     * 回访voc
     */
    private String visitVoc;

    /**
     * 原单服务费
     */
    private String serviceMoney;

    /**
     * 费用扣还标志
     */
    private String repayMark;

    /**
     * 允许申诉标志
     */
    private String complaintMark;

    /**
     * 产品满意度分值
     */
    private String productScore;

    /**
     * 服务满意度分值
     */
    private String serviceScore;

    /**
     * 关联考核标志
     */
    private String checkMark;

    /**
     * 转热线标志
     */
    private String hotlineMark;

    /**
     *  核算中心
     */
    private String accountingCenter;

    /**
     *  核算大区
     */
    private String accountingRegional;

    /**
     * 核算片区
     */
    private String accountingArea;

    /**
     * 回访来源
     */
    private String reviewSource;

}
