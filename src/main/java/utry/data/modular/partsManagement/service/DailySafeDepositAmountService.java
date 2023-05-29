package utry.data.modular.partsManagement.service;

import utry.data.util.RetResult;

import java.math.BigDecimal;

/**
 * @program: data
 * @description: 每日安全在库金额业务接口
 * @author: WangXinhao
 * @create: 2022-06-20 16:59
 **/

public interface DailySafeDepositAmountService {

    /**
     * 计算指定日期安全在库金额
     * @param date 日期
     * @return 安全在库金额
     */
    RetResult<Double> calculateAndInsertSafeDepositAmountByDate(String date);

    /**
     * 计算指定日期安全在库金额
     * @param date 日期
     * @return 需求金额
     */
    BigDecimal calculateSafeDepositAmountByDate(String date);

    /**
     * 插入或修改每日安全在库金额
     * @param date 日期
     * @param amount 安全在库金额
     * @return 影响条数
     */
    int insertOrUpdateSafeDepositAmount(String date, BigDecimal amount);
}
