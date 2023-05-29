package utry.data.modular.partsManagement.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.ConditionUtil;
import utry.data.util.DateConditionUtil;

import java.io.Serializable;

/**
 * 收货单Vo
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class ReceiptConditionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 作业订单号
	 */
	@ApiModelProperty("作业订单")
	private ConditionUtil documentNumber;
	/**
	 * 收货单号
	 */
	@ApiModelProperty("收货单号")
	private ConditionUtil receiptNumber;
	/**
	 * 订单日期
	 */
	@ApiModelProperty("订单日期")
	private DateConditionUtil orderDate;
	/**
	 * 装箱日期
	 */
	@ApiModelProperty("装箱日期")
	private DateConditionUtil loadingDate;
	/**
	 * 发货时间
	 */
	@ApiModelProperty("发货时间")
	private DateConditionUtil deliveryTime;
	/**
	 * 出货历时
	 */
	@ApiModelProperty("出货历时")
	private ConditionUtil shipmentTime;
	/**
	 * 妥投时间
	 */
	@ApiModelProperty("妥投时间")
	private DateConditionUtil appropriateInvestTime;
	/**
	 * 物流耗时
	 */
	@ApiModelProperty("物流耗时")
	private ConditionUtil logisticsTime;
	/**
	 * 物流状态
	 */
	@ApiModelProperty("物流状态")
	private ConditionUtil logisticsStatus;
	/**
	 * 需求量
	 */
	@ApiModelProperty("需求量")
	private ConditionUtil demand;
	/**
	 * 未发行数
	 */
	@ApiModelProperty("未发行数")
	private ConditionUtil notOffer;
	/**
	 * 开始时间
	 */
	@ApiModelProperty("开始时间")
	private String startDate;
	/**
	 * 结束时间
	 */
	@ApiModelProperty("结束时间")
	private String endDate;
	/**
	 * 分页页数
	 */
	@ApiModelProperty("分页页数")
	private String pageNum;
	/**
	 * 分页大小
	 */
	@ApiModelProperty("分页大小")
	private String pageSize;


}
