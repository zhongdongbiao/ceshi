package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 技术品质首页一次性修复率柱状图实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class RepairRateHistogramDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@ApiModelProperty("名称")
	private String name;
	/**
	 * 编码
	 */
	@ApiModelProperty("编码")
	private String code;
	/**
	 * 值
	 */
	@ApiModelProperty("值")
	private String value;
}
