package utry.data.modular.partsManagement.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * NDS2、NDS3每月达成情况
 */
@Data
public class PartsReceiptsHeatMapVo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 月份
     */
    private String date;
    /**
     * nds2或3达成率
     */
    private String dataValue;
}
