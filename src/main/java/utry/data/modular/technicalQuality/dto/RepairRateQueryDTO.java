package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.DateConditionUtil;
import utry.data.util.DateTimeUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 一次性修复率查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class RepairRateQueryDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 开始日期
	 */
	@ApiModelProperty("开始日期")
	private String startTime;
	/**
	 * 结束日期
	 */
	@ApiModelProperty("结束日期")
	private String endTime;
	/**
	 * 是否查看异常
	 */
	@ApiModelProperty("0否1是")
	private String ifException;
	/**
	 * 派工单号
	 */
	@ApiModelProperty("派工单号")
	private ConditionDTO dispatchingOrder;
	/**
	 * 服务店名称
	 */
	@ApiModelProperty("服务店名称")
	private ConditionDTO storeName;
	/**
	 * 工程师
	 */
	@ApiModelProperty("工程师")
	private ConditionDTO engineerName;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private ConditionDTO productCategory;
	/**
	 * 派工时间
	 */
	@ApiModelProperty("派工时间")
	private DateConditionUtil dispatchingTime;
	/**
	 * 预约时间
	 */
	@ApiModelProperty("预约时间")
	private DateConditionUtil appointmentTime;
	/**
	 * 完成时间
	 */
	@ApiModelProperty("完成时间")
	private ConditionDTO finishTime;
	/**
	 * 已流转时间
	 */
	@ApiModelProperty("已流转时间")
	private ConditionDTO turnoverTime;
	/**
	 * 上门次数
	 */
	@ApiModelProperty("上门次数")
	private ConditionDTO visitsNumber;
	/**
	 * 所属大区
	 */
	@ApiModelProperty("所属大区")
	private ConditionDTO accountingArea;
	/**
	 * 地区
	 */
	@ApiModelProperty("地区")
	private ConditionDTO area;
	/**
	 * 服务类型
	 */
	@ApiModelProperty("服务类型")
	private ConditionDTO serviceType;
	/**
	 * 当前状态
	 */
	@ApiModelProperty("当前状态")
	private ConditionDTO systemState;
	/**
	 * 型号id
	 */
	@ApiModelProperty("型号id(对应label=3)")
	private List<String> modelList;
	/**
	 * 类型id
	 */
	@ApiModelProperty("类型id(对应label=2)")
	private List<String> list;
	/**
	 * 产品品类集合
	 */
	@ApiModelProperty("产品品类集合(对应label=1)")
	private List<String> productCategoryList;
	/**
	 * 工程师id
	 */
	@ApiModelProperty("工程师id")
	private String engineerId;
	/**
	 * 是否全部导出
	 */
	@ApiModelProperty("是否全部导出 1是0否")
	private String allExport;
	/**
	 * 导出数量
	 */
	@ApiModelProperty("导出数量")
	private int number;
}
