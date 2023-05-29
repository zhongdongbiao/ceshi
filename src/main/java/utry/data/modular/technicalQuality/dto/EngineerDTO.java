package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.util.ConditionUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 工程师列表实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class EngineerDTO implements Serializable {
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
	private String engineerName;
	/**
	 * 工程师编号
	 */
	@ApiModelProperty("工程师编号")
	private String engineerId;
	/**
	 * 所属门店
	 */
	@ApiModelProperty("所属门店")
	private String storeName;
	/**
	 * 对应区管
	 */
	@ApiModelProperty("对应区管")
	private String adminName;
	/**
	 * 一次修复率
	 */
	@ApiModelProperty("一次修复率")
	private String repairRate;
	/**
	 * 服务总量
	 */
	@ApiModelProperty("服务总量")
	private String total;
	/**
	 * 一次修复率
	 */
	@ApiModelProperty("达标总量")
	private String eligible;
	/**
	 * 未达标总量
	 */
	@ApiModelProperty("未达标总量")
	private String unEligible;
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
	 * 产品类型
	 */
	@ApiModelProperty("产品类型")
	private String productType;
	/**
	 * 产品类型编码
	 */
	@ApiModelProperty("产品类型编码")
	private String productTypeCode;
}
