package utry.data.modular.partsManagement.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.bo.DateAmountBO;
import utry.data.modular.partsManagement.model.DailyDemandAmount;

import java.util.List;

/**
 * @program: data
 * @description: 每日需求金额dao层
 * @author: WangXinhao
 * @create: 2022-06-20 16:03
 **/
@Mapper
public interface DailyDemandAmountDao {

    /**
     * 插入一条需求金额
     * @param dailyDemandAmount 需求金额
     * @return 影响条数
     */
    int insert(@Param("dailyDemandAmount") DailyDemandAmount dailyDemandAmount);

    /**
     * 查询某天条数
     * @param date 日期
     * @return 条数
     */
    int selectCountByDate(@Param("date") String date);

    /**
     * 根据日期修改一条需求金额
     * @param dailyDemandAmount 需求金额
     * @return 影响条数
     */
    int updateByDate(@Param("dailyDemandAmount") DailyDemandAmount dailyDemandAmount, @Param("date") String date);

    /**
     * 根据时间范围和聚合方式查询需求金额
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param aggregateType 0按日聚合；1按月聚合
     * @return 需求金额
     */
    List<DateAmountBO> selectTotalAmountByDateAndAggregateType(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("aggregateType") String aggregateType);
}
