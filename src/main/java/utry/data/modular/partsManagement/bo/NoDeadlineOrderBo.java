package utry.data.modular.partsManagement.bo;


/**
 * 未添加考核时间订单Bo
 *
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
public class NoDeadlineOrderBo {
    /**
     * 来源单号/单据号
     */
    private String documentNumber;
    /**
     * 作业订单提交时间
     */
    private String orderSubmitTime;
    /**
     * 订单类型
     */
    private String orderType;
    /**
     * 考核开始日
     */
    private String assessmentDate;
    /**
     * 考核截至日
     */
    private String deadline;

    public NoDeadlineOrderBo() {
    }

    public String getAssessmentDate() {
        return assessmentDate;
    }

    public NoDeadlineOrderBo(String documentNumber, String orderSubmitTime, String orderType, String assessmentDate, String deadline) {
        this.documentNumber = documentNumber;
        this.orderSubmitTime = orderSubmitTime;
        this.orderType = orderType;
        this.assessmentDate = assessmentDate;
        this.deadline = deadline;
    }

    public void setAssessmentDate(String assessmentDate) {
        this.assessmentDate = assessmentDate;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getOrderSubmitTime() {
        return orderSubmitTime;
    }

    public void setOrderSubmitTime(String orderSubmitTime) {
        this.orderSubmitTime = orderSubmitTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
}
