package utry.data.modular.partsManagement.model;


import lombok.Data;

import java.io.Serializable;

/**
 * 收货单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class ReceiptDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 收货单号/来源单号
	 */
	private String documentNumber;
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
	private String associatedOrderNumber;
	/**
	 * 实收数量
	 */
	private String goodNumber;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 申请数量
	 */
	private String applyNumber;
	/**
	 * 未收数量
	 */
	private String notReceive;
	/**
	 * 异常数量
	 */
	private String abnormalNumber;

}
