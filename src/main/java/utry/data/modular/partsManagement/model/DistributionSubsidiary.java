package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 配货明细
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class DistributionSubsidiary implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 配货单号
	 */
	private String distributionSingleNo;
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
	 * 库位号
	 */
	private String location;
	/**
	 * 订单日期
	 */
	private String orderDate;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * 库管员
	 */
	private String warehouseKeeper;
	/**
	 * 配货号
	 */
	private String distribution;
	/**
	 * 申请数量
	 */
	private String applyNumber;
	/**
	 * 配货数量
	 */
	private String distributionNumber;
	/**
	 * 关联订单号
	 */
	private String associatedOrderNumber;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
