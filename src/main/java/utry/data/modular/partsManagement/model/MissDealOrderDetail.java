package utry.data.modular.partsManagement.model;


import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 缺件处理单明细
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Data
public class MissDealOrderDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 单据号
	 */
	private String documentNo;
	/**
	 * 订单类型
	 */
	private String orderType;
	/**
	 * 部件图号
	 */
	private String partDrawingNo;
	/**
	 * 图号描述
	 */
	private String describedDrawingNo;
	/**
	 * 缺货数量
	 */
	private String stockoutNumber;
	/**
	 * 处理结果
	 */
	private String processResults;
	/**
	 * 到货预计
	 */
	private String goodarrivalTime;
	/**
	 * 备注（明细）
	 */
	private String detailNote;
	/**
	 * 更新时间
	 */
	private Date updateTime;

}
