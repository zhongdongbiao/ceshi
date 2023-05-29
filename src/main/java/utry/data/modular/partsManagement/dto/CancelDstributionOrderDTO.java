package utry.data.modular.partsManagement.dto;


import lombok.Data;
import utry.data.modular.partsManagement.model.CancelDstributionOrderDetail;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 配货取消单DTO
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class CancelDstributionOrderDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据号
	 */
	private String documentNo;
	/**
	 * 单据日期
	 */
	private String documentDate;
	/**
	 * 核算中心代码
	 */
	private String accountingCenterCode;
	/**
	 * 仓库代码
	 */
	private String warehouseCode;
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 图号描述
	 */
	private String describedDrawingNo;
	/**
	 * 说明
	 */
	private String instructions;
	/**
	 * 核算中心
	 */
	private String accountingCenter;

	/**
	 * 系统状态
	 */
	private String systemState;


	private List<CancelDstributionOrderDetail> detail;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
