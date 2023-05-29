package utry.data.modular.partsManagement.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import utry.data.constant.ConditionSortConstant;
import utry.data.modular.account.dao.HrmAccountInfoDao;
import utry.data.modular.account.model.AccountInfoBO;
import utry.data.modular.partsManagement.bo.*;
import utry.data.modular.partsManagement.dao.*;
import utry.data.modular.partsManagement.dto.FactoryAmountQueryDTO;
import utry.data.modular.partsManagement.dto.FactoryCountQueryDTO;
import utry.data.modular.partsManagement.dto.PartDrawingAmountQueryDTO;
import utry.data.modular.partsManagement.dto.PartDrawingCountQueryDTO;
import utry.data.modular.partsManagement.service.PartDrawingStockService;
import utry.data.modular.partsManagement.vo.*;
import utry.data.util.ConditionUtil;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @program: data
 * @description: 部品库存业务接口实现类
 * @author: WangXinhao
 * @create: 2022-06-13 11:14
 **/
@Service
public class PartDrawingStockServiceImpl implements PartDrawingStockService {

    @Autowired
    private PartDrawingStockDao partDrawingStockDao;

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private HrmAccountInfoDao hrmAccountInfoDao;

    @Autowired
    private PurchaseOrderDao purchaseOrderDao;

    @Autowired
    private MissDealOrderDao missDealOrderDao;

    @Autowired
    private InventoryWarningDao inventoryWarningDao;

    /**
     * 部品库存工厂在库金额表格
     * @param factoryAmountQueryDTO 查询条件
     * @return 统一返回
     */
    @Override
    public RetResult<JSONObject> getFactoryAmount(FactoryAmountQueryDTO factoryAmountQueryDTO) {
        String dateTime = factoryAmountQueryDTO.getDateTime();
        if (StringUtils.isEmpty(dateTime)) {
            // 如果入参时间为空则取今天的时间
            dateTime = LocalDate.now().toString();
        }
        String endDate = LocalDate.parse(dateTime).plusDays(1).toString();
        // 1.在库金额、用户名称
        // 查询工厂-用户id-工厂在库金额（仟元），同时支持根据工厂名称右模糊查询
        List<FactoryUserIdAmountBO> factoryUserIdAmountBOList = new ArrayList<>();
        try {
            factoryUserIdAmountBOList = partDrawingStockDao.selectFactoryAmountByDateFactoryName(dateTime, endDate, factoryAmountQueryDTO.getFactoryName());
        } catch (Exception ignored) {
        }
        // 最终返回视图
        List<FactoryAmountVO> resultVO = new ArrayList<>(factoryUserIdAmountBOList.size());
        // 查人员进行关联
        List<AccountInfoBO> accountInfoBOList = hrmAccountInfoDao.selectAllAccountInfo();
        // 将 工厂代码、工厂名称、担当姓名、在库金额 插入视图
        for (FactoryUserIdAmountBO factoryUserIdAmountBO : factoryUserIdAmountBOList) {
            String userId = factoryUserIdAmountBO.getUserId();
            AccountInfoBO accountInfoBO = accountInfoBOList.stream().filter(bo -> userId.equals(bo.getAccountId())).findFirst().orElse(null);
            if (accountInfoBO == null) {
                continue;
            }
            String realName = accountInfoBO.getRealName();
            FactoryAmountVO vo = FactoryAmountVO.builder()
                    .factoryCode(factoryUserIdAmountBO.getFactoryCode())
                    .factoryName(factoryUserIdAmountBO.getFactoryName())
                    .userName(realName)
                    .costAmount(factoryUserIdAmountBO.getFactoryAmount().setScale(2, BigDecimal.ROUND_DOWN).doubleValue())
                    .build();
            resultVO.add(vo);
        }

        // 2.计算需求金额
        // 查部件图号的需要数量
        List<PartDrawingNoNeedNumberBO> partDrawingNoNeedNumberBOList = orderDetailDao.selectPartDrawingNoNeedNumberByDate(factoryAmountQueryDTO.getDateTime());
        // 查成本单价
        List<PartDrawingCostPriceBO> partDrawingCostPriceBOList = new ArrayList<>();
        try {
            partDrawingCostPriceBOList = partDrawingStockDao.selectCostPrice(dateTime, endDate,null);
        } catch (Exception ignored) {
        }
        for (PartDrawingNoNeedNumberBO partDrawingNoNeedNumberBO : partDrawingNoNeedNumberBOList) {
            String partDrawingNo = partDrawingNoNeedNumberBO.getPartDrawingNo();
            String warehouseCode = partDrawingNoNeedNumberBO.getWarehouseCode();
            // partDrawingNo、warehouseCode 都相同确定一个单价
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNo().equals(partDrawingNo) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            String factoryCode = partDrawingCostPriceBO.getFactoryCode();
            // 某仓库一个部件的需求金额（仟元）
            BigDecimal demandAmountBigDecimalValue = partDrawingCostPriceBO.getCostPrice().multiply(BigDecimal.valueOf(partDrawingNoNeedNumberBO.getNeedNumber())).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_DOWN);
            resultVO.stream().filter(vo -> vo.getFactoryCode().equals(factoryCode)).forEach(vo -> vo.setDemandAmount(demandAmountBigDecimalValue.add(BigDecimal.valueOf(vo.getDemandAmount() == null ? 0 : vo.getDemandAmount())).doubleValue()));
        }
        partDrawingNoNeedNumberBOList = null;

        // 3.计算安全在库金额
        // 查安全在库数量
        List<SecurityBO> securityBOList = inventoryWarningDao.getSecurityBO(dateTime);
        for (SecurityBO securityBO : securityBOList) {
            String warehouseCode = securityBO.getWarehouseCode();
            String partDrawingNumber = securityBO.getPartDrawingNumber();
            // partDrawingNumber、warehouseCode 都相同确定一个单价
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNumber().equals(partDrawingNumber) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            String factoryCode = partDrawingCostPriceBO.getFactoryCode();
            BigDecimal safeCostAmountBigDecimalValue = partDrawingCostPriceBO.getCostPrice().multiply(BigDecimal.valueOf(securityBO.getCount())).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_DOWN);
            resultVO.stream().filter(vo -> vo.getFactoryCode().equals(factoryCode)).forEach(vo -> vo.setSafeCostAmount(safeCostAmountBigDecimalValue.add(BigDecimal.valueOf(vo.getSafeCostAmount() == null ? 0 : vo.getSafeCostAmount())).doubleValue()));
        }

        // 4.计算采购在途订单金额
        // 查采购在途订单数量
        List<PurchaseOrderCountBO> purchaseOrderCountBOList = purchaseOrderDao.selectPurchaseOrderCount(dateTime);
        for (PurchaseOrderCountBO purchaseOrderCountBO : purchaseOrderCountBOList) {
            String partDrawingNumber = purchaseOrderCountBO.getPartDrawingNumber();
            String warehouseCode = purchaseOrderCountBO.getWarehouseCode();
            // partDrawingNumber、warehouseCode 都相同确定一个单价
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNumber().equals(partDrawingNumber) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            String factoryCode = partDrawingCostPriceBO.getFactoryCode();
            BigDecimal purchaseInTransitAmountBigDecimalValue = partDrawingCostPriceBO.getCostPrice().multiply(BigDecimal.valueOf(purchaseOrderCountBO.getCount())).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_DOWN);
            resultVO.stream().filter(vo -> vo.getFactoryCode().equals(factoryCode)).forEach(vo -> vo.setPurchaseInTransitAmount(purchaseInTransitAmountBigDecimalValue.add(BigDecimal.valueOf(vo.getPurchaseInTransitAmount() == null ? 0 : vo.getPurchaseInTransitAmount())).doubleValue()));
        }

        // 4.1 计算采购在途合计、异常订单
        List<PurchaseOrderBO> purchaseOrderBOList = purchaseOrderDao.getPurchaseOrderBO(dateTime);
        for (PurchaseOrderBO purchaseOrderBO : purchaseOrderBOList) {
            String warehouseCode = purchaseOrderBO.getWarehouseCode();
            String partDrawingNumber = purchaseOrderBO.getPartDrawingNumber();
            // partDrawingNumber、warehouseCode 都相同确定工厂
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNumber().equals(partDrawingNumber) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            String factoryCode = partDrawingCostPriceBO.getFactoryCode();
            int purchaseInTransitTotalCount = purchaseOrderBO.getTotal();
            //int purchaseInTransitAbnormalCount = purchaseOrderBO.getAbnormal() == null ? 0 : purchaseOrderBO.getAbnormal();
            resultVO.stream().filter(vo -> vo.getFactoryCode().equals(factoryCode)).forEach(vo -> {
                vo.setPurchaseInTransitTotalCount(purchaseInTransitTotalCount + (vo.getPurchaseInTransitTotalCount() == null ? 0L : vo.getPurchaseInTransitTotalCount()));
                //vo.setPurchaseInTransitAbnormalCount(purchaseInTransitAbnormalCount + (vo.getPurchaseInTransitAbnormalCount() == null ? 0L : vo.getPurchaseInTransitAbnormalCount()));
            });
        }

        // 对金额 null 的字段值填充 0
        resultVO.forEach(vo -> {
            vo.setCostAmount(vo.getCostAmount() == null ? 0.0 : vo.getCostAmount());
            vo.setDemandAmount(vo.getDemandAmount() == null ? 0.0 : vo.getDemandAmount());
            vo.setPurchaseInTransitAmount(vo.getPurchaseInTransitAmount() == null ? 0.0 : vo.getPurchaseInTransitAmount());
            vo.setSafeCostAmount(vo.getSafeCostAmount() == null ? 0.0 : vo.getSafeCostAmount());
            vo.setPurchaseInTransitTotalCount(vo.getPurchaseInTransitTotalCount() == null ? 0L : vo.getPurchaseInTransitTotalCount());
            vo.setPurchaseInTransitAbnormalCount(vo.getPurchaseInTransitAbnormalCount() == null ? 0L : vo.getPurchaseInTransitAbnormalCount());
        });

        // 5.根据条件筛选过滤
        // 5.1 工厂名称 筛选在sql中完成
        // 5.2 工厂担当 筛选
        ConditionUtil userNameCondition = factoryAmountQueryDTO.getUserName();
        if (!StringUtils.isEmpty(userNameCondition.getValue())) {
            String userNameConditionValue = userNameCondition.getValue();
            String userNameConditionType = userNameCondition.getType();
            if (StringUtils.isEmpty(userNameConditionType) || "0".equals(userNameConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getUserName().equals(userNameConditionValue)).collect(Collectors.toList());
            } else if ("1".equals(userNameConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getUserName().contains(userNameConditionValue)).collect(Collectors.toList());
            } else if ("2".equals(userNameConditionType)) {
                resultVO = resultVO.stream().filter(vo -> !vo.getUserName().contains(userNameConditionValue)).collect(Collectors.toList());
            }
        }
        // 5.3 在库金额 筛选
        ConditionUtil costAmountCondition = factoryAmountQueryDTO.getCostAmount();
        if (!StringUtils.isEmpty(costAmountCondition.getValue())) {
            double costAmountConditionValue = Double.parseDouble(costAmountCondition.getValue());
            String costAmountConditionType = costAmountCondition.getType();
            if (StringUtils.isEmpty(costAmountConditionType) || "=".equals(costAmountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() == costAmountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() >= costAmountConditionValue).collect(Collectors.toList());
            } else if (">".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() > costAmountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() != costAmountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() <= costAmountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() < costAmountConditionValue).collect(Collectors.toList());
            }
        }
        // 5.4 需求金额 筛选
        ConditionUtil demandAmountCondition = factoryAmountQueryDTO.getDemandAmount();
        if (!StringUtils.isEmpty(demandAmountCondition.getValue())) {
            double demandAmountConditionValue = Double.parseDouble(demandAmountCondition.getValue());
            String demandAmountConditionType = demandAmountCondition.getType();
            if (StringUtils.isEmpty(demandAmountConditionType) || "=".equals(demandAmountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() == demandAmountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() >= demandAmountConditionValue).collect(Collectors.toList());
            } else if (">".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() > demandAmountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() != demandAmountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() <= demandAmountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() < demandAmountConditionValue).collect(Collectors.toList());
            }
        }
        // 5.5 安全在库金额 筛选
        ConditionUtil safeCostAmountCondition = factoryAmountQueryDTO.getSafeCostAmount();
        if (!StringUtils.isEmpty(safeCostAmountCondition.getValue())) {
            double safeCostAmountConditionValue = Double.parseDouble(safeCostAmountCondition.getValue());
            String safeCostAmountConditionType = safeCostAmountCondition.getType();
            if (StringUtils.isEmpty(safeCostAmountConditionType) || "=".equals(safeCostAmountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() == safeCostAmountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() >= safeCostAmountConditionValue).collect(Collectors.toList());
            } else if (">".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() > safeCostAmountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() != safeCostAmountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() <= safeCostAmountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() < safeCostAmountConditionValue).collect(Collectors.toList());
            }
        }
        // 5.6 采购在途订单金额 筛选
        ConditionUtil purchaseInTransitAmountCondition = factoryAmountQueryDTO.getPurchaseInTransitAmount();
        if (!StringUtils.isEmpty(purchaseInTransitAmountCondition.getValue())) {
            double purchaseInTransitAmountConditionValue = Double.parseDouble(purchaseInTransitAmountCondition.getValue());
            String purchaseInTransitAmountConditionType = purchaseInTransitAmountCondition.getType();
            if (StringUtils.isEmpty(purchaseInTransitAmountConditionType) || "=".equals(purchaseInTransitAmountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() == purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() >= purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if (">".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() > purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() != purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() <= purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() < purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            }
        }

        // 6.排序（默认对在库金额desc降序排序）
        // 6.1 在库金额 排序
        String costAmountConditionSort = costAmountCondition.getSort();
        if (StringUtils.isEmpty(costAmountConditionSort) || ConditionSortConstant.DESC.equals(costAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryAmountVO::getCostAmount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(costAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryAmountVO::getCostAmount)).collect(Collectors.toList());
        }
        // 6.2 需求金额 排序
        String demandAmountConditionSort = demandAmountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(demandAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryAmountVO::getDemandAmount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(demandAmountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryAmountVO::getDemandAmount)).collect(Collectors.toList());
        }
        // 6.3 安全在库金额 排序
        String safeCostAmountConditionSort = safeCostAmountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(safeCostAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryAmountVO::getSafeCostAmount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(safeCostAmountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryAmountVO::getSafeCostAmount)).collect(Collectors.toList());
        }
        // 6.4 采购在途金额 排序
        String purchaseInTransitAmountConditionSort = purchaseInTransitAmountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(purchaseInTransitAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryAmountVO::getPurchaseInTransitAmount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(purchaseInTransitAmountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryAmountVO::getPurchaseInTransitAmount)).collect(Collectors.toList());
        }

        // 7.分页
        long pageNum = factoryAmountQueryDTO.getPageNum();
        long pageSize = factoryAmountQueryDTO.getPageSize();
        long startRow = pageNum * pageSize - pageSize;
        int size = resultVO.size();
        resultVO = resultVO.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", resultVO);
        jsonObject.put("count", size);
        return RetResponse.makeOKRsp(jsonObject);
    }

    /**
     * 部品库存工厂在库数量表格
     * @param factoryCountQueryDTO 查询条件
     * @return 统一返回
     */
    @Override
    public RetResult<JSONObject> getFactoryCount(FactoryCountQueryDTO factoryCountQueryDTO) {
        String dateTime = factoryCountQueryDTO.getDateTime();
        if (StringUtils.isEmpty(dateTime)) {
            // 如果入参时间为空则取今天的时间
            dateTime = LocalDate.now().toString();
        }
        String endDate = LocalDate.parse(dateTime).plusDays(1).toString();
        // 1.在库数量、用户名称
        List<FactoryUserIdCountBO> factoryUserIdCountBOList = new ArrayList<>();
        try {
            factoryUserIdCountBOList = partDrawingStockDao.selectFactoryCountByDateFactoryName(dateTime, endDate, factoryCountQueryDTO.getFactoryName());
        } catch (Exception ignored) {
        }
        // 最终返回视图
        List<FactoryCountVO> resultVO = new ArrayList<>(factoryUserIdCountBOList.size());
        // 查人员进行关联
        List<AccountInfoBO> accountInfoBOList = hrmAccountInfoDao.selectAllAccountInfo();
        // 将 工厂代码、工厂名称、担当姓名、在库数量 插入视图
        for (FactoryUserIdCountBO factoryUserIdCountBO : factoryUserIdCountBOList) {
            String userId = factoryUserIdCountBO.getUserId();
            AccountInfoBO accountInfoBO = accountInfoBOList.stream().filter(bo -> userId.equals(bo.getAccountId())).findFirst().orElse(null);
            if (accountInfoBO == null) {
                continue;
            }
            String realName = accountInfoBO.getRealName();
            FactoryCountVO vo = FactoryCountVO.builder()
                    .factoryCode(factoryUserIdCountBO.getFactoryCode())
                    .factoryName(factoryUserIdCountBO.getFactoryName())
                    .userName(realName)
                    .costCount(factoryUserIdCountBO.getFactoryCount())
                    .build();
            resultVO.add(vo);
        }

        // 2.计算需求数量
        List<PartDrawingNoNeedNumberBO> partDrawingNoNeedNumberBOList = orderDetailDao.selectPartDrawingNoNeedNumberByDate(factoryCountQueryDTO.getDateTime());
        // 查工厂-仓库-图号关系
        List<PartDrawingCostPriceBO> partDrawingCostPriceBOList = new ArrayList<>();
        try {
            partDrawingCostPriceBOList = partDrawingStockDao.selectCostPrice(dateTime, endDate,null);
        } catch (Exception ignored) {
        }
        for (PartDrawingNoNeedNumberBO partDrawingNoNeedNumberBO : partDrawingNoNeedNumberBOList) {
            String partDrawingNo = partDrawingNoNeedNumberBO.getPartDrawingNo();
            String warehouseCode = partDrawingNoNeedNumberBO.getWarehouseCode();

            // partDrawingNo、warehouseCode 都相同确定一个工厂
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNo().equals(partDrawingNo) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            String factoryCode = partDrawingCostPriceBO.getFactoryCode();
            long needNumber = partDrawingNoNeedNumberBO.getNeedNumber();
            // 工厂数量累加
            resultVO.stream().filter(vo -> vo.getFactoryCode().equals(factoryCode)).forEach(vo -> vo.setDemandCount((vo.getDemandCount() == null ? 0 : vo.getDemandCount()) + needNumber));
        }
        partDrawingNoNeedNumberBOList = null;

        // 3.计算安全在库数量
        // 查安全在库数量
        List<SecurityBO> securityBOList = inventoryWarningDao.getSecurityBO(dateTime);
        for (SecurityBO securityBO : securityBOList) {
            String warehouseCode = securityBO.getWarehouseCode();
            String partDrawingNumber = securityBO.getPartDrawingNumber();
            // partDrawingNumber、warehouseCode 都相同确定一个单价
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNumber().equals(partDrawingNumber) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            String factoryCode = partDrawingCostPriceBO.getFactoryCode();
            long securityBOCount = securityBO.getCount();
            resultVO.stream().filter(vo -> vo.getFactoryCode().equals(factoryCode)).forEach(vo -> vo.setSafeCostCount((vo.getSafeCostCount() == null ? 0 : vo.getSafeCostCount()) + securityBOCount));
        }

        // 4.计算采购在途订单数量
        // 查采购在途订单数量
        List<PurchaseOrderCountBO> purchaseOrderCountBOList = purchaseOrderDao.selectPurchaseOrderCount(dateTime);
        for (PurchaseOrderCountBO purchaseOrderCountBO : purchaseOrderCountBOList) {
            String partDrawingNumber = purchaseOrderCountBO.getPartDrawingNumber();
            String warehouseCode = purchaseOrderCountBO.getWarehouseCode();
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNumber().equals(partDrawingNumber) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            String factoryCode = partDrawingCostPriceBO.getFactoryCode();
            long purchaseOrderCount = purchaseOrderCountBO.getCount();
            resultVO.stream().filter(vo -> vo.getFactoryCode().equals(factoryCode)).forEach(vo -> vo.setPurchaseInTransitCount((vo.getPurchaseInTransitCount() == null ? 0 : vo.getPurchaseInTransitCount()) + purchaseOrderCount));
        }
        // 4.1 计算采购在途合计、异常订单
        List<PurchaseOrderBO> purchaseOrderBOList = purchaseOrderDao.getPurchaseOrderBO(dateTime);
        for (PurchaseOrderBO purchaseOrderBO : purchaseOrderBOList) {
            String warehouseCode = purchaseOrderBO.getWarehouseCode();
            String partDrawingNumber = purchaseOrderBO.getPartDrawingNumber();
            // partDrawingNumber、warehouseCode 都相同确定工厂
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNumber().equals(partDrawingNumber) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            String factoryCode = partDrawingCostPriceBO.getFactoryCode();
            int purchaseInTransitTotalCount = purchaseOrderBO.getTotal();
            //int purchaseInTransitAbnormalCount = purchaseOrderBO.getAbnormal() == null ? 0 : purchaseOrderBO.getAbnormal();
            resultVO.stream().filter(vo -> vo.getFactoryCode().equals(factoryCode)).forEach(vo -> {
                vo.setPurchaseInTransitTotalCount(purchaseInTransitTotalCount + (vo.getPurchaseInTransitTotalCount() == null ? 0L : vo.getPurchaseInTransitTotalCount()));
                //vo.setPurchaseInTransitAbnormalCount(purchaseInTransitAbnormalCount + (vo.getPurchaseInTransitAbnormalCount() == null ? 0L : vo.getPurchaseInTransitAbnormalCount()));
            });
        }

        // 对金额 null 的字段值填充 0
        resultVO.forEach(vo -> {
            vo.setCostCount(vo.getCostCount() == null ? 0L : vo.getCostCount());
            vo.setDemandCount(vo.getDemandCount() == null ? 0L : vo.getDemandCount());
            vo.setPurchaseInTransitCount(vo.getPurchaseInTransitCount() == null ? 0L : vo.getPurchaseInTransitCount());
            vo.setSafeCostCount(vo.getSafeCostCount() == null ? 0L : vo.getSafeCostCount());
            vo.setPurchaseInTransitTotalCount(vo.getPurchaseInTransitTotalCount() == null ? 0L : vo.getPurchaseInTransitTotalCount());
            vo.setPurchaseInTransitAbnormalCount(vo.getPurchaseInTransitAbnormalCount() == null ? 0L : vo.getPurchaseInTransitAbnormalCount());
        });

        // 5.根据条件筛选过滤
        // 5.1 工厂名称 筛选在sql中完成
        // 5.2 工厂担当 筛选
        ConditionUtil userNameCondition = factoryCountQueryDTO.getUserName();
        if (!StringUtils.isEmpty(userNameCondition.getValue())) {
            String userNameConditionValue = userNameCondition.getValue();
            String userNameConditionType = userNameCondition.getType();
            if (StringUtils.isEmpty(userNameConditionType) || "0".equals(userNameConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getUserName().equals(userNameConditionValue)).collect(Collectors.toList());
            } else if ("1".equals(userNameConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getUserName().contains(userNameConditionValue)).collect(Collectors.toList());
            } else if ("2".equals(userNameConditionType)) {
                resultVO = resultVO.stream().filter(vo -> !vo.getUserName().contains(userNameConditionValue)).collect(Collectors.toList());
            }
        }
        // 5.3 在库数量 筛选
        ConditionUtil costCountCondition = factoryCountQueryDTO.getCostCount();
        if (!StringUtils.isEmpty(costCountCondition.getValue())) {
            long costCountConditionValue = Long.parseLong(costCountCondition.getValue());
            String costCountConditionType = costCountCondition.getType();
            if (StringUtils.isEmpty(costCountConditionType) || "=".equals(costCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() == costCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() >= costCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() > costCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() != costCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() <= costCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() < costCountConditionValue).collect(Collectors.toList());
            }
        }
        // 5.4 需求数量 筛选
        ConditionUtil demandCountCondition = factoryCountQueryDTO.getDemandCount();
        if (!StringUtils.isEmpty(demandCountCondition.getValue())) {
            double demandCountConditionValue = Long.parseLong(demandCountCondition.getValue());
            String demandCountConditionType = demandCountCondition.getType();
            if (StringUtils.isEmpty(demandCountConditionType) || "=".equals(demandCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() == demandCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() >= demandCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() > demandCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() != demandCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() <= demandCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() < demandCountConditionValue).collect(Collectors.toList());
            }
        }
        // 5.5 安全在库数量 筛选
        ConditionUtil safeCostCountCondition = factoryCountQueryDTO.getSafeCostCount();
        if (!StringUtils.isEmpty(safeCostCountCondition.getValue())) {
            double safeCostCountConditionValue = Long.parseLong(safeCostCountCondition.getValue());
            String safeCostCountConditionType = safeCostCountCondition.getType();
            if (StringUtils.isEmpty(safeCostCountConditionType) || "=".equals(safeCostCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() == safeCostCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() >= safeCostCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() > safeCostCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() != safeCostCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() <= safeCostCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() < safeCostCountConditionValue).collect(Collectors.toList());
            }
        }
        // 5.6 采购在途数量 筛选
        ConditionUtil purchaseInTransitCountCondition = factoryCountQueryDTO.getPurchaseInTransitCount();
        if (!StringUtils.isEmpty(purchaseInTransitCountCondition.getValue())) {
            double purchaseInTransitCountConditionValue = Double.parseDouble(purchaseInTransitCountCondition.getValue());
            String purchaseInTransitCountConditionType = purchaseInTransitCountCondition.getType();
            if (StringUtils.isEmpty(purchaseInTransitCountConditionType) || "=".equals(purchaseInTransitCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() == purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() >= purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() > purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() != purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() <= purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() < purchaseInTransitCountConditionValue).collect(Collectors.toList());
            }
        }

        // 6.排序（默认对在库数量desc降序排序）
        // 6.1 在库数量 排序
        String costCountConditionSort = costCountCondition.getSort();
        if (StringUtils.isEmpty(costCountConditionSort) || ConditionSortConstant.DESC.equals(costCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryCountVO::getCostCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(costCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryCountVO::getCostCount)).collect(Collectors.toList());
        }
        // 6.2 需求数量 排序
        String demandCountConditionSort = demandCountCondition.getSort();
        if (StringUtils.isEmpty(demandCountConditionSort) || ConditionSortConstant.DESC.equals(demandCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryCountVO::getDemandCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(demandCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryCountVO::getDemandCount)).collect(Collectors.toList());
        }
        // 6.3 安全在库金额 排序
        String safeCostCountConditionSort = safeCostCountCondition.getSort();
        if (StringUtils.isEmpty(safeCostCountConditionSort) || ConditionSortConstant.DESC.equals(safeCostCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryCountVO::getSafeCostCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(safeCostCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryCountVO::getSafeCostCount)).collect(Collectors.toList());
        }
        // 6.4 采购在途金额 排序
        String purchaseInTransitCountConditionSort = purchaseInTransitCountCondition.getSort();
        if (StringUtils.isEmpty(purchaseInTransitCountConditionSort) || ConditionSortConstant.DESC.equals(purchaseInTransitCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryCountVO::getPurchaseInTransitCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(purchaseInTransitCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(FactoryCountVO::getPurchaseInTransitCount)).collect(Collectors.toList());
        }

        // 7.分页
        long pageNum = factoryCountQueryDTO.getPageNum();
        long pageSize = factoryCountQueryDTO.getPageSize();
        long startRow = pageNum * pageSize - pageSize;
        int size = resultVO.size();
        resultVO = resultVO.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", resultVO);
        jsonObject.put("count", size);
        return RetResponse.makeOKRsp(jsonObject);
    }

    /**
     * 工厂别在库金额环形图
     * @param date 日期
     * @return 统一返回
     */
    @Override
    public RetResult<List<FactoryAmountRingChartVO>> getFactoryAmountRingChart(String date) {
        if (StringUtils.isEmpty(date)) {
            // 如果入参时间为空则取今天的时间
            date = LocalDate.now().toString();
        }
        String endDate = LocalDate.parse(date).plusDays(1).toString();
        List<FactoryUserIdAmountBO> factoryUserIdAmountBOList = new ArrayList<>();
        try {
            factoryUserIdAmountBOList = partDrawingStockDao.selectFactoryAmountByDate(date, endDate);
        } catch (Exception ignored) {
        }
        // 该日期的总在库金额
        List<BigDecimal> amountByUserId = inventoryWarningDao.getAmountByUserId(null, date, endDate);
        double sum = amountByUserId.stream().filter(Objects::nonNull).mapToDouble(BigDecimal::doubleValue).sum();
        BigDecimal sumBigDecimal = BigDecimal.valueOf(sum);
        List<FactoryAmountRingChartVO> resultVO = new ArrayList<>(factoryUserIdAmountBOList.size());
        factoryUserIdAmountBOList.forEach(factoryUserIdAmountBO -> {
            BigDecimal percent = factoryUserIdAmountBO.getFactoryAmount().divide(sumBigDecimal, 4, BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2, BigDecimal.ROUND_DOWN);
            FactoryAmountRingChartVO factoryAmountRingChartVO = FactoryAmountRingChartVO.builder()
                    .factoryName(factoryUserIdAmountBO.getFactoryName())
                    .amount(factoryUserIdAmountBO.getFactoryAmount().setScale(2, BigDecimal.ROUND_DOWN).doubleValue())
                    .percent(percent.toString())
                    .build();
            resultVO.add(factoryAmountRingChartVO);
        });

        return RetResponse.makeOKRsp(resultVO);
    }

    /**
     * 工厂别在库数量环形图
     * @param date 日期
     * @return 统一返回
     */
    @Override
    public RetResult<List<FactoryCountRingChartVO>> getFactoryCountRingChart(String date) {
        if (StringUtils.isEmpty(date)) {
            // 如果入参时间为空则取今天的时间
            date = LocalDate.now().toString();
        }
        String endDate = LocalDate.parse(date).plusDays(1).toString();
        List<FactoryCountBO> factoryCountBOList = new ArrayList<>();
        try {
            factoryCountBOList = partDrawingStockDao.selectFactoryCountByDate(date, endDate);
        } catch (Exception ignored) {
        }
        // 该日期的总在库数量
        long count = partDrawingStockDao.selectCountByDate(date, endDate);
        List<FactoryCountRingChartVO> resultVO = new ArrayList<>(factoryCountBOList.size());
        factoryCountBOList.forEach(factoryCountBO -> {
            double percent = Double.valueOf(factoryCountBO.getRealityNumber()) / count;
            FactoryCountRingChartVO factoryCountRingChartVO = FactoryCountRingChartVO.builder()
                    .factoryName(factoryCountBO.getFactoryName())
                    .count(factoryCountBO.getRealityNumber())
                    .percent(BigDecimal.valueOf(percent).setScale(2, BigDecimal.ROUND_HALF_UP).toString())
                    .build();
            resultVO.add(factoryCountRingChartVO);
        });
        return RetResponse.makeOKRsp(resultVO);
    }

    /**
     * 部品库存部品图号在库金额表格
     * @param partDrawingAmountQueryDTO 查询条件
     * @return 统一返回
     */
    @Override
    public RetResult<JSONObject> getPartDrawingNoAmount(PartDrawingAmountQueryDTO partDrawingAmountQueryDTO) {
        String dateTime = partDrawingAmountQueryDTO.getDateTime();
        if (StringUtils.isEmpty(dateTime)) {
            // 如果入参时间为空则取今天的时间
            dateTime = LocalDate.now().toString();
        }
        String endDate = LocalDate.parse(dateTime).plusDays(1).toString();
        // 1.查询部件图号-在库金额（仟元）
        List<PartDrawingNoAmountBO> partDrawingNoAmountBOList = new ArrayList<>();
        try {
            partDrawingNoAmountBOList = partDrawingStockDao.selectPartDrawingNoAmountByDatePartDrawingNo(dateTime, endDate, partDrawingAmountQueryDTO.getFactoryCode(), partDrawingAmountQueryDTO.getPartDrawingNo());
        } catch (Exception ignored) {
        }
        // 最终返回视图
        List<PartDrawingNoAmountVO> resultVO = new ArrayList<>(partDrawingNoAmountBOList.size());
        // 将 部件图号、在库金额 插入视图
        for (PartDrawingNoAmountBO partDrawingNoAmountBO : partDrawingNoAmountBOList) {
            PartDrawingNoAmountVO vo = PartDrawingNoAmountVO.builder()
                    .partDrawingNo(partDrawingNoAmountBO.getPartDrawingNo())
                    .costAmount(partDrawingNoAmountBO.getAmount().setScale(2, BigDecimal.ROUND_DOWN).doubleValue())
                    .build();
            resultVO.add(vo);
        }
        // 2.计算缺件订单量
        List<MissDealOrderBO> missDealOrderBOList = missDealOrderDao.selectMissDealOrderCount(dateTime, partDrawingAmountQueryDTO.getFactoryCode());
        for (MissDealOrderBO missDealOrderBO : missDealOrderBOList) {
            String partDrawingNo = missDealOrderBO.getPartDrawingNo();
            long missDealOrderBOCount = missDealOrderBO.getCount();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setShortageOrderCount(missDealOrderBOCount + (vo.getShortageOrderCount() == null ? 0 : vo.getShortageOrderCount())));
        }

        // 3.计算需求金额
        // 查部件图号的需要数量
        List<PartDrawingNoNeedNumberBO> partDrawingNoNeedNumberBOList = orderDetailDao.selectPartDrawingNoNeedNumberByDate(partDrawingAmountQueryDTO.getDateTime());
        // 查成本单价
        List<PartDrawingCostPriceBO> partDrawingCostPriceBOList = new ArrayList<>();
        try {
            partDrawingCostPriceBOList = partDrawingStockDao.selectCostPrice(dateTime, endDate,partDrawingAmountQueryDTO.getFactoryCode());
        } catch (Exception ignored) {
        }
        for (PartDrawingNoNeedNumberBO partDrawingNoNeedNumberBO : partDrawingNoNeedNumberBOList) {
            String partDrawingNo = partDrawingNoNeedNumberBO.getPartDrawingNo();
            String warehouseCode = partDrawingNoNeedNumberBO.getWarehouseCode();
            // partDrawingNo、warehouseCode 都相同确定一个单价
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNo().equals(partDrawingNo) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            // 某仓库一个部件的需求金额（仟元）
            BigDecimal demandAmountBigDecimalValue = partDrawingCostPriceBO.getCostPrice().multiply(BigDecimal.valueOf(partDrawingNoNeedNumberBO.getNeedNumber())).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_DOWN);
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setDemandAmount(demandAmountBigDecimalValue.add(BigDecimal.valueOf(vo.getDemandAmount() == null ? 0 : vo.getDemandAmount())).doubleValue()));
        }

        // 4.计算安全在库金额
        // 查安全在库数量
        List<SecurityBO> securityBOList = inventoryWarningDao.getSecurityBO(dateTime);
        for (SecurityBO securityBO : securityBOList) {
            String warehouseCode = securityBO.getWarehouseCode();
            String partDrawingNumber = securityBO.getPartDrawingNumber();
            // partDrawingNumber、warehouseCode 都相同确定一个单价
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNumber().equals(partDrawingNumber) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            BigDecimal safeCostAmountBigDecimalValue = partDrawingCostPriceBO.getCostPrice().multiply(BigDecimal.valueOf(securityBO.getCount())).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_DOWN);
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNumber)).forEach(vo -> vo.setSafeCostAmount(safeCostAmountBigDecimalValue.add(BigDecimal.valueOf(vo.getSafeCostAmount() == null ? 0 : vo.getSafeCostAmount())).doubleValue()));
        }

        // 5.计算采购在途订单金额
        // 查采购在途订单数量
        List<PurchaseOrderCountBO> purchaseOrderCountBOList = purchaseOrderDao.selectPurchaseOrderCount(dateTime);
        for (PurchaseOrderCountBO purchaseOrderCountBO : purchaseOrderCountBOList) {
            String partDrawingNo = purchaseOrderCountBO.getPartDrawingNo();
            String warehouseCode = purchaseOrderCountBO.getWarehouseCode();
            // partDrawingNo、warehouseCode 都相同确定一个单价
            PartDrawingCostPriceBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNo().equals(partDrawingNo) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            BigDecimal purchaseInTransitAmountBigDecimalValue = partDrawingCostPriceBO.getCostPrice().multiply(BigDecimal.valueOf(purchaseOrderCountBO.getCount())).divide(BigDecimal.valueOf(1000), 2, BigDecimal.ROUND_DOWN);
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setPurchaseInTransitAmount(purchaseInTransitAmountBigDecimalValue.add(BigDecimal.valueOf(vo.getPurchaseInTransitAmount() == null ? 0 : vo.getPurchaseInTransitAmount())).doubleValue()));
        }
        // 5.1 合计采购在途订单、异常订单
        List<PurchaseOrderBO> purchaseOrderBOList = purchaseOrderDao.getPurchaseOrderBO(dateTime);
        for (PurchaseOrderBO purchaseOrderBO : purchaseOrderBOList) {
            String partDrawingNo = purchaseOrderBO.getPartDrawingNo();
            int purchaseInTransitTotalCount = purchaseOrderBO.getTotal();
            //int purchaseInTransitAbnormalCount = purchaseOrderBO.getAbnormal() == null ? 0 : purchaseOrderBO.getAbnormal();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> {
                vo.setPurchaseInTransitTotalCount(purchaseInTransitTotalCount + (vo.getPurchaseInTransitTotalCount() == null ? 0L : vo.getPurchaseInTransitTotalCount()));
                //vo.setPurchaseInTransitAbnormalCount(purchaseInTransitAbnormalCount + (vo.getPurchaseInTransitAbnormalCount() == null ? 0L : vo.getPurchaseInTransitAbnormalCount()));
            });
        }

        // 6.计算服务店订单量
        List<ServiceStoreOrderCountBO> serviceStoreOrderCountBOList = orderDetailDao.selectServiceStoreOrderCount(dateTime);
        for (ServiceStoreOrderCountBO serviceStoreOrderCountBO : serviceStoreOrderCountBOList) {
            String partDrawingNo = serviceStoreOrderCountBO.getPartDrawingNo();
            long count = serviceStoreOrderCountBO.getCount();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setServiceStoreOrderCount(count));
        }
        // 6.1 计算服务店异常订单
        List<ServiceStoreOrderAbnormalCountBO> serviceStoreOrderAbnormalCountBOList = orderDetailDao.selectServiceStoreOrderAbnormalCount(dateTime);
        for (ServiceStoreOrderAbnormalCountBO serviceStoreOrderAbnormalCountBO : serviceStoreOrderAbnormalCountBOList) {
            String partDrawingNo = serviceStoreOrderAbnormalCountBO.getPartDrawingNo();
            long count = serviceStoreOrderAbnormalCountBO.getCount();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setServiceStoreOrderAbnormalCount(count + (vo.getServiceStoreOrderAbnormalCount() == null ? 0L : vo.getServiceStoreOrderAbnormalCount())));
        }

        // 对金额/数量 null 的字段值填充 0
        resultVO.forEach(vo -> {
            vo.setShortageOrderCount(vo.getShortageOrderCount() == null ? 0L : vo.getShortageOrderCount());
            vo.setCostAmount(vo.getCostAmount() == null ? 0.0 : vo.getCostAmount());
            vo.setDemandAmount(vo.getDemandAmount() == null ? 0.0 : vo.getDemandAmount());
            vo.setPurchaseInTransitAmount(vo.getPurchaseInTransitAmount() == null ? 0.0 : vo.getPurchaseInTransitAmount());
            vo.setSafeCostAmount(vo.getSafeCostAmount() == null ? 0.0 : vo.getSafeCostAmount());
            vo.setServiceStoreOrderCount(vo.getServiceStoreOrderCount() == null ? 0L : vo.getServiceStoreOrderCount());
            vo.setPurchaseInTransitTotalCount(vo.getPurchaseInTransitTotalCount() == null ? 0L : vo.getPurchaseInTransitTotalCount());
            vo.setPurchaseInTransitAbnormalCount(vo.getPurchaseInTransitAbnormalCount() == null ? 0L : vo.getPurchaseInTransitAbnormalCount());
            vo.setServiceStoreOrderAbnormalCount(vo.getServiceStoreOrderAbnormalCount() == null ? 0L : vo.getServiceStoreOrderAbnormalCount());
        });

        // 7.根据条件筛选过滤
        // 7.1 部件图号 筛选在sql中完成
        // 7.2 缺件订单量 筛选
        ConditionUtil shortageOrderCountCondition = partDrawingAmountQueryDTO.getShortageOrderCount();
        if (!StringUtils.isEmpty(shortageOrderCountCondition.getValue())) {
            long shortageOrderCountConditionValue = Long.parseLong(shortageOrderCountCondition.getValue());
            String shortageOrderCountConditionType = shortageOrderCountCondition.getType();
            if (StringUtils.isEmpty(shortageOrderCountConditionType) || "=".equals(shortageOrderCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() == shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() >= shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() > shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() != shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() <= shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() < shortageOrderCountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.3 在库金额 筛选
        ConditionUtil costAmountCondition = partDrawingAmountQueryDTO.getCostAmount();
        if (!StringUtils.isEmpty(costAmountCondition.getValue())) {
            double costAmountConditionValue = Double.parseDouble(costAmountCondition.getValue());
            String costAmountConditionType = costAmountCondition.getType();
            if (StringUtils.isEmpty(costAmountConditionType) || "=".equals(costAmountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() == costAmountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() >= costAmountConditionValue).collect(Collectors.toList());
            } else if (">".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() > costAmountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() != costAmountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() <= costAmountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(costAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostAmount() < costAmountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.4 需求金额 筛选
        ConditionUtil demandAmountCondition = partDrawingAmountQueryDTO.getDemandAmount();
        if (!StringUtils.isEmpty(demandAmountCondition.getValue())) {
            double demandAmountConditionValue = Double.parseDouble(demandAmountCondition.getValue());
            String demandAmountConditionType = demandAmountCondition.getType();
            if (StringUtils.isEmpty(demandAmountConditionType) || "=".equals(demandAmountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() == demandAmountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() >= demandAmountConditionValue).collect(Collectors.toList());
            } else if (">".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() > demandAmountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() != demandAmountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() <= demandAmountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(demandAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandAmount() < demandAmountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.5 安全在库金额 筛选
        ConditionUtil safeCostAmountCondition = partDrawingAmountQueryDTO.getSafeCostAmount();
        if (!StringUtils.isEmpty(safeCostAmountCondition.getValue())) {
            double safeCostAmountConditionValue = Double.parseDouble(safeCostAmountCondition.getValue());
            String safeCostAmountConditionType = safeCostAmountCondition.getType();
            if (StringUtils.isEmpty(safeCostAmountConditionType) || "=".equals(safeCostAmountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() == safeCostAmountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() >= safeCostAmountConditionValue).collect(Collectors.toList());
            } else if (">".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() > safeCostAmountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() != safeCostAmountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() <= safeCostAmountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(safeCostAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostAmount() < safeCostAmountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.6 采购在途金额 筛选
        ConditionUtil purchaseInTransitAmountCondition = partDrawingAmountQueryDTO.getPurchaseInTransitAmount();
        if (!StringUtils.isEmpty(purchaseInTransitAmountCondition.getValue())) {
            double purchaseInTransitAmountConditionValue = Double.parseDouble(purchaseInTransitAmountCondition.getValue());
            String purchaseInTransitAmountConditionType = purchaseInTransitAmountCondition.getType();
            if (StringUtils.isEmpty(purchaseInTransitAmountConditionType) || "=".equals(purchaseInTransitAmountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() == purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() >= purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if (">".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() > purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() != purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() <= purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(purchaseInTransitAmountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitAmount() < purchaseInTransitAmountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.7 服务店订单量
        ConditionUtil serviceStoreOrderCountCondition = partDrawingAmountQueryDTO.getServiceStoreOrderCount();
        if (!StringUtils.isEmpty(serviceStoreOrderCountCondition.getValue())) {
            long serviceStoreOrderCountConditionValue = Long.parseLong(serviceStoreOrderCountCondition.getValue());
            String serviceStoreOrderCountConditionType = serviceStoreOrderCountCondition.getType();
            if (StringUtils.isEmpty(serviceStoreOrderCountConditionType) || "=".equals(serviceStoreOrderCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() == serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() >= serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() > serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() != serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() <= serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() < serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            }
        }

        // 8.排序（默认对在库金额desc降序排序）
        // 8.1 在库金额 排序
        String costAmountConditionSort = costAmountCondition.getSort();
        if (StringUtils.isEmpty(costAmountConditionSort) || ConditionSortConstant.DESC.equals(costAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getCostAmount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(costAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getCostAmount)).collect(Collectors.toList());
        }
        // 8.2 缺件订单量 排序
        String shortageOrderCountConditionSort = shortageOrderCountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(shortageOrderCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getShortageOrderCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(shortageOrderCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getShortageOrderCount)).collect(Collectors.toList());
        }
        // 8.3 需求金额 排序
        String demandAmountConditionSort = demandAmountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(demandAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getDemandAmount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(demandAmountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getDemandAmount)).collect(Collectors.toList());
        }
        // 8.4 安全在库金额 排序
        String safeCostAmountConditionSort = safeCostAmountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(safeCostAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getSafeCostAmount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(safeCostAmountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getSafeCostAmount)).collect(Collectors.toList());
        }
        // 8.5 采购在途金额 排序
        String purchaseInTransitAmountConditionSort = purchaseInTransitAmountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(purchaseInTransitAmountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getPurchaseInTransitAmount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(purchaseInTransitAmountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getPurchaseInTransitAmount)).collect(Collectors.toList());
        }
        // 8.6 服务店订单量 排序
        String serviceStoreOrderCountConditionSort = serviceStoreOrderCountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(serviceStoreOrderCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getServiceStoreOrderCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(serviceStoreOrderCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoAmountVO::getServiceStoreOrderCount)).collect(Collectors.toList());
        }
        // 9.分页
        long pageNum = partDrawingAmountQueryDTO.getPageNum();
        long pageSize = partDrawingAmountQueryDTO.getPageSize();
        long startRow = pageNum * pageSize - pageSize;
        int size = resultVO.size();
        resultVO = resultVO.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", resultVO);
        jsonObject.put("count", size);
        return RetResponse.makeOKRsp(jsonObject);
    }

    /**
     * 部品库存部品图号在库数量表格
     * @param partDrawingCountQueryDTO 查询条件
     * @return 统一返回
     */
    @Override
    public RetResult<JSONObject> getPartDrawingNoCount(PartDrawingCountQueryDTO partDrawingCountQueryDTO) {
        String dateTime = partDrawingCountQueryDTO.getDateTime();
        if (StringUtils.isEmpty(dateTime)) {
            // 如果入参时间为空则取今天的时间
            dateTime = LocalDate.now().toString();
        }
        String endDate = LocalDate.parse(dateTime).plusDays(1).toString();
        // 1.查询部件图号-图号描述-在库数量
        List<PartDrawingNoCountBO> partDrawingNoCountBOList = new ArrayList<>();
        try {
            partDrawingNoCountBOList = partDrawingStockDao.selectPartDrawingNoCountByDatePartDrawingNo(dateTime, endDate, partDrawingCountQueryDTO.getFactoryCode(), partDrawingCountQueryDTO.getPartDrawingNo());
        } catch (Exception ignored) {
        }
        // 最终返回视图
        List<PartDrawingNoCountVO> resultVO = new ArrayList<>(partDrawingNoCountBOList.size());
        // 将 部件图号、图号描述、在库数量 插入视图
        for (PartDrawingNoCountBO partDrawingNoCountBO : partDrawingNoCountBOList) {
            PartDrawingNoCountVO vo = PartDrawingNoCountVO.builder()
                    .partDrawingNo(partDrawingNoCountBO.getPartDrawingNo())
                    .describedDrawingNo(partDrawingNoCountBO.getDescribedDrawingNo())
                    .costCount(partDrawingNoCountBO.getPartDrawingCount())
                    .build();
            resultVO.add(vo);
        }
        // 2.计算缺件订单量
        List<MissDealOrderBO> missDealOrderBOList = missDealOrderDao.selectMissDealOrderCount(dateTime, partDrawingCountQueryDTO.getFactoryCode());
        for (MissDealOrderBO missDealOrderBO : missDealOrderBOList) {
            String partDrawingNo = missDealOrderBO.getPartDrawingNo();
            long missDealOrderBOCount = missDealOrderBO.getCount();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setShortageOrderCount(missDealOrderBOCount + (vo.getShortageOrderCount() == null ? 0 : vo.getShortageOrderCount())));
        }

        // 3.计算需求数量
        // 查部件图号的需要数量
        List<PartDrawingNoNeedNumberBO> partDrawingNoNeedNumberBOList = orderDetailDao.selectPartDrawingNoNeedNumberByDate(partDrawingCountQueryDTO.getDateTime());
        for (PartDrawingNoNeedNumberBO partDrawingNoNeedNumberBO : partDrawingNoNeedNumberBOList) {
            String partDrawingNo = partDrawingNoNeedNumberBO.getPartDrawingNo();
            long needNumber = partDrawingNoNeedNumberBO.getNeedNumber();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setDemandCount((vo.getDemandCount() == null ? 0 : vo.getDemandCount()) + needNumber));
        }
        partDrawingNoNeedNumberBOList = null;

        // 4.计算安全在库数量
        // 查安全在库数量
        List<SecurityBO> securityBOList = inventoryWarningDao.getSecurityBO(dateTime);
        for (SecurityBO securityBO : securityBOList) {
            String partDrawingNumber = securityBO.getPartDrawingNumber();
            long securityBOCount = securityBO.getCount();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNumber)).forEach(vo -> vo.setSafeCostCount((vo.getSafeCostCount() == null ? 0 : vo.getSafeCostCount()) + securityBOCount));
        }

        // 5.计算采购在途订单数量
        // 查采购在途订单数量
        List<PurchaseOrderCountBO> purchaseOrderCountBOList = purchaseOrderDao.selectPurchaseOrderCount(dateTime);
        for (PurchaseOrderCountBO purchaseOrderCountBO : purchaseOrderCountBOList) {
            String partDrawingNo = purchaseOrderCountBO.getPartDrawingNo();
            long purchaseOrderCount = purchaseOrderCountBO.getCount();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setPurchaseInTransitCount((vo.getPurchaseInTransitCount() == null ? 0 : vo.getPurchaseInTransitCount()) + purchaseOrderCount));
        }
        // 5.1计算采购在途异常订单
        /*List<PurchaseOrderBO> purchaseOrderBOList = purchaseOrderDao.getPurchaseOrderBO(dateTime);
        for (PurchaseOrderBO purchaseOrderBO : purchaseOrderBOList) {
            String partDrawingNo = purchaseOrderBO.getPartDrawingNo();
            int purchaseInTransitAbnormalCount = purchaseOrderBO.getAbnormal() == null ? 0 : purchaseOrderBO.getAbnormal();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> {
                vo.setPurchaseInTransitAbnormalCount(purchaseInTransitAbnormalCount + (vo.getPurchaseInTransitAbnormalCount() == null ? 0 : vo.getPurchaseInTransitAbnormalCount()));
            });
        }*/

        // 6.服务店订单量
        List<ServiceStoreOrderCountBO> serviceStoreOrderCountBOList = orderDetailDao.selectServiceStoreOrderCount(dateTime);
        for (ServiceStoreOrderCountBO serviceStoreOrderCountBO : serviceStoreOrderCountBOList) {
            String partDrawingNo = serviceStoreOrderCountBO.getPartDrawingNo();
            long count = serviceStoreOrderCountBO.getCount();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setServiceStoreOrderCount(count));
        }
        // 6.1 计算服务店异常订单
        List<ServiceStoreOrderAbnormalCountBO> serviceStoreOrderAbnormalCountBOList = orderDetailDao.selectServiceStoreOrderAbnormalCount(dateTime);
        for (ServiceStoreOrderAbnormalCountBO serviceStoreOrderAbnormalCountBO : serviceStoreOrderAbnormalCountBOList) {
            String partDrawingNo = serviceStoreOrderAbnormalCountBO.getPartDrawingNo();
            long count = serviceStoreOrderAbnormalCountBO.getCount();
            resultVO.stream().filter(vo -> vo.getPartDrawingNo().equals(partDrawingNo)).forEach(vo -> vo.setServiceStoreOrderAbnormalCount(count + (vo.getServiceStoreOrderAbnormalCount() == null ? 0L : vo.getServiceStoreOrderAbnormalCount())));
        }

        // 对金额/数量 null 的字段值填充 0
        resultVO.forEach(vo -> {
            vo.setShortageOrderCount(vo.getShortageOrderCount() == null ? 0L : vo.getShortageOrderCount());
            vo.setCostCount(vo.getCostCount() == null ? 0L : vo.getCostCount());
            vo.setDemandCount(vo.getDemandCount() == null ? 0L : vo.getDemandCount());
            vo.setPurchaseInTransitCount(vo.getPurchaseInTransitCount() == null ? 0L : vo.getPurchaseInTransitCount());
            vo.setSafeCostCount(vo.getSafeCostCount() == null ? 0L : vo.getSafeCostCount());
            vo.setServiceStoreOrderCount(vo.getServiceStoreOrderCount() == null ? 0L : vo.getServiceStoreOrderCount());
            vo.setPurchaseInTransitAbnormalCount(vo.getPurchaseInTransitAbnormalCount() == null ? 0L : vo.getPurchaseInTransitAbnormalCount());
            vo.setServiceStoreOrderAbnormalCount(vo.getServiceStoreOrderAbnormalCount() == null ? 0L : vo.getServiceStoreOrderAbnormalCount());
        });

        // 7.根据条件筛选过滤
        // 7.1 部件图号 筛选在sql中完成
        // 7.2 缺件订单量 筛选
        ConditionUtil shortageOrderCountCondition = partDrawingCountQueryDTO.getShortageOrderCount();
        if (!StringUtils.isEmpty(shortageOrderCountCondition.getValue())) {
            long shortageOrderCountConditionValue = Long.parseLong(shortageOrderCountCondition.getValue());
            String shortageOrderCountConditionType = shortageOrderCountCondition.getType();
            if (StringUtils.isEmpty(shortageOrderCountConditionType) || "=".equals(shortageOrderCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() == shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() >= shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() > shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() != shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() <= shortageOrderCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(shortageOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getShortageOrderCount() < shortageOrderCountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.3 在库数量 筛选
        ConditionUtil costCountCondition = partDrawingCountQueryDTO.getCostCount();
        if (!StringUtils.isEmpty(costCountCondition.getValue())) {
            long costCountConditionValue = Long.parseLong(costCountCondition.getValue());
            String costCountConditionType = costCountCondition.getType();
            if (StringUtils.isEmpty(costCountConditionType) || "=".equals(costCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() == costCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() >= costCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() > costCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() != costCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() <= costCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(costCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getCostCount() < costCountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.4 需求数量 筛选
        ConditionUtil demandCountCondition = partDrawingCountQueryDTO.getDemandCount();
        if (!StringUtils.isEmpty(demandCountCondition.getValue())) {
            long demandCountConditionValue = Long.parseLong(demandCountCondition.getValue());
            String demandCountConditionType = demandCountCondition.getType();
            if (StringUtils.isEmpty(demandCountConditionType) || "=".equals(demandCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() == demandCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() >= demandCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() > demandCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() != demandCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() <= demandCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(demandCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getDemandCount() < demandCountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.5 安全在库数量 筛选
        ConditionUtil safeCostCountCondition = partDrawingCountQueryDTO.getSafeCostCount();
        if (!StringUtils.isEmpty(safeCostCountCondition.getValue())) {
            long safeCostCountConditionValue = Long.parseLong(safeCostCountCondition.getValue());
            String safeCostCountConditionType = safeCostCountCondition.getType();
            if (StringUtils.isEmpty(safeCostCountConditionType) || "=".equals(safeCostCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() == safeCostCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() >= safeCostCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() > safeCostCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() != safeCostCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() <= safeCostCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(safeCostCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getSafeCostCount() < safeCostCountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.6 采购在途数量 筛选
        ConditionUtil purchaseInTransitCountCondition = partDrawingCountQueryDTO.getPurchaseInTransitCount();
        if (!StringUtils.isEmpty(purchaseInTransitCountCondition.getValue())) {
            long purchaseInTransitCountConditionValue = Long.parseLong(purchaseInTransitCountCondition.getValue());
            String purchaseInTransitCountConditionType = purchaseInTransitCountCondition.getType();
            if (StringUtils.isEmpty(purchaseInTransitCountConditionType) || "=".equals(purchaseInTransitCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() == purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() >= purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() > purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() != purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() <= purchaseInTransitCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(purchaseInTransitCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getPurchaseInTransitCount() < purchaseInTransitCountConditionValue).collect(Collectors.toList());
            }
        }
        // 7.7 服务店订单量
        ConditionUtil serviceStoreOrderCountCondition = partDrawingCountQueryDTO.getServiceStoreOrderCount();
        if (!StringUtils.isEmpty(serviceStoreOrderCountCondition.getValue())) {
            long serviceStoreOrderCountConditionValue = Long.parseLong(serviceStoreOrderCountCondition.getValue());
            String serviceStoreOrderCountConditionType = serviceStoreOrderCountCondition.getType();
            if (StringUtils.isEmpty(serviceStoreOrderCountConditionType) || "=".equals(serviceStoreOrderCountConditionType)) {
                // 有值无type默认为等值查询
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() == serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if (">=".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() >= serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if (">".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() > serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if ("!=".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() != serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if ("<=".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() <= serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            } else if ("<".equals(serviceStoreOrderCountConditionType)) {
                resultVO = resultVO.stream().filter(vo -> vo.getServiceStoreOrderCount() < serviceStoreOrderCountConditionValue).collect(Collectors.toList());
            }
        }

        // 8.排序（默认对在库金额desc降序排序）
        // 8.1 在库数量 排序
        String costCountConditionSort = costCountCondition.getSort();
        if (StringUtils.isEmpty(costCountConditionSort) || ConditionSortConstant.DESC.equals(costCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getCostCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(costCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getCostCount)).collect(Collectors.toList());
        }
        // 8.2 缺件订单量 排序
        String shortageOrderCountConditionSort = shortageOrderCountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(shortageOrderCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getShortageOrderCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(shortageOrderCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getShortageOrderCount)).collect(Collectors.toList());
        }
        // 8.3 需求数量 排序
        String demandCountConditionSort = demandCountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(demandCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getDemandCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(demandCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getDemandCount)).collect(Collectors.toList());
        }
        // 8.4 安全在库数量 排序
        String safeCostCountConditionSort = safeCostCountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(safeCostCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getSafeCostCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(safeCostCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getSafeCostCount)).collect(Collectors.toList());
        }
        // 8.5 采购在途数量 排序
        String purchaseInTransitCountConditionSort = purchaseInTransitCountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(purchaseInTransitCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getPurchaseInTransitCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(purchaseInTransitCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getPurchaseInTransitCount)).collect(Collectors.toList());
        }
        // 8.6 服务店订单量 排序
        String serviceStoreOrderCountConditionSort = serviceStoreOrderCountCondition.getSort();
        if (ConditionSortConstant.DESC.equals(serviceStoreOrderCountConditionSort)) {
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getServiceStoreOrderCount).reversed()).collect(Collectors.toList());
        } else if (ConditionSortConstant.ASC.equals(serviceStoreOrderCountConditionSort)){
            resultVO = resultVO.stream().sorted(Comparator.comparing(PartDrawingNoCountVO::getServiceStoreOrderCount)).collect(Collectors.toList());
        }
        // 9.分页
        long pageNum = partDrawingCountQueryDTO.getPageNum();
        long pageSize = partDrawingCountQueryDTO.getPageSize();
        long startRow = pageNum * pageSize - pageSize;
        int size = resultVO.size();
        resultVO = resultVO.stream().skip(startRow).limit(pageSize).collect(Collectors.toList());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("data", resultVO);
        jsonObject.put("count", size);
        return RetResponse.makeOKRsp(jsonObject);
    }
}
