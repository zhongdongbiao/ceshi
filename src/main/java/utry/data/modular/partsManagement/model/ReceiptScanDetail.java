package utry.data.modular.partsManagement.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 收货单详情
 *
 * @author zhongdongbiao
 * @date 2022/4/14 10:30
 */
@Data
public class ReceiptScanDetail implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 收货单号/来源单号
     */
    private String documentNumber;
    /**
     * 来源单号
     */
    private String associatedOrderNumber;
    /**
     * 部件图号
     */
    private String partDrawingNo;
    /**
     * 部件条码
     */
    private String partBarcode;
    /**
     * 条码数量
     */
    private String barCodeNumber;
    /**
     * 收货状态
     */
    private String stateGoods;
    /**
     * 扫描人员
     */
    private String scannerPerson;
    /**
     * 扫描时间
     */
    private String sweepTime;
}
