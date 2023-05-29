package utry.data.modular.baseConfig.service;

import utry.data.modular.baseConfig.dto.UserFactoryDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.partsManagement.model.FactoryData;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface UserFactoryService {
    /**
     * 查询所有人员数据
     */
    List<HrmAccountInfoDTO> selectUser();
    /**
     * 查询未配置的工厂
     */
    List<FactoryData> selectFactory();
    /**
     * 新增配置
     */
    int addConfig(UserFactoryDTO userFactoryDTO);
    /**
     * 查询配置
     */
    List<HrmAccountInfoDTO> selectConfig();
    /**
     * 删除配置
     */
    int deleteConfig(String accountId);
    /**
     * 查询用户绑定的工厂
     */
    List<FactoryData> selectUserFactory(String accountId);
    /**
     * 工厂是否存在
     */
    boolean ifExist(List<String> list,String oldAccountId);
    /**
     * 判断该工厂是否已经被配置
     */
    boolean ifExist(List<String> list);
}
