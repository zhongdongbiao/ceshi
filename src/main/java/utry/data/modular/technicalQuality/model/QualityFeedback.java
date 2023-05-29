package utry.data.modular.technicalQuality.model;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 品质反馈单
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Data
public class QualityFeedback implements Serializable {
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
	 * 派工单号
	 */
	private String dispatchingOrder;
	/**
	 * 提交时间
	 */
	private String submitTime;
	/**
	 * 审核完成时间
	 */
	private String auditTime;
	/**
	 * 单据日期
	 */
	private String documentDate;
	/**
	 * 服务单号
	 */
	private String serviceNumber;
	/**
	 * 工厂代码
	 */
	private String factoryCode;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 核算中心
	 */
	private String accountingCenter;
	/**
	 * 服务店编号
	 */
	private String serviceStoreNumber;
	/**
	 * 服务店名称
	 */
	private String serviceStoreName;
	/**
	 * 工程师编号
	 */
	private String engineerId;
	/**
	 * 工程师姓名
	 */
	private String engineerName;
	/**
	 * 产品型号
	 */
	private String productModel;
	/**
	 * 产品类型
	 */
	private String productType;
	/**
	 * 产品品类
	 */
	private String productCategory;
	/**
	 * 产品系列
	 */
	private String productSeries;

	/**
	 * 制造日期
	 */
	private String manufacturingDate;
	/**
	 * 购买日期
	 */
	private String purchaseDate;
	/**
	 * 机器编号
	 */
	private String machinaryCode;
	/**
	 * 产品故障现象
	 */
	private String productSymptom;
	/**
	 * 服务店备注
	 */
	private String serviceRemark;
	/**
	 * 产品类型代码
	 */
	private String productTypeCode;
	/**
	 * 产品品类代码
	 */
	private String productCategoryCode;
	/**
	 * 产品系列代码
	 */
	private String productSeriesCode;
	/**
	 * 部件信息
	 */
	private List<PartInformation> partInformationList;
}
