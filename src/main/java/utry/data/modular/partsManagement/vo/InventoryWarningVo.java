package utry.data.modular.partsManagement.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 订单列表回显
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class InventoryWarningVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 部件图号
	 */
	private String partDrawingNumber;
	/**
	 * 部件名称
	 */
	private String partDrawingName;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 产品品类
	 */
	private String productCategory;
	/**
	 * 担当
	 */
	private String bear;
	/**
	 * 库位号
	 */
	private String location;
	/**
	 * 采购在途
	 */
	private String procurement;
	/**
	 * 最小安全库存
	 */
	private String minSafetyStock;
	/**
	 * 当前库存
	 */
	private String currentInventory;
	/**
	 * 订单需求
	 */
	private String orderNeeds;
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	/**
	 * 供应商名称
	 */
	private String supplierName;

}
