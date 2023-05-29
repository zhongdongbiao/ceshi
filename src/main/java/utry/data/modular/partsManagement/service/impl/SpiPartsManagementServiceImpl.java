package utry.data.modular.partsManagement.service.impl;

import org.springframework.stereotype.Service;
import utry.data.modular.partsManagement.dao.*;
import utry.data.modular.partsManagement.dto.*;
import utry.data.modular.partsManagement.model.*;
import utry.data.modular.partsManagement.service.SpiPartsManagementService;

import javax.annotation.Resource;
import java.util.List;

/**
 * 零件管理SPI实现类
 *
 * @author zhongdongbiao
 * @date 2022/4/8 9:47
 */
@Service
public class SpiPartsManagementServiceImpl implements SpiPartsManagementService {

    @Resource
    private OrderDetailDao orderDetailDao;

    @Resource
    private DistributionSingleDao distributionSingleDao;

    @Resource
    private CancelDstributionOrderDao cancelDstributionOrderDao;

    @Resource
    private DistributionListCancelDao distributionListCancelDao;

    @Resource
    private PackingListDao packingListDao;

    @Resource
    private ReceiptDao receiptDao;

    @Resource
    private MissDealOrderDao missDealOrderDao;

    @Resource
    private MissStockUpOrderDao missStockUpOrderDao;

    @Resource
    private PurchaseOrderDao purchaseOrderDao;

    @Resource
    private CancelPurchaseOrderDao cancelPurchaseOrderDao;

    @Resource
    private InventoryWarningDao inventoryWarningDao;

    @Resource
    private CancelServiceOrderDao cancelServiceOrderDao;

    @Resource
    private LogisticsInformationDao logisticsInformationDao;


    /**
     * 订单详情数据创建
     * @param orderDetailDTO
     */
    @Override
    public void createOrderDetail(OrderDetailDTO orderDetailDTO) {
        orderDetailDao.insertOrderDetail(orderDetailDTO);
    }

    /**
     * 配货单详情数据创建
     * @param distributionSingleDTO
     */
    @Override
    public void createDistributionSingle(DistributionSingleDTO distributionSingleDTO) {
        distributionSingleDao.insertDistributionSingle(distributionSingleDTO);
    }

    /**
     * 配货取消单数据创建
     * @param cancelDstributionOrderDTO
     */
    @Override
    public void cancelDstributionOrder(CancelDstributionOrderDTO cancelDstributionOrderDTO) {
        cancelDstributionOrderDao.insertCancelDstributionOrder(cancelDstributionOrderDTO);
    }

    /**
     * 配货明细取消单数据创建
     * @param distributionListCancelDTO
     */
    @Override
    public void cancelDstributionDetailOrder(DistributionListCancelDTO distributionListCancelDTO) {
        distributionListCancelDao.insertDistributionListCancell(distributionListCancelDTO);
    }

    /**
     * 装箱单详情数据创建
     * @param packingListDTO
     */
    @Override
    public void createPackingList(PackingListDTO packingListDTO) {
        packingListDao.insertPackingList(packingListDTO);
    }

    /**
     * 收货单详情数据创建
     * @param receiptDTO
     */
    @Override
    public void createReceipt(ReceiptDTO receiptDTO) {
        receiptDao.insertReceipt(receiptDTO);
    }

    /**
     * 缺件处理单数据创建
     * @param missDealOrderDTO
     */
    @Override
    public void createMissDealOrder(MissDealOrderDTO missDealOrderDTO) {
        missDealOrderDao.insertMissDealOrder(missDealOrderDTO);
    }

    /**
     * 缺件备货单详情数据
     * @param missStockUpOrder
     */
    @Override
    public void createMissStockUpOrder(MissStockUpOrder missStockUpOrder) {
        missStockUpOrderDao.insertMissStockUpOrder(missStockUpOrder);
    }

    /**
     * 缺件备货单详情数据修改
     * @param missStockUpOrder
     */
    @Override
    public void updateMissStockUpOrder(MissStockUpOrder missStockUpOrder) {
        missStockUpOrderDao.updateMissStockUpOrder(missStockUpOrder);
    }

    /**
     * 采购订单详情数据创建
     * @param purchaseOrderDTO
     */
    @Override
    public void createPurchaseOrder(PurchaseOrderDTO purchaseOrderDTO) {
        purchaseOrderDao.insertPurchaseOrder(purchaseOrderDTO);
    }

    /**
     * 采购订单详情数据修改
     * @param purchaseOrder
     */
    @Override
    public void updatePurchaseOrder(PurchaseOrder purchaseOrder) {
        purchaseOrderDao.updatePurchaseOrder(purchaseOrder);
    }

    /**
     * 采购订单取消数据创建
     * @param cancelPurchaseOrderDTO
     */
    @Override
    public void cancelPurchaseOrder(CancelPurchaseOrderDTO cancelPurchaseOrderDTO) {
        cancelPurchaseOrderDao.insertCancelPurchaseOrder(cancelPurchaseOrderDTO);
    }

    /**
     * 库存预警数据创建
     * @param inventoryWarning
     */
    @Override
    public void createInventoryWarning(InventoryWarning inventoryWarning) {
        inventoryWarningDao.insertInventoryWarning(inventoryWarning);
    }

    /**
     * 服务店备货单取消单数据创建
     * @param cancelServiceOrderDTO
     */
    @Override
    public void cancelServiceOrder(CancelServiceOrderDTO cancelServiceOrderDTO) {
        cancelServiceOrderDao.insertCancelServiceOrder(cancelServiceOrderDTO);
    }

    /**
     * 物流信息数据创建
     * @param logisticsInformation
     */
    @Override
    public void createlogisticsInformation(LogisticsInformation logisticsInformation) {
        logisticsInformationDao.insertLogisticsInformation(logisticsInformation);
    }

    @Override
    public void updateReceipt(ReceiptDTO receiptDTO) {
        receiptDao.deleteReceipt(receiptDTO.getDocumentNumber());
        receiptDao.insertReceipt(receiptDTO);
    }

    @Override
    public void createWarehouseInventoryWarning(WarehouseInventoryWarning warehouseInventoryWarning) {
        inventoryWarningDao.createWarehouseInventoryWarning(warehouseInventoryWarning);
    }
}
