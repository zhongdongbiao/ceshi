package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 品质反馈时长查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class CompletionRateQueryDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 日期
	 */
	@ApiModelProperty("日期:年月")
	private String time;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private ConditionDTO productCategory;
	/**
	 * 产品品类集合
	 */
	@ApiModelProperty("产品类型集合")
	private List<String> list;
	/**
	 * 担当名称
	 */
	@ApiModelProperty("担当名称")
	private ConditionDTO name;
	/**
	 * 新品上市资料七天完备率
	 */
	@ApiModelProperty("新品上市资料七天完备率")
	private ConditionDTO completionRate;
	/**
	 * 关联产品量
	 */
	@ApiModelProperty("关联产品量")
	private ConditionDTO count;
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
