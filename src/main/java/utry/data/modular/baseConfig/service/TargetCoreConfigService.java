package utry.data.modular.baseConfig.service;

import utry.data.modular.baseConfig.dto.*;
import utry.data.modular.baseConfig.model.Target;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface TargetCoreConfigService {
    /**
     * 查询业务下的核心配置
     */
    List<Target> selectTarget(Target target);
    /**
     * 查询当月是否已经配置过目标
     */
    boolean ifExist(TargetAddDTO targetAddDTO);
    /**
     * 新增
     */
    void addTarget(TargetAddDTO targetAddDTO);
    /**
     * 编辑前查询
     */
    List<IndicatorDTO> editQuery(TargetAddDTO targetAddDTO);
    /**
     * 编辑
     */
    void edit(TargetAddDTO targetAddDTO);
    /**
     * 删除
     */
    void delete(TargetAddDTO targetAddDTO);
    /**
     * 查询本月目标
     */
    List<IndicatorDTO> select(String businessCode);
}
