package utry.data.modular.baseConfig.model;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 目标配置实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class Target implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@ApiModelProperty("id")
	private String id;
	/**
	 * 业务类型编码
	 */
	@ApiModelProperty("业务类型编码")
	private String businessCode;
	/**
	 * 创建时间
	 */
	@ApiModelProperty("创建时间")
	private String createTime;
	/**
	 * 更新时间
	 */
	@ApiModelProperty("更新时间")
	private String updateTime;
	/**
	 * 目标名称
	 */
	@ApiModelProperty("目标名称")
	private String targetName;
	/**
	 * 目标年月
	 */
	@ApiModelProperty("目标年月")
	private String targetMonth;
	/**
	 * 是否是核心目标
	 */
	@ApiModelProperty("是否是核心目标")
	private String ifTarget;
}
