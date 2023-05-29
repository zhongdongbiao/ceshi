package utry.data.modular.partsManagement.vo;


import lombok.Data;
import utry.data.util.ConditionUtil;
import utry.data.util.DateConditionUtil;

import java.io.Serializable;
import java.util.Date;

;

/**
 * 采购订单查询条件DTO
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PurchaseOrderListVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据号
	 */
	private String documentNo;

	/**
	 * 单据日期
	 */
	private String documentDate;
	/**
	 * 采购订单类型
	 */
	private String purchaseOrderType;
	/**
	 * 采购货期
	 */
	private String purchaseTime;
	/**
	 * 完成日期
	 */
	private String completionDate;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * 供应商代码
	 */
	private String supplierCode;
	/**
	 * 供应商名称
	 */
	private String supplierName;
	/**
	 * 种类合计
	 */
	private String speciesTotal;
	/**
	 * 订货仓库
	 */
	private String goodWarehouse;
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	/**
	 * 数量合计
	 */
	private String numberTotal;
	/**
	 * 金额合计
	 */
	private String aggregateAmount;
	/**
	 * 运输方式
	 */
	private String transportMethod;

	/**
	 * 订单历时
	 */
	private String orderAfter;
	/**
	 * 是否异常 0正常 1 异常
	 */
	private String isAbnormal;
	/**
	 * 超时时间
	 */
	private String abnormalTime;

    /**
     * 系统状态
     */
    private String systemState;


}
