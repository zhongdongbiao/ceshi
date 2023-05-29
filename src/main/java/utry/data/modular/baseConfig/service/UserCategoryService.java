package utry.data.modular.baseConfig.service;

import utry.data.modular.baseConfig.dto.CategoryRootDTO;
import utry.data.modular.baseConfig.dto.UserCategoryConfigDTO;
import utry.data.modular.baseConfig.dto.UserTypeDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.partsManagement.model.ProductType;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface UserCategoryService {
    /**
     * 查询用户
     */
    List<HrmAccountInfoDTO> selectUser();
    /**
     * 查询所有品类
     */
    List<String> selectCategory();
    /**
     * 新增配置
     */
    int addCategory(UserTypeDTO userTypeDTO);
    /**
     * 查询配置
     */
    List<UserCategoryConfigDTO> selectConfig();
    /**
     * 全部的品类-类型
     */
    List<CategoryRootDTO> selectTypeTree();
//    /**
//     * 判断该品类是否已经被配置
//     */
//    boolean ifExist(List<String> list);
    /**
     * 删除配置
     */
    int deleteConfig(String accountId);
    /**
     * 查询用户已经绑定的品类-类型
     */
    List<String> selectUserType(String accountId);
    /**
     * 查询默认担当
     */
    HrmAccountInfoDTO selectDefault();
    /**
     * 保存默认担当
     */
    int insertOrUpdateDefault(String accountId,String id);
    /**
     * 判断该品类是否已经被配置
     */
    boolean ifExist(List<String> list,String accountId);
    /**
     * 删除数据
     */
    void deleteMyself(String accountId);
}
