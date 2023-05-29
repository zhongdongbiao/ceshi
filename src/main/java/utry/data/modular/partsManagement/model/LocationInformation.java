package utry.data.modular.partsManagement.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 库位资料数据
 *
 * @author zhongdongbiao
 * @date 2022/4/15 10:06
 */
@Data
public class LocationInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private String id;
    /**
     * 系统状态
     */
    private String systemState;
    /**
     * 库位编号
     */
    private String locationNumber;
    /**
     * 仓库代码
     */
    private String warehouseCode;
    /**
     * 仓库名称
     */
    private String warehouseName;
    /**
     * 部件图号
     */
    private String partDrawingNo;
    /**
     * 库管员
     */
    private String warehouseKeeper;
}
