package utry.data.modular.partsManagement.dto;


import lombok.Data;
import utry.data.modular.partsManagement.model.ReceiptDetail;
import utry.data.modular.partsManagement.model.ReceiptScanDetail;

import java.io.Serializable;
import java.util.List;

/**
 * 收货单
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class ReceiptDTO implements Serializable {
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
	 * 收货时间
	 */
	private String goodTime;
	/**
	 * 预计到货时间
	 */
	private String exceptgoodTime;
	/**
	 * 物流单号
	 */
	private String logisticsSingleNumber;
	/**
	 *收货单详情数据
	 */
	private List<ReceiptDetail> detail;

	/**
	 * 收货扫描单详情
	 */
	private List<ReceiptScanDetail> receiptScanDetails;


}
