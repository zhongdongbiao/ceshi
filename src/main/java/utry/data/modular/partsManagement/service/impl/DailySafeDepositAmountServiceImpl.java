package utry.data.modular.partsManagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import utry.data.enums.BizCodeEnum;
import utry.data.modular.partsManagement.bo.PartDrawingCostPriceRangeTimeBO;
import utry.data.modular.partsManagement.bo.SecurityBO;
import utry.data.modular.partsManagement.dao.DailySafeDepositAmountDao;
import utry.data.modular.partsManagement.dao.InventoryWarningDao;
import utry.data.modular.partsManagement.dao.PartDrawingStockDao;
import utry.data.modular.partsManagement.model.DailySafeDepositAmount;
import utry.data.modular.partsManagement.service.DailySafeDepositAmountService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @program: data
 * @description: 每日安全在库金额业务实现类
 * @author: WangXinhao
 * @create: 2022-06-20 17:00
 **/
@Service
public class DailySafeDepositAmountServiceImpl implements DailySafeDepositAmountService {

    @Autowired
    private InventoryWarningDao inventoryWarningDao;

    @Autowired
    private PartDrawingStockDao partDrawingStockDao;

    @Autowired
    private DailySafeDepositAmountDao dailySafeDepositAmountDao;

    @Override
    public RetResult<Double> calculateAndInsertSafeDepositAmountByDate(String date) {
        if (StringUtils.isEmpty(date)) {
            return RetResponse.makeErrRsp(BizCodeEnum.PARAMETER_EMPTY.getMessage());
        }
        StopWatch stopWatch = new StopWatch("calculateAndInsertSafeDepositAmountByDate");
        stopWatch.start("查出该日 安全在库数量");
        BigDecimal amount = calculateSafeDepositAmountByDate(date);
        stopWatch.stop();

        stopWatch.start("插入数据库");
        int count = insertOrUpdateSafeDepositAmount(date, amount);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

        return RetResponse.makeOKRsp(amount.doubleValue());
    }

    @Override
    public BigDecimal calculateSafeDepositAmountByDate(String date) {
        Objects.requireNonNull(date, "date");
        String endDate = LocalDate.parse(date).plusDays(1).toString();
        // 查出该日 安全在库数量
        List<SecurityBO> safeDepositCountList = inventoryWarningDao.getAllSecurityBO(date, endDate);
        Assert.notNull(safeDepositCountList, BizCodeEnum.NO_DATA.getMessage());
        // 查该日 单价
        List<PartDrawingCostPriceRangeTimeBO> partDrawingCostPriceBOList = partDrawingStockDao.selectPartDrawingCostPriceRangeTime(date, endDate);
        Assert.isTrue(!partDrawingCostPriceBOList.isEmpty(), BizCodeEnum.NO_DATA.getMessage());

        // 计算
        // 总安全在库金额
        BigDecimal amount = new BigDecimal(0);
        for (SecurityBO securityBO : safeDepositCountList) {
            String partDrawingNo = securityBO.getPartDrawingNo();
            String warehouseCode = securityBO.getWarehouseCode();
            PartDrawingCostPriceRangeTimeBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNo().equals(partDrawingNo) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            amount = amount.add(partDrawingCostPriceBO.getCostPrice().multiply(BigDecimal.valueOf(securityBO.getCount())));
        }
        return amount;
    }

    @Override
    public int insertOrUpdateSafeDepositAmount(String date, BigDecimal amount) {
        Date createUpdateTime = new Date();
        int count = 0;
        DailySafeDepositAmount dailySafeDepositAmount = DailySafeDepositAmount.builder()
                .amount(amount)
                .date(LocalDate.parse(date))
                .updateTime(createUpdateTime)
                .build();
        // 判断当日数据是否存在
        if (existByDate(date)) {
            // 存在-更新数据
            count = dailySafeDepositAmountDao.updateByDate(dailySafeDepositAmount, date);
        } else {
            // 不存在-插入数据库
            dailySafeDepositAmount.setCreateTime(createUpdateTime);
            count = dailySafeDepositAmountDao.insert(dailySafeDepositAmount);
        }
        return count;
    }

    /**
     * 判断某天需求金额是否已存在
     * @param date 日期
     * @return true存在；false不存在
     */
    private boolean existByDate(String date) {
        int existCount = dailySafeDepositAmountDao.selectCountByDate(date);
        return existCount != 0;
    }
}
