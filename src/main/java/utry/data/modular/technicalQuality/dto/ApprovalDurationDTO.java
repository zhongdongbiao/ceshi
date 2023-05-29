package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 品质反馈时长查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class ApprovalDurationDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 日期条件
	 */
	@ApiModelProperty("日期条件")
	private String aggregateDate;
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
	@ApiModelProperty("0否 1是 默认1")
	private String ifException;
	/**
	 * 状态
	 */
	@ApiModelProperty("状态")
	private String systemState;
	/**
	 * 管理编号
	 */
	@ApiModelProperty("管理编号")
	private String manageNumber;
	/**
	 * 单据日期
	 */
	@ApiModelProperty("单据日期")
	private String documentDate;
	/**
	 * 服务单号
	 */
	@ApiModelProperty("服务单号")
	private String serviceNumber;
	/**
	 * 服务店名称
	 */
	@ApiModelProperty("服务店名称")
	private String serviceStoreName;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private String productCategory;
	/**
	 * 产品系列
	 */
	@ApiModelProperty("产品系列")
	private String productSeries;
	/**
	 * 产品型号
	 */
	@ApiModelProperty("产品型号")
	private String productModel;
	/**
	 * 工厂名称
	 */
	@ApiModelProperty("工厂名称")
	private String factoryName;
	/**
	 * 审核时长
	 */
	@ApiModelProperty("审核时长")
	private String auditDuration;
	/**
	 * 制造日期
	 */
	@ApiModelProperty("制造日期")
	private String manufacturingDate;
	/**
	 * 购买日期
	 */
	@ApiModelProperty("购买日期")
	private String purchaseDate;
	/**
	 * 产品故障现象
	 */
	@ApiModelProperty("产品故障现象")
	private String productSymptom;
}
