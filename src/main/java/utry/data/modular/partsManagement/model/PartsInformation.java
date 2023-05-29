package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 部件资料
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PartsInformation implements Serializable {
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
	 * 部件代码
	 */
	private String partCode;
	/**
	 * 部件名称
	 */
	private String partName;
	/**
	 * 核算中心代码
	 */
	private String centerCode;
	/**
	 * 核算中心
	 */
	private String center;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
