package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 品质反馈时长查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class ApprovalDurationTimeDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 已提交数量
	 */
	@ApiModelProperty("已提交数量")
	private String submitNum;
	/**
	 * 已审核数量
	 */
	@ApiModelProperty("已审核数量")
	private String auditNum;
	/**
	 * 已审阅数量
	 */
	@ApiModelProperty("已审阅数量")
	private String reviewNum;
	/**
	 * 已关单数量
	 */
	@ApiModelProperty("已关单数量")
	private String closeOrderNum;
	/**
	 * 已提交-已审阅历时
	 */
	@ApiModelProperty("已提交-已审阅历时")
	private String reviewTime;
	/**
	 * 已提交-已审核历时
	 */
	@ApiModelProperty("已提交-已审核历时")
	private String auditTime;
	/**
	 * 已提交-已关单历时
	 */
	@ApiModelProperty("已提交-已关单历时")
	private String closeOrderTime;
	/**
	 * 提交时间
	 */
	@ApiModelProperty("提交时间")
	private String submitTime;
}
