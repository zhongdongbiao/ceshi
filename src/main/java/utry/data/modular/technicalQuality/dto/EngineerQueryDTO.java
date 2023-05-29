package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 工程师列表实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class EngineerQueryDTO implements Serializable {
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
	 * 类型id
	 */
	@ApiModelProperty("类型id(对应label=2)")
	private List<String> list;
	/**
	 * 型号id
	 */
	@ApiModelProperty("型号id(对应label=3)")
	private List<String> modelList;
	/**
	 * 产品品类集合
	 */
	@ApiModelProperty("产品品类集合(对应label=1)")
	private List<String> productCategoryList;
	/**
	 * 工程师名称
	 */
	@ApiModelProperty("工程师名称")
	private ConditionDTO engineerName;
	/**
	 * 工程师编号
	 */
	@ApiModelProperty("工程师编号")
	private ConditionDTO engineerId;
	/**
	 * 所属门店
	 */
	@ApiModelProperty("所属门店")
	private ConditionDTO storeName;
	/**
	 * 对应区管
	 */
	@ApiModelProperty("对应区管")
	private ConditionDTO adminName;
	/**
	 * 一次修复率
	 */
	@ApiModelProperty("一次修复率")
	private ConditionDTO repairRate;
	/**
	 * 服务总量
	 */
	@ApiModelProperty("服务总量")
	private ConditionDTO total;
	/**
	 * 一次修复率
	 */
	@ApiModelProperty("达标总量")
	private ConditionDTO eligible;
	/**
	 * 未达标总量
	 */
	@ApiModelProperty("未达标总量")
	private ConditionDTO unEligible;
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
	/**
	 * 符合条件的工程师
	 */
	@ApiModelProperty("符合条件的工程师")
	private List<String> engineers;

}
