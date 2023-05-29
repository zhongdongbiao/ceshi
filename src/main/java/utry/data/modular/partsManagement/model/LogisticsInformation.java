package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 物流信息数据
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class LogisticsInformation implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 物流单号
	 */
	private String logisticsSingleNumber;
	/**
	 * 物流公司
	 */
	private String logisticsCompany;
	/**
	 * 到达时间
	 */
	private String arrivalTime;
	/**
	 * 到达信息
	 */
	private String arrivalMessage;
	/**
	 * 状态
	 */
	private String type;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
