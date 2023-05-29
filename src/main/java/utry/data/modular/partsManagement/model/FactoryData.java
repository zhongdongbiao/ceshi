package utry.data.modular.partsManagement.model;



import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 工厂资料
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class FactoryData implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;
	/**
	 * 系统状态
	 */
	private String systemState;
	/**
	 * 供应商代码
	 */
	private String supplierCode;
	/**
	 * 供应商名称
	 */
	private String supplierName;
	/**
	 * 工厂代码
	 */
	private String factoryCode;
	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 工厂简称
	 */
	private String factoryShortName;
	/**
	 * 工厂类型
	 */
	private String factoryType;
	/**
	 * 省份
	 */
	private String provinces;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 更新时间
	 */
	private Date createTime;
}
