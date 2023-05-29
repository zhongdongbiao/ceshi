package utry.data.modular.partsManagement.dto;


import lombok.Data;
import utry.data.modular.partsManagement.model.PartOrder;
import utry.data.util.ConditionUtil;
import utry.data.util.DateConditionUtil;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 订单详情查询条件Dto
 *
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class OrderDetailConditionDTO implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 来源单号/单据号
	 */
	private ConditionUtil documentNumber;
	/**
	 * 担当
	 */
	private ConditionUtil bear;
	/**
	 * 工厂名称
	 */
	private ConditionUtil factoryName;
	/**
	 * 服务店名称
	 */
	private ConditionUtil storeName;
	/**
	 * 预计完成时间
	 */
	private DateConditionUtil exceptCompletionTime;
	/**
	 * 单据日期
	 */
	private DateConditionUtil documentDate;
	/**
	 * 备货数量
	 */
	private ConditionUtil goodQuantity;
	/**
	 * 订单历时
	 */
	private ConditionUtil orderAfter;
	/**
	 * 订单历时
	 */
	private ConditionUtil orderNeed;
	/**
	 * 采购历时
	 */
	private String purchaseTime;
	/**
	 * 预计到货时间
	 */
	private DateConditionUtil exceptGoodTime;
	/**
	 * 发货进度
	 */
	private ConditionUtil deliverySchedule;
	/**
	 * 采购订单数
	 */
	private ConditionUtil purchaseOrderNumber;
	/**
	 * 物流历时
	 */
	private ConditionUtil logisticsTime;
	/**
	 * 出货历时
	 */
	private ConditionUtil shipmentTime;
	/**
	 * 装箱时间
	 */
	private DateConditionUtil packingTime;
	/**
	 * 发货时间
	 */
	private DateConditionUtil deliveryTime;
	/**
	 * 物流单号
	 */
	private ConditionUtil logisticsSingleNumber;
	/**
	 * 妥投时间
	 */
	private DateConditionUtil appropriateInvestTime;

	/**
	 * 开始时间
	 */
	private String startDate;
	/**
	 * 采购单号
	 */
	private String documentNo;
	/**
	 * 结束时间
	 */
	private String endDate;
	/**
	 * 订单类型 作业订单，服务店备货订单
	 */
	private String orderType;
	/**
	 * 产品品类代码
	 */
	private String productCategoryCode;
	/**
	 * 订单状态 0全部 1待处理 2缺件采购中 3已装箱 4已妥投
	 */
	private String orderState;
	/**
	 * 库存日期
	 */
	private String inventoryDate;
	/**
	 * 部件图号
	 */
	private List<String> partDrawingNos;
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
	/**
	 * 分页页数
	 */
	private String pageNum;
	/**
	 * 分页大小
	 */
	private String pageSize;


}
