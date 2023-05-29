package utry.data.modular.partsManagement.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 工厂别出货即纳率VO
 *
 * @author zhongdongbiao
 * @date 2022/4/27 11:19
 */
@Data
public class FactoryShipmentVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工厂名称
     */
    private String factoryName;
    /**
     * 出货即纳率
     */
    private Double shipment;

}
