package utry.data.modular.partsManagement.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 在库金额查询条件Dto
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class AmountConditionDto implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 开始时间
	 */
	private String startDate;
	/**
	 * 结束时间
	 */
	private String endDate;
	/**
	 * 仓库名称
	 */
	private String warehouseName;
	/**
	 * 用户id
	 */
	private String userId;


}
