package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 通过品类查询完备率返回实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class CompletionRateByCategoryDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 日期
	 */
	@ApiModelProperty("日期:年月日")
	private String time;
	/**
	 * 产品型号
	 */
	@ApiModelProperty("产品型号")
	private String productModel;
	/**
	 * 产品类型
	 */
	@ApiModelProperty("产品类型")
	private String productType;
	/**
	 * 担当
	 */
	@ApiModelProperty("担当名称")
	private String name;
	/**
	 * 新品发布日期
	 */
	@ApiModelProperty("新品发布日期")
	private String listingDate;
	/**
	 * 新品说明书上传日期
	 */
	@ApiModelProperty("新品说明书上传日期")
	private String manualTime;
	/**
	 * 新品维修手册上传日期
	 */
	@ApiModelProperty("新品维修手册上传日期")
	private String serviceManualTime;
	/**
	 * 导出标志
	 */
	@ApiModelProperty("导出标志")
	private String flag;
}
