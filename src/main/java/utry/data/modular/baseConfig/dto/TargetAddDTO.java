package utry.data.modular.baseConfig.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 目标配置实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class TargetAddDTO implements Serializable {
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
	 * 是否是核心目标
	 */
	@ApiModelProperty("是否是核心目标")
	private String ifTarget;
	/**
	 * 指标编码-指标名称-指标值
	 */
	@ApiModelProperty("指标编码-指标名称-指标值")
	private List<IndicatorDTO> list;
	/**
	 * 指标编码-指标名称-指标值
	 */
	@ApiModelProperty("担当id-指标编码-指标名称-指标值")
	private List<IndicatorUserDTO> userList;

	/**
	 * 担当ids
	 */
	@ApiModelProperty("担当ids")
	private List<String> users;
}
