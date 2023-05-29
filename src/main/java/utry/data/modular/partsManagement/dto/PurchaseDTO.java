package utry.data.modular.partsManagement.dto;

import lombok.Data;
import utry.data.modular.partsManagement.vo.PurchaseOrderVo;

import java.util.List;

/**
 * 采购订单DTO
 *
 * @author zhongdongbiao
 * @date 2022/4/29 14:01
 */
@Data
public class PurchaseDTO {

    /**
     * id
     */
    private String id;
    /**
     * 工厂名称
     */
    private String factoryName;
    /**
     * 订单时间
     */
    private String orderTime;
    /**
     * 采购订单行数
     */
    private String purchaseLine;
    /**
     * 完成日期
     */
    private String completionDate;
    /**
     * 预计到货时间
     */
    private String exceptGoodTime;
    /**
     * 状态
     */
    private String state;
    /**
     * 订单编号
     */
    private String documentNo;

    /**
     * 采购订单Vo
     */
    private PurchaseOrderVo purchaseOrderVoList;
}
