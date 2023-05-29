package utry.data.modular.baseConfig.service;

import utry.data.modular.baseConfig.dto.UserDistrictDTO;
import utry.data.modular.baseConfig.model.UserDistrict;
import utry.data.modular.partsManagement.model.DistrictAccounting;
import utry.data.modular.partsManagement.model.UserData;

import java.util.List;

/**
 * @author WJ
 * @date 2022/4/11 9:47
 */
public interface UserDistrictService {
    /**
     * 查询所有大区
     */
    List<UserDistrictDTO> selectDistrict();
    /**
     * 修改配置
     */
    int editDistrictConfig(UserDistrict userDistrict);
    /**
     * 判断是否是未配置的大区
     */
    boolean ifExist(String accountId,List<String> list);
//    /**
//     * 判断是否是未配置的大区
//     */
//    boolean ifExistDistrict(String accountId);
    /**
     * 查未配置的大区
     */
    List<UserDistrictDTO> select();
    /**
     * 修改配置
     */
    int addDistrictConfig(UserDistrict userDistrict);
    /**
     * 删除配置
     */
    void deleteDistrictConfig(String districtId);
}
