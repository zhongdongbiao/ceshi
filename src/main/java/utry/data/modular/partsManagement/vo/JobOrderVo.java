package utry.data.modular.partsManagement.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 作业订单部品动态
 */
@Data
public class JobOrderVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 作业订单单号
     */
    private String jobOrderNumber;
    /**
     * 服务店名称
     */
    private String serviceShopName;
    /**
     * 当前状态
     */
    private String currentState;
    /**
     * 状态变更时间
     */
    private String statusChangeTime;
}
