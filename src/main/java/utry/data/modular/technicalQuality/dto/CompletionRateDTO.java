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
public class CompletionRateDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 日期
	 */
	@ApiModelProperty("日期:年月日")
	private String time;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private String productCategory;
	/**
	 * 产品品类编码
	 */
	@ApiModelProperty("产品品类编码")
	private String productCategoryCode;
	/**
	 * 担当名称
	 */
	@ApiModelProperty("担当名称")
	private String name;
	/**
	 * 新品上市资料七天完备率
	 */
	@ApiModelProperty("新品上市资料七天完备率")
	private String completionRate;
	/**
	 * 关联产品量
	 */
	@ApiModelProperty("关联产品量")
	private String count;
}
