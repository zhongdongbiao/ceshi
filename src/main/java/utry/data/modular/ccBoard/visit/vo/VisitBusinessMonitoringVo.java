package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 回访业务监控
 *
 * @author zhongdongbiao
 * @date 2022/10/24 14:19
 */
@Data
public class VisitBusinessMonitoringVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 回访任务数量
     */
    private Integer visitTask;

    /**
     * 未完成工单数量
     */
    private Integer notCompleteWork;

    /**
     * 已完成回访未审核
     */
    private Integer complete;

    /**
     * 未申诉
     */
    private Integer noComplaint;

    /**
     * 第二次审核
     */
    private Integer audit;

    /**
     * 回访结案
     */
    private Integer visitCase;

}
