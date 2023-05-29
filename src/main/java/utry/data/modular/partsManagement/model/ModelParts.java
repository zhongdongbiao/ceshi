package utry.data.modular.partsManagement.model;

import lombok.Data;

@Data
public class ModelParts {
    /**
     * 唯一标识
     */
    private String id;
    /**
     * 系统状态
     */
    private String systemState;
    /**
     * 产品型号
     */
    private String productModel;
    /**
     * 部件图号
     */
    private String partDrawingNo;
    /**
     * 部件名称
     */
    private String partName;
}
