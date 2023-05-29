package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 故障分析饼图实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class PieChartQueryDTO implements Serializable {
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
	 * 筛选---故障原因
	 */
	@ApiModelProperty("筛选---故障原因代码")
	private String faultCauseCode;
	/**
	 * 部品图号
	 */
	@ApiModelProperty("部品图号")
	private ConditionDTO partDrawingNo;
	/**
	 * 图号描述
	 */
	@ApiModelProperty("图号描述")
	private ConditionDTO describedDrawingNo;
	/**
	 * 零件更换数
	 */
	@ApiModelProperty("零件更换数")
	private ConditionDTO replaceNum;
	/**
	 * 零件调整数
	 */
	@ApiModelProperty("零件调整数")
	private ConditionDTO repairNum;
}
