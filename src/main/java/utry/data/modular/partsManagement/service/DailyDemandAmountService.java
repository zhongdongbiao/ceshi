package utry.data.modular.partsManagement.service;

import utry.data.util.RetResult;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 每日需求金额业务接口
 * @author: WangXinhao
 * @create: 2022-06-20 16:20
 **/

public interface DailyDemandAmountService {

    /**
     * 计算并插入指定日期需求金额
     * @param date 日期
     * @return 需求金额
     */
    RetResult<Double> calculateAndInsertDemandAmountByDate(String date);

    /**
     * 计算指定日期需求金额
     * @param date 日期
     * @return 需求金额
     */
    BigDecimal calculateDemandAmountByDate(String date);

    /**
     * 插入或修改每日需求金额
     * @param date 日期
     * @param amount 需求金额
     * @return 影响条数
     */
    int insertOrUpdateDailyDemandAmount(String date, BigDecimal amount);
}
