package utry.data.modular.partsManagement.dto;


import lombok.Data;;
import utry.data.util.ConditionUtil;
import utry.data.util.DateConditionUtil;

import java.io.Serializable;
import java.util.Date;

/**
 * 采购订单查询条件DTO
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PurchaseOrderConditionDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据号
	 */
	private ConditionUtil documentNo;

	/**
	 * 单据日期
	 */
	private DateConditionUtil documentDate;
	/**
	 * 采购订单类型
	 */
	private ConditionUtil purchaseOrderType;
	/**
	 * 采购货期
	 */
	private String purchaseTime;
	/**
	 * 完成日期
	 */
	private DateConditionUtil completionDate;
	/**
	 * 订单类型
	 */
	private ConditionUtil orderType;
	/**
	 * 供应商代码
	 */
	private ConditionUtil supplierCode;
	/**
	 * 供应商名称
	 */
	private ConditionUtil supplierName;
	/**
	 * 种类合计
	 */
	private ConditionUtil speciesTotal;
	/**
	 * 订货仓库
	 */
	private ConditionUtil goodWarehouse;
	/**
	 * 仓库名称
	 */
	private ConditionUtil warehouseName;
	/**
	 * 数量合计
	 */
	private ConditionUtil numberTotal;
	/**
	 * 金额合计
	 */
	private ConditionUtil aggregateAmount;
	/**
	 * 运输方式
	 */
	private ConditionUtil transportMethod;

	/**
	 * 订单历时
	 */
	private ConditionUtil orderAfter;
	/**
	 * 是否异常 0正常 1 异常
	 */
	private String isAbnormal;
	/**
	 * 超时时间
	 */
	private String abnormalTime;

    /**
     * 系统状态
     */
    private ConditionUtil systemState;
	/**
	 * 工厂名称
	 */
	private String factoryName;

	/**
	 * 部件图号
	 */
	private String partDrawingNo;

	/**
	 * 开始时间
	 */
	private String startDate;
	/**
	 * 结束时间
	 */
	private String endDate;
	/**
	 * 查询日期
	 */
	private String inventoryDate;
	/**
	 * 分页页数
	 */
	private String pageNum;
	/**
	 * 分页大小
	 */
	private String pageSize;

	/**
	 * 更新时间
	 */
	private Date updateTime;

}
