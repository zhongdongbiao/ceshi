package utry.data.modular.baseConfig.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.dto.DeleteDTO;
import utry.data.modular.baseConfig.dto.UserFactoryDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.partsManagement.model.FactoryData;

import java.util.List;
import java.util.Map;

/**
 * 用户工厂
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface UserFactoryDao {
    /**
     *用户查询
     */
    List<HrmAccountInfoDTO> selectUser();
    /**
     *工厂查询
     */
    List<FactoryData> selectFactory();

    /**
     *查询全部工厂数据
     */
    List<FactoryData> selectAllFactory();
    /**
     *用户工厂关联新增
     */
    int addConfig(UserFactoryDTO u);
    /**
     *用户工厂关联查询
     */
    List<HrmAccountInfoDTO> selectConfig();
    /**
     *用户工厂关联批量删除
     */
    int deleteConfig(String accountId);
    /**
     *查询用户绑定的工厂
     */
    List<FactoryData> selectUserFactory(String accountId);
    /**
     * 查询工厂是否存在
     */
    String ifExist(List<String> list);
    /**
     * 查询工厂是否存在
     */
    String ifEditExist(@Param("oldAccountId") String oldAccountId, @Param("list")List<String> list);
    /**
     * 查询核心目标id
     */
    List<String> selectTargetId(String accountId);
    /**
     * 删除用户核心目标id
     */
    void deleteUserTargetId(@Param("list")List<String> list,@Param("accountId") String accountId);
    /**
     * 删除核心目标id
     */
    void deleteTargetId(List<String> list);
    /**
     * 查询是否还有数据
     */
    List<String> selectLast(@Param("list")List<String> list,@Param("accountId") String accountId);
}
