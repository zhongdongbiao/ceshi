package utry.data.modular.partsManagement.dto;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单详情明细DTO
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class PartOrderDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 来源单号/单据号
	 */
	private String documentNumber;
	/**
	 * 服务店编号
	 */
	private String storeNumber;
	/**
	 * 产品品类
	 */
	private String productCategory;
	/**
	 * 产品品类代码
	 */
	private String productCategoryCode;
	/**
	 * 服务店名称
	 */
	private String storeName;
	/**
	 * 核算中心
	 */
	private String accountingCenter;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;

	/**
	 * 装箱日期
	 */
	private String loadingDate;
	/**
	 * 作业订单提交时间
	 */
	private String orderSubmitTime;
	/**
	 * 考核开始时间
	 */
	private  String assessmentDate;
	/**
	 * 订单类型
	 */
	private String orderType;

}
