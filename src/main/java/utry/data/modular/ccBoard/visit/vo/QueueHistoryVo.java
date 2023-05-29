package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 队列历史记录
 *
 * @author zhongdongbiao
 * @date 2022/10/24 11:14
 */
@Data
public class QueueHistoryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 队列名称
     */
    private String queueName;

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
