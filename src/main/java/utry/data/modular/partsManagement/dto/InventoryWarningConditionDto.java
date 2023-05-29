package utry.data.modular.partsManagement.dto;


import lombok.Data;
import utry.data.util.ConditionUtil;

import java.io.Serializable;

/**
 * 订单详情查询条件Dto
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class InventoryWarningConditionDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 部件图号
	 */
	private ConditionUtil partDrawingNumber;
	/**
	 * 部件名称
	 */
	private ConditionUtil partDrawingName;
	/**
	 * 产品品类
	 */
	private ConditionUtil productCategory;
	/**
	 * 担当
	 */
	private ConditionUtil bear;
	/**
	 * 库位号
	 */
	private ConditionUtil location;
	/**
	 * 采购在途
	 */
	private ConditionUtil procurement;
	/**
	 * 最小安全库存
	 */
	private ConditionUtil minSafetyStock;
	/**
	 * 当前库存
	 */
	private ConditionUtil currentInventory;
	/**
	 * 订单需求
	 */
	private ConditionUtil orderNeeds;
	/**
	 * 仓库名称
	 */
	private ConditionUtil warehouseName;
	/**
	 * 供应商名称
	 */
	private ConditionUtil supplierName;
	/**
	 * 分页页数
	 */
	private String pageNum;
	/**
	 * 分页大小
	 */
	private String pageSize;


}
