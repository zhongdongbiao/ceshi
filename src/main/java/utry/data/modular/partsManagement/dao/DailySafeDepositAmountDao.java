package utry.data.modular.partsManagement.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.bo.DateAmountBO;
import utry.data.modular.partsManagement.model.DailySafeDepositAmount;

import java.util.List;

/**
 * @program: data
 * @description: 每日安全在库金额dao层
 * @author: WangXinhao
 * @create: 2022-06-20 17:00
 **/
@Mapper
public interface DailySafeDepositAmountDao {

    /**
     * 插入一条安全在库金额
     * @param dailySafeDepositAmount 需求金额
     * @return 影响条数
     */
    int insert(@Param("dailySafeDepositAmount") DailySafeDepositAmount dailySafeDepositAmount);

    /**
     * 查询某天条数
     * @param date 日期
     * @return 条数
     */
    int selectCountByDate(@Param("date") String date);

    /**
     * 根据日期修改一条安全在库金额
     * @param dailySafeDepositAmount 安全在库金额
     * @return 影响条数
     */
    int updateByDate(@Param("dailySafeDepositAmount") DailySafeDepositAmount dailySafeDepositAmount, @Param("date") String date);

    /**
     * 根据时间范围和聚合方式查询安全在库金额
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param aggregateType 0按日聚合；1按月聚合
     * @return 安全在库金额
     */
    List<DateAmountBO> selectTotalAmountByDateAndAggregateType(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("aggregateType") String aggregateType);
}
