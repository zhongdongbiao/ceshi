package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 配货单详情数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class DistributionSingle implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 配货单号
	 */
	private String distributionSingleNo;
	/**
	 * 配货日期
	 */
	private String distributionDate;
	/**
	 * 服务店配货号
	 */
	private String serviceStoreNumber;

	/**
	 * 系统状态
	 */
	private String systemState;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
