package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 导出查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class ExportConditionDTO implements Serializable {
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
	 * 产品类型代码
	 */
	@ApiModelProperty("产品类型代码")
	private String productTypeCode;
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
