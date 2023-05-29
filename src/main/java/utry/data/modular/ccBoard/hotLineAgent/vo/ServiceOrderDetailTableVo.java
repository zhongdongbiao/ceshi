package utry.data.modular.ccBoard.hotLineAgent.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: data
 * @description: 人力数据图表-时段在线及工时表格-热线服务单明细表格视图
 * @author: WangXinhao
 * @create: 2022-10-21 10:44
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOrderDetailTableVo {

    /**
     * 状态
     */
    private String state;

    /**
     * 热线服务单号
     */
    private String hotlineServiceOrderNo;

    /**
     * 当日是否跟进：1是；0否
     */
    private Integer sameDayFollowFlag;

    /**
     * 最近跟进时间
     */
    private String latestFollowDateTime;

    /**
     * 呼叫时间
     */
    private String callDateTime;

    /**
     * 10s率达标：1是；0否
     */
    private Integer tenSecondRateFlag;

    /**
     * 部门/队列
     */
    private String queueName;

    /**
     * 工号
     */
    private String agentId;

    /**
     * 产品品类
     */
    private String productCategory;

    /**
     * 产品类型
     */
    private String productType;

    /**
     * 服务类型
     */
    private String serviceType;

    /**
     * 服务明细
     */
    private String serviceDetail;

    /**
     * 客户评价
     */
    private String customerEvaluation;

    /**
     * 录音文件名
     */
    private String soundRecordFileName;
}
