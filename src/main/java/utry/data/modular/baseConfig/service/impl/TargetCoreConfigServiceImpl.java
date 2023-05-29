package utry.data.modular.baseConfig.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.baseConfig.dao.TargetCoreConfigDao;
import utry.data.modular.baseConfig.dto.*;
import utry.data.modular.baseConfig.model.Target;
import utry.data.modular.baseConfig.service.TargetCoreConfigService;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 目标配置管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class TargetCoreConfigServiceImpl implements TargetCoreConfigService {

    @Resource
    private TargetCoreConfigDao targetCoreConfigDao;

    @Override
    public List<Target> selectTarget(Target target) {
        return targetCoreConfigDao.selectTarget(target);
    }

    @Override
    public boolean ifExist(TargetAddDTO targetAddDTO) {
        boolean flag = false;
        if(StringUtils.isNotEmpty(targetCoreConfigDao.ifExist(targetAddDTO))){
            flag = true;
        }
        return flag;
    }

    @Override
    public void addTarget(TargetAddDTO targetAddDTO) {
        String id = UUID.randomUUID().toString().replaceAll("-", "");
        targetAddDTO.setId(id);
        targetCoreConfigDao.addTarget(targetAddDTO);
        targetCoreConfigDao.addIndicator(targetAddDTO);
    }

    @Override
    public List<IndicatorDTO> editQuery(TargetAddDTO targetAddDTO) {
        return targetCoreConfigDao.editQuery(targetAddDTO);
    }

    @Override
    public void edit(TargetAddDTO targetAddDTO) {
        targetCoreConfigDao.updateTarget(targetAddDTO);
        targetCoreConfigDao.edit(targetAddDTO);
    }

    @Override
    public void delete(TargetAddDTO targetAddDTO) {
        targetCoreConfigDao.deleteTarget(targetAddDTO);
        targetCoreConfigDao.deleteIndicator(targetAddDTO);
    }

    @Override
    public List<IndicatorDTO> select(String businessCode) {
        //日期格式化
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM");
        //获取操作时间
        String month = dateFormat.format(new Date());
        List<IndicatorDTO> list = targetCoreConfigDao.select(businessCode,month);
        return list;
    }
}

