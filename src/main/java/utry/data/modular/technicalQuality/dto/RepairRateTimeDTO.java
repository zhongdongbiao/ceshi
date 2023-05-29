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
public class RepairRateTimeDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 上门数量
	 */
	@ApiModelProperty("上门数量")
	private String visitsNum;
	/**
	 * 挂单数量
	 */
	@ApiModelProperty("挂单数量")
	private String pendingOrderNum;
	/**
	 * 解挂数量
	 */
	@ApiModelProperty("解挂数量")
	private String cancelOrderNum;
	/**
	 * 服务完成数量
	 */
	@ApiModelProperty("服务完成数量")
	private String serviceFinishNum;
	/**
	 * 已提交-已关单历时
	 */
	@ApiModelProperty("已上门-服务完成历时")
	private String turnoverTime;
}
