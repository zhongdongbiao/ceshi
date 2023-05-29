package utry.data.modular.ccBoard.visit.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 回访违约Vo
 * @author zhongdongbiao
 * @date 2022/11/2 11:02
 */
@Data
public class VisitDefaultVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 状态
     */
    private String systemState;

    /**
     * 单据号
     */
    private String documentNo;

    /**
     * 单据日期
     */
    private String date;

    /**
     * 服务类别
     */
    private String serviceType;

    /**
     * 服务单号
     */
    private String serviceNumber;

    /**
     * 违约描述
     */
    private String defaultDescription;

    /**
     * 作业日期
     */
    private String serviceDate;

    /**
     * 赔偿金额
     */
    private String money;

    /**
     * 审核结果
     */
    private String result;
}
