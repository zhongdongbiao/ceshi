package utry.data.modular.ccBoard.visit.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 回访坐席列表dto
 * @author zhongdongbiao
 * @date 2022/11/8 9:24
 */
@Data
public class VisitTableDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 坐席状态
     */
    private String state;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 结束日期
     */
    private String operationType;

    /**
     * 队列id
     */
    private List<String> queueId;

    /**
     * 分页页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
