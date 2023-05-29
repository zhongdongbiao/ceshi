package utry.data.modular.partsManagement.vo;


import lombok.Data;

import java.io.Serializable;

/**
 * 订单列表回显
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class DayAmountVo implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 工厂名称
	 */
	private String factoryName;
	/**
	 * 担当
	 */
	private String bear;
	/**
	 * 在库
	 */
	private String inLibrary;
	/**
	 * 需求
	 */
	private String demand;
	/**
	 * 安全在库
	 */
	private String securityLibrary;
	/**
	 * 采购在途
	 */
	private String procurementTransit;
	/**
	 * 总订单数
	 */
	private String totalOrder;
	/**
	 * 异常订单数
	 */
	private String abnormalOrder;


}
