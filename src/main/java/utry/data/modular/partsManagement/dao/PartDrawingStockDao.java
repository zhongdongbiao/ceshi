package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.bo.*;
import utry.data.modular.partsManagement.dto.FactoryBearAmountQueryDTO;
import utry.data.modular.partsManagement.model.PartDrawingStock;
import utry.data.util.ConditionUtil;

import java.util.List;

/**
 * @program: data
 * @description: 部品库存dao层
 * @author: WangXinhao
 * @create: 2022-06-08 13:23
 **/
@Mapper
public interface PartDrawingStockDao {

    /**
     * 批量插入部品库存
     * @param partDrawingStockList 数据数组
     * @return 影响条数
     */
    @DS("gits_sharding")
    int insertBatch(@Param("partDrawingStockList") List<PartDrawingStock> partDrawingStockList);

    /**
     * 查询担当别在库金额（仟元）
     * @return BearAmountBO
     */
    @DS("gits_sharding")
    List<BearAmountBO> selectBearAmount(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据查询条件查指定担当管理的各工厂在库金额（仟元）
     * @param factoryBearAmountQueryDTO 查询条件
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return FactoryBearAmountVO
     */
    @DS("gits_sharding")
    List<FactoryBearAmountBO> selectFactoryBearAmountByQueryDTO(@Param("factoryBearAmountQueryDTO") FactoryBearAmountQueryDTO factoryBearAmountQueryDTO, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据时间查询工厂-用户id-在库金额
     * @param date 日期
     * @return FactoryUserIdAmountBO
     */
    /**
     * 根据时间查询工厂-用户id-在库金额
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    @DS("gits_sharding")
    List<FactoryUserIdAmountBO> selectFactoryAmountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据日期、工厂代码查成本单价
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param factoryCode 工厂代码
     * @return PartDrawingCostPriceBO
     */
    @DS("gits_sharding")
    List<PartDrawingCostPriceBO> selectCostPrice(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("factoryCode") String factoryCode);

    /**
     * 根据时间查询工厂别实际数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return FactoryCountBO
     */
    @DS("gits_sharding")
    List<FactoryCountBO> selectFactoryCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据工厂名称、时间查询工厂-用户id-在库金额
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param factoryNameCondition 工厂名称查询条件
     * @return FactoryUserIdAmountBO
     */
    @DS("gits_sharding")
    List<FactoryUserIdAmountBO> selectFactoryAmountByDateFactoryName(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("factoryNameCondition") ConditionUtil factoryNameCondition);

    /**
     * 根据工厂名称、时间查询工厂-用户id-在库数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param factoryNameCondition 工厂名称查询条件
     * @return FactoryUserIdCountBO
     */
    @DS("gits_sharding")
    List<FactoryUserIdCountBO> selectFactoryCountByDateFactoryName(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("factoryNameCondition") ConditionUtil factoryNameCondition);

    /**
     * 根据部件图号、时间查询部件图号-在库金额（仟元）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param factoryCode 工厂代码
     * @param partDrawingNoCondition 部件图号查询条件
     * @return PartDrawingNoAmountBO
     */
    @DS("gits_sharding")
    List<PartDrawingNoAmountBO> selectPartDrawingNoAmountByDatePartDrawingNo(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("factoryCode") String factoryCode, @Param("partDrawingNoCondition") ConditionUtil partDrawingNoCondition);

    /**
     * 根据部件图号、时间查询部件图号-图号描述-在库数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param factoryCode 工厂代码
     * @param partDrawingNoCondition 部件图号查询条件
     * @return PartDrawingNoCountBO
     */
    @DS("gits_sharding")
    List<PartDrawingNoCountBO> selectPartDrawingNoCountByDatePartDrawingNo(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("factoryCode") String factoryCode, @Param("partDrawingNoCondition") ConditionUtil partDrawingNoCondition);

    /**
     * 查一天的单价
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return PartDrawingCostPriceRangeTimeBO
     */
    @DS("gits_sharding")
    List<PartDrawingCostPriceRangeTimeBO> selectPartDrawingCostPriceRangeTime(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 查数量
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 总数量
     */
    @DS("gits_sharding")
    Long selectCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
