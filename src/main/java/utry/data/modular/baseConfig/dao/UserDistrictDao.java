package utry.data.modular.baseConfig.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.dto.UserDistrictDTO;
import utry.data.modular.baseConfig.model.UserDistrict;
import utry.data.modular.partsManagement.model.DistrictAccounting;
import utry.data.modular.partsManagement.model.UserData;

import java.util.List;

/**
 * 用户大区
 * 
 * @author WJ
 * @date 2022-04-07 13:23:25
 */
@Mapper
public interface UserDistrictDao {
    /**
     *查询所有大区
     */
    List<UserDistrictDTO> selectDistrict();
    /**
     * 修改配置
     */
    int editDistrictConfig(UserDistrict userDistrict);
    /**
     * 判断是否是未配置的大区
     */
    String ifExist(@Param("districtId") String districtId,@Param("list") List<String> list);
    /**
     * 查询未配置的大区
     */
    List<UserDistrictDTO> select();
    /**
     * 判断是否是未配置的大区
     */
    String ifExistDistrict(String accountId);
    /**
     * 修改配置
     */
    int addDistrictConfig(UserDistrict userDistrict);
    /**
     * 修改配置
     */
    void deleteDistrictConfig(String districtId);

}
