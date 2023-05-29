package utry.data.modular.ccBoard.visit.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 已完成回访项目
 * @author zhongdongbiao
 * @date 2022/10/31 16:08
 */
@Data
public class CompleteProject implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 数量
     */
    private String total;

    /**
     * 完成数量
     */
    private String complete;

    /**
     * 完成数量占比
     */
    private String completeRate;

    /**
     * 灰色数量
     */
    private String gray;

    /**
     * 灰色数量占比
     */
    private String grayRate;

    /**
     * 违约
     */
    private String defaultCount;

    /**
     * 违约占比
     */
    private String defaultCountRate;
}
