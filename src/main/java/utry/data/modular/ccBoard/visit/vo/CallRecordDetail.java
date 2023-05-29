package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 系统通话记录Vo
 * @author zhongdongbiao
 * @date 2022/10/31 10:28
 */
@Data
public class CallRecordDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 录音文件
     */
    private String recordFileName;

    /**
     * 服务评价结果
     */
    private String customerEvaluation;

    /**
     * 呼叫时间
     */
    private String callTime;

    /**
     * 坐席工号
     */
    private String number;

    /**
     * 录音id
     */
    private String recordId;


    /**
     * 录音名称
     */
    private String recordName;

    /**
     * 热线编号
     */
    private String hotlineNumber;

    /**
     * 回访时间
     */
    private String visitTime;


    /**
     * 回访结果
     */
    private String result;

    /**
     * 部门
     */
    private String dept;


    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 服务明细
     */
    private String serviceDetail;


    /**
     * 服务单号
     */
    private String serviceNumber;

    /**
     * 派工单号
     */
    private String dispatchingOrder;

    /**
     * 回访单号
     */
    private String accountCode;
}
