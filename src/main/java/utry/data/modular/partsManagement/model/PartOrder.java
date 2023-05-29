package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 订单详情明细
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PartOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 来源单号/单据号
	 */
	private String documentNumber;
	/**
	 * 担当
	 */
	private String bear;
	/**
	 * 产品型号
	 */
	private String productModel;
	/**
	 * 部件代码
	 */
	private String partCode;
	/**
	 * 部件名称
	 */
	private String partName;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 图号描述
	 */
	private String describedDrawingNo;
	/**
	 * 部件单价
	 */
	private String unitPrice;
	/**
	 * 申请数量
	 */
	private String orderNumber;
	/**
	 * 部件重量
	 */
	private String unitWeight;
	/**
	 * 订货数量
	 */
	private String goodNumber;
	/**
	 * 最小批量
	 */
	private String minQuantity;
	/**
	 * 总库可用数
	 */
	private String totalNumber;
	/**
	 * 是否符合即纳 1 符合 0 不符合 2未打标签
	 */
	private String isShipment;
	/**
	 * 出货时间
	 */
	private String shipmentTime;
	/**
	 * 可用库存
	 */
	private String useStock;
	/**
	 * 发货数量
	 */
	private String outNumber;
	/**
	 * 是否异常 0 异常 1 正常
	 */
	private String abnormal;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
