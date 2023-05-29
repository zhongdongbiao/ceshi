package utry.data.modular.baseConfig.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.dto.*;
import utry.data.modular.baseConfig.model.Target;

import java.util.List;

/**
 * 目标配置
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface TargetCoreConfigDao {
    /**
     *查询业务下的配置
     */
    List<Target> selectTarget(Target target);
    /**
     *查询当月是否已经配置过目标
     */
    String ifExist(TargetAddDTO targetAddDTO);
    /**
     *新增目标
     */
    int addTarget(TargetAddDTO targetAddDTO);
    /**
     *新增指标
     */
    void addIndicator(TargetAddDTO targetAddDTO);
    /**
     *编辑前查询
     */
    List<IndicatorDTO> editQuery(TargetAddDTO targetAddDTO);
    /**
     *编辑
     */
    int edit(TargetAddDTO targetAddDTO);
    /**
     *删除目标
     */
    void deleteTarget(TargetAddDTO targetAddDTO);
    /**
     *删除关联指标
     */
    void deleteIndicator(TargetAddDTO targetAddDTO);
    /**
     *更新目标编辑时间
     */
    void updateTarget(TargetAddDTO targetAddDTO);
    /**
     *查询指标信息
     */
    List<IndicatorDTO> select(@Param("businessCode") String businessCode,@Param("month") String month);

    /**
     *查询指定指标信息
     */
    IndicatorDTO selectTargetByIndicatorCode(@Param("businessCode") String businessCode,@Param("month") String month,@Param("indicatorCode") String indicatorCode,@Param("userId") String userId);

    /**
     *查询最新的在库金额
     */
    IndicatorDTO getNewStockAmount(@Param("userId")String userId,@Param("targetMonth")String targetMonth);

    /**
     *查询最新的部品出货即纳率
     */
    IndicatorDTO getshipment();
}
