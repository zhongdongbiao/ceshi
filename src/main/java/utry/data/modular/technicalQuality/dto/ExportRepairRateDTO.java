package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 导出查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class ExportRepairRateDTO implements Serializable {
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
	 * 产品品类集合
	 */
	@ApiModelProperty("产品品类集合(对应label=1)")
	private List<String> productCategoryList;
	/**
	 * 产品类型集合
	 */
	@ApiModelProperty("产品类型集合(对应label=1)")
	private List<String> typeList;
	/**
	 * 产品类型代码
	 */
	@ApiModelProperty("产品类型代码")
	private String productTypeCode;
	/**
	 * 产品类型
	 */
	@ApiModelProperty("产品类型")
	private String productType;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private String productCategory;
	/**
	 * 产品品类代码
	 */
	@ApiModelProperty("产品品类代码")
	private String productCategoryCode;
	/**
	 * 全年服务总量
	 */
	@ApiModelProperty("全年服务总量")
	private int total;
	/**
	 * 全年达标总量
	 */
	@ApiModelProperty("全年达标总量")
	private int eligible;
	/**
	 * 全年一次性修复率
	 */
	@ApiModelProperty("全年一次性修复率")
	private String repairRate;
	/**
	 * 月一次性修复率
	 */
	@ApiModelProperty("月一次性修复率")
	private String one;
	private String two;
	private String three;
	private String four;
	private String five;
	private String six;
	private String seven;
	private String eight;
	private String nine;
	private String ten;
	private String eleven;
	private String twelve;
	/**
	 * 月别推移信息
	 */
	@ApiModelProperty("月别推移信息")
	private List<ExportRepairRateByMonthDTO> list;
}
