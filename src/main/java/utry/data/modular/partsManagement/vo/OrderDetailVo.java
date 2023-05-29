package utry.data.modular.partsManagement.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 订单列表回显
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class OrderDetailVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 来源单号/单据号
	 */
	private String documentNumber;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 担当
	 */
	private String bear;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 服务店名称
	 */
	private String storeName;
	/**
	 * 预计完成时间
	 */
	private String exceptCompletionTime;
	/**
	 * 单据日期
	 */
	private String documentDate;
	/**
	 * 备货数量
	 */
	private Integer goodQuantity;
	/**
	 * 订单历时
	 */
	private String orderAfter;
	/**
	 * 物流历时
	 */
	private String logisticsTime;
	/**
	 * 出货历时
	 */
	private String shipmentTime;

	/**
	 * 订单需求
	 */
	private String orderNeed;

	/**
	 * 采购历时
	 */
	private String purchaseTime;

	/**
	 * 当前状态
	 */
	private String currentState ;
	/**
	 * 预计到货时间
	 */
	private String exceptGoodTime;
	/**
	 * 发货进度
	 */
	private String deliverySchedule;
	/**
	 * 采购订单数
	 */
	private String purchaseOrderNumber;
	/**
	 * 装箱时间
	 */
	private String packingTime;
	/**
	 * 发货时间
	 */
	private String deliveryTime;
	/**
	 * 物流单号
	 */
	private String logisticsSingleNumber;
	/**
	 * 妥投时间
	 */
	private String appropriateInvestTime;
	/**
	 * 是否异常 0异常 1正常
	 */
	private String isAbnormal;
	/**
	 * 即纳率是否异常 0异常 1正常
	 */
	private String isShipAbnormal;
	/**
	 * NDS2是否异常 0异常 1正常
	 */
	private String isNds2Abnormal;

}
