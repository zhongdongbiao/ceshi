package utry.data.modular.partsManagement.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 收货单Vo
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class ReceiptVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 作业订单号
	 */
	private String documentNumber;
	/**
	 * 收货单号
	 */
	private String receiptNumber;
	/**
	 * 订单日期
	 */
	private String orderDate;
	/**
	 * 装箱日期
	 */
	private String loadingDate;
	/**
	 * 发货时间
	 */
	private String deliveryTime;
	/**
	 * 出货历时
	 */
	private String shipmentTime;
	/**
	 * 出货历时超时天数 0正常
	 */
	private String overTime;
	/**
	 * 物流耗时超时天数 0正常
	 */
	private String logisticsTimeOut;
	/**
	 * 妥投时间
	 */
	private String appropriateInvestTime;
	/**
	 * 物流耗时
	 */
	private String logisticsTime;
	/**
	 * 物流状态
	 */
	private String logisticsStatus;
	/**
	 * 需求量
	 */
	private String demand;
	/**
	 * 未发行数
	 */
	private String notOffer;


}
