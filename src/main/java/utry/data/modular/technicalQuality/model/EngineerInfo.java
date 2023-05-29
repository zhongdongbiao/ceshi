package utry.data.modular.technicalQuality.model;


import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工程师信息
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class EngineerInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 工程师编号
	 */
	private String engineerId;
	/**
	 * 工程师姓名
	 */
	private String engineerName;
	/**
	 * 服务店编号
	 */
	private String serviceStoreNumber;
	/**
	 * 服务店名称
	 */
	private String serviceStoreName;
	/**
	 * 在职状态
	 */
	private String duringEmploymentStatus;
	/**
	 * 工程师资质信息
	 */
	private List<EngineerQualification> engineerQualificationList;
	/**
	 * 培训信息
	 */
	private List<TrainingInfo> trainingInfoList;
}
