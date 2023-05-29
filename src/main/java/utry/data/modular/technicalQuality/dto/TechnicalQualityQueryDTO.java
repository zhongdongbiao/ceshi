package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 技术品质首页查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class TechnicalQualityQueryDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 日期条件
	 */
	@ApiModelProperty("日期条件")
	private String aggregateDate;
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
	 * 排序
	 */
	@ApiModelProperty("排序 升序-ASC 降序-DESC")
	private String sort;
	/**
	 * 一次性修复率类型
	 */
	@ApiModelProperty("一次性修复率类型 产品类型别-0 大区别-1")
	private String type;
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
	 * 担当
	 */
	@ApiModelProperty("担当返回")
	private List<TechnicalQualityUserDTO> technicalQualityUserDTOS;
}
