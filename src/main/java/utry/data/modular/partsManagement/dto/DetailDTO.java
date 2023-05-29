package utry.data.modular.partsManagement.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * 订单详情查询条件Dto
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class DetailDTO implements Serializable {
	private static final long serialVersionUID = 1L;


	/**
	 * 来源单号/单据号
	 */
	private String documentNumber;



}
