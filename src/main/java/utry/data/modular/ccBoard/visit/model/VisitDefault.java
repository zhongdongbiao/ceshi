package utry.data.modular.ccBoard.visit.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 回访违约单
 *
 * @author zhongdongbiao
 * @date 2022/10/24 13:33
 */
@Data
public class VisitDefault implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 系统状态
     */
    private String systemState;

    /**
     * 允许申诉标志
     */
    private String complaintMark;

    /**
     * 关联考核标志
     */
    private String checkMark;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 单据号
     */
    private String documentNo;

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
     * 服务日期
     */
    private String serviceDate;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 产品型号
     */
    private String productModel;

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
     * 上年台量
     */
    private String lastNumber;

    /**
     * 违约代码
     */
    private String defaultCode;

    /**
     * 违约描述
     */
    private String defaultDescription;

    /**
     * 费用扣还标志
     */
    private String repayMark;

    /**
     * 原单服务费
     */
    private String serviceMoney;

    /**
     * 违约详细说明
     */
    private String defaultDetailInstruction;

    /**
     * 保内零件数
     */
    private String insurancePart;

    /**
     * 保内零件费
     */
    private String insuranceMoney;

    /**
     * 原定赔偿金额
     */
    private String originalMoney;

    /**
     * 赔偿金额
     */
    private String money;

    /**
     * 审核结果
     */
    private String result;

    /**
     * 申诉核实结果
     */
    private String complaintResult;

    /**
     * 申诉内容
     */
    private String complaintContent;

    /**
     * 申阅意见
     */
    private String reviewOpinions;

    /**
     * 申诉核实说明
     */
    private String completeNote;

    /**
     * 审核意见
     */
    private String complaintOpinion;

    /**
     * 核算中心
     */
    private String accountingCenter;

    /**
     * 核算大区
     */
    private String accountingRegional;

    /**
     * 核算片区
     */
    private String accountingArea;

}
