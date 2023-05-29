package utry.data.modular.partsManagement.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 在库金额DTO
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class AmountDTO implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 订单日期
	 */
	private String documentDate;

	/**
	 * 需求金额
	 */
	private Integer demandAmount;


}
