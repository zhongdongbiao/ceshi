package utry.data.modular.partsManagement.vo;

import lombok.Data;
import utry.data.util.ConditionUtil;

import java.io.Serializable;

/**
 * 产品品类Vo
 *
 * @author zhongdongbiao
 * @date 2022/6/8 15:59
 */
@Data
public class ProductCategoryVo implements Serializable {
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
     * 担当
     */
    private String bear;
    /**
     * 关联订单
     */
    private String asOrder;
    /**
     * 未完成
     */
    private String noCom;
    /**
     * 超时
     */
    private String noTime;
    /**
     * 即纳率
     */
    private String ship;
    /**
     * 平均完成时间
     */
    private String comTime;
}
