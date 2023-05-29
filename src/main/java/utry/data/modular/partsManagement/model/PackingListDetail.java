package utry.data.modular.partsManagement.model;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 装箱单数据详情数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PackingListDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 装箱单号
	 */
	private String packingListNo;
	/**
	 * 来源单号
	 */
	private String associatedOrderNumber;
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
	private String partDrawingNo ;
	/**
	 * 来源单行号
	 */
	private String sourceLineNumber ;
	/**
	 * 申请数量
	 */
	private String applyNumber;
	/**
	 * 配货数量
	 */
	private String goodNumber;
	/**
	 * 出库数量
	 */
	private String outNumber;
	/**
	 * 配货单号
	 */
	private String distributionSingleNo;

}
