package utry.data.modular.partsManagement.service;

import utry.data.modular.partsManagement.dto.*;
import utry.data.modular.partsManagement.model.*;

import java.util.List;

/**
 * @author zhongdongbiao
 * @date 2022/4/8 9:47
 */
public interface SpiPartsManagementService {

    /**
     * 订单详情数据创建
     * @param orderDetailDTO
     */
    void createOrderDetail(OrderDetailDTO orderDetailDTO);

    /**
     * 配货单详情数据创建
     * @param distributionSingleDTO
     */
    void createDistributionSingle(DistributionSingleDTO distributionSingleDTO);

    /**
     * 配货取消单数据创建
     * @param cancelDstributionOrderDTO
     */
    void cancelDstributionOrder(CancelDstributionOrderDTO cancelDstributionOrderDTO);

    /**
     * 配货明细取消单数据创建
     * @param distributionListCancelDTO
     */
    void cancelDstributionDetailOrder(DistributionListCancelDTO distributionListCancelDTO);

    /**
     * 装箱单详情数据创建
     * @param packingListDTO
     */
    void createPackingList(PackingListDTO packingListDTO);

    /**
     * 收货单详情数据创建
     * @param receiptDTO
     */
    void createReceipt(ReceiptDTO receiptDTO);

    /**
     * 缺件处理单数据创建
     * @param missDealOrderDTO
     */
    void createMissDealOrder(MissDealOrderDTO missDealOrderDTO);

    /**
     * 缺件备货单详情数据
     * @param missStockUpOrder
     */
    void createMissStockUpOrder(MissStockUpOrder missStockUpOrder);

    /**
     * 缺件备货单详情数据修改
     * @param missStockUpOrder
     */
    void updateMissStockUpOrder(MissStockUpOrder missStockUpOrder);

    /**
     * 采购订单详情数据创建
     * @param purchaseOrderDTO
     */
    void createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO);

    /**
     * 采购订单详情数据修改
     * @param purchaseOrder
     */
    void updatePurchaseOrder(PurchaseOrder purchaseOrder);

    /**
     * 采购订单取消数据创建
     * @param cancelPurchaseOrderDTO
     */
    void cancelPurchaseOrder(CancelPurchaseOrderDTO cancelPurchaseOrderDTO);

    /**
     * 库存预警数据创建
     * @param inventoryWarning
     */
    void createInventoryWarning(InventoryWarning inventoryWarning);

    /**
     * 服务店备货单取消单数据创建
     * @param cancelServiceOrderDTO
     */
    void cancelServiceOrder(CancelServiceOrderDTO cancelServiceOrderDTO);

    /**
     * 物流信息数据创建
     * @param logisticsInformation
     */
    void createlogisticsInformation(LogisticsInformation logisticsInformation);

    /**
     * 收货单详情数据创建
     * @param receiptDTO
     */
    void updateReceipt(ReceiptDTO receiptDTO);


    /**
     * 仓库别库存预警数据创建
     * @param requestToObject
     */
    void createWarehouseInventoryWarning(WarehouseInventoryWarning warehouseInventoryWarning);
}
