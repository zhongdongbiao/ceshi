package utry.data.modular.partsManagement.dto;


import lombok.Data;
import utry.data.modular.partsManagement.model.PartOrder;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单详情DTO
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class OrderDetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 来源单号
	 */
	private String documentNumber;
	/**
	 * 预计完成时间
	 */
	private String exceptCompletionTime;
	/**
	 * 预计到货时间
	 */
	private String exceptGoodTime;
	/**
	 * 单据日期
	 */
	private String documentDate;
	/**
	 * 当前状态
	 */
	private String currentState;
	/**
	 * 来源单名称
	 */
	private String documentName;
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
	 * 产品型号
	 */
	private String productModel;
	/**
	 * 核算中心
	 */
	private String accountingCenter;
	/**
	 * 核算大区
	 */
	private String accountingDistrict;
	/**
	 * 核算片区
	 */
	private String accountingArea;
	/**
	 * 使用金预类型
	 */
	private String useGoldPreType;
	/**
	 * 仓库代码
	 */
	private String warehouseCode;
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	/**
	 * 可用金额
	 */
	private String amountAvailable;
	/**
	 * 备货数量
	 */
	private String goodQuantity;
	/**
	 * 金额合计
	 */
	private String aggregateAmount;
	/**
	 * 总重量
	 */
	private String totalWeight;
	/**
	 * 订单创建时间
	 */
	private String orderStartTime;
	/**
	 * 派工单号
	 */
	private String dispatchingOrder;
	/**
	 * 作业订单提交时间
	 */
	private String orderSubmitTime;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * 备注
	 */
	private String note;
	/**
	 * 数量合计
	 */
	private String numberTotal;

	/**
	 * 系统状态
	 */
	private String systemState;

	/**
	 * 订单详情
	 */
	private List<PartOrder> partInformationList;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
