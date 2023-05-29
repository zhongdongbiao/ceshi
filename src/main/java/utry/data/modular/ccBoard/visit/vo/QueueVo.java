package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 队列信息
 *
 * @author zhongdongbiao
 * @date 2022/10/24 11:30
 */
@Data
public class QueueVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 队列id
     */
    private String queueId;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 等级
     */
    private String label;

    /**
     * 子队列
     */
    private List<QueueVo> children;

}
