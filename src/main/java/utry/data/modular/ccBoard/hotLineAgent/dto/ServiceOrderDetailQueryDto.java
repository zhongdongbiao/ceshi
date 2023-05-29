package utry.data.modular.ccBoard.hotLineAgent.dto;

import lombok.Data;

import java.util.List;

/**
 * @program: data
 * @description: 热线服务单明细表格查询条件数据传输类
 * @author: WangXinhao
 * @create: 2022-10-25 13:45
 **/

@Data
public class ServiceOrderDetailQueryDto {

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
     * 工号
     */
    private String agentId;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 客户评价
     */
    private String customerEvaluation;

    /**
     * 分页页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;
}
