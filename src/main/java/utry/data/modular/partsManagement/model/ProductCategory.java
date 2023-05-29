package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 产品品类数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:24
 */
@Data
public class ProductCategory implements Serializable {
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
	 * 产品品类
	 */
	private String productCategory;
	/**
	 * 产品品类代码
	 */
	private String productCategoryCode;
	/**
	 * 核算中心
	 */
	private String center;
	/**
	 * 更新时间
	 */
	private Date updateTime;
	/**
	 * 创建时间
	 */
	private Date createTime;
}
