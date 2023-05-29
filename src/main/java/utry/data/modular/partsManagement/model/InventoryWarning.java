package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 库存预警
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class InventoryWarning implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 库存图号
	 */
	private String partDrawingNumber;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 部件代码
	 */
	private String partCode;
	/**
	 * 部件名称
	 */
	private String partDrawingName;
	/**
	 * 图号描述
	 */
	private String describedDrawingNo;
	/**
	 * 图号属性
	 */
	private String attributesDrawingNo;
	/**
	 * 核算中心
	 */
	private String accountingCenter;
	/**
	 * 供应商名称
	 */
	private String supplierName;
	/**
	 * 采购单价不含税
	 */
	private String purchasingPrice;
	/**
	 * 适用机型
	 */
	private String suitableModel;
	/**
	 * 产品品类
	 */
	private String productCategory;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 最小安全库存
	 */
	private String minSafetyStock;
	/**
	 * 最大安全库存
	 */
	private String maxSafetyStock;
	/**
	 * 当前库存
	 */
	private String currentInventory;

	/**
	 * 系统状态
	 */
	private String systemState;

	/**
	 * 库存日期
	 */
	private String inventoryDate;
	/**
	 * 供应商编号
	 */
	private String supplierCode;
	/**
	 * 采购订单类型
	 */
	private String purchaseOrderType;
	/**
	 * 工厂代码
	 */
	private String factoryCode;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
