package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 采购订单明细
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PurchaseOrderDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据号
	 */
	private String documentNo;
	/**
	 * 库存图号
	 */
	private String partDrawingNumber;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 图号描述
	 */
	private String describedDrawingNo;
	/**
	 * 工厂简述
	 */
	private String factoryBriefly;
	/**
	 * 订购数量
	 */
	private String orderNumber;
	/**
	 * 实际数量
	 */
	private String actualNumber;
	/**
	 * 完成数量
	 */
	private String completeNumber;
	/**
	 * 取消数量
	 */
	private String cancelNumber;
	/**
	 * 成本单价
	 */
	private String costPrice;
	/**
	 * 成本金额
	 */
	private String costAmout;
	/**
	 * 采购不含税单价
	 */
	private String procurement;
	/**
	 * 采购含税单价
	 */
	private String taxUnitPrice;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
