package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.modular.baseConfig.dto.IndicatorUserDTO;

import java.io.Serializable;
import java.util.List;

/**
 * 目标配置实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class TechnicalQualityUserDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 担当id
	 */
	@ApiModelProperty("担当id")
	private String accountId;
	/**
	 * 担当名称
	 */
	@ApiModelProperty("担当名称")
	private String name;
	/**
	 * 一次修复率
	 */
	@ApiModelProperty("一次修复率")
	private String repairRate;
	/**
	 * 一次修复率达成
	 */
	@ApiModelProperty("一次修复率达成")
	private String rEligible;
	/**
	 * 一次修复率总
	 */
	@ApiModelProperty("一次修复率总")
	private String rTotal;
	/**
	 * 一次修复率达成
	 */
	@ApiModelProperty("品质单审核作业时长达成")
	private String aEligible;
	/**
	 * 一次修复率总
	 */
	@ApiModelProperty("品质单审核作业时长总")
	private String aTotal;
	/**
	 * 一次修复率达成
	 */
	@ApiModelProperty("新品上市资料七天完备率达成")
	private String cEligible;
	/**
	 * 一次修复率总
	 */
	@ApiModelProperty("新品上市资料七天完备率总")
	private String cTotal;
	/**
	 * 品质单审核作业时长
	 */
	@ApiModelProperty("品质单审核作业时长")
	private String approvalDuration;
	/**
	 * 新品上市资料七天完备率
	 */
	@ApiModelProperty("新品上市资料七天完备率")
	private String completionRate;
	/**
	 * 一次修复率环比
	 */
	@ApiModelProperty("一次修复率环比")
	private String repairRateChainRatio;
	/**
	 * 品质单审核作业时长环比
	 */
	@ApiModelProperty("品质单审核作业时长环比")
	private String approvalDurationChainRatio;
	/**
	 * 目标值
	 */
	@ApiModelProperty("目标值")
	private List<IndicatorUserDTO> list;
}
