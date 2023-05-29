package utry.data.modular.ccBoard.visit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @program: data
 * @description: 完成回访项目传输类
 * @author: zhongdongbiao
 * @create: 2022-10-24 14:27
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteProjectDto {

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

}
