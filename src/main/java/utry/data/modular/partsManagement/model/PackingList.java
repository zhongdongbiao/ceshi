package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 装箱单数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PackingList implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 装箱单号
	 */
	private String packingListNo;
	/**
	 * 装箱日期
	 */
	private String loadingDate;
	/**
	 * 货运单号
	 */
	private String billsLadingNo;
	/**
	 * 装箱类型
	 */
	private String packingType;
	/**
	 * 收货人
	 */
	private String consignee;
	/**
	 * 收货人联系电话
	 */
	private String consigneePhone;
	/**
	 * 收货地址
	 */
	private String consigneeAddress;
	/**
	 * 邮寄类型
	 */
	private String mailType;
	/**
	 * 运输方式
	 */
	private String transportMethod;
	/**
	 * 运费合计
	 */
	private String freightAmount;
	/**
	 * 物流单位
	 */
	private String logisticsUnits;

	/**
	 * 系统状态
	 */
	private String systemState;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
