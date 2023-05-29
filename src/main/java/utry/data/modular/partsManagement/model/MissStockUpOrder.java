package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 缺件备货单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class MissStockUpOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据号
	 */
	private String documentNo;
	/**
	 * 产品型号
	 */
	private String productModel;
	/**
	 * 部件代码
	 */
	private String partCode;
	/**
	 * 单据日期
	 */
	private String documentDate;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * 备货数量
	 */
	private String goodQuantity;
	/**
	 * 处理数量
	 */
	private String processNumber;
	/**
	 * 关联订单号
	 */
	private String associatedOrderNumber;
	/**
	 * 服务店编号
	 */
	private String serviceStoreNumber;
	/**
	 * 服务店名称
	 */
	private String serviceStoreName;
	/**
	 * 预计到货时间
	 */
	private String goodarrivalTime;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 缺件处理单ID
	 */
	private String missDealOrderId;

	/**
	 * 系统状态
	 */
	private String systemState;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
