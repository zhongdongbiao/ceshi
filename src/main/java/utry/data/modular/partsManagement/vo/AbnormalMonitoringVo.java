package utry.data.modular.partsManagement.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 零件管理异常监控Vo
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class AbnormalMonitoringVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 作业订单数
	 */
	private String workOrderNumber;
	/**
	 * 作业订单超时数
	 */
	private String workOrderTimeOut;
	/**
	 * 作业订单超时行数
	 */
	private String workOrderTimeOutLine;
	/**
	 * 作业订单到配货订单平均时间
	 */
	private String workOrderAverageTime;
	/**
	 * 配货单数
	 */
	private String distributionNumber;
	/**
	 * 配货订单到装箱单平均时间
	 */
	private String distributionAverageTime;
	/**
	 * 缺件单数
	 */
	private String missOrderNumber;
	/**
	 * 缺件单超时数
	 */
	private String missOrderTimeOut;
	/**
	 * 配货单到缺件处理单平均时间
	 */
	private String missOrderAverageTime;
	/**
	 * 装箱单数
	 */
	private String packingListNumber;
	/**
	 * 装箱单到收货单平均时间
	 */
	private String packingListAverageTime;
	/**
	 * 收货单数
	 */
	private String receiptNumber;
	/**
	 * 收货单到妥投时间平均时间
	 */
	private String receiptAverageTime;
	/**
	 * 订单妥投数
	 */
	private String voteNumber;
	/**
	 * 订单妥投超时数
	 */
	private String voteTimeOut;
	/**
	 * 订单妥投超时行数
	 */
	private String voteTimeOutLine;


}
