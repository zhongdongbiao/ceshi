package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 服务店备货取消单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class CancelServiceOrder implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据号
	 */
	private String receiptNumber;
	/**
	 * 单据日期
	 */
	private String documentDate;
	/**
	 * 服务店编号
	 */
	private String storeNumber;
	/**
	 * 服务店名称
	 */
	private String storeName;
	/**
	 * 仓库代码
	 */
	private String warehouseCode;
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	/**
	 * 金额合计
	 */
	private String aggregateAmount;
	/**
	 * 备注
	 */
	private String note;
	/**
	 * 核算中心
	 */
	private String accountingCenter;

	/**
	 * 系统状态
	 */
	private String systemState;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
