package utry.data.modular.partsManagement.dto;


import lombok.Data;
import utry.data.util.ConditionUtil;

import java.io.Serializable;

/**
 * 订单列表回显
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class DayAmountDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 工厂名称
	 */
	private ConditionUtil factoryName;
	/**
	 * 担当
	 */
	private ConditionUtil bear;
	/**
	 * 在库
	 */
	private ConditionUtil inLibrary;
	/**
	 * 需求
	 */
	private ConditionUtil demand;
	/**
	 * 安全在库
	 */
	private ConditionUtil securityLibrary;
	/**
	 * 采购在途
	 */
	private ConditionUtil procurementTransit;
	/**
	 * 总订单数
	 */
	private ConditionUtil totalOrder;
	/**
	 * 异常订单数
	 */
	private ConditionUtil abnormalOrder;
	/**
	 * 查询时间
	 */
	private String date;
	/**
	 * 分页页数
	 */
	private String pageNum;
	/**
	 * 分页大小
	 */
	private String pageSize;


}
