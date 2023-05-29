package utry.data.modular.partsManagement.service.impl;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import utry.core.bo.ResponseEntity;
import utry.core.websocket.bo.UserInfo;
import utry.data.modular.account.service.ApiUserService;
import utry.data.modular.partsManagement.dao.FactoryDataDao;
import utry.data.modular.partsManagement.dao.UserDataDao;
import utry.data.modular.partsManagement.model.FactoryData;
import utry.data.modular.partsManagement.model.UserData;
import utry.data.modular.partsManagement.service.ApiFactoryService;
import utry.data.util.CrossSubStationUtil;
import utry.data.util.MessageUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 工厂管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class ApiFactoryServiceImpl implements ApiFactoryService {

    @Resource
    private FactoryDataDao factoryDataDao;

    @Override
    public int batchDelete() {
        return factoryDataDao.batchDelete();
    }

    @Override
    public int batchFactoryData(List<FactoryData> list) {
        return factoryDataDao.batchFactoryData(list);
    }

}

