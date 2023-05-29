package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 一次性修复率折线图实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class LineChartQueryDTO implements Serializable {
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
	 * 日期条件
	 */
	@ApiModelProperty("日期条件")
	private String aggregateDate;
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
	 * 聚合方式
	 */
	@ApiModelProperty("聚合方式 0按日聚合 1按月聚合")
	private String polymerization;
}
