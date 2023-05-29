package utry.data.modular.partsManagement.dto;


import lombok.Data;
import utry.data.modular.partsManagement.model.PurchaseOrderDetail;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 采购订单DTO
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PurchaseOrderDTO implements Serializable {
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
     * 系统状态
     */
    private String systemState;

	/**
	 * 采购订单明细
	 */
	private List<PurchaseOrderDetail> partInformationList;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
