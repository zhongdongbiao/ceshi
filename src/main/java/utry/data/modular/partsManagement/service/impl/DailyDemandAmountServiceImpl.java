package utry.data.modular.partsManagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import utry.data.enums.BizCodeEnum;
import utry.data.modular.partsManagement.bo.PartDrawingCostPriceRangeTimeBO;
import utry.data.modular.partsManagement.bo.PartDrawingNoNeedNumberOrderStartTimeBO;
import utry.data.modular.partsManagement.dao.DailyDemandAmountDao;
import utry.data.modular.partsManagement.dao.OrderDetailDao;
import utry.data.modular.partsManagement.dao.PartDrawingStockDao;
import utry.data.modular.partsManagement.model.DailyDemandAmount;
import utry.data.modular.partsManagement.service.DailyDemandAmountService;
import utry.data.util.RetResponse;
import utry.data.util.RetResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @program: data
 * @description: 每日需求金额业务实现类
 * @author: WangXinhao
 * @create: 2022-06-20 16:20
 **/
@Service
public class DailyDemandAmountServiceImpl implements DailyDemandAmountService {

    @Autowired
    private OrderDetailDao orderDetailDao;

    @Autowired
    private PartDrawingStockDao partDrawingStockDao;

    @Autowired
    private DailyDemandAmountDao dailyDemandAmountDao;

    /**
     * 计算并插入指定日期需求金额
     * @param date 日期
     * @return 需求金额
     */
    @Override
    public RetResult<Double> calculateAndInsertDemandAmountByDate(String date) {
        if (StringUtils.isEmpty(date)) {
            return RetResponse.makeErrRsp(BizCodeEnum.PARAMETER_EMPTY.getMessage());
        }
        StopWatch stopWatch = new StopWatch("calculateAndInsertDemandAmountByDate");
        stopWatch.start("计算需求金额");
        BigDecimal amount = calculateDemandAmountByDate(date);
        stopWatch.stop();

        stopWatch.start("插入数据库");
        int count = insertOrUpdateDailyDemandAmount(date, amount);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());

        return RetResponse.makeOKRsp(amount.doubleValue());
    }

    /**
     * 判断某天需求金额是否已存在
     * @param date 日期
     * @return true存在；false不存在
     */
    private boolean existByDate(String date) {
        int existCount = dailyDemandAmountDao.selectCountByDate(date);
        return existCount != 0;
    }

    /**
     * 计算指定日期需求金额
     * @param date 日期
     * @return 需求金额
     */
    @Override
    public BigDecimal calculateDemandAmountByDate(String date) {
        Objects.requireNonNull(date, "date");
        String endDate = LocalDate.parse(date).plusDays(1).toString();
        // 查出该日 需求数量
        List<PartDrawingNoNeedNumberOrderStartTimeBO> partDrawingNoNeedNumberOrderStartTimeBOList = orderDetailDao.selectPartDrawingNoNeedNumberOrderStartTimeByDate(date, endDate);
        Assert.notNull(partDrawingNoNeedNumberOrderStartTimeBOList, BizCodeEnum.NO_DATA.getMessage());

        // 查该日 单价
        List<PartDrawingCostPriceRangeTimeBO> partDrawingCostPriceBOList = partDrawingStockDao.selectPartDrawingCostPriceRangeTime(date, endDate);
        if (partDrawingCostPriceBOList.isEmpty()) {
            System.out.println("该日单价" + BizCodeEnum.NO_DATA.getMessage());
            return BigDecimal.valueOf(0);
        }
        // 缓存
        /*if (redisTemplate.hasKey(RedisKeyConstant.PART_DRAWING_STOCK_COST_PRICE)) {
            Object partDrawingStockCostPriceObject = redisTemplate.opsForValue().get(RedisKeyConstant.PART_DRAWING_STOCK_COST_PRICE);
            if (partDrawingStockCostPriceObject instanceof ArrayList) {
                partDrawingCostPriceRangeTimeBOList.addAll((List<PartDrawingCostPriceRangeTimeBO>) partDrawingStockCostPriceObject);
            }
        } else {
            partDrawingCostPriceRangeTimeBOList = partDrawingStockDao.selectPartDrawingCostPriceRangeTime(date);
            redisTemplate.opsForValue().set(RedisKeyConstant.PART_DRAWING_STOCK_COST_PRICE, partDrawingCostPriceRangeTimeBOList, 24, TimeUnit.HOURS);
        }*/
        // 计算
        // 总需求金额
        BigDecimal amount = new BigDecimal(0);
        for (PartDrawingNoNeedNumberOrderStartTimeBO countBO : partDrawingNoNeedNumberOrderStartTimeBOList) {
            String partDrawingNo = countBO.getPartDrawingNo();
            String warehouseCode = countBO.getWarehouseCode();
            PartDrawingCostPriceRangeTimeBO partDrawingCostPriceBO = partDrawingCostPriceBOList.stream().filter(bo -> bo.getPartDrawingNo().equals(partDrawingNo) && bo.getWarehouseCode().equals(warehouseCode)).findFirst().orElse(null);
            if (partDrawingCostPriceBO == null) {
                continue;
            }
            amount = amount.add(partDrawingCostPriceBO.getCostPrice().multiply(BigDecimal.valueOf(countBO.getNeedNumber())));
        }
        return amount;
    }

    /**
     * 插入或修改每日需求金额
     * @param date 日期
     * @param amount 需求金额
     * @return 影响条数
     */
    @Override
    public int insertOrUpdateDailyDemandAmount(String date, BigDecimal amount) {
        Date createUpdateTime = new Date();
        int count = 0;
        DailyDemandAmount dailyDemandAmount = DailyDemandAmount.builder()
                .amount(amount)
                .date(LocalDate.parse(date))
                .updateTime(createUpdateTime)
                .build();
        // 判断当日数据是否存在
        if (existByDate(date)) {
            // 存在-更新数据
            count = dailyDemandAmountDao.updateByDate(dailyDemandAmount, date);
        } else {
            // 不存在-插入数据库
            dailyDemandAmount.setCreateTime(createUpdateTime);
            count = dailyDemandAmountDao.insert(dailyDemandAmount);
        }
        return count;
    }
}
