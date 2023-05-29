package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import utry.data.modular.technicalQuality.model.PartInformation;

import java.io.Serializable;
import java.util.List;

/**
 * 一次性修复率查询实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class RepairRateDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 派工单号
	 */
	@ApiModelProperty("派工单号")
	private String dispatchingOrder;
	/**
	 * 服务店名称
	 */
	@ApiModelProperty("服务店名称")
	private String storeName;
	/**
	 * 工程师
	 */
	@ApiModelProperty("工程师姓名")
	private String engineerName;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private String productCategory;
	/**
	 * 派工时间
	 */
	@ApiModelProperty("派工时间")
	private String dispatchingTime;
	/**
	 * 预约时间
	 */
	@ApiModelProperty("预约时间")
	private String appointmentTime;
	/**
	 * 完成时间
	 */
	@ApiModelProperty("完成时间")
	private String finishTime;
	/**
	 * 已流转时间
	 */
	@ApiModelProperty("已流转时间")
	private String turnoverTime;
	/**
	 * 上门次数
	 */
	@ApiModelProperty("上门次数")
	private String visitsNumber;
	/**
	 * 上门时间
	 */
	@ApiModelProperty("上门时间")
	private String visitsTime;
	/**
	 * 所属大区
	 */
	@ApiModelProperty("所属大区")
	private String accountingArea;
	/**
	 * 地区
	 */
	@ApiModelProperty("地区")
	private String area;
	/**
	 * 服务类型
	 */
	@ApiModelProperty("服务类型")
	private String serviceType;
	/**
	 * 当前状态
	 */
	@ApiModelProperty("当前状态")
	private String systemState;
	/**
	 * 工程师编号
	 */
	@ApiModelProperty("工程师编号")
	private String engineerId;
	/**
	 * 服务单号
	 */
	@ApiModelProperty("服务单号")
	private String serviceNumber;
	/**
	 * 时长
	 */
	@ApiModelProperty("时长")
	private String time;
	/**
	 * 型号id
	 */
	@ApiModelProperty("型号id(对应label=3)")
	private List<String> modelList;
}
