package utry.data.modular.baseConfig.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.dto.CategoryRootDTO;
import utry.data.modular.baseConfig.dto.UserCategoryConfigDTO;
import utry.data.modular.baseConfig.dto.UserTypeDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.partsManagement.model.ProductType;

import java.util.List;
import java.util.Map;

/**
 * 用户大区
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface UserCategoryDao {
    /**
     *查询所有用户
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
     * 查询用户已经绑定的品类
     */
    List<ProductType> selectTypeTree();
    /**
     * 查询全部品类-类型
     */
    String ifExist(List<String> list);
    /**
     * 删除配置
     */
    int deleteConfig(String accountId);
    /**
     * 查询用户已经绑定的品类
     */
    List<String> selectUserType(String accountId);
    /**
     * 查询默认担当
     */
    HrmAccountInfoDTO selectDefault();
    /**
     * 更新默认担当
     */
    int updateDefault(@Param("accountId") String accountId,@Param("id") String id);
    /**
     * 新增默认担当
     */
    int insertDefault(String accountId);
    /**
     * 查询根节点id
     */
    List<CategoryRootDTO> selectRoot();
    /**
     * 查询全部品类-类型
     */
    String ifEditExist(@Param("accountId") String oldAccountId,@Param("list")List<String> list);
    /**
     * 查询核心id
     */
    List<String> selectTargetId(String accountId);
    /**
     * 删除用户核心目标
     */
    void deleteUserTargetId(@Param("list")List<String> list,@Param("accountId") String accountId);
    /**
     * 删除核心目标
     */
    void deleteTargetId(List<String> list);
    /**
     * 查询是否还有数据
     */
    List<String> selectLast(@Param("list")List<String> list,@Param("accountId") String accountId);
    /**
     * 删除除了默认担当以外的数据
     */
    void deleteMyself(String accountId);
}
