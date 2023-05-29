package utry.data.modular.partsManagement.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.partsManagement.bo.EveryAmountByDateBO;
import utry.data.modular.partsManagement.bo.EveryCountByDateBO;
import utry.data.modular.partsManagement.bo.SecurityBO;
import utry.data.modular.partsManagement.bo.WarehouseAmountBO;
import utry.data.modular.partsManagement.dto.AmountDTO;
import utry.data.modular.partsManagement.dto.DayAmountDTO;
import utry.data.modular.partsManagement.dto.InventoryWarningConditionDto;
import utry.data.modular.partsManagement.model.InventoryWarning;
import utry.data.modular.partsManagement.model.WarehouseInventoryWarning;
import utry.data.modular.partsManagement.vo.DayAmountVo;
import utry.data.modular.partsManagement.vo.InventoryWarningVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 库存预警
 * 
 * @author zhongdongbiao
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface InventoryWarningDao{

    /**
     *库存预警添加
     * @param inventoryWarning
     */
    void insertInventoryWarning(InventoryWarning inventoryWarning);
    /**
     *库存预警批量删除
     */
    int batchDelete();
    /**
     *库存预警批量新增
     * @param list
     */
    int batchInventoryWarning(List<InventoryWarning> list);

    /**
     *部件图号历史数据新增
     * @param list
     */
    int batchPartsHistory(List<InventoryWarning> list);

    /**
     * 根据时间段获取部品历史库存
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    int getAmountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("userId") String userId);

    /**
     * 获取部品在线库存金额
     * @return
     */
    @DS("git_adb")
    int getAmount();

    /**
     * 获取库存预警的部件数
     * @return
     */
    @DS("git_adb")
    int getInventoryWarning();

    /**
     * 获取库存缺件的部件数
     * @return
     */
    @DS("git_adb")
    int getLack();

    /**
     * 根据时间段获取每日的部品在库金额
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param aggregateType 0按日聚合；1按月聚合
     * @param userId 用户id
     * @return EveryAmountByDateBO
     */
    @DS("gits_sharding")
    List<EveryAmountByDateBO> getEveryAmountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate,
                                                   @Param("aggregateType") String aggregateType, @Param("userId") String userId);

    /**
     * 根据时间段获取每日的部品在库
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> getEveryCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("aggregateType") String aggregateType);

    /**
     * 获取在库金额品类全景
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> getProductCategory();

    /**
     * 获取担当别在库金额
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> getBear();

    /**
     * 查询工厂别在库金额
     * @param warehouseCode 工厂编码
     * @param userId 用户id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return WarehouseAmountBO
     */
    @DS("gits_sharding")
    List<WarehouseAmountBO> getAllWarehouseAmount(@Param("warehouseCode") String warehouseCode, @Param("userId") String userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据库存预警查询条件获取库存预警列表
     * @param inventoryWarningConditionDto
     * @return
     */
    @DS("git_adb")
    List<InventoryWarningVo> getInventoryWarningVo(InventoryWarningConditionDto inventoryWarningConditionDto);

    /**
     * 分页条件查询缺货部品列表
     * @param inventoryWarningConditionDto
     * @return
     */
    @DS("git_adb")
    List<InventoryWarningVo> getStockGoods(InventoryWarningConditionDto inventoryWarningConditionDto);

    /**
     * 根据部件图号查询库存预警信息
     * @param partDrawingNo
     * @return
     */
    @DS("git_adb")
    InventoryWarning getInventoryWarningByPartDrawingNo(String partDrawingNo);

    /**
     * 获取工厂别缺件部品
     * @param startDate
     * @param endDate
     * @param sort
     * @return
     */
    @DS("git_adb")
    List<Map<Object,Object>> getFactoryStockGoods(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("sort") String sort);

    /**
     * 获取每日需求金额
     */
    @DS("git_adb")
    List<AmountDTO> getAmountBydate(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("aggregateType") String aggregateType);

    /**
     * 获取每日需求数量
     */
    @DS("git_adb")
    List<AmountDTO> getCountByDate(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("aggregateType") String aggregateType);

    /**
     * 获取单日在库金额列表
     * @param dayAmountDTO
     * @return
     */
    @DS("git_adb")
    List<DayAmountVo> getDayAmount(DayAmountDTO dayAmountDTO);

    /**
     * 获取单日在库数量列表
     * @param dayAmountDTO
     * @return
     */
    @DS("git_adb")
    List<DayAmountVo> getDayCount(DayAmountDTO dayAmountDTO);

    /**
     * 根据时间段获取每日的部品安全在库数量
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> getSecurityNumber(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("aggregateType") String aggregateType);

    /**
     * 根据时间段获取每日的部品安全在库金额
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    List<Map<String,String>> getSecurityMoney(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("aggregateType") String aggregateType);

    /**
     * 根据userId查询在库金额
     * @param userId 用户id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 在库金额
     */
    @DS("gits_sharding")
    List<BigDecimal> getAmountByUserId(@Param("userId") String userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 根据时间、用户id查询在库金额
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param userId 用户id
     * @return 各分表在库金额数组
     */
    @DS("gits_sharding")
    List<BigDecimal> getAmountByMonthUserId(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("userId") String userId);

    /**
     * 仓库别库存预警数据创建
     * @param warehouseInventoryWarning
     */

    void createWarehouseInventoryWarning(WarehouseInventoryWarning warehouseInventoryWarning);

    /**
     * 获取安全在库数量
     * @param date
     */
    @DS("git_adb")
    List<SecurityBO> getSecurityBO(@Param("date") String date);

    /**
     * 获取某个时间段的安全在库数量
     * @param startDate
     * @param endDate
     * @return
     */
    @DS("git_adb")
    List<SecurityBO> getAllSecurityBO(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 获取实际在库金额（元）
     * @param userId 用户id
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    @DS("gits_sharding")
    List<BigDecimal> getAmountInStock(@Param("userId") String userId, @Param("startDate") String startDate, @Param("endDate") String endDate);

    Integer select(InventoryWarning requestToObject);

    Integer selectWarehouseInventoryWarning(WarehouseInventoryWarning requestToObject);

    /**
     * 查时间范围在库数量
     * @param startDate
     * @param endDate
     * @param aggregateType
     * @return
     */
    @DS("gits_sharding")
    List<EveryCountByDateBO> getEveryCountInStockByDate(@Param("startDate") String startDate, @Param("endDate") String endDate, @Param("aggregateType") String aggregateType);
}
