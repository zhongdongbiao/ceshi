package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 配货取消单明细
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class CancelDstributionOrderDetail implements Serializable {
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
	 * 来源单号
	 */
	private String documentNumber;
	/**
	 * 来源单行号
	 */
	private String sourceLineNumber;
	/**
	 * 配货单号
	 */
	private String distributionSingleNo;
	/**
	 * 服务店编号
	 */
	private String stockoutNumber;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * 订单日期
	 */
	private String orderTime;
	/**
	 * 申请数量
	 */
	private String applyNumber;
	/**
	 * 分配数量
	 */
	private String distributionNumber;
	/**
	 * 出库数量
	 */
	private String outNumber;
	/**
	 * 备货取消数量
	 */
	private String cancelDstributionOrderNumber;
	/**
	 * 取消类型
	 */
	private String cancelType;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
