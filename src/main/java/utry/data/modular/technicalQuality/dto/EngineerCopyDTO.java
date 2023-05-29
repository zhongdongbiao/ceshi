package utry.data.modular.technicalQuality.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 工程师列表实体类
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
public class EngineerCopyDTO implements Serializable {
	private static final long serialVersionUID = 1L;

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
	 * 工程师名称
	 */
	@ApiModelProperty("工程师名称")
	private String engineerName;
	/**
	 * 工程师编号
	 */
	@ApiModelProperty("工程师编号")
	private String engineerId;
	/**
	 * 所属门店
	 */
	@ApiModelProperty("所属门店")
	private String storeName;
	/**
	 * 对应区管
	 */
	@ApiModelProperty("对应区管")
	private String adminName;
	/**
	 * 一次修复率
	 */
	@ApiModelProperty("一次修复率")
	private String repairRate;
	/**
	 * 服务总量
	 */
	@ApiModelProperty("服务总量")
	private int total;
	/**
	 * 一次修复率
	 */
	@ApiModelProperty("达标总量")
	private int eligible;
	/**
	 * 未达标总量
	 */
	@ApiModelProperty("未达标总量")
	private int unEligible;
	/**
	 * 产品品类
	 */
	@ApiModelProperty("产品品类")
	private String productCategory;
	/**
	 * 产品品类编码
	 */
	@ApiModelProperty("产品品类编码")
	private String productCategoryCode;
	/**
	 * 产品类型
	 */
	@ApiModelProperty("产品类型")
	private String productType;
	/**
	 * 产品类型编码
	 */
	@ApiModelProperty("产品类型编码")
	private String productTypeCode;

	public EngineerCopyDTO(String engineerName, String engineerId, String storeName, String adminName, String repairRate, int total, int eligible, int unEligible) {
		this.engineerName = engineerName;
		this.engineerId = engineerId;
		this.storeName = storeName;
		this.adminName = adminName;
		this.repairRate = repairRate;
		this.total = total;
		this.eligible = eligible;
		this.unEligible = unEligible;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}

	public List<String> getModelList() {
		return modelList;
	}

	public void setModelList(List<String> modelList) {
		this.modelList = modelList;
	}

	public List<String> getProductCategoryList() {
		return productCategoryList;
	}

	public void setProductCategoryList(List<String> productCategoryList) {
		this.productCategoryList = productCategoryList;
	}

	public String getEngineerName() {
		return engineerName;
	}

	public void setEngineerName(String engineerName) {
		this.engineerName = engineerName;
	}

	public String getEngineerId() {
		return engineerId;
	}

	public void setEngineerId(String engineerId) {
		this.engineerId = engineerId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getRepairRate() {
		return repairRate;
	}

	public void setRepairRate(String repairRate) {
		this.repairRate = repairRate;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getEligible() {
		return eligible;
	}

	public void setEligible(int eligible) {
		this.eligible = eligible;
	}

	public int getUnEligible() {
		return unEligible;
	}

	public void setUnEligible(int unEligible) {
		this.unEligible = unEligible;
	}

	public String getProductCategory() {
		return productCategory;
	}

	public void setProductCategory(String productCategory) {
		this.productCategory = productCategory;
	}

	public String getProductCategoryCode() {
		return productCategoryCode;
	}

	public void setProductCategoryCode(String productCategoryCode) {
		this.productCategoryCode = productCategoryCode;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getProductTypeCode() {
		return productTypeCode;
	}

	public void setProductTypeCode(String productTypeCode) {
		this.productTypeCode = productTypeCode;
	}

	public EngineerCopyDTO(String startTime, String endTime, List<String> list, List<String> modelList, List<String> productCategoryList, String engineerName, String engineerId, String storeName, String adminName, String repairRate, int total, int eligible, int unEligible, String productCategory, String productCategoryCode, String productType, String productTypeCode) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.list = list;
		this.modelList = modelList;
		this.productCategoryList = productCategoryList;
		this.engineerName = engineerName;
		this.engineerId = engineerId;
		this.storeName = storeName;
		this.adminName = adminName;
		this.repairRate = repairRate;
		this.total = total;
		this.eligible = eligible;
		this.unEligible = unEligible;
		this.productCategory = productCategory;
		this.productCategoryCode = productCategoryCode;
		this.productType = productType;
		this.productTypeCode = productTypeCode;
	}

	public EngineerCopyDTO() {
	}

	@Override
	public String toString() {
		return "EngineerCopyDTO{" +
				"startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				", list=" + list +
				", modelList=" + modelList +
				", productCategoryList=" + productCategoryList +
				", engineerName='" + engineerName + '\'' +
				", engineerId='" + engineerId + '\'' +
				", storeName='" + storeName + '\'' +
				", adminName='" + adminName + '\'' +
				", repairRate='" + repairRate + '\'' +
				", total=" + total +
				", eligible=" + eligible +
				", unEligible=" + unEligible +
				", productCategory='" + productCategory + '\'' +
				", productCategoryCode='" + productCategoryCode + '\'' +
				", productType='" + productType + '\'' +
				", productTypeCode='" + productTypeCode + '\'' +
				'}';
	}
}
