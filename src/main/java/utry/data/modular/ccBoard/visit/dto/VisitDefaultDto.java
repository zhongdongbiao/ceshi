package utry.data.modular.ccBoard.visit.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 回访违约单Dto
 * @author zhongdongbiao
 * @date 2022/11/2 10:21
 */
@Data
public class VisitDefaultDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 队列id
     */
    private List<String> queueId;

    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 核算大区
     */
    private String accountingArea;


    /**
     * 审核结果
     */
    private String result;


    /**
     * 分页大小
     */
    private String pageSize;

    /**
     * 分页数量
     */
    private String pageNum;

}
