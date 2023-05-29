package utry.data.modular.partsManagement.dto;

import lombok.Data;
import utry.data.util.ConditionUtil;

import java.io.Serializable;

/**
 * 品类查询条件Dto
 *
 * @author zhongdongbiao
 * @date 2022/6/8 15:54
 */
@Data
public class ProductCategoryConditionDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 产品品类
     */
    private ConditionUtil productCategory;
    /**
     * 担当
     */
    private ConditionUtil bear;
    /**
     * 关联订单
     */
    private ConditionUtil asOrder;
    /**
     * 未完成
     */
    private ConditionUtil noCom;
    /**
     * 超时
     */
    private ConditionUtil noTime;
    /**
     * 即纳率
     */
    private ConditionUtil ship;
    /**
     * 平均完成时间
     */
    private ConditionUtil comTime;
    /**
     * 开始时间
     */
    private String startDate;
    /**
     * 结束时间
     */
    private String endDate;
    /**
     *
     */
    private String inventoryDate;
    /**
     * 分页页数
     */
    private String pageNum;
    /**
     * 分页大小
     */
    private String pageSize;
}
