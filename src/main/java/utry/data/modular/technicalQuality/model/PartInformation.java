package utry.data.modular.technicalQuality.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PartInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;
    /**
     * 部件代码
     */
    private String partCode;
    /**
     * 部件名称
     */
    private String partName;
    /**
     * 部件图号
     */
    private String partDrawingNo;
    /**
     * 图号描述
     */
    private String describedDrawingNo;
    /**
     * 故障原因代码
     */
    private String faultReasonCode;
    /**
     * 故障原因
     */
    private String faultReason;
    /**
     * 故障现象代码
     */
    private String symptomCode;
    /**
     * 故障现象
     */
    private String symptom;
    /**
     * 修理方式
     */
    private String repairMethod;
    /**
     * 处理方法
     */
    private String handleMethod;
    /**
     * 管理编号
     */
    private String manageNumber;
}
