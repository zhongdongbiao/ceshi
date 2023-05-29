package utry.data.modular.baseConfig.service;

import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.dto.IndicatorUserDTO;
import utry.data.modular.baseConfig.dto.TargetAddDTO;
import utry.data.modular.baseConfig.dto.TargetUserDTO;
import utry.data.modular.baseConfig.model.Target;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface TargetUserConfigService {
    /**
     * 查询业务下的核心配置
     */
    List<TargetUserDTO> selectTarget(TargetUserDTO targetUserDTO);
    /**
     * 判断当月是否已经配置过担当指标
     */
    boolean ifExist(TargetAddDTO targetAddDTO);
    /**
     * 新增
     */
    void addTarget(TargetAddDTO targetAddDTO);
    /**
     * 编辑前查询
     */
    List<IndicatorUserDTO> editQuery(TargetAddDTO targetAddDTO);
    /**
     * 删除所有关联指标数据
     */
    void deleteAll(TargetAddDTO targetAddDTO);
    /**
     * 新增关联指标数据
     */
    void addIndicator(TargetAddDTO targetAddDTO);
    /**
     * 删除
     */
    void delete(TargetAddDTO targetAddDTO);
    /**
     * 查询当月指标
     */
    List<IndicatorUserDTO> select(String businessCode);
    /**
     * 查询已配置用户
     */
    List<HrmAccountInfoDTO> selectUser(String businessCode);
}
