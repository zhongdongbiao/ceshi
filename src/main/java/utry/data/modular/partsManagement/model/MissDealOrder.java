package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 缺件处理单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class MissDealOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据号
	 */
	private String documentNo;
	/**
	 * 单据日期
	 */
	private String documentDate;
	/**
	 * 核算中心代码
	 */
	private String accountingCenterCode;
	/**
	 * 核算中心
	 */
	private String accountingCenter;
	/**
	 * 仓库代码
	 */
	private String warehouseCode;
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	/**
	 * 工厂代码
	 */
	private String factoryCode;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 备注
	 */
	private String note;

	/**
	 * 系统状态
	 */
	private String systemState;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
