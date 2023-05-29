package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 话务明细
 * @author zhongdongbiao
 * @date 2022/10/31 9:32
 */
@Data
public class CallDetailVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 录音id
     */
    private String recordId;


    /**
     * 录音名称
     */
    private String recordName;

    /**
     * 录音文件名称
     */
    private String recordFileName;

    /**
     * 热线编号
     */
    private String hotlineNumber;

    /**
     * 回访时间
     */
    private String visitTime;


    /**
     * 呼叫时间
     */
    private String callTime;

    /**
     * 回访结果
     */
    private String result;

    /**
     * 部门
     */
    private String dept;

    /**
     * 工号
     */
    private String number;

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
     * 客户评价
     */
    private String customerEvaluation;

    /**
     * 服务单号
     */
    private String serviceNumber;

    /**
     * 派工单号
     */
    private String dispatchingOrder;


}
