package utry.data.modular.baseConfig.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.dto.IndicatorUserDTO;
import utry.data.modular.baseConfig.dto.TargetAddDTO;
import utry.data.modular.baseConfig.dto.TargetUserDTO;
import utry.data.modular.baseConfig.model.Target;

import java.util.List;

/**
 * 目标配置
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface TargetUserConfigDao {
    /**
     *查询业务下的配置
     */
    List<TargetUserDTO> selectTarget(TargetUserDTO targetUserDTO);
    /**
     *判断当月是否已经配置过担当指标
     */
    String ifExist(TargetAddDTO targetAddDTO);
    /**
     *新增担当目标
     */
    void addTarget(TargetAddDTO targetAddDTO);
    /**
     *新增担当指标
     */
    void addIndicator(TargetAddDTO targetAddDTO);
    /**
     *编辑前查询
     */
    List<IndicatorUserDTO> editQuery(TargetAddDTO targetAddDTO);
    /**
     *删除所有关联指标数据
     */
    void deleteAll(TargetAddDTO targetAddDTO);
    /**
     *删除目标
     */
    void deleteTarget(TargetAddDTO targetAddDTO);
    /**
     *更新目标时间
     */
    void updateTarget(TargetAddDTO targetAddDTO);
    /**
     *查询当月指标
     */
    List<IndicatorUserDTO> select(@Param("businessCode") String businessCode,@Param("month") String month);
    /**
     *查询已配置用户
     */
    List<HrmAccountInfoDTO> selectUser(String businessCode);
    /**
     *查询已配置用户
     */
    List<HrmAccountInfoDTO> selectCategoryUser();
    /**
     *查询已配置用户
     */
    List<HrmAccountInfoDTO> selectPartManagementUser();
    /**
     *查询已配置用户
     */
    List<HrmAccountInfoDTO> selectDistrictUser();
}
