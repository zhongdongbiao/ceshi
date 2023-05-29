package utry.data.modular.spi;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utry.core.base.controller.CommonController;
import utry.core.common.LoginInfoParams;
import utry.data.modular.partsManagement.dao.*;
import utry.data.modular.partsManagement.dto.*;
import utry.data.modular.partsManagement.model.*;
import utry.data.modular.partsManagement.service.DailyDemandAmountService;
import utry.data.modular.partsManagement.service.SpiPartsManagementService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;
import utry.data.util.TimeUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 测试Controller
 *
 * @author lidakai
 */
@RestController
@RequestMapping("subApi/spiPartsManagement")
@Api(tags = "零件管理SPI")
public class SpiPartsManagementController extends CommonController {

    @Resource
    private SpiPartsManagementService spiPartsManagementService;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private ReceiptDao receiptDao;
    @Resource
    private DistributionSingleDao distributionSingleDao;
    @Resource
    private CancelDstributionOrderDao cancelDstributionOrderDao;
    @Resource
    private DistributionListCancelDao distributionListCancelDao;
    @Resource
    private MissDealOrderDao missDealOrderDao;
    @Resource
    private PackingListDao packingListDao;
    @Resource
    private MissStockUpOrderDao missStockUpOrderDao;
    @Resource
    private PurchaseOrderDao purchaseOrderDao;
    @Resource
    private CancelPurchaseOrderDao cancelPurchaseOrderDao;
    @Resource
    private CancelServiceOrderDao cancelServiceOrderDao;
    @Resource
    private LogisticsInformationDao logisticsInformationDao;
    @Resource
    private InventoryWarningDao inventoryWarningDao;
    @Resource
    private DailyDemandAmountService dailyDemandAmountService;


    @ApiOperation(value = "订单详情数据创建", notes = "订单详情数据创建")
    @PostMapping("/createOrderDetail")
    public RetResult createOrderDetail(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{

            if(request!=null){
                OrderDetailDTO orderDetailDTO =new OrderDetailDTO();
                OrderDetailDTO orderDetail = (OrderDetailDTO)TimeUtil.requestToObject(request,orderDetailDTO);
                String orderDetailFlag = orderDetailDao.getOrderDetailFlag(orderDetail.getDocumentNumber());
                if(orderDetailFlag!=null
                        &&!"".equals(orderDetailFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                if("作业订单".equals(orderDetail.getOrderType())||"服务店备货订单".equals(orderDetail.getOrderType())){
                    spiPartsManagementService.createOrderDetail(orderDetail);
                }
            }else {
                return RetResponse.makeRsp(500,"创建失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeRsp(500,"创建失败！");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "配货单详情数据创建", notes = "配货单详情数据创建")
    @PostMapping("/createDistributionSingle")
    public RetResult createDistributionSingle(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                DistributionSingleDTO distributionSingleDTO = new DistributionSingleDTO();
                DistributionSingleDTO createDate = (DistributionSingleDTO)TimeUtil.requestToObject(request,distributionSingleDTO);
                String distributionSingleFlag = distributionSingleDao.getOrderDetailFlag(createDate.getDistributionSingleNo(),createDate.getServiceStoreNumber());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.createDistributionSingle(createDate);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "配货取消单数据创建", notes = "配货取消单数据创建")
    @PostMapping("/cancelDstributionOrder")
    public RetResult cancelDstributionOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                CancelDstributionOrderDTO cancelDstributionOrderDTO = new CancelDstributionOrderDTO();
                CancelDstributionOrderDTO createDate = (CancelDstributionOrderDTO)TimeUtil.requestToObject(request,cancelDstributionOrderDTO);

                String distributionSingleFlag = cancelDstributionOrderDao.getOrderDetailFlag(createDate.getDocumentNo());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.cancelDstributionOrder(createDate);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "配货明细取消单数据创建", notes = "配货明细取消单数据创建")
    @PostMapping("/cancelDstributionDetailOrder")
    public RetResult cancelDstributionDetailOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                DistributionListCancelDTO distributionListCancelDTO = new DistributionListCancelDTO();
                DistributionListCancelDTO createDate = (DistributionListCancelDTO)TimeUtil.requestToObject(request,distributionListCancelDTO);

                String distributionSingleFlag = distributionListCancelDao.getOrderDetailFlag(createDate.getDocumentNo());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.cancelDstributionDetailOrder(createDate);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "装箱单详情数据创建", notes = "装箱单详情数据创建")
    @PostMapping("/createPackingList")
    public RetResult createPackingList(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                PackingListDTO packingListDTO = new PackingListDTO();
                PackingListDTO createDate = (PackingListDTO)TimeUtil.requestToObject(request,packingListDTO);

                String distributionSingleFlag = packingListDao.getOrderDetailFlag(createDate.getPackingListNo());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.createPackingList(createDate);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "收货单详情数据创建", notes = "收货单详情数据创建")
    @PostMapping("/createReceipt")
    public RetResult createReceipt(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                ReceiptDTO receiptDTO = new ReceiptDTO();
                ReceiptDTO createDate = (ReceiptDTO)TimeUtil.requestToObject(request,receiptDTO);

                if(receiptDao.getReceiptFlag(createDate.getDocumentNumber())!=null
                        &&!"".equals(receiptDao.getReceiptFlag(createDate.getDocumentNumber()))){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.createReceipt(createDate);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "收货单详情数据修改", notes = "收货单详情数据修改")
    @PostMapping("/updateReceipt")
    public RetResult updateReceipt(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                ReceiptDTO receiptDTO = new ReceiptDTO();
                ReceiptDTO crReceiptDTO = (ReceiptDTO)TimeUtil.requestToObject(request,receiptDTO);
                if(receiptDao.getReceiptFlag(crReceiptDTO.getDocumentNumber())!=null
                        &&!"".equals(receiptDao.getReceiptFlag(crReceiptDTO.getDocumentNumber()))){
                    spiPartsManagementService.updateReceipt(crReceiptDTO);
                }else {
                    return RetResponse.makeErrRsp("订单不存在！");
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("修改失败！");
        }

        return RetResponse.makeOKRsp();
    }


    @ApiOperation(value = "缺件处理单数据创建", notes = "缺件处理单数据创建")
    @PostMapping("/createMissDealOrder")
    public RetResult createMissDealOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                MissDealOrderDTO missDealOrderDTO = new MissDealOrderDTO();
                MissDealOrderDTO createDate = (MissDealOrderDTO)TimeUtil.requestToObject(request,missDealOrderDTO);

                String distributionSingleFlag = missDealOrderDao.getOrderDetailFlag(createDate.getDocumentNo());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.createMissDealOrder(createDate);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "缺件备货单详情数据", notes = "缺件备货单详情数据")
    @PostMapping("/createMissStockUpOrder")
    public RetResult createMissStockUpOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                MissStockUpOrder missStockUpOrder = new MissStockUpOrder();
                MissStockUpOrder crReceiptDTO = (MissStockUpOrder)TimeUtil.requestToObject(request,missStockUpOrder);

                String distributionSingleFlag = missStockUpOrderDao.getFlag(crReceiptDTO.getDocumentNo());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.createMissStockUpOrder(crReceiptDTO);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "缺件备货单详情数据修改", notes = "缺件备货单详情数据修改")
    @PostMapping("/updateMissStockUpOrder")
    public RetResult updateMissStockUpOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                MissStockUpOrder missStockUpOrder = new MissStockUpOrder();
                MissStockUpOrder crMissStockUpOrder = (MissStockUpOrder)TimeUtil.requestToObject(request,missStockUpOrder);
                if(missStockUpOrderDao.getFlag(crMissStockUpOrder.getDocumentNo())!=null
                        &&!"".equals(missStockUpOrderDao.getFlag(crMissStockUpOrder.getDocumentNo()))){
                    spiPartsManagementService.updateMissStockUpOrder(crMissStockUpOrder);
                }else {
                    return RetResponse.makeErrRsp("订单不存在！");
                }


            }else {
                return RetResponse.makeErrRsp("数据为空，修改失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("修改失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "采购订单详情数据创建", notes = "采购订单详情数据创建")
    @PostMapping("/createPurchaseOrder")
    public RetResult createPurchaseOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                PurchaseOrderDTO purchaseOrderDTO =new PurchaseOrderDTO();
                PurchaseOrderDTO createDate = (PurchaseOrderDTO)TimeUtil.requestToObject(request,purchaseOrderDTO);

                String distributionSingleFlag = purchaseOrderDao.getFlag(createDate.getDocumentNo());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.createPurchaseOrder(createDate);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "采购订单详情数据修改", notes = "采购订单详情数据修改")
    @PostMapping("/updatePurchaseOrder")
    public RetResult updatePurchaseOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                PurchaseOrder purchaseOrder =new PurchaseOrder();

                PurchaseOrder createDate = (PurchaseOrder)TimeUtil.requestToObject(request,purchaseOrder);

                String distributionSingleFlag = purchaseOrderDao.getFlag(createDate.getDocumentNo());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    spiPartsManagementService.updatePurchaseOrder(createDate);
                }else {
                    return RetResponse.makeErrRsp("订单不存在！");
                }

            }else {
                return RetResponse.makeErrRsp("数据为空，修改失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("修改失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "采购订单取消数据创建", notes = "采购订单取消数据创建")
    @PostMapping("/cancelPurchaseOrder")
    public RetResult cancelPurchaseOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                CancelPurchaseOrderDTO cancelPurchaseOrderDTO = new CancelPurchaseOrderDTO();
                CancelPurchaseOrderDTO createDate = (CancelPurchaseOrderDTO)TimeUtil.requestToObject(request,cancelPurchaseOrderDTO);

                String distributionSingleFlag = cancelPurchaseOrderDao.getFlag(createDate.getReceiptNumber());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }

                spiPartsManagementService.cancelPurchaseOrder(createDate);
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "库存预警数据创建", notes = "库存预警数据创建")
    @PostMapping("/createInventoryWarning")
    public RetResult createInventoryWarning(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                InventoryWarning inventoryWarning =new InventoryWarning();
                InventoryWarning newIntory =  (InventoryWarning) TimeUtil.requestToObject(request, inventoryWarning);
                Integer select = inventoryWarningDao.select(newIntory);
                if(select==0){
                    spiPartsManagementService.createInventoryWarning(newIntory);
                }else {
                    return RetResponse.makeRsp(401,"数据已存在！");
                }
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "仓库别库存预警数据创建", notes = "仓库别库存预警数据创建")
    @PostMapping("/createWarehouseInventoryWarning")
    public RetResult createWarehouseInventoryWarning(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                WarehouseInventoryWarning warehouseInventoryWarning =new WarehouseInventoryWarning();
                WarehouseInventoryWarning warning = (WarehouseInventoryWarning) TimeUtil.requestToObject(request, warehouseInventoryWarning);
                Integer select = inventoryWarningDao.selectWarehouseInventoryWarning(warning);
                if(select==0){
                    spiPartsManagementService.createWarehouseInventoryWarning(warning);
                }else {
                    return RetResponse.makeRsp(401,"数据已存在！");
                }
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "服务店备货单取消单数据创建", notes = "服务店备货单取消单数据创建")
    @PostMapping("/cancelServiceOrder")
    public RetResult cancelServiceOrder(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                CancelServiceOrderDTO cancelServiceOrderDTO =new CancelServiceOrderDTO();
                CancelServiceOrderDTO createDate = (CancelServiceOrderDTO)TimeUtil.requestToObject(request,cancelServiceOrderDTO);

                String distributionSingleFlag = cancelServiceOrderDao.getFlag(createDate.getReceiptNumber());
                if(distributionSingleFlag!=null
                        &&!"".equals(distributionSingleFlag)){
                    return RetResponse.makeRsp(401,"订单已存在！");
                }
                spiPartsManagementService.cancelServiceOrder(createDate);
                // 重新计算并插入/修改该日需求金额
                try {
                    dailyDemandAmountService.calculateAndInsertDemandAmountByDate(createDate.getDocumentDate());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }

    @ApiOperation(value = "物流信息数据创建", notes = "物流信息数据创建")
    @PostMapping("/createlogisticsInformation")
    public RetResult createlogisticsInformation(HttpServletRequest request) {
        // 此处为自测时才放开，生产环境需注释掉（除非此接口对外）
        LoginInfoParams.addCompanyIdAsLoginBean("08d181119a7b4c0e94ff368942fd4420");
        try{
            if(request!=null){
                LogisticsInformation logisticsInformation =new LogisticsInformation();
                LogisticsInformation newLogisticsInformation =  (LogisticsInformation) TimeUtil.requestToObject(request, logisticsInformation);
                Integer select = logisticsInformationDao.select(newLogisticsInformation);
                if(select==0){
                    spiPartsManagementService.createlogisticsInformation(newLogisticsInformation);
                }else {
                    return RetResponse.makeRsp(401,"数据已存在！");
                }
            }else {
                return RetResponse.makeErrRsp("数据为空，添加失败！");
            }

        }catch (Exception e){
            e.printStackTrace();
            return RetResponse.makeErrRsp("创建失败");
        }

        return RetResponse.makeOKRsp();
    }


}
