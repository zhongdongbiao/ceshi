package utry.data.modular.ccBoard.hotline.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description
 * @Author zh
 * @Date 2022/11/7 11:14
 */
@Data
public class HotlineVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 录音文件
     */
    private String recordFileName;

    /**
     * 热线服务单号
     */
    private String hotlineNumber;

    /**
     * 状态
     */
    private String systemState;

    /**
     * 当日是否跟进
     */
    private String isFollow;

    /**
     * 最近跟进时间
     */
    private String lastFollowTime;

    /**
     * 呼叫时间
     */
    private String callTime;

    /**
     * 10s率达标（1是0否）
     */
    private String tenStandard;

    /**
     * 部门
     */
    private String deptId;

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
    private String serviceDetails;

    /**
     * 客户评价
     */
    private String customerEvaluation;

    /**
     * 派工单号
     */
    private String dispatchingOrder;

}
