package utry.data.modular.technicalQuality.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class EngineerQualification implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工程师资质类别
     */
    private String engineerType;
    /**
     * 工程师等级
     */
    private String engineerLevel;
    /**
     * 等级生效日期
     */
    private String effectiveDate;
    /**
     * 等级到期日期
     */
    private String expirationDate;
}
