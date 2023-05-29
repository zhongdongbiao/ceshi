package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.DateConditionUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 品质反馈时长查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class ApprovalDurationQueryDTO implements Serializable {
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
	@ApiModelProperty("-1否 其他传目标值 默认其他")
	private String ifException;
	/**
	 * 状态
	 */
	@ApiModelProperty("状态")
	private ConditionDTO systemState;
	/**
	 * 管理编号
	 */
	@ApiModelProperty("管理编号")
	private ConditionDTO manageNumber;
	/**
	 * 单据日期
	 */
	@ApiModelProperty("单据日期")
	private DateConditionUtil documentDate;
	/**
	 * 服务单号
	 */
	@ApiModelProperty("服务单号")
	private ConditionDTO serviceNumber;
	/**
	 * 服务店名称
	 */
	@ApiModelProperty("服务店名称")
	private ConditionDTO serviceStoreName;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private ConditionDTO productCategory;
	/**
	 * 产品系列
	 */
	@ApiModelProperty("产品系列")
	private ConditionDTO productSeries;
	/**
	 * 产品型号
	 */
	@ApiModelProperty("产品型号")
	private ConditionDTO productModel;
	/**
	 * 工厂名称
	 */
	@ApiModelProperty("工厂名称")
	private ConditionDTO factoryName;
	/**
	 * 审核时长
	 */
	@ApiModelProperty("审核时长")
	private ConditionDTO auditDuration;
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
