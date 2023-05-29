package utry.data.modular.partsManagement.controller;


import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utry.core.base.controller.CommonController;
import utry.core.bo.PageBean;
import utry.data.modular.baseConfig.dao.TargetCoreConfigDao;
import utry.data.modular.baseConfig.dao.UserFactoryDao;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.dto.IndicatorDTO;
import utry.data.modular.partsManagement.dao.PurchaseOrderDao;
import utry.data.modular.partsManagement.dto.*;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.modular.partsManagement.model.ProductCategory;
import utry.data.modular.partsManagement.model.PurchaseOrder;
import utry.data.modular.partsManagement.model.PurchaseOrderDetail;
import utry.data.modular.partsManagement.service.CoreIndexService;
import utry.data.modular.partsManagement.vo.*;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;


/**
 * 核心指标Controller
 *
 * @author zhongdongbiao
 * @date 2022/4/18 11:18
 */
@RestController
@RequestMapping("/coreIndex")
@Api(tags = "核心指标Controller")
public class CoreIndexController extends CommonController {

    @Autowired
    CoreIndexService coreIndexService;
    @Resource
    private UserFactoryDao userFactoryDao;
    @Resource
    private TargetCoreConfigDao targetCoreConfigDao;
    @Resource
    private PurchaseOrderDao purchaseOrderDao;

    /**
     * 最大信号量，例如此处1，生成环境可以做成可配置项，通过注入方式进行注入
     */
    private static final int MAX_SEMAPHORE = 10;
    /**
     * 获取信号量最大等待时间
     */
//    @Value("${sysConst.maxWait}")
    private static int TIME_OUT = 15;

    /**
     * Semaphore主限流，全局就行
     */
    private static final Semaphore SEMAPHORE = new Semaphore(MAX_SEMAPHORE, false);

    @ApiOperation(value = "获取核心指标", notes = "startDate 开始时间 endDate 结束时间 isGet 是否获取差值 0 获取 1 不获取 userId  userId查询担当 1不查询担当")
    @PostMapping("/getCoreIndex")
    public RetResult getCoreIndex(@RequestBody JSONObject jsonObject) throws InterruptedException {
        // 开始时间
        String startDate = jsonObject.getString("startDate");
        // 结束时间
        String endDate = jsonObject.getString("endDate");
        // 是否获取差值 0 获取 1 不获取
        String isGet = jsonObject.getString("isGet");
        // 担当id
        String userId = jsonObject.getString("userId");
        // 担当姓名
        String realName = jsonObject.getString("realName");
        if("1".equals(userId)){
            Map<Object, Object> coreIndex = coreIndexService.getCoreIndex(startDate, endDate, isGet, null, null);
            return RetResponse.makeOKRsp(coreIndex);
        }
        return RetResponse.makeOKRsp(coreIndexService.getCoreIndex(startDate, endDate, isGet,userId,realName));
    }

    @ApiOperation(value = "获取担当信息")
    @PostMapping("/getAllBear")
    public RetResult getAllBear() {
        List<HrmAccountInfoDTO> hrmAccountInfoDTOS = userFactoryDao.selectConfig();
        System.out.println("123");
        return RetResponse.makeOKRsp(hrmAccountInfoDTOS);
    }

    @ApiOperation(value = "分页条件查询订单列表", notes = "分页条件查询订单列表")
    @PostMapping("/getOrderDetailList")
    public RetResult getOrderDetailList(@RequestBody OrderDetailConditionDTO orderDetailConditionDto) {
        List<String> partDrawingNo=new ArrayList<>();
        if(orderDetailConditionDto.getDocumentNo()!=null&& !"".equals(orderDetailConditionDto.getDocumentNo())){
            partDrawingNo = purchaseOrderDao.getPartDrawingNo(orderDetailConditionDto.getDocumentNo());
        }
        if(partDrawingNo!=null && partDrawingNo.size()>0){
            orderDetailConditionDto.setPartDrawingNos(partDrawingNo);
        }
        PageBean pageBean = getPageBean(orderDetailConditionDto.getPageNum(),orderDetailConditionDto.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<OrderDetailVo> list = coreIndexService.getOrderDetailList(orderDetailConditionDto);
        PageInfo<OrderDetailVo> pageInfo = new PageInfo<>(list);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "查询全部工厂", notes = "查询全部工厂")
    @PostMapping("/getFactoryDate")
    public RetResult getFactoryDate() {
        List<FactoryData> list= coreIndexService.getFactoryDate();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "查询全部品类", notes = "查询全部品类")
    @PostMapping("/getAllProductCategory")
    public RetResult getAllProductCategory() {
        List<ProductCategory> list= coreIndexService.getAllProductCategory();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "切换为图表", notes = "切换为图表")
    @PostMapping("/getChart")
    public RetResult getChart(@RequestBody ChartConditionDto chartConditionDto) {
        Map<Object,Object> map = coreIndexService.getChart(chartConditionDto);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", map);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "切换为品类视角", notes = "切换为品类视角")
    @PostMapping("/getProductCategory")
    public RetResult getProductCategory(@RequestBody ProductCategoryConditionDto productCategoryConditionDto) {
        PageBean pageBean = getPageBean(productCategoryConditionDto.getPageNum(),productCategoryConditionDto.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<ProductCategoryVo> list = coreIndexService.getProductCategory(productCategoryConditionDto);
        PageInfo<ProductCategoryVo> pageInfo = new PageInfo<>(list);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "查询在库金额页面", notes = "查询在库金额页面")
    @PostMapping("/getAmount")
    public RetResult getAmount(@RequestBody AmountConditionDto amountConditionDto) {
        Map<Object,Object> map = coreIndexService.getAmount(amountConditionDto);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", map);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "查询全部仓库", notes = "查询全部仓库")
    @PostMapping("/getAllWarehouse")
    public RetResult getAllWarehouse() {
        List<Map<Object,Object>> list= coreIndexService.getAllWarehouse();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "查询工厂别在库金额", notes = "查询工厂别在库金额")
    @PostMapping("/getAllWarehouseAmount")
    public RetResult getAllWarehouseAmount(@RequestBody FactoryAmountDTO factoryAmountDTO) {
        List<WarehouseAmountVO> list= coreIndexService.getAllWarehouseAmount(factoryAmountDTO);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "分页条件查询库存预警列表", notes = "分页条件查询库存预警列表")
    @PostMapping("/getInventoryWarning")
    public RetResult getInventoryWarning(@RequestBody InventoryWarningConditionDto inventoryWarningConditionDto) {
        PageBean pageBean = getPageBean(inventoryWarningConditionDto.getPageNum(), inventoryWarningConditionDto.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<InventoryWarningVo> list = coreIndexService.getInventoryWarning(inventoryWarningConditionDto);
        PageInfo<InventoryWarningVo> pageInfo = new PageInfo<>(list);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "分页条件查询缺货部品列表", notes = "分页条件查询缺货部品列表")
    @PostMapping("/getStockGoods")
    public RetResult getStockGoods(@RequestBody InventoryWarningConditionDto inventoryWarningConditionDto) {
        PageBean pageBean = getPageBean(inventoryWarningConditionDto.getPageNum(), inventoryWarningConditionDto.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<InventoryWarningVo> list = coreIndexService.getStockGoods(inventoryWarningConditionDto);
        PageInfo<InventoryWarningVo> pageInfo = new PageInfo<>(list);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "根据部件图号查询数据", notes = "根据部件图号查询数据")
    @PostMapping("/getDateByPartDrawingNo")
    public RetResult getDateByPartDrawingNo(@RequestBody JSONObject jsonObject) {
        String partDrawingNo = jsonObject.getString("partDrawingNo");
        Map<Object,Object> map = coreIndexService.getDateByPartDrawingNo(partDrawingNo);
        jsonObject.put("data", map);
        return RetResponse.makeOKRsp(jsonObject);
    }
    @ApiOperation(value = "根据部件图号查询采购订单数据", notes = "根据部件图号查询数据")
    @PostMapping("/getPurchaseByPartDrawingNo")
    public RetResult getPurchaseByPartDrawingNo(@RequestBody JSONObject jsonObject) {
        String partDrawingNo = jsonObject.getString("partDrawingNo");
        String pageNum = jsonObject.getString("pageNum");
        String pageSize = jsonObject.getString("pageSize");
        PageBean pageBean = getPageBean(pageNum,pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<PurchaseDTO> list = coreIndexService.getPurchaseByPartDrawingNo(partDrawingNo);
        PageInfo<PurchaseDTO> pageInfo = new PageInfo<>(list);
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }
    @ApiOperation(value = "查询品类别在库金额", notes = "查询品类别在库金额")
    @PostMapping("/getProductCategoryAmount")
    public RetResult getProductCategoryAmount() {
        Map<Object,Object> map = coreIndexService.getProductCategoryAmount();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", map);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "根据品类查询部品出货即纳率", notes = "startDate 开始时间 endDate 结束时间 timeType 时间类型 sort 排序 0升序 1降序")
    @PostMapping("/getShipmentBySort")
    public RetResult getShipmentBySort(@RequestBody JSONObject jsonObject) throws ParseException {
        SimpleDateFormat monthSimpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String sort = jsonObject.getString("sort");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        String endMonth = monthSimpleDateFormat.format(monthSimpleDateFormat.parse(endDate));
        List<ShipmentVo> list = coreIndexService.getShipmentBySort(startDate,endDate,sort);
        // 获取最新的出货即纳率目标
        IndicatorDTO shipment = targetCoreConfigDao.selectTargetByIndicatorCode("partManagement",endMonth,"partImmediate",null);
        if(shipment!=null){
            jsonObject.put("shipment",Double.parseDouble(shipment.getIndicatorValue()));
        }else {
            jsonObject.put("shipment",0);
        }
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "根据品类查询工厂别部品出货即纳率", notes = "startDate 开始时间 endDate 结束时间 timeType 时间类型 sort 排序 0升序 1降序")
    @PostMapping("/getFactoryShipmentBySort")
    public RetResult getFactoryShipmentBySort(@RequestBody JSONObject jsonObject) throws ParseException {
        SimpleDateFormat monthSimpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String sort = jsonObject.getString("sort");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        String endMonth = monthSimpleDateFormat.format(monthSimpleDateFormat.parse(endDate));
        List<FactoryShipmentVo> list = coreIndexService.getFactoryShipmentBySort(startDate,endDate,sort);
        // 获取最新的出货即纳率目标
        IndicatorDTO shipment = targetCoreConfigDao.selectTargetByIndicatorCode("partManagement",endMonth,"partImmediate",null);
        if(shipment!=null){
            jsonObject.put("shipment",Double.parseDouble(shipment.getIndicatorValue()));
        }else {
            jsonObject.put("shipment",0);
        }
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }
    @ApiOperation(value = "根据品类查询出货时间", notes = "根据品类查询出货时间")
    @PostMapping("/getShipmentTimeBySort")
    public RetResult getShipmentTimeBySort(@RequestBody JSONObject jsonObject) {
        String sort = jsonObject.getString("sort");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        List<ShipmentTimeVo> list = coreIndexService.getShipmentTimeBySort(startDate,endDate,sort);
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }
    @ApiOperation(value = "根据省份查询货品在途时间", notes = "根据省份查询货品在途时间")
    @PostMapping("/getGoodTimeBySort")
    public RetResult getGoodTimeBySort(@RequestBody JSONObject jsonObject) {
        String sort = jsonObject.getString("sort");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        List<Map<Object,Object>> list = coreIndexService.getGoodTimeBySort(startDate,endDate,sort);
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取工厂别缺件部品", notes = "获取工厂别缺件部品")
    @PostMapping("/getFactoryStockGoods")
    public RetResult getFactoryStockGoods(@RequestBody JSONObject jsonObject) {
        String sort = jsonObject.getString("sort");
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        List<Map<Object,Object>> list = coreIndexService.getFactoryStockGoods(startDate,endDate,sort);
        jsonObject.put("data", list);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "根据作业订单号获取作业订单详情", notes = "根据作业订单号获取作业订单详情 documentNumber 订单编号")
    @PostMapping("/getOrderDetailByNumber")
    public RetResult getOrderDetailByNumber(@RequestBody JSONObject jsonObject) {
        String documentNumber = jsonObject.getString("documentNumber");
        Map<Object,Object> map = coreIndexService.getOrderDetailByNumber(documentNumber);
        jsonObject.put("data", map);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取部品在库金额曲线", notes = "获取部品在库金额曲线 startDate 开始时间 endDate 结束时间 aggregateType 时间类型 0 按日聚合 1 按月聚合")
    @PostMapping("/getLibraryAmount")
    public RetResult getLibraryAmount(@RequestBody JSONObject jsonObject) {
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        String aggregateType = jsonObject.getString("aggregateType");
        LibraryAmountVO libraryAmountVO = coreIndexService.getLibraryAmount(startDate,endDate,aggregateType);
        jsonObject.put("data", libraryAmountVO);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取部品库存曲线", notes = "获取部品库存曲线 startDate 开始时间 endDate 结束时间 aggregateType 时间类型 0 按日聚合 1 按月聚合")
    @PostMapping("/getCount")
    public RetResult getCount(@RequestBody JSONObject jsonObject) {
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        String aggregateType = jsonObject.getString("aggregateType");
        LibraryCountVO count = coreIndexService.getCount(startDate, endDate, aggregateType);
        jsonObject.put("data", count);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取单日在库金额列表", notes = "获取单日在库金额列表")
    @PostMapping("/getDayAmount")
    public RetResult getDayAmount(@RequestBody DayAmountDTO dayAmountDTO) {
        PageBean pageBean = getPageBean(dayAmountDTO.getPageNum(),dayAmountDTO.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<DayAmountVo> dayAmount = coreIndexService.getDayAmount(dayAmountDTO);
        PageInfo<DayAmountVo> pageInfo = new PageInfo<>(dayAmount);
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取单日在库数量列表", notes = "获取单日在库数量列表")
    @PostMapping("/getDayCount")
    public RetResult getDayCount(@RequestBody DayAmountDTO dayAmountDTO) {
        PageBean pageBean = getPageBean(dayAmountDTO.getPageNum(),dayAmountDTO.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<DayAmountVo> dayAmount = coreIndexService.getDayCount(dayAmountDTO);
        PageInfo<DayAmountVo> pageInfo = new PageInfo<>(dayAmount);
        JSONObject jsonObject =new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "零件管理异常监控", notes = "startDate 开始时间 endDate 结束时间")
    @PostMapping("/getAbnormalMonitoring")
    public RetResult getAbnormalMonitoring(@RequestBody JSONObject jsonObject) throws InterruptedException {
        String startDate = jsonObject.getString("startDate");
        String endDate = jsonObject.getString("endDate");
        AbnormalMonitoringVo abnormalMonitoringVo =coreIndexService.getAbnormalMonitoring(startDate,endDate);
        jsonObject.put("data", abnormalMonitoringVo);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取服务店收货单列表", notes = "获取服务店收货单列表")
    @PostMapping("/getReceipt")
    public RetResult getReceiptList(@RequestBody ReceiptConditionDTO receiptConditionDTO) {
        PageBean pageBean = getPageBean(receiptConditionDTO.getPageNum(),receiptConditionDTO.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<ReceiptVo> dayAmount = coreIndexService.getReceiptList(receiptConditionDTO);
        PageInfo<ReceiptVo> pageInfo = new PageInfo<>(dayAmount);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取采购订单列表", notes = "获取采购订单列表")
    @PostMapping("/getPurchaseOrder")
    public RetResult getPurchaseOrder(@RequestBody PurchaseOrderConditionDTO purchaseOrderConditionDTO) {
        PageBean pageBean = getPageBean(purchaseOrderConditionDTO.getPageNum(),purchaseOrderConditionDTO.getPageSize());
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());

        List<PurchaseOrderListVo> dayAmount = coreIndexService.getPurchaseOrder(purchaseOrderConditionDTO);
        PageInfo<PurchaseOrderListVo> pageInfo = new PageInfo<>(dayAmount);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取采购订单详情", notes = "获取采购订单详情")
    @PostMapping("/getPurchaseOrderDetail")
    public RetResult getPurchaseOrderDetail(@RequestBody JSONObject jsonObject) {
        String documentNumber = jsonObject.getString("documentNumber");
        PurchaseOrder purchaseOrder = coreIndexService.getPurchaseOrderDetail(documentNumber);
        jsonObject.put("data", purchaseOrder);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取采购订单详情列表", notes = "获取采购订单详情列表")
    @PostMapping("/getPurchaseOrderDetailList")
    public RetResult getPurchaseOrderDetailList(@RequestBody JSONObject jsonObject) {
        String documentNumber = jsonObject.getString("documentNumber");
        String pageNum = jsonObject.getString("pageNum");
        String pageSize = jsonObject.getString("pageSize");
        PageBean pageBean = getPageBean(pageNum,pageSize);
        Page page = PageHelper.startPage(pageBean.getCurrentPage(), pageBean.getPageSize());
        List<PurchaseOrderDetail> dayAmount = coreIndexService.getPurchaseOrderDetailList(documentNumber);
        PageInfo<PurchaseOrderDetail> pageInfo = new PageInfo<>(dayAmount);
        jsonObject.put("data", pageInfo.getList());
        jsonObject.put("count", page.getTotal());
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "担当别在库金额（仟元）", notes = "担当别在库金额（仟元）")
    @PostMapping("/getBearAmount")
    public RetResult<List<BearAmountVO>> getBearAmount() {
        return coreIndexService.getBearAmount();
    }

    @ApiOperation(value = "根据userId查询担当下的各工厂在库金额", notes = "表格")
    @PostMapping("/getBearAmountByUserId")
    public RetResult<JSONObject> getBearAmountByUserId(@Valid @RequestBody FactoryBearAmountQueryDTO factoryBearAmountQueryDTO) {
        return coreIndexService.getBearAmountByUserId(factoryBearAmountQueryDTO);
    }

    @ApiOperation(value = "订单达标统计", notes = "订单达标统计")
    @PostMapping("/getStandardOrder")
    public RetResult getStandardOrder(@RequestBody JSONObject jsonObject) {
        String date = jsonObject.getString("date");
        Map<Object,Object> map = coreIndexService.getStandardOrder(date);
        jsonObject.put("data", map);
        return RetResponse.makeOKRsp(jsonObject);
    }

    @ApiOperation(value = "获取实际在库金额（元）")
    @RequestMapping(value = "/getActualAmountInStock", method = RequestMethod.GET)
    public RetResult<Double> getActualAmountInStock(@RequestParam("userId") String userId) {
        return coreIndexService.getActualAmountInStock(userId);
    }
}
