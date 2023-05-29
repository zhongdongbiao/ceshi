package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 采购订单取消明细
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class CancelPurchaseOrderDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据编号
	 */
	private String receiptNumber;
	/**
	 * 采购单号
	 */
	private String purchaseOrderNo;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 库存图号
	 */
	private String partDrawingNumber ;
	/**
	 * 库存图号描述
	 */
	private String describedDrawingNo;
	/**
	 * 订货日期
	 */
	private String placeOrderTime;
	/**
	 * 订单数量
	 */
	private String orderNumber;
	/**
	 * 已发货数量
	 */
	private String shippedQuantity;
	/**
	 * 已取消数量
	 */
	private String quantityCancelled;
	/**
	 * 剩余订购数量
	 */
	private String remainingOrderQuantity;
	/**
	 * 取消数量
	 */
	private String cancelNumber;
	/**
	 * 行备注
	 */
	private String lineNote;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
