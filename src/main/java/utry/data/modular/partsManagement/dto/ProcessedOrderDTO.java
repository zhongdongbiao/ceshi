package utry.data.modular.partsManagement.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 待处理订单详情明细DTO
 *
 * @author zhongdongbiao
 * @date 2022/4/22 16:01
 */
@Data
public class ProcessedOrderDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 来源单号/单据号
     */
    private String documentNumber;
    /**
     * 订单类型
     */
    private String orderType;
    /**
     * 担当
     */
    private String bear;
    /**
     * 工厂名称
     */
    private String factoryName;
    /**
     * 服务店名称
     */
    private String storeName;
    /**
     * 核算中心
     */
    private String accountingCenter;
    /**
     * 服务店编号
     */
    private String storeNumber;
    /**
     * 作业订单提交时间
     */
    private String orderSubmitTime;
    /**
     * 订单创建时间
     */
    private String orderStartTime;
    /**
     * 备货数量
     */
    private String goodQuantity;
    /**
     * 单据日期
     */
    private String documentDate;

}
