package utry.data.modular.baseConfig.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.baseConfig.dao.UserDistrictDao;
import utry.data.modular.baseConfig.dto.UserDistrictDTO;
import utry.data.modular.baseConfig.model.UserDistrict;
import utry.data.modular.baseConfig.service.UserDistrictService;
import utry.data.modular.partsManagement.model.DistrictAccounting;
import utry.data.modular.partsManagement.model.UserData;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 人员大区管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class UserDistrictServiceImpl implements UserDistrictService {

    @Resource
    private UserDistrictDao userDistrictDao;

    @Override
    public List<UserDistrictDTO> selectDistrict() {
        return userDistrictDao.selectDistrict();
    }

    @Override
    public int editDistrictConfig(UserDistrict userDistrict) {
        return userDistrictDao.editDistrictConfig(userDistrict);
    }

    @Override
    public boolean ifExist(String districtId, List<String> list) {
        boolean flag = false;
        if(StringUtils.isNotEmpty(userDistrictDao.ifExist(districtId,list))){
            flag = true;
        }
        return flag;
    }

//    @Override
//    public boolean ifExistDistrict(String accountId) {
//        boolean flag = false;
//        if(StringUtils.isNotEmpty(userDistrictDao.ifExistDistrict(accountId))){
//            flag = true;
//        }
//        return flag;
//    }

    @Override
    public List<UserDistrictDTO> select() {
        return userDistrictDao.select();
    }

    @Override
    public int addDistrictConfig(UserDistrict userDistrict) {
        return userDistrictDao.addDistrictConfig(userDistrict);
    }

    @Override
    public void deleteDistrictConfig(String districtId) {
        userDistrictDao.deleteDistrictConfig(districtId);
    }
}

