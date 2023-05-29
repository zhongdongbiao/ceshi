package utry.data.modular.technicalQuality.dto;


import lombok.Data;
import utry.data.modular.technicalQuality.model.PartInformation;

import java.io.Serializable;
import java.util.List;

/**
 * 品质反馈单
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class SpiQualityFeedbackEditDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 系统状态
	 */
	private String systemState;
	/**
	 * 管理编号
	 */
	private String manageNumber;
	/**
	 * 动作
	 */
	private String action;
	/**
	 * 业务时间
	 */
	private String businessTime;
	/**
	 * 提交时间
	 */
	private String submitTime;
	/**
	 * 审阅时间
	 */
	private String reviewTime;
	/**
	 * 审核时间
	 */
	private String auditTime;
	/**
	 * 关单时间
	 */
	private String closeOrderTime;
	/**
	 * 审核完成时间
	 */
	private String auditFinishTime;
}
