package utry.data.modular.ccBoard.visit.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * 已完成回访项目
 * @author zhongdongbiao
 * @date 2022/10/31 16:08
 */
@Data
public class Complaint implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 横坐标
     */
    private String abscissa;

    /**
     * 申诉率
     */
    private String complaintRate;

    /**
     * 申诉不通过率
     */
    private String noComplaintRate;

}
