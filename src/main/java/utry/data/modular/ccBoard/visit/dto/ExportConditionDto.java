package utry.data.modular.ccBoard.visit.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: data
 * @description: 导出条件dto
 * @author: zhongdongbiao
 * @create: 2022-10-24 14:27
 **/
@Data
public class ExportConditionDto {

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
     * 查询类型 0任务分类维度 1 大区维度 2 产品品类维度 3 坐席维度
     */
    private String type;

    /**
     * 导出类型 0 已完成回访项目-任务分类维度-导出 1 已完成回访项目-任务分类维度-灰色-导出
     * 2 申诉统计-导出 3 回访坐席利用率 4 违约率-导出 5 回访呼出量/呼通量-导出 6 回访坐席满意度-导出
     */
    private String exportType;

}
