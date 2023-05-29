package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 坐席状态历史
 *
 * @author zhongdongbiao
 * @date 2022/10/24 10:36
 */
@Data
public class TableStatusHistoryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    private String currentState;

    /**
     * 开始时间
     */
    private String startDate;

    /**
     * 结束时间
     */
    private String endDate;

    /**
     * 历时
     */
    private String after;

}
