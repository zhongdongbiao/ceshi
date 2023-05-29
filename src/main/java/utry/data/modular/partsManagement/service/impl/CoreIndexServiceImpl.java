package utry.data.modular.partsManagement.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import utry.core.sysConfig.impl.SysConfServiceImpl;
import utry.core.util.UUIDUtils;
import utry.data.constant.AggregateTypeConstant;
import utry.data.enums.BizCodeEnum;
import utry.data.modular.account.dao.HrmAccountInfoDao;
import utry.data.modular.account.model.AccountInfoBO;
import utry.data.modular.aop.LogAspectForSongXia;
import utry.data.modular.baseConfig.dao.TargetCoreConfigDao;
import utry.data.modular.baseConfig.dao.UserFactoryDao;
import utry.data.modular.baseConfig.dto.IndicatorDTO;
import utry.data.modular.calendar.model.CalendarDto;
import utry.data.modular.partsManagement.bo.*;
import utry.data.modular.partsManagement.dao.*;
import utry.data.modular.partsManagement.dto.*;
import utry.data.modular.partsManagement.model.*;
import utry.data.modular.partsManagement.service.CoreIndexService;
import utry.data.modular.partsManagement.service.DailyDemandAmountService;
import utry.data.modular.partsManagement.vo.*;
import utry.data.util.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 核心指标实现类
 *
 * @author zhongdongbiao
 * @date 2022/4/19 9:58
 */
@Service
public class CoreIndexServiceImpl implements CoreIndexService {

//    public static final ThreadPoolExecutor HISTORY_EXECUTOR = new ThreadPoolExecutor(
//            20,
//            190,
//            2000L,
//            TimeUnit.SECONDS,
//            new LinkedBlockingDeque<>(6000),
//            Executors.defaultThreadFactory(),
//            new ThreadPoolExecutor.AbortPolicy()
//    );

    @Resource
    OrderDetailDao orderDetailDao;
    @Resource
    DistributionCycleDao distributionCycleDao;
    @Resource
    InventoryWarningDao inventoryWarningDao;
    @Resource
    ReceiptDao receiptDao;
    @Resource
    MissStockUpOrderDao missStockUpOrderDao;
    @Resource
    PackingListDao packingListDao;
    @Resource
    PurchaseOrderDao purchaseOrderDao;
    @Resource
    LocationInformationDao locationInformationDao;
    @Resource
    ProductCategoryDao productCategoryDao;
    @Resource
    private TargetCoreConfigDao targetCoreConfigDao;
    @Resource
    private DistributionSingleDao distributionSingleDao;
    @Resource
    private CancelDstributionOrderDao cancelDstributionOrderDao;
    @Resource
    private CancelServiceOrderDao cancelServiceOrderDao;
    @Resource
    private DistributionListCancelDao distributionListCancelDao;
    @Resource
    private MissDealOrderDao missDealOrderDao;
    @Resource
    private LogisticsInformationDao logisticsInformationDao;
    @Resource
    private SysConfServiceImpl sysConfService;
    @Resource
    private UserFactoryDao userFactoryDao;
    @Resource
    private PartDrawingStockDao partDrawingStockDao;
    @Resource
    private HrmAccountInfoDao hrmAccountInfoDao;
    @Resource(type = DailyDemandAmountDao.class)
    private DailyDemandAmountDao dailyDemandAmountDao;
    @Resource(type = DailyDemandAmountService.class)
    private DailyDemandAmountService dailyDemandAmountService;
    @Resource(type = DailySafeDepositAmountDao.class)
    private DailySafeDepositAmountDao dailySafeDepositAmountDao;
    @Resource
    private RedisUtils redisUtils;

    @Override
    public Map<Object, Object> getCoreIndex(String startDate, String endDate, String isGet, String userId, String RealName) {
        SimpleDateFormat monthSimpleDateFormat = new SimpleDateFormat("yyyy-MM");
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        String endMonth="";
        // 获取月份用于获取上月的日期
        double amount;
        double lastAmount = 0;
        if(!"".equals(endDate)){
            try {
                endMonth = monthSimpleDateFormat.format(monthSimpleDateFormat.parse(endDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Map<Object,Object> coreIndex = new HashMap<>();
        try {
            // 判断是否查询担当
            if(!"".equals(RealName) && RealName!=null){
                // 担当姓名
                coreIndex.put("RealName",RealName);
                // 担当id
                coreIndex.put("userId",userId);
            }
            // 获取在库金额
            LocalDate now = LocalDate.now();
            String nowString = now.toString();
            String endString = now.plusDays(1).toString();
            amount = calculateAmount(inventoryWarningDao.getAmountByUserId(userId, nowString, endString));
            coreIndex.put("amount",amount);
            // 获取最新的目标在库金额
            IndicatorDTO newStockAmount = targetCoreConfigDao.getNewStockAmount(userId,endMonth);
            if(newStockAmount!=null){
                coreIndex.put("targetAmount",Double.parseDouble(newStockAmount.getIndicatorValue()));
            }else {
                coreIndex.put("targetAmount",0);
            }
            // 获取部品出货即纳率
            Double shipment = getShipment(startDate,endDate,userId,inventoryDate);
            if(shipment!=null){
                if(shipment>100){
                    coreIndex.put("shipment",100);
                }else {
                    coreIndex.put("shipment",shipment);
                }
            }else {
                coreIndex.put("shipment",0);
            }
            // 获取本月的部品出货即纳率目标
            IndicatorDTO indicatorDTO=null;
            indicatorDTO = targetCoreConfigDao.selectTargetByIndicatorCode("partManagement",endMonth,"partImmediate",userId);
            if(indicatorDTO!=null){
                coreIndex.put("nowShipment",Double.parseDouble(indicatorDTO.getIndicatorValue()));
            }else {
                coreIndex.put("nowShipment",0);
            }
            // 获取NDS2
            int countByDate=0;
            int countByNDS2=0;
            int inventoryWarning=0;
            int lack=0;
            try {
                Future<Integer> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                    // 获取担当的所有的服务店收货订单行数
                    Integer countByDate1  = receiptDao.getCountByDate(startDate, endDate,userId,inventoryDate);
                    return countByDate1;
                });
                Future<Integer> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                    // 获取符合nds2的收货单行数
                    Integer countByNDS21  = receiptDao.getCountByNDS2(startDate, endDate,userId,inventoryDate);
                    return countByNDS21;
                });
                Future<Integer> submit3 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                    // 获取库存预警数量
                    Integer inventoryWarning1  = inventoryWarningDao.getInventoryWarning();
                    return inventoryWarning1;
                });
                Future<Integer> submit4 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                    // 获取缺货部品数量
                    Integer lack1  = inventoryWarningDao.getLack();
                    return lack1;
                });
                countByDate = submit1.get();
                countByNDS2 = submit2.get();
                inventoryWarning=submit3.get();
                lack=submit4.get();
            }catch (Exception e){
                e.printStackTrace();
            }
            Double nds2 = TimeUtil.getRate(countByNDS2,countByDate);
            if(nds2!=null){
                coreIndex.put("nds2",nds2);
            }else {
                coreIndex.put("nds2",0);
            }
            // 获取本月的nds2目标
            IndicatorDTO nowNds2 =targetCoreConfigDao.selectTargetByIndicatorCode("partManagement",endMonth,"nds2",userId);
            if(nowNds2!=null){
                coreIndex.put("nowNds2",Double.parseDouble(nowNds2.getIndicatorValue()));
            }else {
                coreIndex.put("nowNds2",0);
            }
            coreIndex.put("inventoryWarning",inventoryWarning);
            coreIndex.put("lack",lack);
            if("0".equals(isGet)){
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                //获取上个月的第一天
                //获取当前日期
                Calendar cal_1=Calendar.getInstance();
                Date parse = format.parse(startDate);
                //设置当前日期
                cal_1.setTime(parse);
                cal_1.add(Calendar.MONTH, -1);
                //设置为1号
                cal_1.set(Calendar.DAY_OF_MONTH,1);
                cal_1.set(Calendar.HOUR_OF_DAY,0);
                cal_1.set(Calendar.MINUTE,0);
                cal_1.set(Calendar.SECOND,0);
                String firstDay = format.format(cal_1.getTime());
                //获取上个月的最后一天
                Calendar cal_2 = Calendar.getInstance();
                //设置当前日期
                cal_2.setTime(parse);
                //设置为1号,当前日期既为本月第一天
                cal_2.set(Calendar.DAY_OF_MONTH,0);
                cal_2.set(Calendar.HOUR_OF_DAY,23);
                cal_2.set(Calendar.MINUTE,59);
                cal_2.set(Calendar.SECOND,59);
                String lastDay = format.format(cal_2.getTime());
                Double lastShipment=0.0;
                // 获取上月的部品出货即纳率
                if(redisUtils.get("lastShipment"+userId)==null){
                    lastShipment= getShipment(firstDay,lastDay,userId,inventoryDate);
                    redisUtils.set("lastShipment"+userId,lastShipment,86400);
                }else {
                    lastShipment = (Double) redisUtils.get("lastShipment"+userId);
                }
                // 获取部品出货即纳率差值
                if(shipment!=null && lastShipment!=null){
                    if(lastShipment>100){
                        lastShipment=100.0;
                    }
                    coreIndex.put("poorShipment",TimeUtil.getRate(shipment-lastShipment,lastShipment));
                }else {
                    coreIndex.put("poorShipment",0);
                }
                // 获取上月在库金额
                try {
                    lastAmount = calculateAmount(inventoryWarningDao.getAmountByMonthUserId(lastDay, LocalDate.parse(lastDay).plusDays(1).toString(), userId));
                } catch (Exception ignored) {
                }
                // 获取在库金额差值
                if(TimeUtil.getRate(amount-lastAmount,lastAmount)!=null){
                    coreIndex.put("poorAmount",TimeUtil.getRate(amount-lastAmount,lastAmount));
                }else {
                    coreIndex.put("poorAmount",0);
                }
                // 获取NDS2
                Double lastNds2=0.0;
                if(redisUtils.get("lastNds2"+userId)==null){
                    // 获取上月的所有的服务店收货订单行数
                    int lastCountByDate = receiptDao.getCountByDate(firstDay, lastDay,userId,inventoryDate);
                    // 获取上月的担当的符合NDS2服务店收货订单行数
                    int lastCountByNDS2 =receiptDao.getCountByNDS2(firstDay, lastDay,userId,inventoryDate);
                    lastNds2 = TimeUtil.getRate(lastCountByNDS2,lastCountByDate);
                    redisUtils.set("lastNds2"+userId,lastNds2,86400);
                }else {
                    lastNds2 = (Double) redisUtils.get("lastNds2"+userId);
                }
                if(nds2!=null && lastNds2!=null){
                    if(TimeUtil.getRate(nds2-lastNds2,lastNds2)!=null){
                        // 获取NDS2差值
                        coreIndex.put("poorNds2",TimeUtil.getRate(nds2-lastNds2,lastNds2));
                    }else {
                        // 获取NDS2差值
                        coreIndex.put("poorNds2",0);
                    }
                }else {
                    coreIndex.put("poorNds2",0);
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return coreIndex;
    }

    @Override
    public List<OrderDetailVo> getOrderDetailList(OrderDetailConditionDTO orderDetailConditionDto) {
        List<OrderDetailVo> orderDetailVos = new ArrayList<>();
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        orderDetailConditionDto.setInventoryDate(inventoryDate);
        if(orderDetailConditionDto.getOrderState()!=null){
            switch (orderDetailConditionDto.getOrderState()){
                case "0":
                    orderDetailVos = orderDetailDao.selectAllDetail(orderDetailConditionDto);
                    break;
                case "1":
                    // 根据条件查询获取待处理订单详细数据
                    orderDetailVos = orderDetailDao.getprocessedOrder(orderDetailConditionDto);
                    break;
                case "2":
                    // 根据条件查询获取缺件处理订单详细数据
                    orderDetailVos = orderDetailDao.getMissStockUpOrder(orderDetailConditionDto);
                    break;
                case "3":
                    // 根据条件查询获取已装箱订单详细数据
                    orderDetailVos = orderDetailDao.getVoteOrder(orderDetailConditionDto);
                    break;
                case "4":
                    // 根据条件查询获取已妥投订单详细数据
                    orderDetailVos = orderDetailDao.getPackingListDetail(orderDetailConditionDto);
                    break;
                case "5":
                    // 根据条件查询获取已完成订单详细数据
                    orderDetailVos = orderDetailDao.getCompleteListDetail(orderDetailConditionDto);
                    break;
                default:

            }
        }
        return orderDetailVos;
    }

    @Override
    public Map<Object, Object> getAmount(AmountConditionDto amountConditionDto) {
        SimpleDateFormat daySimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Date> distanceDate = TimeUtil.getDistanceDateByDate(amountConditionDto.getStartDate(), amountConditionDto.getEndDate());
        List<String> dates = new ArrayList<>();
        List<Double> everyAmount = new ArrayList<>();
        List<Double> lastEveryAmount=new ArrayList<>();
        Map<Object,Object> map = new HashMap<>();
        //获取上个月的第一天
        //获取当前日期
        Calendar cal_1=Calendar.getInstance();
        Date parse = null;
        try {
            parse = daySimpleDateFormat.parse(amountConditionDto.getStartDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //设置当前日期
        cal_1.setTime(parse);
        cal_1.add(Calendar.MONTH, -1);
        //设置为1号
        cal_1.set(Calendar.DAY_OF_MONTH,1);
        cal_1.set(Calendar.HOUR_OF_DAY,0);
        cal_1.set(Calendar.MINUTE,0);
        cal_1.set(Calendar.SECOND,0);
        String firstDay = daySimpleDateFormat.format(cal_1.getTime());
        //获取上个月的最后一天
        Calendar cal_2 = Calendar.getInstance();
        //设置当前日期
        cal_2.setTime(parse);
        //设置为1号,当前日期既为本月第一天
        cal_2.set(Calendar.DAY_OF_MONTH,0);
        cal_2.set(Calendar.HOUR_OF_DAY,23);
        cal_2.set(Calendar.MINUTE,59);
        cal_2.set(Calendar.SECOND,59);
        String lastDay = daySimpleDateFormat.format(cal_2.getTime());
        List<Date> lastDistanceDate = TimeUtil.getDistanceDateByDate(firstDay,lastDay);
        // 本月每天在库金额
        List<EveryAmountByDateBO> everyAmountByDate = inventoryWarningDao.getEveryAmountByDate(amountConditionDto.getStartDate(), LocalDate.parse(amountConditionDto.getEndDate()).plusDays(1).toString(),"0",amountConditionDto.getUserId());
        // 上月每天在库金额
        List<EveryAmountByDateBO> lastEveryAmountByDate = new ArrayList<>();
        try {
            lastEveryAmountByDate = inventoryWarningDao.getEveryAmountByDate(firstDay, LocalDate.parse(lastDay).plusDays(1).toString(),"0",amountConditionDto.getUserId());
        } catch (Exception ignored) {
        }
        for (Date date:distanceDate) {
            String format1 = daySimpleDateFormat.format(date);
            dates.add(format1);
            int flag =0;
            if(everyAmountByDate.size()!=0){
                for (EveryAmountByDateBO map1:everyAmountByDate) {
                    if(format1.equals(map1.getInventoryDate())){
                        everyAmount.add(map1.getAmount().doubleValue());
                        flag=1;
                        break;
                    }else {
                        flag =0;
                    }
                }
                if(flag==0){
                    everyAmount.add(0.0);
                }
            }else {
                everyAmount.add(0.0);
            }
        }
        for (Date date:lastDistanceDate) {
            String format1 = daySimpleDateFormat.format(date);
            int flag1=0;
            if(lastEveryAmountByDate.size()!=0){
                for (EveryAmountByDateBO map1:lastEveryAmountByDate) {
                    if(format1.equals(map1.getInventoryDate())){
                        lastEveryAmount.add(map1.getAmount().doubleValue());
                        flag1=1;
                        break;
                    }else {
                        flag1=0;
                    }
                }
                if(flag1==0){
                    lastEveryAmount.add(0.0);
                }
            }else {
                lastEveryAmount.add(0.0);
            }
        }
        // 保留2位小数
        for (int i = 0; i < everyAmount.size(); i++) {
            everyAmount.set(i, BigDecimal.valueOf(everyAmount.get(i)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        }
        for (int i = 0; i < lastEveryAmount.size(); i++) {
            lastEveryAmount.set(i, BigDecimal.valueOf(lastEveryAmount.get(i)).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        }
        Map<Object, Object> productCategoryAmount = getProductCategoryAmount();
        map.put("date",dates);
        map.put("everyAmount",everyAmount);
        map.put("lastEveryAmount",lastEveryAmount);
        Object productCategory =  productCategoryAmount.get("品类别在库金额");
        map.put("productCategory",productCategory);
        List<Map<String, String>> bear = inventoryWarningDao.getBear();
        map.put("bear",bear);
        return map;
    }

    @Override
    public List<Map<Object, Object>> getAllWarehouse() {
        LocalDate now = LocalDate.now();
        String startDate = now.toString();
        String endDate = now.plusDays(1).toString();
        return locationInformationDao.getAllLocationInformation(startDate, endDate);
    }

    /**
     * 查询工厂别在库金额
     * @param factoryAmountDTO
     * @return
     */
    @Override
    public List<WarehouseAmountVO> getAllWarehouseAmount(FactoryAmountDTO factoryAmountDTO) {
        LocalDate now = LocalDate.now();
        String startDate = now.toString();
        String endDate = now.plusDays(1).toString();
        String warehouseCode = factoryAmountDTO.getWarehouseCode();
        String userId = factoryAmountDTO.getUserId();
        List<WarehouseAmountBO> allWarehouseAmount = inventoryWarningDao.getAllWarehouseAmount(warehouseCode, userId, startDate, endDate);
        if (allWarehouseAmount == null) {
            return Collections.emptyList();
        }
        List<WarehouseAmountVO> warehouseAmountVOList = new ArrayList<>(allWarehouseAmount.size());
        double amount = calculateAmount(inventoryWarningDao.getAmountByUserId(null, startDate, endDate));
        BigDecimal amountBigDecimal = new BigDecimal(amount);
        for (WarehouseAmountBO warehouseAmountBO : allWarehouseAmount) {
            BigDecimal percent = warehouseAmountBO.getTotal().divide(amountBigDecimal,4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_DOWN);
            WarehouseAmountVO vo = WarehouseAmountVO.builder()
                    .factoryCode(warehouseAmountBO.getFactoryCode())
                    .factoryName(warehouseAmountBO.getFactoryName())
                    .total(warehouseAmountBO.getTotal().setScale(2, BigDecimal.ROUND_DOWN).doubleValue())
                    .percent(percent.doubleValue())
                    .build();
            warehouseAmountVOList.add(vo);
        }
        return warehouseAmountVOList;
    }

    @Override
    public List<InventoryWarningVo> getInventoryWarning(InventoryWarningConditionDto inventoryWarningConditionDto) {
        return inventoryWarningDao.getInventoryWarningVo(inventoryWarningConditionDto);
    }

    @Override
    public List<InventoryWarningVo> getStockGoods(InventoryWarningConditionDto inventoryWarningConditionDto) {
        return inventoryWarningDao.getStockGoods(inventoryWarningConditionDto);
    }

    @Override
    public Map<Object, Object> getDateByPartDrawingNo(String partDrawingNo) {
        Map<Object,Object> map =new HashMap<>();
        InventoryWarning inventoryWarningByPartDrawingNo = inventoryWarningDao.getInventoryWarningByPartDrawingNo(partDrawingNo);
        map.put("inventoryWarningByPartDrawingNo",inventoryWarningByPartDrawingNo);
        return map;
    }

    @Override
    public List<PurchaseDTO> getPurchaseByPartDrawingNo(String partDrawingNo) {
        // 获取采购订单DTO
        List<PurchaseDTO> purchaseDTOByPartDrawingNo = purchaseOrderDao.getPurchaseDTOByPartDrawingNo(partDrawingNo);
        for (PurchaseDTO purchaseDTO:purchaseDTOByPartDrawingNo
             ) {
            // 获取采购订单VO
            PurchaseOrderVo purchaseOrderVoByPartDrawingNo = purchaseOrderDao.getPurchaseOrderVoByDocumentNo(purchaseDTO.getDocumentNo());
           // 获取采购订单详情
            List<PurchaseOrderDetail> purchaseOrderDetailByNo = purchaseOrderDao.getPurchaseOrderDetailByNo(purchaseDTO.getDocumentNo(),partDrawingNo);
            purchaseOrderVoByPartDrawingNo.setPartInformationList(purchaseOrderDetailByNo);
            purchaseDTO.setPurchaseOrderVoList(purchaseOrderVoByPartDrawingNo);
        }
        return purchaseDTOByPartDrawingNo;
    }

    @Override
    public Map<Object, Object> getProductCategoryAmount() {
        Map<Object,Object> map = new HashMap<>();
        List<Map<String, String>> productCategory = inventoryWarningDao.getProductCategory();
        map.put("productCategory",productCategory);
        return map;
    }

    @Override
    public List<ShipmentVo> getShipmentBySort(String startDate,String endDate, String sort) {
        List<ProductCategory> allProductCategory = productCategoryDao.getAllProductCategory();
        // 计算核心指标
        // 获取所有订单行数量
        List<Map<Object, Object>> countByDate = orderDetailDao.getCountByDateByProductCategory(startDate, endDate);
        List<ShipmentVo> list = new ArrayList<>();
        List<Map<Object, Object>> countByOrder = orderDetailDao.countByOrderByProductCategory(startDate, endDate);
        for (ProductCategory productCategory:allProductCategory
             ) {
            ShipmentVo shipmentVo = new ShipmentVo();
            int allCount=0;
            // 符合的订单数
            int nowOrderConformity=0;
            // 目标：筛选出同一品类的元素
            List<Map<Object, Object>> countByProductCategory = countByDate.stream()
                    .filter(oc -> oc.get("productCategoryCode")!=null  && oc.get("productCategoryCode").equals(productCategory.getProductCategoryCode()))
                    .collect(Collectors.toList());
            if(countByProductCategory!=null&&countByProductCategory.size()!=0){
                allCount =  Integer.parseInt(countByProductCategory.get(0).get("total").toString());
            }
            // 目标：筛选出同一品类的元素
            List<Map<Object, Object>> countByOrderProductCategory = countByOrder.stream()
                    .filter(oc -> oc.get("productCategoryCode")!=null  && oc.get("productCategoryCode").equals(productCategory.getProductCategoryCode()))
                    .collect(Collectors.toList());
            if(countByOrderProductCategory!=null&&countByOrderProductCategory.size()!=0){
                nowOrderConformity =  Integer.parseInt(countByOrderProductCategory.get(0).get("total").toString());
            }
            if(allCount!=0){
                Double shipment =  TimeUtil.getRate(nowOrderConformity,allCount);
                if(shipment==null){
                    shipmentVo.setShipment(Double.valueOf("0"));
                }else {
                    // 设置品类
                    shipmentVo.setProductCategory(productCategory.getProductCategory());
                    if(shipment>100){
                        shipmentVo.setShipment(Double.parseDouble("100"));
                    }else {
                        shipmentVo.setShipment(shipment);
                    }
                    list.add(shipmentVo);
                }
            }


        }
        // 升序
        if("0".equals(sort)){
            list = list.stream().sorted(Comparator.comparing(ShipmentVo::getShipment,Comparator.nullsLast(Double::compareTo)))
                    .collect(Collectors.toList());
        // 降序
        }else {
            list = list.stream().sorted(Comparator.comparing(ShipmentVo::getShipment,Comparator.nullsLast(Double::compareTo)).reversed())
                    .collect(Collectors.toList());
        }
        return list;
    }

    @Override
    public List<ShipmentTimeVo> getShipmentTimeBySort(String startDate,String endDate, String sort) {
        List<ProductCategory> allProductCategory = productCategoryDao.getAllProductCategory();
        List<ShipmentTimeVo> list = new ArrayList<>();
        List<Map<Object, Object>> countByOrder = orderDetailDao.getShipmentTime(startDate, endDate);
        for (ProductCategory productCategory:allProductCategory
                ) {
            ShipmentTimeVo shipmentTimeVo =new ShipmentTimeVo();
            Double shipmentTime=null;
            // 目标：筛选出同一部品品类
            List<Map<Object, Object>> countByOrderProductCategory = countByOrder.stream()
                    .filter(oc -> oc.get("productCategoryCode")!=null  && oc.get("productCategoryCode").equals(productCategory.getProductCategoryCode()))
                    .collect(Collectors.toList());
            if(countByOrderProductCategory!=null&&countByOrderProductCategory.size()!=0){
                shipmentTime =  Double.parseDouble(countByOrderProductCategory.get(0).get("shipmentTime").toString());
            }
            // 赋值出货时间
            if(shipmentTime==null){
                shipmentTimeVo.setShipmentTime(Double.valueOf("0"));
            }else {
                // 赋值部品品类
                shipmentTimeVo.setProductCategory(productCategory.getProductCategory());
                shipmentTimeVo.setShipmentTime(Double.parseDouble(shipmentTime+""));
                list.add(shipmentTimeVo);
            }

        }

        if("0".equals(sort)){
            list = list.stream().sorted(Comparator.comparing(ShipmentTimeVo::getShipmentTime,Comparator.nullsLast(Double::compareTo)))
                    .collect(Collectors.toList());
        }else {
            list = list.stream().sorted(Comparator.comparing(ShipmentTimeVo::getShipmentTime,Comparator.nullsLast(Double::compareTo)).reversed())
                    .collect(Collectors.toList());
        }

        return list;
    }

    @Override
    public List<Map<Object, Object>> getGoodTimeBySort(String startDate, String endDate, String sort) {
        return receiptDao.getGoodTimeBySort(startDate,endDate,sort);
    }

    @Override
    public List<Map<Object, Object>> getFactoryStockGoods(String startDate, String endDate, String sort) {
        return inventoryWarningDao.getFactoryStockGoods(startDate,endDate,sort);
    }

    @Override
    public void addTime(String startDate,String endDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<PartOrderDTO> updatePartOrder = orderDetailDao.getUpdatePartOrder(startDate, endDate);
        DistributionCycle distributionStateByCondition=new DistributionCycle();
        List<OrderDetailVo> orderDetailVolist = new ArrayList<>();
        // 获取所有缺件备货单详情数据
        List<MissStockUpOrder> allMissStockUpOrderByOrders = missStockUpOrderDao.getMissStockUpOrderByOrder(null);
        // 获取服务店的配货信息
        List<DistributionCycle> allDistributionStateByCondition = distributionCycleDao.getDistributionStateByCondition();
        List<Map<String, String>> allPurchaseOrderByPartDrawingNoList = purchaseOrderDao.getPurchaseOrderByPartDrawingNo();
        for (PartOrderDTO partOrderDTO: updatePartOrder
                ) {
            // 获取缺件备货单详情数据
            // 目标：筛选出元素
            List<MissStockUpOrder> missStockUpOrderByOrders = allMissStockUpOrderByOrders.stream()
                    .filter(oc -> oc.getAssociatedOrderNumber()!=null  && partOrderDTO.getDocumentNumber().equals(oc.getAssociatedOrderNumber()))
                    .collect(Collectors.toList());
            // 创建订单详情数据
            OrderDetailVo orderDetailVo = new OrderDetailVo();
            orderDetailVo.setDocumentNumber(partOrderDTO.getDocumentNumber());
            if("作业订单".equals(partOrderDTO.getOrderType())){
                Date parse=null;
                // 作业订单为“发起时间”+3天
                try {
                    parse = simpleDateFormat.parse(partOrderDTO.getOrderSubmitTime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                orderDetailVo.setExceptCompletionTime(simpleDateFormat.format(TimeUtil.getDateByDistance(parse,3)));
            }else if("服务店备货订单".equals(partOrderDTO.getOrderType())){
                List<Date> distributionCycle = new ArrayList<>();
                List<Date> timeInterval = new ArrayList<>();
                List<Date> nextTimeInterval = new ArrayList<>();
                int distributionCycleTime = 0;
                try {
                    if(partOrderDTO.getOrderSubmitTime()!=null){
                        timeInterval = TimeUtil.getTimeInterval(simpleDateFormat.parse(partOrderDTO.getOrderSubmitTime()));
                        nextTimeInterval = TimeUtil.getNextTimeInterval(simpleDateFormat.parse(partOrderDTO.getOrderSubmitTime()));
                    }
                    // 获取服务店的配货信息
                    // 目标：筛选出元素
                    List<DistributionCycle> distributionStateByConditionList = allDistributionStateByCondition.stream()
                            .filter(oc -> oc.getStoreNumber()!=null
                                    && oc.getAccountingCenter()!=null
                                    && partOrderDTO.getStoreNumber().equals(oc.getStoreNumber())
                                    && partOrderDTO.getAccountingCenter().equals(oc.getAccountingCenter()))
                            .collect(Collectors.toList());
                    if(distributionStateByConditionList!=null && distributionStateByConditionList.size()!=0){
                        distributionStateByCondition = distributionStateByConditionList.get(0);
                    }
                    if(timeInterval.size()>0 &&nextTimeInterval.size()>0){
                        if("True".equals(distributionStateByCondition.getMon())){
                            distributionCycle.add(timeInterval.get(0));
                            distributionCycle.add(nextTimeInterval.get(0));
                        }
                        if("True".equals(distributionStateByCondition.getTue())){
                            distributionCycle.add(timeInterval.get(1));
                            distributionCycle.add(nextTimeInterval.get(1));
                        }
                        if("True".equals(distributionStateByCondition.getWed())){
                            distributionCycle.add(timeInterval.get(2));
                            distributionCycle.add(nextTimeInterval.get(2));
                        }
                        if("True".equals(distributionStateByCondition.getThurs())){
                            distributionCycle.add(timeInterval.get(3));
                            distributionCycle.add(nextTimeInterval.get(3));
                        }
                        if("True".equals(distributionStateByCondition.getFri())){
                            distributionCycle.add(timeInterval.get(4));
                            distributionCycle.add(nextTimeInterval.get(4));
                        }
                        if("True".equals(distributionStateByCondition.getSta())){
                            distributionCycle.add(timeInterval.get(5));
                            distributionCycle.add(nextTimeInterval.get(5));
                        }
                        if("True".equals(distributionStateByCondition.getSun())){
                            distributionCycle.add(timeInterval.get(6));
                            distributionCycle.add(nextTimeInterval.get(6));
                        }
                    }

                    Date[] distributionCycleArray = new Date[distributionCycle.size()];
                    distributionCycleArray = distributionCycle.toArray(distributionCycleArray);
                    Arrays.sort(distributionCycleArray);
                    for (int i = 0; i < distributionCycleArray.length; i++) {
                        if((simpleDateFormat.format(distributionCycleArray[i]).compareTo(partOrderDTO.getOrderSubmitTime()))>0){
                            distributionCycleTime = i;
                        }
                    }
                    if(distributionCycleArray.length>0){
                        orderDetailVo.setExceptCompletionTime(simpleDateFormat.format(TimeUtil.getDateByDistance(distributionCycleArray[distributionCycleTime],2)));
                    }else {
                        orderDetailVo.setExceptCompletionTime(null);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            List<Map<String,String>> maps = new ArrayList<>();
            for (MissStockUpOrder missStockUpOrder:missStockUpOrderByOrders
                 ) {
                List<Map> purchaseOrderByPartDrawingNoList = allPurchaseOrderByPartDrawingNoList.stream()
                        .filter(oc -> oc.get("partDrawingNo")!=null
                                && oc.get("documentDate")!=null
                                && missStockUpOrder.getPartDrawingNo().equals(oc.get("partDrawingNo")))
                        .collect(Collectors.toList());
                Map<String, String> purchaseOrderByPartDrawingNo = new HashMap<>();
                if(purchaseOrderByPartDrawingNoList!=null && purchaseOrderByPartDrawingNoList.size()!=0){
                    purchaseOrderByPartDrawingNo = purchaseOrderByPartDrawingNoList.get(0);
                }
                if(purchaseOrderByPartDrawingNo!=null){
                    maps.add(purchaseOrderByPartDrawingNo);
                }
            }
            Map map = new HashMap();
            for(int i=0;i<maps.size()-1;i++){//控制比较轮次，一共 n-1 趟
                for(int j=0;j<maps.size()-1-i;j++){//控制两个挨着的元素进行比较
                    try {
                        if(maps.get(j).get("documentDate")!=null && maps.get(j+1).get("documentDate")!=null){
                            if(DateTimeUtil.getTimeDifference(simpleDateFormat.parse(maps.get(j).get("documentDate")),simpleDateFormat.parse(maps.get(j+1).get("documentDate")))>0 ){
                                map = maps.get(j);

                            }else {
                                map = maps.get(j+1);
                            }
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            if(map!=null && map.size()!=0){
                try {
                    orderDetailVo.setExceptGoodTime(simpleDateFormat.format(TimeUtil.getDateByDistance(simpleDateFormat.parse(map.get("documentDate").toString()),Integer.parseInt(map.get("purchaseTime").toString()))));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                orderDetailVo.setExceptGoodTime(null);
            }
            orderDetailVolist.add(orderDetailVo);
        }
        if(orderDetailVolist.size()>0){
            orderDetailDao.updateOrder(orderDetailVolist);
        }

    }

    @Override
    public Map<Object, Object> getOrderDetailByNumber(String documentNumber) {
        Map<Object,Object> map = new HashMap<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        // 时间轴
        List<Map<Object,Object>> list = new ArrayList<>();
        // 待处理
        OrderDetailDTO orderDetailDTO = orderDetailDao.getOrderDetailDTO(documentNumber);
        orderDetailDTO.setDocumentDate(orderDetailDTO.getDocumentDate().substring(0,10));
        List<PartOrder> partOrder = orderDetailDao.getPartOrder(orderDetailDTO.getDocumentNumber(),inventoryDate);
        orderDetailDTO.setPartInformationList(partOrder);
        map.put("orderDetailDTO",orderDetailDTO);
        Map<Object,Object> timeMap = new HashMap<>();
        timeMap.put("processName","待处理");
        timeMap.put("completeTime",orderDetailDTO.getOrderSubmitTime().substring(0,10));
        // 0 未完成 1 已完成 2 处理中
        timeMap.put("state","1");
        list.add(timeMap);
        // 配货单
        List<DistributionSingleDTO> distributionSingleDTOs = distributionSingleDao.getDistributionSingleDTO(documentNumber);
        for (DistributionSingleDTO distributionSingleDTO:distributionSingleDTOs
             ) {
            List<DistributionSubsidiary> distributionSubsidiary = distributionSingleDao.getDistributionSubsidiary(distributionSingleDTO.getDistributionSingleNo(),documentNumber);
            distributionSingleDTO.setDistributionDate(distributionSingleDTO.getDistributionDate().substring(0,10));
            distributionSingleDTO.setDistributionSubsidiary(distributionSubsidiary);
        }
        map.put("distributionSingleDTO",distributionSingleDTOs);

        // 缺件采购中
        List<MissStockUpOrder> missStockUpOrderByOrder = missStockUpOrderDao.getMissStockUpOrderByOrder(documentNumber);
        // 根据单据日期对缺件备货单排序
        List<MissStockUpOrder> missStockUpOrderByOrderBySort = missStockUpOrderByOrder.stream().sorted(Comparator.comparing(MissStockUpOrder::getDocumentDate,Comparator.nullsLast(String::compareTo)))
                .collect(Collectors.toList());
        if(missStockUpOrderByOrder!=null&& missStockUpOrderByOrder.size()>0){
            map.put("missStockUpOrderByOrder",missStockUpOrderByOrder);
            List<MissDealOrderDTO> missDealOrderDTOs = new ArrayList<>();
            List<PurchaseDTO> purchaseOrderVos = new ArrayList<>();
            String deliverySchedule = orderDetailDao.getDeliverySchedule(documentNumber);
            Map<Object,Object> timeMap1 = new HashMap<>();
            timeMap1.put("processName","缺件采购中");
            timeMap1.put("completeTime",missStockUpOrderByOrderBySort.get(missStockUpOrderByOrderBySort.size()-1).getDocumentDate().substring(0,10));
            timeMap1.put("state","1");
            if(!"取消".equals(missStockUpOrderByOrderBySort.get(missStockUpOrderByOrderBySort.size()-1).getSystemState())
                &&!"作废".equals(missStockUpOrderByOrderBySort.get(missStockUpOrderByOrderBySort.size()-1).getSystemState())){
                int flag = Integer.parseInt(deliverySchedule.split("/")[1])-Integer.parseInt(deliverySchedule.split("/")[0]);
                if(flag>0){
                    timeMap1.put("statement","已发"+deliverySchedule.split("/")[0]+"件，剩余"+flag+"件待采购");
                }
            }else{
                timeMap1.put("statement","订单取消");
            }

            list.add(timeMap1);
            for (MissStockUpOrder missStockUpOrder : missStockUpOrderByOrder) {
                // 获取缺件处理单
                MissDealOrderDTO missDealOrderDTO = missDealOrderDao.getMissDealOrderDTO(missStockUpOrder.getMissDealOrderId());
                if(missDealOrderDTO!=null){
                    List<MissDealOrderDetail> missDealOrderDetail = missDealOrderDao.getMissDealOrderDetail(missDealOrderDTO.getDocumentNo(),missStockUpOrder.getPartDrawingNo());
                    missDealOrderDTO.setDetail(missDealOrderDetail);
                    missDealOrderDTOs.add(missDealOrderDTO);
                }
                // 获取采购订单
                PurchaseDTO purchaseDTOByPartDrawingNo = purchaseOrderDao.getPurchaseOrderVo(missStockUpOrder.getPartDrawingNo());
                if(purchaseDTOByPartDrawingNo!=null){
                    purchaseDTOByPartDrawingNo.setId(UUIDUtils.getUUID());
                    PurchaseOrderVo purchaseOrderVoByPartDrawingNo = purchaseOrderDao.getPurchaseOrderVoByDocumentNo(purchaseDTOByPartDrawingNo.getDocumentNo());
                    List<PurchaseOrderDetail> purchaseOrderDetailByNo = purchaseOrderDao.getPurchaseOrderDetailByNo(purchaseDTOByPartDrawingNo.getDocumentNo(),missStockUpOrder.getPartDrawingNo());
                    purchaseOrderVoByPartDrawingNo.setPartInformationList(purchaseOrderDetailByNo);
                    purchaseDTOByPartDrawingNo.setPurchaseOrderVoList(purchaseOrderVoByPartDrawingNo);
                    purchaseOrderVos.add(purchaseDTOByPartDrawingNo);
                }

            }
            // 过滤重复采购订单数据
            List<PurchaseDTO> purchaseOrderVosByFilter = purchaseOrderVos.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PurchaseDTO::getDocumentNo ))),ArrayList::new));
            map.put("missDealOrderDetail",missDealOrderDTOs);
            map.put("purchaseOrderVo",purchaseOrderVosByFilter);
            // 根据单据日期对采购订单排序
            List<PurchaseDTO> purchaseOrderVosBySort = purchaseOrderVos.stream().sorted(Comparator.comparing(PurchaseDTO::getOrderTime,Comparator.nullsLast(String::compareTo)))
                    .collect(Collectors.toList());
            // 获取最新的采购订单，判断是否采购完成
            if(purchaseOrderVosBySort!=null && purchaseOrderVosBySort.size()>0 && purchaseOrderVosBySort.get(purchaseOrderVosBySort.size()-1).getCompletionDate()!=null
                    && !"".equals(purchaseOrderVosBySort.get(purchaseOrderVosBySort.size()-1).getCompletionDate())){
                Map<Object,Object> timeMap3 = new HashMap<>();
                timeMap3.put("processName","缺件已到货");
                timeMap3.put("completeTime",purchaseOrderVosBySort.get(purchaseOrderVosBySort.size()-1).getCompletionDate().substring(0,10));
                timeMap3.put("state","1");
                list.add(timeMap3);

            }else {
                Map<Object,Object> timeMap3 = new HashMap<>();
                timeMap3.put("processName","缺件已到货");
                // 判断是否有预计收货时间
                if(orderDetailDTO.getExceptGoodTime()!=null && !"".equals(orderDetailDTO.getExceptGoodTime())){
                    timeMap3.put("exceptTime",orderDetailDTO.getExceptGoodTime().substring(0,10));
                }
                timeMap3.put("state","2");
                list.add(timeMap3);
            }
        }
        // 装箱
        List<PackingListDTO> packingListDTOs = packingListDao.getPackingListDTO(orderDetailDTO.getDocumentNumber());
        map.put("packingListDTO",packingListDTOs);
        if(packingListDTOs!=null && packingListDTOs.size()>0){
            for (PackingListDTO packingListDTO:packingListDTOs
                 ) {
                packingListDTO.setLoadingDate(packingListDTO.getLoadingDate().substring(0,10));
                List<LogisticsInformation> logisticsInformation = logisticsInformationDao.getLogisticsInformation(packingListDTO.getBillsLadingNo());
                List<PackingListDetail> packingListDetail = packingListDao.getPackingListDetail(packingListDTO.getPackingListNo());
                packingListDTO.setDetail(packingListDetail);
                packingListDTO.setLogisticsInformations(logisticsInformation);
            }
            // 判断是否走了缺件采购，如果走了就是到了装箱完成
            if(missStockUpOrderByOrder!=null){
                Map<Object,Object> timeMap1 = new HashMap<>();
                timeMap1.put("processName","已装箱");
                timeMap1.put("completeTime",packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate().substring(0,10));
                // 判断是否超时 1未超时 0超时
                int isTimeOut=1;
                int time=0;
                for (PartOrder partOrder1:orderDetailDTO.getPartInformationList()
                        ) {
                    if("0".equals(partOrder1.getIsShipment())){
                        isTimeOut=0;
                    }
                    if("作业订单".equals(orderDetailDTO.getOrderType())){
                        if(partOrder1.getShipmentTime()!=null){
                            if(Integer.parseInt(partOrder1.getShipmentTime())-1>time){
                                time=Integer.parseInt(partOrder1.getShipmentTime())-1;
                            }
                        }
                    }else if("服务店备货订单".equals(orderDetailDTO.getOrderType())){
                        if(partOrder1.getShipmentTime()!=null){
                            if(Integer.parseInt(partOrder1.getShipmentTime())-2>time){
                                time=Integer.parseInt(partOrder1.getShipmentTime())-2;
                            }
                        }

                    }
                }
                if(isTimeOut==0){
                    timeMap1.put("isTimeOut",isTimeOut);
                    timeMap1.put("time",time);
                }
                timeMap1.put("state","1");
                list.add(timeMap1);
            }else {
                Map<Object,Object> timeMap1 = new HashMap<>();
                timeMap1.put("processName","已装箱");
                timeMap1.put("state","0");
                list.add(timeMap1);
            }
        }else {
            Map<Object,Object> timeMap1 = new HashMap<>();
            timeMap1.put("processName","已装箱");
            try {
                if(missStockUpOrderByOrder==null|| missStockUpOrderByOrder.size()==0){
                    if(orderDetailDTO.getOrderSubmitTime()!=null && !"".equals(orderDetailDTO.getOrderSubmitTime())){
                        timeMap1.put("exceptTime",simpleDateFormat.format(TimeUtil.getDateByDistance(simpleDateFormat.parse(orderDetailDTO.getOrderSubmitTime()),1)));
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            timeMap1.put("state","2");
            list.add(timeMap1);
        }

        // 妥投
        List<ReceiptDTO> receiptDTOs = receiptDao.getReceiptDTO(documentNumber);
        map.put("receiptDTO",receiptDTOs);
        if(receiptDTOs!=null && receiptDTOs.size()>0){
            for (ReceiptDTO receiptDTO:receiptDTOs
                 ) {
                if("待收货".equals(receiptDTO.getSystemState())){
                    try {
                        receiptDTO.setExceptgoodTime(dateFormat.format(TimeUtil.getDateByDistance(dateFormat.parse(receiptDTO.getDeliveryTime()),2)));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }else {
                    receiptDTO.setExceptgoodTime(receiptDTO.getGoodTime());
                }
                List<ReceiptScanDetail> receiptScanDetails = null;
                String IP = sysConfService.getSystemConfig("SHUODE_HOST", "100060");
                String url = IP + "/GetScanDetail";
                String postResult="";
                try {
                    Map<Object,Object> conditionMap =new HashMap<>();
                    conditionMap.put("documentNumber",receiptDTO.getDocumentNumber());
                    // 获取服务店收获单扫描详情
                    postResult = HttpClientUtil.post(url,HttpClientUtil.getParam(conditionMap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(!"".equals(postResult)){
                    JSONObject jsonObject = JSONObject.fromObject(postResult);
                    String RESULT = (String) jsonObject.get("RESULT");
                    if("T".equals(RESULT)){
                        String str = jsonObject.get("data").toString();
                        receiptScanDetails = JSON.parseArray(str,ReceiptScanDetail.class);
                    }
                }
                receiptDTO.setReceiptScanDetails(receiptScanDetails);
            }
            Map<Object,Object> timeMap2 = new HashMap<>();
            timeMap2.put("processName","已妥投");
            String end="";
            if(!"".equals(receiptDTOs.get(receiptDTOs.size()-1).getAppropriateInvestTime()) && receiptDTOs.get(receiptDTOs.size()-1).getAppropriateInvestTime()!=null
                    && !"".equals(receiptDTOs.get(receiptDTOs.size()-1).getGoodTime()) && receiptDTOs.get(receiptDTOs.size()-1).getGoodTime()!=null){
                if(DateTimeUtil.getTimeDifference(DateTimeUtil.getFormatDateFromString(receiptDTOs.get(receiptDTOs.size()-1).getAppropriateInvestTime(),"yyyy-MM-dd HH:mm:ss"),DateTimeUtil.getFormatDateFromString(receiptDTOs.get(receiptDTOs.size()-1).getGoodTime(),"yyyy-MM-dd HH:mm:ss"))>0){
                    end=receiptDTOs.get(receiptDTOs.size()-1).getAppropriateInvestTime();
                }else {
                    end=receiptDTOs.get(receiptDTOs.size()-1).getGoodTime();
                }
            }else {
                if(!"".equals(receiptDTOs.get(receiptDTOs.size()-1).getAppropriateInvestTime()) && receiptDTOs.get(receiptDTOs.size()-1).getAppropriateInvestTime()!=null){
                    end = receiptDTOs.get(receiptDTOs.size()-1).getAppropriateInvestTime();
                }
                if(!"".equals(receiptDTOs.get(receiptDTOs.size()-1).getGoodTime()) && receiptDTOs.get(receiptDTOs.size()-1).getGoodTime()!=null){
                    end = receiptDTOs.get(receiptDTOs.size()-1).getGoodTime();
                }
            }
            if(!"".equals(end)){
                List<Date> distanceDate = TimeUtil.getDistanceDate(receiptDTOs.get(receiptDTOs.size()-1).getDeliveryTime().substring(0,10)+" 00:00:00", end.substring(0,10)+" 23:59:59");
                if(distanceDate.size()-1>2){
                    timeMap2.put("isTimeOut",0);
                    timeMap2.put("time",distanceDate.size()-1);
                }else {
                    timeMap2.put("isTimeOut",1);
                }
                timeMap2.put("completeTime",end.substring(0,10));
                timeMap2.put("state","1");
            }else {
                if(packingListDTOs!=null&&packingListDTOs.size()>0&&packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate()!=null){
                    try {
                        if(missStockUpOrderByOrder==null|| missStockUpOrderByOrder.size()==0){
                            if(packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate()!=null && !"".equals(packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate())){
                                timeMap2.put("exceptTime",simpleDateFormat.format(TimeUtil.getDateByDistance(simpleDateFormat.parse(packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate()),2)));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                timeMap2.put("state","2");
            }
            list.add(timeMap2);

            if(receiptDTOs.get(receiptDTOs.size()-1).getGoodTime()!=null && !"".equals(receiptDTOs.get(receiptDTOs.size()-1).getGoodTime())){
                Map<Object,Object> timeMap3 = new HashMap<>();
                timeMap3.put("processName","已收货确认");
                timeMap3.put("completeTime",receiptDTOs.get(receiptDTOs.size()-1).getGoodTime().substring(0,10));
                timeMap3.put("state","1");
                list.add(timeMap3);
            }else {
                if(!"".equals(end)){
                    Map<Object,Object> timeMap3 = new HashMap<>();
                    timeMap3.put("processName","已收货确认");
                    timeMap3.put("state","2");
                    list.add(timeMap3);
                }else {
                    Map<Object,Object> timeMap3 = new HashMap<>();
                    timeMap3.put("processName","已收货确认");
                    timeMap3.put("state","0");
                    list.add(timeMap3);
                }
            }
        }else {
            if(packingListDTOs!=null && packingListDTOs.size()>0){
                Map<Object,Object> timeMap2 = new HashMap<>();
                timeMap2.put("processName","已妥投");
                if(packingListDTOs!=null&&packingListDTOs.size()>0&&packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate()!=null){
                    try {
                        if(missStockUpOrderByOrder==null|| missStockUpOrderByOrder.size()==0){
                            if(packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate()!=null&&!"".equals(packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate())){
                                timeMap2.put("exceptTime",simpleDateFormat.format(TimeUtil.getDateByDistance(simpleDateFormat.parse(packingListDTOs.get(packingListDTOs.size()-1).getLoadingDate()),2)));
                            }
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                timeMap2.put("state","2");
                list.add(timeMap2);
                Map<Object,Object> timeMap3 = new HashMap<>();
                timeMap3.put("processName","已收货确认");
                timeMap3.put("state","0");
                list.add(timeMap3);
            }else {
                Map<Object,Object> timeMap2 = new HashMap<>();
                timeMap2.put("processName","已妥投");
                timeMap2.put("state","0");
                list.add(timeMap2);
                Map<Object,Object> timeMap3 = new HashMap<>();
                timeMap3.put("processName","已收货确认");
                timeMap3.put("state","0");
                list.add(timeMap3);
            }

        }
        map.put("timeline",list);
        return map;
    }

    /**
     * 部件库存 在库金额折线图
     * @param startDate 开始日期 yyyy-MM-dd
     * @param endDate 结束日期 yyyy-MM-dd
     * @param aggregateType 0按日聚合；1按月聚合
     * @return 在库金额折线、需求金额折线、安全在库金额折线、3日均线、7日均线
     */
    @Override
    public LibraryAmountVO getLibraryAmount(String startDate, String endDate, String aggregateType) {
        if (StringUtils.isEmpty(startDate) && StringUtils.isEmpty(endDate)) {
            return null;
        }
        List<String> timeInterval = findDaysStr(startDate, endDate, aggregateType);

        // 在库金额
        List<EveryAmountByDateBO> inventoryAmount = getInventoryAmountLineChart(startDate, endDate, aggregateType);

        // 在库金额结果
        List<Double> inventoryAmountResult = new ArrayList<>();
        for (String date : timeInterval) {
            if (AggregateTypeConstant.MONTHLY_AGGREGATION.equals(aggregateType)) {
                date = date.substring(0, 7);
            }
            AtomicReference<Double> amount = new AtomicReference<>(0.0);
            String finalSubstringDate = date;
            inventoryAmount.stream().filter(entity -> entity.getInventoryDate().equals(finalSubstringDate)).forEach(entity -> amount.set(entity.getAmount().setScale(2, BigDecimal.ROUND_DOWN).doubleValue()));
            inventoryAmountResult.add(amount.get());
        }
        // 需求金额折线
        List<Double> demandAmountResult = getDemandAmountResult(startDate, endDate, aggregateType, timeInterval);

        // 安全在库金额折线
        List<Double> safeDepositAmountResult = getSafeDepositAmount(startDate, endDate, aggregateType, timeInterval);

        // 3日均线
        List<Double> threeDayAverageResult = getThreeDayAmount(startDate, endDate, aggregateType, timeInterval);

        // 7日均线
        List<Double> sevenDayAverageResult = getSevenDayAmount(startDate, endDate, aggregateType, timeInterval);

        LibraryAmountVO libraryAmountVO = LibraryAmountVO.builder()
                .shijian(timeInterval)
                .zaikuquxian(inventoryAmountResult)
                .zxuqiuquxian(demandAmountResult)
                .anquanquxian(safeDepositAmountResult)
                .treeday(threeDayAverageResult)
                .sivenday(sevenDayAverageResult)
                .build();
        return libraryAmountVO;
    }

    /**
     * 七日均线
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param aggregateType 0按日聚合；1按月聚合
     * @param timeInterval 时间
     * @return 七日均线
     */
    private List<Double> getSevenDayAmount(String startDate, String endDate, String aggregateType, List<String> timeInterval) {
        List<Double> sevenDayAmountResult = new ArrayList<>();
        int size = timeInterval.size();
        // 6天前的时间
        LocalDate startLocalDate = LocalDate.parse(startDate).plusDays(-6);
        if (AggregateTypeConstant.DAILY_AGGREGATION.equals(aggregateType)) {
            size += 6;
            // 数据库中统计的数据
            List<DateAmountBO> demandAmountList = dailyDemandAmountDao.selectTotalAmountByDateAndAggregateType(startLocalDate.toString(), endDate, aggregateType);
            // 存在前六天的时间
            List<String> newTimeInterval = new ArrayList<>(size);
            // 存在前六天数据
            List<DateAmountBO> totalEveryAmount = new ArrayList<>(size);
            for (int i = 0; i < 6; i++) {
                newTimeInterval.add(startLocalDate.plusDays(i).toString());
            }
            newTimeInterval.addAll(timeInterval);
            // 填充数据及补零
            for (String time : newTimeInterval) {
                DateAmountBO dateAmountBO = demandAmountList.stream().filter(bo -> bo.getDate().equals(time)).findFirst().orElse(null);
                if (dateAmountBO == null) {
                    DateAmountBO bo = DateAmountBO.builder().date(time).amount(BigDecimal.valueOf(0)).build();
                    totalEveryAmount.add(bo);
                    continue;
                }
                totalEveryAmount.add(dateAmountBO);
            }
            // 计算7日均线
            sevenDayAmountResult = slidingWindowAverageAmount(totalEveryAmount, 7);
        }
        /*if (AggregateTypeConstant.MONTHLY_AGGREGATION.equals(aggregateType)) {
            // 按月聚合
            endDate = timeInterval.get(size - 1);
            // 数据库中统计的数据
            List<DateAmountBO> demandAmountList = dailyDemandAmountDao.selectTotalAmountByDateAndAggregateType(startLocalDate.toString(), endDate, aggregateType);
            // 查询范围内每个月1号及前6天的数据
            size *= 7;
            List<String> newTimeInterval = new ArrayList<>(size);
            // totalEveryAmount格式：2021-12-25,……,2021-12-31,2022-01-01,2022-01-25,……,2022-01-31,2022-02-01
            List<DateAmountBO> totalEveryAmount = new ArrayList<>(size);
            for (String monthFirstDay : timeInterval) {
                // monthFirstDay：2022-01-01/2022-02-01……
                LocalDate monthFirstLocalDate = LocalDate.parse(monthFirstDay);
                // 前6天日期
                for (int i = -6; i < 0; i++) {
                    LocalDate temp = monthFirstLocalDate.plusDays(i);
                    newTimeInterval.add(temp.toString());
                }
                newTimeInterval.add(monthFirstLocalDate.toString());
            }
            // 填充数据及补零
            for (String time : newTimeInterval) {
                DateAmountBO dateAmountBO = demandAmountList.stream().filter(bo -> bo.getDate().equals(time)).findFirst().orElse(null);
                if (dateAmountBO == null) {
                    DateAmountBO bo = DateAmountBO.builder().date(time).amount(BigDecimal.valueOf(0)).build();
                    totalEveryAmount.add(bo);
                    continue;
                }
                totalEveryAmount.add(dateAmountBO);
            }
            sevenDayAmountResult = skipWindowAverageAmount(totalEveryAmount, 7);
        }*/
        return sevenDayAmountResult;
    }

    /**
     * 三日均线
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param aggregateType 0按日聚合；1按月聚合
     * @param timeInterval 时间
     * @return 三日均线
     */
    private List<Double> getThreeDayAmount(String startDate, String endDate, String aggregateType, List<String> timeInterval) {
        List<Double> threeDayAmountResult = new ArrayList<>();
        int size = timeInterval.size();
        // 2天前的时间
        LocalDate startLocalDate = LocalDate.parse(startDate).plusDays(-2);
        if (AggregateTypeConstant.DAILY_AGGREGATION.equals(aggregateType)) {
            size += 2;
            // 数据库中统计的数据
            List<DateAmountBO> demandAmountList = dailyDemandAmountDao.selectTotalAmountByDateAndAggregateType(startLocalDate.toString(), endDate, aggregateType);
            // 存在前两天的时间
            List<String> newTimeInterval = new ArrayList<>(size);
            // 存在前两天数据
            List<DateAmountBO> totalEveryAmount = new ArrayList<>(size);
            for (int i = 0; i < 2; i++) {
                newTimeInterval.add(startLocalDate.plusDays(i).toString());
            }
            newTimeInterval.addAll(timeInterval);
            // 填充数据及补零
            for (String time : newTimeInterval) {
                DateAmountBO dateAmountBO = demandAmountList.stream().filter(bo -> bo.getDate().equals(time)).findFirst().orElse(null);
                if (dateAmountBO == null) {
                    DateAmountBO bo = DateAmountBO.builder().date(time).amount(BigDecimal.valueOf(0)).build();
                    totalEveryAmount.add(bo);
                    continue;
                }
                totalEveryAmount.add(dateAmountBO);
            }
            // 计算3日均线
            threeDayAmountResult = slidingWindowAverageAmount(totalEveryAmount, 3);
        }
        /*if (AggregateTypeConstant.MONTHLY_AGGREGATION.equals(aggregateType)) {
            // 按月聚合
            endDate = timeInterval.get(size - 1);
            // 数据库中统计的数据
            List<DateAmountBO> demandAmountList = dailyDemandAmountDao.selectTotalAmountByDateAndAggregateType(startLocalDate.toString(), endDate, aggregateType);
            // 查询范围内每个月1号及前2天的数据
            size *= 3;
            List<String> newTimeInterval = new ArrayList<>(size);
            // totalEveryAmount格式：2021-12-30,2021-12-31,2022-01-01,2022-01-30,2022-01-31,2022-02-01
            List<DateAmountBO> totalEveryAmount = new ArrayList<>(size);
            for (String monthFirstDay : timeInterval) {
                // monthFirstDay：2022-01-01/2022-02-01……
                LocalDate monthFirstLocalDate = LocalDate.parse(monthFirstDay);
                // 前2天日期
                for (int i = -2; i < 0; i++) {
                    LocalDate temp = monthFirstLocalDate.plusDays(i);
                    newTimeInterval.add(temp.toString());
                }
                newTimeInterval.add(monthFirstLocalDate.toString());
            }
            // 填充数据及补零
            for (String time : newTimeInterval) {
                DateAmountBO dateAmountBO = demandAmountList.stream().filter(bo -> bo.getDate().equals(time)).findFirst().orElse(null);
                if (dateAmountBO == null) {
                    DateAmountBO bo = DateAmountBO.builder().date(time).amount(BigDecimal.valueOf(0)).build();
                    totalEveryAmount.add(bo);
                    continue;
                }
                totalEveryAmount.add(dateAmountBO);
            }
            threeDayAmountResult = skipWindowAverageAmount(totalEveryAmount, 3);
        }*/
        return threeDayAmountResult;
    }

    /**
     * 求跳跃窗口平均值（按月聚合3日均线、7日均线）
     * @param list 数据
     * @param windowSize 窗口大小
     * @return n日均值
     */
    private List<Double> skipWindowAverageAmount(List<DateAmountBO> list, int windowSize) {
        List<Double> result = new ArrayList<>();
        BigDecimal zero = new BigDecimal(0);
        BigDecimal sum = zero;
        List<BigDecimal> queue = new ArrayList<>();
        for (DateAmountBO dateAmountBO : list) {
            // 新值加入队列
            BigDecimal amount = dateAmountBO.getAmount();
            queue.add(amount);
            sum = sum.add(amount);
            if (queue.size() == windowSize) {
                // 窗口已满，计算一次平均值
                result.add(sum.divide(BigDecimal.valueOf(windowSize), BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
                // 和归零，队列清空
                sum = zero;
                queue.clear();
            }
        }
        return result;
    }

    /**
     * 求滑动窗口平均值（按日聚合3日均线、7日均线）
     * @param list 操作数组
     * @param windowSize 窗口大小
     * @return n日均值
     */
    private List<Double> slidingWindowAverageAmount(List<DateAmountBO> list, int windowSize) {
        List<Double> result = new ArrayList<>();
        LinkedList<BigDecimal> queue = new LinkedList<>();
        BigDecimal sum = new BigDecimal(0);
        for (DateAmountBO dateAmountBO : list) {
            if (queue.size() == windowSize) {
                // 窗口已满，移除第一个，并在sum上减去该值
                sum = sum.add(queue.remove().multiply(BigDecimal.valueOf(-1)));
            }
            // 新值加入队列
            BigDecimal amount = dateAmountBO.getAmount();
            queue.add(amount);
            sum = sum.add(amount);
            result.add(sum.divide(BigDecimal.valueOf(windowSize), BigDecimal.ROUND_DOWN).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        }
        // 将 2天前或7天前 的多余数据删掉
        result = result.subList(windowSize - 1, result.size());
        return result;
    }

    /**
     * 安全在库曲线
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param aggregateType 0按日聚合；1按月聚合
     * @return
     */
    private List<Double> getSafeDepositAmount(String startDate, String endDate, String aggregateType, List<String> timeInterval) {
        List<Double> safeDepositAmountResult = new ArrayList<>(timeInterval.size());
        if (AggregateTypeConstant.MONTHLY_AGGREGATION.equals(aggregateType)) {
            startDate = TimeUtil.getFirstDayOfMonth(startDate);
            endDate = TimeUtil.getLastDayOfMonth(endDate);
        }
        List<DateAmountBO> dateAmountBOList = dailySafeDepositAmountDao.selectTotalAmountByDateAndAggregateType(startDate, endDate, aggregateType);
        for (String time : timeInterval) {
            DateAmountBO dateAmountBO = dateAmountBOList.stream().filter(bo -> bo.getDate().equals(time)).findFirst().orElse(null);
            if (dateAmountBO == null) {
                // 日期无对应数据库数据补 0
                safeDepositAmountResult.add(0.0);
                continue;
            }
            safeDepositAmountResult.add(dateAmountBO.getAmount().setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        }
        return safeDepositAmountResult;
    }

    /**
     * 需求金额曲线
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param aggregateType 0按日聚合；1按月聚合
     * @return
     */
    private List<Double> getDemandAmountResult(String startDate, String endDate, String aggregateType, List<String> timeInterval) {
        int size = timeInterval.size();
        List<Double> demandAmountResult = new ArrayList<>(size);
        // 1.时间范围
        if (AggregateTypeConstant.MONTHLY_AGGREGATION.equals(aggregateType)) {
            startDate = TimeUtil.getFirstDayOfMonth(startDate);
            // 该月最后一天
            endDate = TimeUtil.getLastDayOfMonth(endDate);
        }
        List<DateAmountBO> demandAmountList = dailyDemandAmountDao.selectTotalAmountByDateAndAggregateType(startDate, endDate, aggregateType);

        // 2.对应时间赋值
        for (String time : timeInterval) {
            DateAmountBO dateAmountBO = demandAmountList.stream().filter(bo -> bo.getDate().equals(time)).findFirst().orElse(null);
            if (dateAmountBO == null) {
                // 日期无对应数据库数据补 0
                demandAmountResult.add(0.0);
                continue;
            }
            demandAmountResult.add(dateAmountBO.getAmount().setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
        }
        demandAmountList = null;

        // 3.如果今天被包括在时间范围内（数据库无今天的统计数据）
        LocalDate today = LocalDate.now();
        if (today.isBefore(LocalDate.parse(endDate)) && today.isAfter(LocalDate.parse(startDate))) {
            BigDecimal todayAmount = dailyDemandAmountService.calculateDemandAmountByDate(today.toString());
            int index = 0;
            if (AggregateTypeConstant.MONTHLY_AGGREGATION.equals(aggregateType)) {
                // 将今天所在月的需求金额加上数据库对应月数据
                for (; index < size; index++) {
                    String time = timeInterval.get(index);
                    if (0 == Period.between(today, LocalDate.parse(time)).getMonths()) {
                        break;
                    }
                }
                demandAmountResult.set(index, todayAmount.add(BigDecimal.valueOf(demandAmountResult.get(index))).setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            } else if (AggregateTypeConstant.DAILY_AGGREGATION.equals(aggregateType)) {
                // 将对应日期改为今天的数据
                for (; index < size; index++) {
                    String time = timeInterval.get(index);
                    if (today.isEqual(LocalDate.parse(time))) {
                        break;
                    }
                }
                demandAmountResult.set(index, todayAmount.setScale(2, BigDecimal.ROUND_DOWN).doubleValue());
            }
        }

        return demandAmountResult;
    }

    /**
     * 在库金额曲线
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param aggregateType 0按日聚合；1按月聚合
     * @return EveryAmountByDateBO
     */
    private List<EveryAmountByDateBO> getInventoryAmountLineChart(String startDate, String endDate, String aggregateType) {
        List<EveryAmountByDateBO> everyAmountByDate = new ArrayList<>();
        try {
            everyAmountByDate = inventoryWarningDao.getEveryAmountByDate(startDate, LocalDate.parse(endDate).plusDays(1).toString(), aggregateType, null);
        } catch (Exception ignored) {
        }
        return everyAmountByDate;
    }

    /**
     * 获取两个时间内的每一天时间 yyyy-MM-dd
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    private List<String> findDaysStr(String startDate, String endDate, String aggregateType) {
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);
        List<String> result = null;
        if (AggregateTypeConstant.DAILY_AGGREGATION.equals(aggregateType)) {
            // 按日聚合
            long betweenDays = startLocalDate.until(endLocalDate, ChronoUnit.DAYS) + 1;
            result = new ArrayList<>();
            for (long i = 0; i < betweenDays; i++) {
                LocalDate plusDays = startLocalDate.plusDays(i);
                result.add(plusDays.toString());
            }
        }
        if (AggregateTypeConstant.MONTHLY_AGGREGATION.equals(aggregateType)) {
            // 按月聚合
            LocalDate startFirstDayOfMonth = startLocalDate.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate endFirstDayOfMonth = endLocalDate.with(TemporalAdjusters.firstDayOfMonth());
            long betweenMonths = startFirstDayOfMonth.until(endFirstDayOfMonth, ChronoUnit.MONTHS) + 1;
            result = new ArrayList<>();
            for (long i = 0; i < betweenMonths; i++) {
                LocalDate plusMonths = startFirstDayOfMonth.plusMonths(i);
                result.add(plusMonths.toString());
            }
        }
        return result;
    }

    @Override
    public LibraryCountVO getCount(String startDate, String endDate,String aggregateType) {
        List<String> shijian = findDaysStr(startDate, endDate, aggregateType);

        // 在库数量曲线
        List<EveryCountByDateBO> zaikuquxian = getzaikuquxianCount(startDate, endDate, aggregateType);

        List<Long> zaikuquxianResult = new ArrayList<>();

        for (String date : shijian) {
            if ("1".equals(aggregateType)) {
                date = date.substring(0, 7);
            }
            AtomicReference<Long> count = new AtomicReference<>(0L);
            String finalSubstringDate = date;
            zaikuquxian.stream().filter(entity -> entity.getInventoryDate().equals(finalSubstringDate)).forEach(entity -> count.set(entity.getCount()));
            zaikuquxianResult.add(count.get());
        }

        // 需求数量曲线
        List<Long> zxuqiuquxian = getzxuqiuquxianCount(startDate, endDate, aggregateType);

        // 安全在库数量曲线
        List<Long> anquanquxian = getanquanquxianCount(startDate, endDate, aggregateType);
        //List<Long> anquanquxian = new ArrayList<>();

        //List<Long> treeday = getThreeDay(startDate, endDate, aggregateType, shijian, zxuqiuquxian);
        List<Long> treeday = getThreeDayCount(startDate, endDate, aggregateType, shijian, zxuqiuquxian);

        //List<Long> sivenday = getsevenDay(startDate, endDate, aggregateType, shijian, zxuqiuquxian);
        List<Long> sivenday = getsevenDayCount(startDate, endDate, aggregateType, shijian, zxuqiuquxian);

        LibraryCountVO libraryCountVO = LibraryCountVO.builder()
                .shijian(shijian)
                .zaikuquxian(zaikuquxianResult)
                .zxuqiuquxian(zxuqiuquxian)
                .anquanquxian(anquanquxian)
                .treeday(treeday)
                .sivenday(sivenday)
                .build();
        return libraryCountVO;
    }


    private List<Long> getsevenDayCount(String startDate, String endDate, String aggregateType, List<String> shijian, List<Long> zxuqiuquxian) {
        List<Long> result = new ArrayList<>();
        if ("0".equals(aggregateType)) {
            // 对每日在库金额为空的数据按开始时间、结束时间补全
            // 6天前的时间
            LocalDate startLocalDate = LocalDate.parse(shijian.get(0)).plusDays(-6);
            // 截至时间
            LocalDate endLocalDate = LocalDate.parse(endDate).plusDays(1);
            // 获取需求曲线
            List<Long> sevenDaysNeedLine = getEveryzxuqiuquxianCount(startLocalDate.toString(), endLocalDate.toString());
            int i = 0;
            List<EveryCountByDateBO> totalEveryCount = new ArrayList<>();
            while (!startLocalDate.isEqual(LocalDate.parse(startDate))) {
                EveryCountByDateBO bo = new EveryCountByDateBO();
                bo.setInventoryDate(startLocalDate.toString());
                bo.setCount(sevenDaysNeedLine.get(i));
                totalEveryCount.add(bo);
                startLocalDate = startLocalDate.plusDays(1);
                i++;
            }
            i = 0;
            while (!startLocalDate.isEqual(endLocalDate)) {
                EveryCountByDateBO bo = new EveryCountByDateBO();
                bo.setInventoryDate(startLocalDate.toString());
                bo.setCount(zxuqiuquxian.get(i));
                totalEveryCount.add(bo);
                startLocalDate = startLocalDate.plusDays(1);
                i++;
            }
            result = slidingWindowAverageCount(totalEveryCount, 7);
        }
        /*if ("1".equals(aggregateType)) {
            // 按月聚合
            List<EveryCountByDateBO> totalEveryCount = new ArrayList<>();
            // 查询范围内每个月1号及前6天的数据
            for (String monthFirstDay : shijian) {
                // monthFirstDay：2022-01-01/2022-02-01……
                LocalDate monthFirstLocalDate = LocalDate.parse(monthFirstDay);
                // 前两天的日期
                LocalDate lastTwoDay = monthFirstLocalDate.plusDays(-2);
                // 后一天的日期
                LocalDate afterOneDay = monthFirstLocalDate.plusDays(1);
                // 查询范围 2022-01-25 00:00:00 / 2022-02-02 00:00:00 查出7天数据
                List<EveryCountByDateBO> everyCountByDate = new ArrayList<>();
                List<Long> everyzxuqiuquxian = getEveryzxuqiuquxianCount(lastTwoDay.toString(), afterOneDay.toString());
                int i = 0;
                while (!lastTwoDay.isEqual(afterOneDay)) {
                    EveryCountByDateBO bo = new EveryCountByDateBO();
                    bo.setInventoryDate(lastTwoDay.toString());
                    bo.setCount(everyzxuqiuquxian.get(i));
                    everyCountByDate.add(bo);
                    lastTwoDay = lastTwoDay.plusDays(1);
                    i++;
                }
                // totalEveryAmount格式：2021-12-25,……,2021-12-31,2022-01-01,2022-01-25,……,2022-01-31,2022-02-01
                totalEveryCount.addAll(everyCountByDate);
            }
            result = skipWindowAverageCount(totalEveryCount, 7);
        }*/
        return result;
    }

    private List<Long> getThreeDayCount(String startDate, String endDate, String aggregateType, List<String> shijian, List<Long> zaikuquxian) {
        List<Long> result = new ArrayList<>();
        if ("0".equals(aggregateType)) {
            // 对每日在库金额为空的数据按开始时间、结束时间补全
            // 2天前的时间
            LocalDate startLocalDate = LocalDate.parse(shijian.get(0)).plusDays(-2);
            // 截至时间
            LocalDate endLocalDate = LocalDate.parse(endDate).plusDays(1);
            // 获取前两天需求曲线
            List<Long> twoDaysNeedLine = getEveryzxuqiuquxianCount(startLocalDate.toString(), startDate);
            int i = 0;
            List<EveryCountByDateBO> totalEveryCount = new ArrayList<>();
            while (!startLocalDate.isEqual(LocalDate.parse(startDate))) {
                EveryCountByDateBO bo = new EveryCountByDateBO();
                bo.setInventoryDate(startLocalDate.toString());
                bo.setCount(twoDaysNeedLine.get(i));
                totalEveryCount.add(bo);
                startLocalDate = startLocalDate.plusDays(1);
                i++;
            }
            i = 0;
            while (!startLocalDate.isEqual(endLocalDate)) {
                EveryCountByDateBO bo = new EveryCountByDateBO();
                bo.setInventoryDate(startLocalDate.toString());
                bo.setCount(zaikuquxian.get(i));
                totalEveryCount.add(bo);
                startLocalDate = startLocalDate.plusDays(1);
                i++;
            }
            result = slidingWindowAverageCount(totalEveryCount, 3);
        }
        /*if ("1".equals(aggregateType)) {
            // 按月聚合
            List<EveryCountByDateBO> totalEveryCount = new ArrayList<>();
            // 查询范围内每个月1号及前2天的数据
            for (String monthFirstDay : shijian) {
                // monthFirstDay：2022-01-01/2022-02-01……
                LocalDate monthFirstLocalDate = LocalDate.parse(monthFirstDay);
                // 前两天的日期
                LocalDate lastTwoDay = monthFirstLocalDate.plusDays(-2);
                // 后一天的日期
                LocalDate afterOneDay = monthFirstLocalDate.plusDays(1);
                // 查询范围 2022-01-30 00:00:00 / 2022-02-02 00:00:00 查出3天数据
                List<EveryCountByDateBO> everyAmountByDate = new ArrayList<>();
                List<Long> everyzxuqiuquxian = getEveryzxuqiuquxianCount(lastTwoDay.toString(), afterOneDay.toString());
                int i = 0;
                while (!lastTwoDay.isEqual(afterOneDay)) {
                    EveryCountByDateBO bo = new EveryCountByDateBO();
                    bo.setInventoryDate(lastTwoDay.toString());
                    bo.setCount(everyzxuqiuquxian.get(i));
                    everyAmountByDate.add(bo);
                    lastTwoDay = lastTwoDay.plusDays(1);
                    i++;
                }
                // totalEveryAmount格式：2021-12-30,2021-12-31,2022-01-01,2022-01-30,2022-01-31,2022-02-01
                totalEveryCount.addAll(everyAmountByDate);
            }
            result = skipWindowAverageCount(totalEveryCount, 3);
        }*/
        System.out.println(result);
        return result;
    }

    private List<Long> skipWindowAverageCount(List<EveryCountByDateBO> list, int windowSize) {
        List<Long> result = new ArrayList<>();
        long sum = 0L;
        List<Long> queue = new ArrayList<>();
        for (EveryCountByDateBO everyCountByDateBO : list) {
            if (queue.size() == windowSize) {
                // 窗口已满，计算一次平均值
                result.add(sum/windowSize);
                // 和归零，队列清空
                sum = 0L;
                queue.clear();
            }
            // 新值加入队列
            long count = everyCountByDateBO.getCount();
            queue.add(count);
            sum = sum + count;
        }
        return result;
    }

    private List<Long> slidingWindowAverageCount(List<EveryCountByDateBO> list, int windowSize) {
        List<Long> result = new ArrayList<>();
        long sum = 0L;
        LinkedList<Long> queue = new LinkedList<>();
        for (EveryCountByDateBO everyAmountByDateBO : list) {
            if (queue.size() == windowSize) {
                // 窗口已满，移除第一个，并在sum上减去该值
                sum = sum - queue.remove();
            }
            // 新值加入队列
            long count = everyAmountByDateBO.getCount();
            queue.add(count);
            sum = sum + count;
            result.add(sum);
        }
        // 将 2天前或7天前 的多余数据删掉
        result = result.subList(windowSize - 1, result.size());
        return result;
    }

    private List<Long> getanquanquxianCount(String startDate, String endDate, String aggregateType) {
        List<String> monthBetween = TimeUtil.getMonthBetween(startDate.substring(0, 7), endDate.substring(0, 7));
        List<Long> longs = new ArrayList<>();
        if("1".equals(aggregateType)){
            for (String s : monthBetween) {
                List<String> startAndEnd = TimeUtil.getStartAndEnd(s + "-01");
                String startTime = startAndEnd.get(0);
                String endTime = startAndEnd.get(1);
                List<Long> MonthDoubles = getEveryAnquanquxianCount(startTime,endTime);
                long sum = 0L;
                for (Long monthDouble : MonthDoubles) {
                    sum = sum + monthDouble;
                }
                longs.add(sum);
            }
        }else {
            longs = getEveryAnquanquxianCount(startDate,endDate);
        }
        return longs;
    }

    private List<Long> getEveryAnquanquxianCount(String startDate, String endDate) {
        List<Date> distanceDate =TimeUtil.getDistanceDate(startDate+" 00:00:00", endDate+" 23:59:59");
        List<Long> longs = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 数量
        List<SecurityBO> boList = inventoryWarningDao.getAllSecurityBO(startDate, endDate);
        distanceDate.forEach(date -> {
            String format = simpleDateFormat.format(date);
            // 一天的单价
            //List<PartDrawingCostPriceRangeTimeBO> partDrawingCostPriceRangeTimeBOList = partDrawingStockDao.selectPartDrawingCostPriceRangeTime(format);
            //if(partDrawingCostPriceRangeTimeBOList!=null&&partDrawingCostPriceRangeTimeBOList.size()>0){
                long sum = 0L;
                for (SecurityBO bo : boList) {
                    //for (PartDrawingCostPriceRangeTimeBO partDrawingCostPriceRangeTimeBO : partDrawingCostPriceRangeTimeBOList) {
                        if(format.equals(bo.getInventoryDate()) && bo.getCount()!=null){
                            sum = sum + bo.getCount();
                        }
                    //}
                }
                longs.add(sum);
            //}else {
                //longs.add(Double.parseDouble("0"));
            //}
        });

        return longs;
    }

    /**
     * 需求数量曲线
     * @param startDate
     * @param endDate
     * @param aggregateType
     * @return
     */
    private List<Long> getzxuqiuquxianCount(String startDate, String endDate, String aggregateType) {
        List<String> monthBetween = TimeUtil.getMonthBetween(startDate.substring(0, 7), endDate.substring(0, 7));
        List<Long> longs = new ArrayList<>();
        if("1".equals(aggregateType)){
            for (String s : monthBetween) {
                List<String> startAndEnd = TimeUtil.getStartAndEnd(s + "-01");
                String startTime = startAndEnd.get(0);
                String endTime = startAndEnd.get(1);
                List<Long> MonthLong = getEveryzxuqiuquxianCount(startTime,endTime);
                long sum = 0L;
                for (Long monthDouble : MonthLong) {
                    sum = sum + monthDouble;
                }
                longs.add(sum);
            }
        }else {
            longs = getEveryzxuqiuquxianCount(startDate,endDate);
        }
        return longs;
    }

    private List<Long> getEveryzxuqiuquxianCount(String startDate, String endDate) {
        List<Date> distanceDate =TimeUtil.getDistanceDate(startDate+" 00:00:00", endDate+" 23:59:59");
        List<Long> longs = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        // 数量
        List<PartDrawingNoNeedNumberOrderStartTimeBO> boList = orderDetailDao.selectPartDrawingNoNeedNumberOrderStartTimeByDate(startDate, endDate);
        distanceDate.forEach(date -> {
            String format = simpleDateFormat.format(date);
            // 一天的单价
            //List<PartDrawingCostPriceRangeTimeBO> partDrawingCostPriceRangeTimeBOList = partDrawingStockDao.selectPartDrawingCostPriceRangeTime(format);
            //if(partDrawingCostPriceRangeTimeBOList!=null&&partDrawingCostPriceRangeTimeBOList.size()>0){
                long sum = 0L;
                for (PartDrawingNoNeedNumberOrderStartTimeBO bo : boList) {
                    //for (PartDrawingCostPriceRangeTimeBO partDrawingCostPriceRangeTimeBO : partDrawingCostPriceRangeTimeBOList) {
                        if( format.equals(bo.getOrderStartTime()) && bo.getNeedNumber()!=null) {
                            sum = sum + bo.getNeedNumber();
                        }
                    //}
                }
            longs.add(sum);
            //}else {
                //doubles.add(Double.parseDouble("0"));
            //}
        });

        return longs;
    }

    private List<EveryCountByDateBO> getzaikuquxianCount(String startDate, String endDate, String aggregateType) {
        List<EveryCountByDateBO> everyCountByDateBOList = new ArrayList<>();
        try {
            everyCountByDateBOList = inventoryWarningDao.getEveryCountInStockByDate(startDate, LocalDate.parse(endDate).plusDays(1).toString(), aggregateType);
        } catch (Exception ignored) {
        }
        return everyCountByDateBOList;
    }

    @Override
    public List<DayAmountVo> getDayAmount(DayAmountDTO dayAmountDTO) {
        return inventoryWarningDao.getDayAmount(dayAmountDTO);
    }

    @Override
    public List<DayAmountVo> getDayCount(DayAmountDTO dayAmountDTO) {
        return inventoryWarningDao.getDayCount(dayAmountDTO);
    }

    @Override
    public List<ReceiptVo> getReceiptList(ReceiptConditionDTO receiptConditionDTO) {
        return receiptDao.getReceiptList(receiptConditionDTO);
    }

    @Override
    public AbnormalMonitoringVo getAbnormalMonitoring(String startDate, String endDate) {
        // 获取实时的作业订单数
        int realOrderCountByDate = orderDetailDao.getRealOrderCount();
        // 获取实时的配货订单数
        int distributionOrderCount = distributionSingleDao.distributionOrderCount();
        // 获取实时的缺件单数
        int realMissOrder = missStockUpOrderDao.realMissOrder();
        // 获取实时的装箱单数量
        int packingListNumber = packingListDao.getPackingListCount();
        // 获取实时的收货单数量
        int realReceiptOrder = receiptDao.realReceiptOrder();
        // 获取实时的妥投单数量
        int realVoteNumber = receiptDao.realVoteNumber();
        // 获取超时的作业订单数
        int workOrderTimeOut = orderDetailDao.getTimeOutOrderCountByDate();
        // 获取超时的作业订单行数
        int workOrderTimeOutLine = orderDetailDao.getWorkOrderTimeOutLine();
        // 获取超时的缺件订单数
        int missOrderTimeOut = purchaseOrderDao.getTimeOutOrder();
        // 获取超时的妥投订单数
        int voteTimeOut = receiptDao.getTimeOutReceipt();
        // 获取超时的妥投订单行数
        int voteTimeOutLine = receiptDao.getVoteTimeOutLine();
        // 根据时间段获取配货单总数量
        int distributionNumber = distributionSingleDao.getAllCount(startDate,endDate);
        // 根据时间段获取生成了缺件处理单的装箱单的总数量
        int misCount = packingListDao.getAllMisCount(startDate,endDate);
        // 根据时间段获取没有生成了缺件处理单的装箱单的总数量
        int noMisCount = packingListDao.getNoMisCount(startDate,endDate);
        // 根据时间段获取收货订单数
        int receiptNumber = receiptDao.getReceiptListCountByDate(startDate,endDate);
        // 根据时间段获取妥投订单数
        int voteNumber = receiptDao.getVoteCountByDate(startDate,endDate);
        // 根据时间段获取配货单到作业订单的总时长
        int workOrderAverageTime = distributionSingleDao.getAllTime(startDate,endDate);
        // 根据时间段获取未走缺件处理单的配货单到装箱单的总时长
        int distributionAverageTime = packingListDao.getAllTime(startDate,endDate);
        // 根据时间段获取生成了缺件处理单的配货单到装箱单的总时长
        int missOrderAverageTime = packingListDao.getAllMisTime(startDate,endDate);
        // 装箱单到收货单的总时长
        int packingListAverageTime = receiptDao.getAllTime(startDate,endDate);
        // 收货单到妥投订单的总时长
        int receiptAverageTime = receiptDao.getAllVoteTime(startDate,endDate);
        AbnormalMonitoringVo abnormalMonitoringVo = new AbnormalMonitoringVo();
        abnormalMonitoringVo.setWorkOrderTimeOutLine(workOrderTimeOutLine+"");
        abnormalMonitoringVo.setVoteTimeOutLine(voteTimeOutLine+"");
        abnormalMonitoringVo.setWorkOrderNumber(realOrderCountByDate+"");
        abnormalMonitoringVo.setWorkOrderTimeOut(workOrderTimeOut+"");
        if(distributionNumber!=0){
            if(workOrderAverageTime/distributionNumber<=24){
                abnormalMonitoringVo.setWorkOrderAverageTime(TimeUtil.getDouble(workOrderAverageTime,distributionNumber)+"");
            }else {
                abnormalMonitoringVo.setWorkOrderAverageTime(workOrderAverageTime/distributionNumber+"");
            }

        }
        abnormalMonitoringVo.setDistributionNumber(distributionOrderCount+"");
        if(noMisCount!=0){
            if(distributionAverageTime/noMisCount<=24){
                abnormalMonitoringVo.setDistributionAverageTime(TimeUtil.getDouble(distributionAverageTime,noMisCount)+"");
            }else {
                abnormalMonitoringVo.setDistributionAverageTime(distributionAverageTime/noMisCount+"");
            }

        }
        abnormalMonitoringVo.setPackingListNumber(packingListNumber+"");
        abnormalMonitoringVo.setMissOrderNumber(realMissOrder+"");
        abnormalMonitoringVo.setMissOrderTimeOut(missOrderTimeOut+"");
        if(misCount!=0){
            if(missOrderAverageTime/misCount<=24){
                abnormalMonitoringVo.setMissOrderAverageTime(TimeUtil.getDouble(missOrderAverageTime,misCount)+"");
            }else {
                abnormalMonitoringVo.setMissOrderAverageTime(missOrderAverageTime/misCount+"");
            }

        }
        if(receiptNumber!=0){
            if(packingListAverageTime/receiptNumber<=24){
                abnormalMonitoringVo.setPackingListAverageTime(TimeUtil.getDouble(packingListAverageTime,receiptNumber)+"");
            }else {
                abnormalMonitoringVo.setPackingListAverageTime(packingListAverageTime/receiptNumber+"");
            }
        }
        abnormalMonitoringVo.setReceiptNumber(realReceiptOrder+"");
        if(voteNumber!=0){
            if(receiptAverageTime/voteNumber<=24){
                abnormalMonitoringVo.setReceiptAverageTime(TimeUtil.getDouble(receiptAverageTime,voteNumber)+"");
            }else {
                abnormalMonitoringVo.setReceiptAverageTime(receiptAverageTime/voteNumber+"");
            }
        }
        abnormalMonitoringVo.setVoteNumber(realVoteNumber+"");
        abnormalMonitoringVo.setVoteTimeOut(voteTimeOut+"");
        return abnormalMonitoringVo;
    }

    @Override
    public void updateOrderDetail() {
        orderDetailDao.updateOrderDatil();
    }

    @Override
    public void updateOrder() {
        orderDetailDao.updateNotOrder();
    }

    @Override
    public void updateCancelOrderDetail() {
        orderDetailDao.updateCancelOrderDatil();
    }

    @Override
    public void addTimeOrder() {
        List<Map<String, String>> list = new ArrayList<>();
        SimpleDateFormat daySimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = new Date();//获取当年日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime =  sdf.format(nowDate);//直

        // 获取订单行数详情数据
        List<PartOrderDTO> allPartOrderDTOByDate = orderDetailDao.getPartOrderDTOByDate();
        List<PartOrderDTO> orderSubmitTimeList;
        List<PartOrderDTO> loadingDateList=new ArrayList<>();
        PartOrderDTO orderSubmit =new PartOrderDTO();
        PartOrderDTO loading=new PartOrderDTO();
        if(allPartOrderDTOByDate!=null&&allPartOrderDTOByDate.size()!=0){
            // 升序
            orderSubmitTimeList = allPartOrderDTOByDate.stream().sorted(Comparator.comparing(PartOrderDTO::getAssessmentDate,Comparator.nullsLast(String::compareTo)))
                    .collect(Collectors.toList());
            orderSubmit = orderSubmitTimeList.get(0);
            // 降序
            loadingDateList = allPartOrderDTOByDate.stream().sorted(Comparator.comparing(PartOrderDTO::getLoadingDate,Comparator.nullsFirst(String::compareTo)).reversed())
                    .collect(Collectors.toList());
            loading = loadingDateList.get(0);
        }
        List<CalendarDto> workDayCount = new ArrayList<>();
        List<CalendarDto> nowDayCount = new ArrayList<>();
        // 非工作日时间
        if(orderSubmit.getAssessmentDate()!=null && loading.getLoadingDate()!=null){
            workDayCount = orderDetailDao.getWorkDayCount(orderSubmit.getAssessmentDate().substring(0,10), loading.getLoadingDate().substring(0,10));
        }
        // 非工作日时间
        if(orderSubmit.getAssessmentDate()!=null && nowTime!=null){
            nowDayCount = orderDetailDao.getWorkDayCount(orderSubmit.getAssessmentDate().substring(0,10), nowTime);
        }
        for (PartOrderDTO partOrderDTO:allPartOrderDTOByDate
                ) {
            Map<String,String> map = new HashMap<>();
            map.put("documentNumber",partOrderDTO.getDocumentNumber());
            map.put("partDrawingNo",partOrderDTO.getPartDrawingNo());
            if(partOrderDTO.getAssessmentDate()!=null&&partOrderDTO.getLoadingDate()!=null){
                // 获取订单考核开始时间跟装箱单时间的所有日期
                List<Date> distanceDate = TimeUtil.getDistanceDate(partOrderDTO.getAssessmentDate().substring(0,10)+" 00:00:00", partOrderDTO.getLoadingDate().substring(0,10)+" 23:59:59");
                int distributionDays=0;
                for (Date date:distanceDate
                        ) {
                    // 目标：筛选出元素
                    List<CalendarDto>newcontractList = workDayCount.stream()
                            .filter(oc -> oc.getFullDate()!=null && oc.getFullDate().equals(daySimpleDateFormat.format(date)))
                            .collect(Collectors.toList());
                    if(newcontractList!=null &&newcontractList.size()!=0){
                        distributionDays=distributionDays+1;
                    }
                }
                if("作业订单".equals(partOrderDTO.getOrderType())){
                    if(distanceDate.size()-distributionDays-1<=1){
                        map.put("isShipment","1");
                    }else {
                        map.put("isShipment","0");
                    }


                }else if("服务店备货订单".equals(partOrderDTO.getOrderType())){
                    if(distanceDate.size()-distributionDays-1<=2){
                        map.put("isShipment","1");
                    }else {
                        map.put("isShipment","0");
                    }
                }
                if(distanceDate.size()-distributionDays-1<0){
                    map.put("shipmentTime","0");
                }else {
                    map.put("shipmentTime",distanceDate.size()-distributionDays-1+"");
                }

            }else {
                if(partOrderDTO.getAssessmentDate()!=null&&nowTime!=null){
                    // 获取订单提交时间跟现在的时间所有日期
                    List<Date> distanceDate = TimeUtil.getDistanceDate(partOrderDTO.getAssessmentDate().substring(0,10)+" 00:00:00", nowTime+" 23:59:59");
                    int distributionDays=0;
                    for (Date date:distanceDate
                    ) {
                        // 目标：筛选出元素
                        List<CalendarDto>newcontractList = nowDayCount.stream()
                                .filter(oc -> oc.getFullDate()!=null && oc.getFullDate().equals(daySimpleDateFormat.format(date)))
                                .collect(Collectors.toList());
                        if(newcontractList!=null &&newcontractList.size()!=0){
                            distributionDays=distributionDays+1;
                        }
                    }
                    if("作业订单".equals(partOrderDTO.getOrderType())){
                        if(distanceDate.size()-distributionDays-1>1){
                            map.put("isShipment","0");
                        }else {
                            map.put("isShipment","2");
                        }
                    }else if("服务店备货订单".equals(partOrderDTO.getOrderType())){
                        if(distanceDate.size()-distributionDays-1>2){
                            map.put("isShipment","0");
                        }else {
                            map.put("isShipment","2");
                        }
                    }
                    map.put("shipmentTime",null);
                }else {
                    map.put("isShipment","2");
                    map.put("shipmentTime",null);
                }

            }
            list.add(map);

        }
        if(list!=null&& list.size()!=0){
            orderDetailDao.addTimeOrder(list);
        }
    }

    @Override
    public List<ProductCategory> getAllProductCategory() {
        return  productCategoryDao.getAllProductCategory();
    }

    @Override
    public List<FactoryData> getFactoryDate() {
        return userFactoryDao.selectAllFactory();
    }

    @Override
    public Map<Object, Object> getChart(ChartConditionDto chartConditionDto) {
        Map<Object,Object> map = new HashMap<>();
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        Integer missOrder = orderDetailDao.getMissOrder(chartConditionDto,inventoryDate);
        Integer goodMissOrder = orderDetailDao.getGoodMissOrder(chartConditionDto,inventoryDate);
        Integer workMissOrder = orderDetailDao.getWorkMissOrder(chartConditionDto,inventoryDate);
        // 订单类型统计
        List<Map<Object,Object>> typeValues=new ArrayList<>();
        Map<Object,Object> GoodOrder = new HashMap<>();
        GoodOrder.put("name","服务店备货订单");
        GoodOrder.put("value",orderDetailDao.getGoodOrder(chartConditionDto,inventoryDate));
        typeValues.add(GoodOrder);

        Map<Object,Object> WorkOrder = new HashMap<>();
        WorkOrder.put("name","作业订单");
        WorkOrder.put("value",orderDetailDao.getWorkOrder(chartConditionDto,inventoryDate));
        typeValues.add(WorkOrder);

        Map<Object,Object> missOrderMap = new HashMap<>();
        missOrderMap.put("name","缺件采购中");
        missOrderMap.put("value",missOrder);
        typeValues.add(missOrderMap);

        Map<Object,Object> PakageOrder = new HashMap<>();
        PakageOrder.put("name","已装箱");
        PakageOrder.put("value",orderDetailDao.getPakageOrder(chartConditionDto,inventoryDate));
        typeValues.add(PakageOrder);

        Map<Object,Object> AlreadyVoteOrder = new HashMap<>();
        AlreadyVoteOrder.put("name","已妥投");
        AlreadyVoteOrder.put("value",orderDetailDao.getAlreadyVoteOrder(chartConditionDto,inventoryDate));
        typeValues.add(AlreadyVoteOrder);

        Map<Object,Object> ProcesseOrder = new HashMap<>();
        ProcesseOrder.put("name","待处理");
        ProcesseOrder.put("value",orderDetailDao.getProcesseOrder(chartConditionDto,inventoryDate));
        typeValues.add(ProcesseOrder);

        map.put("typeValue",typeValues);

        // 订单出货历时
        List<Map<Object,Object>> shipmentTimeValues=new ArrayList<>();
        Map<Object,Object> lessNds2 = new HashMap<>();
        lessNds2.put("name","n<=2");
        lessNds2.put("value",orderDetailDao.getOrderShipmentTime(chartConditionDto,"1",inventoryDate));
        shipmentTimeValues.add(lessNds2);

        Map<Object,Object> nds2 = new HashMap<>();
        nds2.put("name","2<n<=4");
        nds2.put("value",orderDetailDao.getOrderShipmentTime(chartConditionDto,"2", inventoryDate));
        shipmentTimeValues.add(nds2);

        Map<Object,Object> nds4 = new HashMap<>();
        nds4.put("name","4<n<=6");
        nds4.put("value",orderDetailDao.getOrderShipmentTime(chartConditionDto,"3", inventoryDate));
        shipmentTimeValues.add(nds4);

        Map<Object,Object> nds6 = new HashMap<>();
        nds6.put("name","n>6");
        nds6.put("value",orderDetailDao.getOrderShipmentTime(chartConditionDto,"4", inventoryDate));
        shipmentTimeValues.add(nds6);

        map.put("shipmentTimeValue",shipmentTimeValues);

        // 缺货订单类型统计
        List<Map<Object,Object>> stockGoodsValues=new ArrayList<>();
        Map<Object,Object> MisGoodOrder = new HashMap<>();
        MisGoodOrder.put("name","服务店备货订单");
        MisGoodOrder.put("value",goodMissOrder);
        MisGoodOrder.put("percent",TimeUtil.getRate(goodMissOrder,missOrder));
        stockGoodsValues.add(MisGoodOrder);

        Map<Object,Object> MisWorkOrder = new HashMap<>();
        MisWorkOrder.put("name","作业订单");
        MisWorkOrder.put("value",workMissOrder);
        MisWorkOrder.put("percent",TimeUtil.getRate(workMissOrder,missOrder));
        stockGoodsValues.add(MisWorkOrder);

        map.put("stockGoodsValue",stockGoodsValues);

        // 缺货订单出货历时统计
        List<Map<Object,Object>> stockGoodsTimeValues=new ArrayList<>();

        Map<Object,Object> MisLessNds2 = new HashMap<>();
        MisLessNds2.put("name","n<2");
        MisLessNds2.put("value",orderDetailDao.getMisOrderShipmentTime(chartConditionDto,"1",inventoryDate));
        stockGoodsTimeValues.add(MisLessNds2);

        Map<Object,Object> MisNds2 = new HashMap<>();
        MisNds2.put("name","2<n<=4");
        MisNds2.put("value",orderDetailDao.getMisOrderShipmentTime(chartConditionDto,"2", inventoryDate));
        stockGoodsTimeValues.add(MisNds2);

        Map<Object,Object> MisNds4 = new HashMap<>();
        MisNds4.put("name","4<n<=6");
        MisNds4.put("value",orderDetailDao.getMisOrderShipmentTime(chartConditionDto,"3", inventoryDate));
        stockGoodsTimeValues.add(MisNds4);


        Map<Object,Object> MisNds6 = new HashMap<>();
        MisNds6.put("name","n>6");
        MisNds6.put("value",orderDetailDao.getMisOrderShipmentTime(chartConditionDto,"4", inventoryDate));
        stockGoodsTimeValues.add(MisNds6);

        map.put("stockGoodsTimeValue",stockGoodsTimeValues);
        return map;
    }

    @Override
    public List<ProductCategoryVo> getProductCategory(ProductCategoryConditionDto productCategoryConditionDto) {
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        productCategoryConditionDto.setInventoryDate(inventoryDate);
        return orderDetailDao.getProductCategory(productCategoryConditionDto);
    }

    @Override
    public List<PurchaseOrderListVo> getPurchaseOrder(PurchaseOrderConditionDTO purchaseOrderConditionDTO) {
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        purchaseOrderConditionDTO.setInventoryDate(inventoryDate);
        return purchaseOrderDao.getPurchaseOrder(purchaseOrderConditionDTO);
    }

    @Override
    public PurchaseOrder getPurchaseOrderDetail(String documentNumber) {
        return purchaseOrderDao.getPurchaseOrderDetail(documentNumber);
    }

    @Override
    public List<PurchaseOrderDetail> getPurchaseOrderDetailList(String documentNumber) {
        return purchaseOrderDao.getPurchaseOrderDetailByNo(documentNumber,null);
    }

    /**
     * 获取部品出货即纳率
     * @param startDate
     * @param endDate
     * @return
     */
    public Double getShipment(String startDate,String endDate,String userId,String inventoryDate){
        // 符合的订单数
        int nowOrderConformity=0;
        // 计算核心指标
        // 获取所有订单行数量
        int allCount=0;
        try {

            Future<Integer> submit1 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                Integer allCount1 = orderDetailDao.getCountByDate(startDate, endDate, userId,inventoryDate);
                return allCount1;
            });
            Future<Integer> submit2 = LogAspectForSongXia.DOCK_EXECUTOR.submit(() -> {
                Integer nowOrderConformity1 = orderDetailDao.countByOrder(startDate, endDate, userId,inventoryDate);
                return nowOrderConformity1;
            });
            allCount = submit1.get();
            nowOrderConformity = submit2.get();
        }catch (Exception e){
            e.printStackTrace();
        }
        return TimeUtil.getRate(nowOrderConformity,allCount);
    }

    /**
     * 计算在库金额
     * @param amountList 各分表的在库金额
     * @return 总在库金额
     */
    private double calculateAmount(List<BigDecimal> amountList) {
        double sum = amountList.stream().filter(Objects::nonNull).mapToDouble(BigDecimal::doubleValue).sum();
        return new BigDecimal(sum).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
    }

    /**
     * 担当别在库金额（仟元）
     * @return 统一返回
     */
    @Override
    public RetResult<List<BearAmountVO>> getBearAmount() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        // 各担当在库金额
        List<BearAmountBO> bearAmountBOList = partDrawingStockDao.selectBearAmount(startDate.toString(), endDate.toString());
        Assert.notNull(bearAmountBOList, BizCodeEnum.NO_DATA.getMessage());

        // 返回前端数据
        List<BearAmountVO> bearAmountVOList = new ArrayList<>(bearAmountBOList.size());
        // 查人员进行关联
        List<AccountInfoBO> accountInfoBOList = hrmAccountInfoDao.selectAllAccountInfo();
        Assert.notNull(accountInfoBOList, BizCodeEnum.NO_DATA.getMessage());

        for (BearAmountBO bearAmountBO : bearAmountBOList) {
            String userId = bearAmountBO.getUserId();
            List<AccountInfoBO> accountInfoCollect = accountInfoBOList.stream().filter(accountInfoBO -> accountInfoBO.getAccountId().equals(userId)).collect(Collectors.toList());
            if (accountInfoCollect.isEmpty()) {
                break;
            }
            AccountInfoBO accountInfoBO = accountInfoCollect.get(0);
            BearAmountVO vo = BearAmountVO.builder()
                    .userId(bearAmountBO.getUserId())
                    .userName(accountInfoBO.getRealName())
                    .total(bearAmountBO.getTotal().setScale(2, BigDecimal.ROUND_DOWN).doubleValue())
                    .build();
            bearAmountVOList.add(vo);
        }
        return RetResponse.makeOKRsp(bearAmountVOList);
    }

    /**
     * 根据userId查询担当下的各工厂在库金额（仟元）
     * @param factoryBearAmountQueryDTO 查询条件
     * @return 查询出的数据
     */
    @Override
    public RetResult<com.alibaba.fastjson.JSONObject> getBearAmountByUserId(FactoryBearAmountQueryDTO factoryBearAmountQueryDTO) {
        LocalDate now = LocalDate.now();
        String startDate;
        if (StringUtils.isEmpty(factoryBearAmountQueryDTO.getDate())) {
            startDate = now.toString();
        } else {
            // 查询月最后一天
            LocalDateTime queryLastDayOfMonth = LocalDateTimeUtil.parse(factoryBearAmountQueryDTO.getDate(), DatePattern.NORM_MONTH_PATTERN).with(TemporalAdjusters.lastDayOfMonth());
            // 判断今天所在月是不是查询月，如果在查询月，那么当月最后一天可能会没有数据
            if (StringUtils.isEmpty(factoryBearAmountQueryDTO.getDate()) || queryLastDayOfMonth.getMonthValue() == now.getMonthValue()) {
                startDate = now.toString();
            } else {
                // 今天不在查询月，取查询月的最后一天
                startDate = queryLastDayOfMonth.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
        }

        // 1.查出担当下所有工厂在库金额
        String endDate = LocalDate.parse(startDate).plusDays(1).toString();
        List<FactoryBearAmountBO> factoryBearAmountBOList = partDrawingStockDao.selectFactoryBearAmountByQueryDTO(factoryBearAmountQueryDTO, startDate, endDate);
        Assert.notNull(factoryBearAmountBOList, BizCodeEnum.NO_DATA.getMessage());

        String userName = factoryBearAmountQueryDTO.getUserName();
        // 2.各工厂分组聚合在库金额
        // 先对 factoryCode 分组，再对 factoryName 分组，最后合计在库金额 {"factoryCode": {"factoryName": amount}}
        Map<String, Double> everyFactoryAmount = factoryBearAmountBOList.stream().collect(Collectors.groupingBy(FactoryBearAmountBO::getFactoryName, Collectors.summingDouble(bo -> bo.getTotal().doubleValue())));
        factoryBearAmountBOList = null;
        // 视图类
        List<FactoryBearAmountVO> factoryBearAmountVOList = new ArrayList<>(everyFactoryAmount.size());
        // 3.对视图类填充数据
        for (Map.Entry<String, Double> entry : everyFactoryAmount.entrySet()) {
            String factoryName = entry.getKey();
            double amount = entry.getValue();
            FactoryBearAmountVO vo = FactoryBearAmountVO.builder()
                    .factoryName(factoryName)
                    .amount(new BigDecimal(amount).setScale(2, BigDecimal.ROUND_DOWN).doubleValue())
                    .userName(userName)
                    .build();
            factoryBearAmountVOList.add(vo);
        }
        everyFactoryAmount = null;

        // 4.过滤
        String amountQueryValue = factoryBearAmountQueryDTO.getAmount().getValue();
        if (!StringUtils.isEmpty(amountQueryValue)) {
            double amountQueryValueDouble = Double.parseDouble(amountQueryValue);
            String type = factoryBearAmountQueryDTO.getAmount().getType();
            if (StringUtils.isEmpty(type) || "=".equals(type)) {
                // 有值无type默认为等值查询
                factoryBearAmountVOList = factoryBearAmountVOList.stream().filter(factoryBearAmountVO -> factoryBearAmountVO.getAmount() == amountQueryValueDouble).collect(Collectors.toList());
            } else if (">=".equals(type)) {
                factoryBearAmountVOList = factoryBearAmountVOList.stream().filter(factoryBearAmountVO -> factoryBearAmountVO.getAmount() >= amountQueryValueDouble).collect(Collectors.toList());
            } else if (">".equals(type)) {
                factoryBearAmountVOList = factoryBearAmountVOList.stream().filter(factoryBearAmountVO -> factoryBearAmountVO.getAmount() > amountQueryValueDouble).collect(Collectors.toList());
            } else if ("!=".equals(type)) {
                factoryBearAmountVOList = factoryBearAmountVOList.stream().filter(factoryBearAmountVO -> factoryBearAmountVO.getAmount() != amountQueryValueDouble).collect(Collectors.toList());
            } else if ("<=".equals(type)) {
                factoryBearAmountVOList = factoryBearAmountVOList.stream().filter(factoryBearAmountVO -> factoryBearAmountVO.getAmount() <= amountQueryValueDouble).collect(Collectors.toList());
            } else if ("<".equals(type)) {
                factoryBearAmountVOList = factoryBearAmountVOList.stream().filter(factoryBearAmountVO -> factoryBearAmountVO.getAmount() < amountQueryValueDouble).collect(Collectors.toList());
            }
        }

        // 5.排序（默认desc降序）
        String sort = factoryBearAmountQueryDTO.getAmount().getSort();
        if (StringUtils.isEmpty(sort) || "desc".equals(sort)) {
            factoryBearAmountVOList = factoryBearAmountVOList.stream().sorted(Comparator.comparing(FactoryBearAmountVO::getAmount).reversed()).collect(Collectors.toList());
        } else {
            factoryBearAmountVOList = factoryBearAmountVOList.stream().sorted(Comparator.comparing(FactoryBearAmountVO::getAmount)).collect(Collectors.toList());
        }

        // 6.分页
        long pageNum = factoryBearAmountQueryDTO.getPageNum();
        long pageSize = factoryBearAmountQueryDTO.getPageSize();
        long startRow = pageNum * pageSize - pageSize;
        int size = factoryBearAmountVOList.size();
        factoryBearAmountVOList = factoryBearAmountVOList.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

        com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
        jsonObject.put("data", factoryBearAmountVOList);
        jsonObject.put("count", size);

        return RetResponse.makeOKRsp(jsonObject);
    }

    @Override
    public Map<Object, Object> getStandardOrder(String date) {
        if (StringUtils.isEmpty(date)) {
            // 如果入参时间为空则取今天的时间
            date = LocalDate.now().toString();
        }
        Map<Object,Object> map = new HashMap<>();
        // 订单类型统计
        List<Map<Object,Object>> typeValues=new ArrayList<>();
        Map<Object,Object> GoodOrder = new HashMap<>();
        GoodOrder.put("name","出货异常");
        GoodOrder.put("value",orderDetailDao.getNotShipment(date));
        typeValues.add(GoodOrder);

        Map<Object,Object> WorkOrder = new HashMap<>();
        WorkOrder.put("name","N+1出货");
        WorkOrder.put("value",orderDetailDao.getIsShipment(date));
        typeValues.add(WorkOrder);

        Map<Object,Object> missOrderMap = new HashMap<>();
        missOrderMap.put("name","部品缺件");
        missOrderMap.put("value",orderDetailDao.getMissOrderDetail(date));
        typeValues.add(missOrderMap);

        map.put("typeValue",typeValues);
        return map;
    }

    /**
     * 获取实际在库金额（元）
     * @param userId 用户id
     * @return 统一返回
     */
    @Override
    public RetResult<Double> getActualAmountInStock(String userId) {
        LocalDate now = LocalDate.now();
        String startDate = now.toString();
        String endDate = now.plusDays(1).toString();
        double amount = calculateAmount(inventoryWarningDao.getAmountInStock(userId, startDate, endDate));
        return RetResponse.makeOKRsp(amount);
    }

    @Override
    public void addDeadline() throws ParseException {
        // 获取未添加考核截至日期的数据
        List<NoDeadlineOrderBo> noDeadlineOrderBo = orderDetailDao.getNoDeadlineOrderBo();
        // 获取非工作日日期
        List<CalendarDto> workDayCount = orderDetailDao.getAllWorkDayCount();
        for (NoDeadlineOrderBo deadlineOrderBo : noDeadlineOrderBo) {
            String assessmentDate ="";
            String deadline ="";
            String startDate = deadlineOrderBo.getOrderSubmitTime().substring(0, 10);
            // 目标：筛选出元素
            List<CalendarDto> newcontractList = workDayCount.stream()
                    .filter(oc -> oc.getFullDate()!=null && oc.getFullDate().equals(startDate))
                    .collect(Collectors.toList());
            assessmentDate = startDate;
            // 获取考核开始日
            while (newcontractList.size()!=0){
                LocalDate start = LocalDate.parse(startDate);
                LocalDate nextDay = start.plusDays(1);
                newcontractList = workDayCount.stream()
                        .filter(oc -> oc.getFullDate()!=null && oc.getFullDate().equals(nextDay))
                        .collect(Collectors.toList());
                assessmentDate = nextDay.toString();
            }
            deadlineOrderBo.setAssessmentDate(assessmentDate);
            deadline=assessmentDate;
            if("作业订单".equals(deadlineOrderBo.getOrderType())){
                int i=0;
                // 获取考核截至日
                while (i<1){
                    LocalDate start = LocalDate.parse(deadline);
                    LocalDate nextDay = start.plusDays(1);
                    newcontractList = workDayCount.stream()
                            .filter(oc -> oc.getFullDate()!=null && oc.getFullDate().equals(nextDay))
                            .collect(Collectors.toList());
                    if(newcontractList.size()==0){
                        i++;
                        deadline=nextDay.toString();
                    }
                }
            }else {
                int i=0;
                // 获取考核截至日
                while (i<2){
                    LocalDate start = LocalDate.parse(deadline);
                    LocalDate nextDay = start.plusDays(1);
                    newcontractList = workDayCount.stream()
                            .filter(oc -> oc.getFullDate()!=null && oc.getFullDate().equals(nextDay))
                            .collect(Collectors.toList());
                    if(newcontractList.size()==0){
                        i++;
                        deadline=nextDay.toString();
                    }
                }
            }
            deadlineOrderBo.setDeadline(deadline);
        }
        if(noDeadlineOrderBo.size()!=0){
            orderDetailDao.updateDeadLine(noDeadlineOrderBo);
        }
    }

    @Override
    public List<FactoryShipmentVo> getFactoryShipmentBySort(String startDate, String endDate, String sort) {
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        return orderDetailDao.getFactoryShipmentBySort(startDate,endDate,inventoryDate,sort);
    }

    @Override
    public List<PhoneShipmentVo> shipmentCurve(String startDate, String endDate) {
        List<PhoneShipmentVo> phoneShipmentVoList = new ArrayList<>();
        List<String> daysStr = TimeUtil.findDaysStr(startDate, endDate, "0");
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        List<PhoneShipmentVo> phoneShipmentVos = orderDetailDao.shipmentCurve(startDate,endDate,inventoryDate);
        for (String s : daysStr) {
            PhoneShipmentVo phoneShipmentVo = new PhoneShipmentVo();
            phoneShipmentVo.setDate(s);
            boolean flag = false;
            for (PhoneShipmentVo shipmentVo : phoneShipmentVos) {
                if(s.equals(shipmentVo.getDate())){
                    phoneShipmentVo.setShipment(shipmentVo.getShipment());
                    flag = true;
                    break;
                }
            }
            if(!flag){
                phoneShipmentVo.setShipment("0");
            }
            phoneShipmentVoList.add(phoneShipmentVo);
        }
        return phoneShipmentVoList;
    }

    @Override
    public List<Nds2CurveVo> nds2Curve(String startDate, String endDate) {
        List<Nds2CurveVo> nds2CurveVoList = new ArrayList<>();
        List<String> daysStr = TimeUtil.findDaysStr(startDate, endDate, "0");
        String inventoryDate = sysConfService.getSystemConfig("inventoryDate", "100060");
        List<Nds2CurveVo> nds2Curve = orderDetailDao.nds2Curve(startDate,endDate,inventoryDate);
        for (String s : daysStr) {
            Nds2CurveVo nds2CurveVo1 = new Nds2CurveVo();
            nds2CurveVo1.setDate(s);
            boolean flag = false;
            for (Nds2CurveVo nds2CurveVo : nds2Curve) {
                if(s.equals(nds2CurveVo.getDate())){
                    nds2CurveVo1.setNds2(nds2CurveVo.getNds2());
                    flag = true;
                    break;
                }
            }
            if(!flag){
                nds2CurveVo1.setNds2("0");
            }
            nds2CurveVoList.add(nds2CurveVo1);
        }
        return nds2CurveVoList;
    }
}
