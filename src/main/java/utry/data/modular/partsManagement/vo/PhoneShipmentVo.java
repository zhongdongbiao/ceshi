package utry.data.modular.partsManagement.vo;

import lombok.Data;

/**
 * 移动端即纳率
 * @author zhongdongbiao
 * @date 2023/3/6 9:58
 */
@Data
public class PhoneShipmentVo {
    /**
     * 日期
     */
    private String date;
    /**
     * 即纳率
     */
    private String shipment;
}
