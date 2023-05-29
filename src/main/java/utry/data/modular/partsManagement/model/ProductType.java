package utry.data.modular.partsManagement.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 产品类型
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class ProductType implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private String id;
	/**
	 * 产品品类
	 */
	private String productCategory;
	/**
	 * 系统状态
	 */
	private String systemState;
	/**
	 * 产品类型代码
	 */
	private String productTypeCode;
	/**
	 * 产品类型
	 */
	private String productType;
	/**
	 * 产品品类代码
	 */
	private String productCategoryCode;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 更新时间
	 */
	private Date createTime;
}
