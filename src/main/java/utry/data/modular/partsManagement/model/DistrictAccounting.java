package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 核算大区资料
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class DistrictAccounting implements Serializable {
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
	 * 核算大区代码
	 */
	private String regionalCode;
	/**
	 * 核算大区
	 */
	private String regional;
	/**
	 * 核算中心代码
	 */
	private String centerCode;
	/**
	 * 核算中心
	 */
	private String center;
	/**
	 * 核算片区代码
	 */
	private String areaCode;
	/**
	 * 核算片区
	 */
	private String area;
	/**
	 * 管理员编号
	 */
	private String adminNo;
	/**
	 * 管理员姓名
	 */
	private String adminName;
	/**
	 * 所辖区域
	 */
	private String reduceArea;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
