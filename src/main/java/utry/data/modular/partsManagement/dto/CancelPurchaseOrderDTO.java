package utry.data.modular.partsManagement.dto;


import lombok.Data;
import utry.data.modular.partsManagement.model.CancelPurchaseOrderDetail;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 采购订单取消DTO
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class CancelPurchaseOrderDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据编号
	 */
	private String receiptNumber;
	/**
	 * 单据日期
	 */
	private String receiptTime;
	/**
	 * 备注
	 */
	private String note;
	/**
	 * 核算中心
	 */
	private String accountingCenter;
	/**
	 * 供应商简称
	 */
	private String supplierShortName;
	/**
	 * 供应商代码
	 */
	private String supplierCode;
	/**
	 * 供应商名称
	 */
	private String supplierName;

	/**
	 * 系统状态
	 */
	private String systemState;

	/**
	 * 采购订单取消明细
	 */
	private List<CancelPurchaseOrderDetail> purchaseOrderdetail;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
