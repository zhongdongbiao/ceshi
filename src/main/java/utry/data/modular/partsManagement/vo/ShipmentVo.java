package utry.data.modular.partsManagement.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 部品出货即纳率VO
 *
 * @author zhongdongbiao
 * @date 2022/4/27 11:19
 */
@Data
public class ShipmentVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 产品品类
     */
    private String productCategory;
    /**
     * 产品品类代码
     */
    private String productCategoryCode;
    /**
     * 出货即纳率
     */
    private Double shipment;

}
