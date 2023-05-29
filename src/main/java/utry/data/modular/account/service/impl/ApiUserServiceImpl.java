package utry.data.modular.account.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import utry.core.bo.ResponseEntity;
import utry.core.websocket.bo.UserInfo;
import utry.data.modular.account.service.ApiUserService;
import utry.data.modular.partsManagement.dao.UserDataDao;
import utry.data.modular.partsManagement.model.UserData;
import utry.data.util.CrossSubStationUtil;
import utry.data.util.MessageUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 人员管理实现类
 * @author WJ
 * @date 2022/4/8 9:47
 */
@Service
public class ApiUserServiceImpl implements ApiUserService {

    @Resource
    private UserDataDao userDataDao;
    @Resource
    private CrossSubStationUtil crossSubStationUtil;

    @Override
    public List<UserData> selectAllUser() {
        return userDataDao.selectAllUser();
    }

    @Override
    public int batchDelete() {
        return userDataDao.batchDelete();
    }

    @Override
    public int batchUserData(List<UserData> list) {
        if(CollectionUtils.isNotEmpty(list)) {
            return userDataDao.batchUserData(list);
        }
        return 0;
    }

    @Override
    public List<UserData> sendMessage(List<UserData> list, List<UserData> oldUserList) {
        List<UserData> info = new ArrayList<>();
        //如果新的的人员表为空
        if(CollectionUtils.isEmpty(list)){
            if(CollectionUtils.isNotEmpty(oldUserList)){
                return oldUserList;
            }
            return null;
        }
        if(CollectionUtils.isNotEmpty(list)&&CollectionUtils.isNotEmpty(oldUserList)){
            //将list转map （map的键去重）
            Map<String, UserData> newMap =
                    list.stream().collect(Collectors.toMap(
                            UserData::getId,
                            u -> u
                    ));
            //原来用户存在有现在不存在
            for(UserData userData : oldUserList){
                if(!newMap.containsKey(userData.getId())){
                    info.add(userData);
                    continue;
                }
                //当用户有效变失效
                if(userData.getState().equals("1") && newMap.get(userData.getId()).getState().equals("0")){
                    info.add(userData);
                }
            }
            return info;
        }
        return null;
    }

    @Override
    public void sendMessage(List<UserData> info) {
        if(CollectionUtils.isNotEmpty(info)){
            ResponseEntity call = crossSubStationUtil.getAccount();
            String res = (String) call.getData();
            List<UserInfo> userInfos = JSONArray.parseArray(res,UserInfo.class);
            MessageUtil.send("人员变动通知","您好，人员数据数据状态发生变化，请自行排查系统~","auto",userInfos);
        }
    }
}

