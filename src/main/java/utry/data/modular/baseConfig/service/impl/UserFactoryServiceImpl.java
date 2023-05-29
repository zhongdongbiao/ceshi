package utry.data.modular.baseConfig.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.baseConfig.dao.UserFactoryDao;
import utry.data.modular.baseConfig.dto.DeleteDTO;
import utry.data.modular.baseConfig.dto.UserFactoryDTO;
import utry.data.modular.baseConfig.dto.HrmAccountInfoDTO;
import utry.data.modular.baseConfig.service.UserFactoryService;
import utry.data.modular.partsManagement.model.FactoryData;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 人员工厂管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class UserFactoryServiceImpl implements UserFactoryService {

    @Resource
    private UserFactoryDao userFactoryDao;

    @Override
    public List<HrmAccountInfoDTO> selectUser() {
        return userFactoryDao.selectUser();
    }

    @Override
    public List<FactoryData> selectFactory() {
        return userFactoryDao.selectFactory();
    }

    @Override
    public int addConfig(UserFactoryDTO userFactoryDTO) {
        return userFactoryDao.addConfig(userFactoryDTO);
    }

    @Override
    public List<HrmAccountInfoDTO> selectConfig() {
        return userFactoryDao.selectConfig();
    }

    @Override
    public int deleteConfig(String accountId) {
        //查询核心id
        List<String> list = userFactoryDao.selectTargetId(accountId);
        if(CollectionUtils.isNotEmpty(list)){
            //删除用户核心目标
            userFactoryDao.deleteUserTargetId(list,accountId);
            List<String> lastList = userFactoryDao.selectLast(list,accountId);
            if (CollectionUtils.isNotEmpty(lastList)){
                list = list.stream().filter(item -> !lastList.contains(item)).collect(Collectors.toList());
            }
            //删除核心目标
            userFactoryDao.deleteTargetId(list);
        }
        return userFactoryDao.deleteConfig(accountId);
    }

    @Override
    public List<FactoryData> selectUserFactory(String accountId) {
        return userFactoryDao.selectUserFactory(accountId);
    }

    @Override
    public boolean ifExist(List<String> list, String oldAccountId) {
        boolean flag = false;
        if(StringUtils.isNotEmpty(userFactoryDao.ifEditExist(oldAccountId,list))){
            flag = true;
        }
        return flag;
    }

    @Override
    public boolean ifExist(List<String> list) {
        boolean flag = false;
        if(StringUtils.isNotEmpty(userFactoryDao.ifExist(list))){
            flag = true;
        }
        return flag;
    }
}

