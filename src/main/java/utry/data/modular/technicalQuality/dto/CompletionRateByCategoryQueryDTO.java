package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.DateConditionUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 通过品类查询完备率实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class CompletionRateByCategoryQueryDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 日期
	 */
	@ApiModelProperty("日期:年月")
	private String time;
	/**
	 * 产品品类编码
	 */
	@ApiModelProperty("产品品类编码")
	private String productCategoryCode;
	/**
	 * 产品品类集合
	 */
	@ApiModelProperty("产品类型集合")
	private List<String> list;
	/**
	 * 产品型号
	 */
	@ApiModelProperty("产品型号")
	private ConditionDTO productModel;
	/**
	 * 产品类型
	 */
	@ApiModelProperty("产品类型")
	private ConditionDTO productType;
	/**
	 * 担当
	 */
	@ApiModelProperty("担当名称")
	private ConditionDTO name;
	/**
	 * 新品发布日期
	 */
	@ApiModelProperty("新品发布日期")
	private DateConditionUtil listingDate;
	/**
	 * 新品说明书上传日期
	 */
	@ApiModelProperty("新品说明书上传日期")
	private DateConditionUtil manualTime;
	/**
	 * 新品维修手册上传日期
	 */
	@ApiModelProperty("新品维修手册上传日期")
	private DateConditionUtil serviceManualTime;
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
