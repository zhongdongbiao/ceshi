package utry.data.modular.partsManagement.service;

import com.alibaba.fastjson.JSONObject;
import utry.data.modular.partsManagement.dto.*;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.modular.partsManagement.model.ProductCategory;
import utry.data.modular.partsManagement.model.PurchaseOrder;
import utry.data.modular.partsManagement.model.PurchaseOrderDetail;
import utry.data.modular.partsManagement.vo.*;
import utry.data.util.RetResult;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author zhongdongbiao
 * @date 2022/4/19 9:57
 */
public interface CoreIndexService {

    /**
     * 获取核心指标
     * @param startDate
     * @param endDate
     * @param isGet
     * @param userID
     * @return
     */
    Map<Object,Object> getCoreIndex(String startDate, String endDate, String isGet,String userID,String RealName);

    /**
     * 分页条件查询订单列表
     * @param orderDetailConditionDto
     * @return
     */
    List<OrderDetailVo> getOrderDetailList(OrderDetailConditionDTO orderDetailConditionDto);

    /**
     * 查询在库金额页面
     * @param amountConditionDto
     * @return
     */
    Map<Object,Object> getAmount(AmountConditionDto amountConditionDto);

    /**
     * 查询全部仓库
     * @return
     */
    List<Map<Object,Object>> getAllWarehouse();

    /**
     * 查询工厂别在库金额
     * @param factoryAmountDTO
     * @return
     */
    List<WarehouseAmountVO> getAllWarehouseAmount(FactoryAmountDTO factoryAmountDTO);

    /**
     * 分页条件查询库存预警列表
     * @param inventoryWarningConditionDto
     * @return
     */
    List<InventoryWarningVo> getInventoryWarning(InventoryWarningConditionDto inventoryWarningConditionDto);

    /**
     * 分页条件查询缺货部品列表
     * @param inventoryWarningConditionDto
     * @return
     */
    List<InventoryWarningVo> getStockGoods(InventoryWarningConditionDto inventoryWarningConditionDto);

    /**
     * 根据部件图号查询数据
     * @param partDrawingNo
     * @return
     */
    Map<Object, Object> getDateByPartDrawingNo(String partDrawingNo);

    /**
     * 根据部件图号查询采购订单数据
     * @param partDrawingNo
     * @return
     */
    List<PurchaseDTO> getPurchaseByPartDrawingNo(String partDrawingNo);

    /**
     * 查询品类别在库金额
     * @return
     */
    Map<Object,Object> getProductCategoryAmount();

    /**
     * 根据品类查询部品出货即纳率
     * @param startDate
     * @param endDate
     * @param sort
     * @return
     */
    List<ShipmentVo> getShipmentBySort(String startDate, String endDate, String sort);

    /**
     * 根据品类查询出货时间
     * @param startDate
     * @param endDate
     * @param sort
     * @return
     */
    List<ShipmentTimeVo> getShipmentTimeBySort(String startDate, String endDate, String sort);

    /**
     * 根据省份查询货品在途时间
     * @param startDate
     * @param endDate
     * @param sort
     * @return
     */
    List<Map<Object,Object>> getGoodTimeBySort(String startDate, String endDate, String sort);

    /**
     * 获取工厂别缺件部品
     * @param startDate
     * @param endDate
     * @param sort
     * @return
     */
    List<Map<Object,Object>> getFactoryStockGoods(String startDate, String endDate, String sort);

    /**
     * 定时添加预计完成时间预计收货时间
     * @param startDate
     * @param endDate
     */
    void addTime(String startDate,String endDate);

    /**
     * 根据作业订单号获取作业订单详情
     * @param documentNumber
     * @return
     */
    Map<Object,Object> getOrderDetailByNumber(String documentNumber);

    /**
     * 获取部品在库金额曲线
     * @param startDate
     * @param endDate
     * @return
     */
    LibraryAmountVO getLibraryAmount(String startDate, String endDate,String aggregateType);

    /**
     * 获取部品库存曲线
     * @param startDate
     * @param endDate
     * @return
     */
    LibraryCountVO getCount(String startDate, String endDate,String aggregateType);

    /**
     * 获取单日在库金额列表
     * @param dayAmountDTO
     * @return
     */
    List<DayAmountVo> getDayAmount(DayAmountDTO dayAmountDTO);

    /**
     * 获取单日在库数量列表
     * @param dayAmountDTO
     * @return
     */
    List<DayAmountVo> getDayCount(DayAmountDTO dayAmountDTO);

    /**
     * 获取服务店收货单列表
     * @param receiptConditionDTO
     * @return
     */
    List<ReceiptVo> getReceiptList(ReceiptConditionDTO receiptConditionDTO);

    /**
     * 零件管理异常监控
     * @param startDate
     * @param endDate
     * @return
     */
    AbnormalMonitoringVo getAbnormalMonitoring(String startDate, String endDate);



    /**
     * 判断作业订单详情是否完成
     */
    void updateOrderDetail() throws ExecutionException, InterruptedException;

    /**
     * 判断作业订单是否完成
     */
    void updateOrder();

    /**
     * 判断作业订单详情是否取消
     */
    void updateCancelOrderDetail();

    /**
     * 给详情加上出货时间
     */
    void addTimeOrder();

    /**
     * 查询全部品类
     * @return
     */
    List<ProductCategory> getAllProductCategory();

    /**
     * 查询全部工厂
     * @return
     */
    List<FactoryData> getFactoryDate();

    /**
     * 切换为图表
     * @param chartConditionDto
     * @return
     */
    Map<Object,Object> getChart(ChartConditionDto chartConditionDto);

    /**
     * 切换为品类视角
     * @param productCategoryConditionDto
     * @return
     */
    List<ProductCategoryVo> getProductCategory(ProductCategoryConditionDto productCategoryConditionDto);

    /**
     * 获取采购订单列表
     * @param purchaseOrderConditionDTO
     * @return
     */
    List<PurchaseOrderListVo> getPurchaseOrder(PurchaseOrderConditionDTO purchaseOrderConditionDTO);

    /**
     * 获取采购订单详情
     * @param documentNumber
     * @return
     */
    PurchaseOrder getPurchaseOrderDetail(String documentNumber);

    /**
     * 获取采购订单详情列表
     * @param documentNumber
     * @return
     */
    List<PurchaseOrderDetail> getPurchaseOrderDetailList(String documentNumber);

    /**
     * 担当别在库金额（仟元）
     * @return 统一返回
     */
    RetResult<List<BearAmountVO>> getBearAmount();

    /**
     * 根据userId查询担当下的各工厂在库金额（仟元）
     * @param factoryBearAmountQueryDTO 查询条件
     * @return 统一返回
     */
    RetResult<JSONObject> getBearAmountByUserId(FactoryBearAmountQueryDTO factoryBearAmountQueryDTO);

    /**
     * 订单达标统计
     *
     * @param date
     * @return
     */
    Map<Object, Object> getStandardOrder(String date);

    /**
     * 获取实际在库金额（元）
     * @param userId 用户id
     * @return 统一返回
     */
    RetResult<Double> getActualAmountInStock(String userId);

    /**
     * 添加订单考核截至日
     */
    void addDeadline() throws ParseException;

    List<FactoryShipmentVo> getFactoryShipmentBySort(String startDate, String endDate, String sort);

    /**
     * 部品出货即纳率曲线
     * @param startDate
     * @param endDate
     * @return
     */
    List<PhoneShipmentVo> shipmentCurve(String startDate, String endDate);

    /**
     * nds2曲线
     * @param startDate
     * @param endDate
     * @return
     */
    List<Nds2CurveVo> nds2Curve(String startDate, String endDate);
}
