package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import lombok.Data;

/**
 * 收货单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class Receipt implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 收货单号/来源单号
	 */
	private String documentNumber;
	/**
	 * 系统状态
	 */
	private String systemState;
	/**
	 * 发货时间
	 */
	private String deliveryTime;
	/**
	 * 收货类型
	 */
	private String receivingType;
	/**
	 * 装箱单号
	 */
	private String packingListNo;
	/**
	 * 妥投时间
	 */
	private String appropriateInvestTime;
	/**
	 * 物流单号
	 */
	private String logisticsSingleNumber;
	/**
	 * 收货时间
	 */
	private String goodTime;


}
