package utry.data.modular.baseConfig.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.baseConfig.dao.TargetCoreConfigDao;
import utry.data.modular.baseConfig.dao.TargetUserConfigDao;
import utry.data.modular.baseConfig.dto.*;
import utry.data.modular.baseConfig.model.Target;
import utry.data.modular.baseConfig.service.TargetCoreConfigService;
import utry.data.modular.baseConfig.service.TargetUserConfigService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 目标配置管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class TargetUserConfigServiceImpl implements TargetUserConfigService {

    @Resource
    private TargetUserConfigDao targetUserConfigDao;

    @Override
    public List<TargetUserDTO> selectTarget(TargetUserDTO targetUserDTO) {
        return targetUserConfigDao.selectTarget(targetUserDTO);
    }

    @Override
    public boolean ifExist(TargetAddDTO targetAddDTO) {
        boolean flag = false;
        if(StringUtils.isNotEmpty(targetUserConfigDao.ifExist(targetAddDTO))){
            flag = true;
        }
        return flag;
    }

    @Override
    public void addTarget(TargetAddDTO targetAddDTO) {
        List<IndicatorUserDTO> users = new ArrayList<>();
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        targetAddDTO.setId(id);
        getUsers(targetAddDTO, users);
        targetUserConfigDao.addTarget(targetAddDTO);
        targetUserConfigDao.addIndicator(targetAddDTO);
    }

    @Override
    public List<IndicatorUserDTO> editQuery(TargetAddDTO targetAddDTO) {
        return targetUserConfigDao.editQuery(targetAddDTO);
    }

    @Override
    public void deleteAll(TargetAddDTO targetAddDTO) {
        targetUserConfigDao.deleteAll(targetAddDTO);
    }

    @Override
    public void addIndicator(TargetAddDTO targetAddDTO) {
        List<IndicatorUserDTO> users = new ArrayList<>();
        targetUserConfigDao.updateTarget(targetAddDTO);
        getUsers(targetAddDTO, users);
        targetUserConfigDao.addIndicator(targetAddDTO);
    }

    private void getUsers(TargetAddDTO targetAddDTO, List<IndicatorUserDTO> users) {
        for(String s : targetAddDTO.getUsers()){
            for (IndicatorDTO i : targetAddDTO.getList()){
                IndicatorUserDTO indicatorUserDTO = new IndicatorUserDTO();
                indicatorUserDTO.setIndicatorCode(i.getIndicatorCode());
                indicatorUserDTO.setIndicatorName(i.getIndicatorName());
                indicatorUserDTO.setIndicatorValue(i.getIndicatorValue());
                indicatorUserDTO.setAccountId(s);
                users.add(indicatorUserDTO);
            }
        }
        targetAddDTO.setUserList(users);
    }

    @Override
    public void delete(TargetAddDTO targetAddDTO) {
        targetUserConfigDao.deleteTarget(targetAddDTO);
        targetUserConfigDao.deleteAll(targetAddDTO);
    }

    @Override
    public List<IndicatorUserDTO> select(String businessCode) {
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        //获取操作时间
        String month = dateFormat.format(new Date());
        return targetUserConfigDao.select(businessCode,month);
    }

    @Override
    public List<HrmAccountInfoDTO> selectUser(String businessCode) {
        List<HrmAccountInfoDTO> list = new ArrayList<>();
        switch(businessCode){
            case "category":
                list = targetUserConfigDao.selectCategoryUser();
                break;
            case "partManagement":
                list = targetUserConfigDao.selectPartManagementUser();
                break;
            case "district":
                list = targetUserConfigDao.selectDistrictUser();
                break;
            default:
                break;
        }
        return list;
    }

}

