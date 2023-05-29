package utry.data.modular.baseConfig.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import utry.data.modular.api.UserDataApi;
import utry.data.modular.baseConfig.controller.CcBaseConfigController;
import utry.data.modular.baseConfig.dao.CcBaseConfigDao;
import utry.data.modular.baseConfig.dao.CcBaseDataDao;
import utry.data.modular.baseConfig.model.CCSeatInfo;
import utry.data.modular.baseConfig.model.SeatStatusReminder;
import utry.data.modular.baseConfig.service.CcBaseDataService;
import utry.data.util.RetResult;
import utry.data.util.SmsUtils;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author lidakai
 */
@Service
@SuppressWarnings("all")
public class CcBaseDataServiceImpl implements CcBaseDataService {

    @Resource
    private CcBaseDataDao ccBaseDataDao;
    @Resource
    private CcBaseConfigDao ccBaseConfigDao;
    @Resource
    private SmsUtils smsUtils;
    @Resource
    private CcBaseConfigController ccBaseConfigController;
    @Resource
    private UserDataApi userDataApi;


    @Override
    public void getCcStatus() {
        List<String> ccStatusList = ccBaseDataDao.getCcStatus();
        // 完善本地基础表
        if (ccStatusList.size() > 0) {
            //获取字典项
            Map<String, String> ccStatusDic = smsUtils.getDictList("CC_STATUS");
            for (String status : ccStatusList) {
                //查看该code 是否已存在
                Integer row = ccBaseDataDao.getIsExit(status);
                if (row == 0 && StringUtils.isNotBlank(ccStatusDic.get(status))) {
                    SeatStatusReminder seatStatusReminder = new SeatStatusReminder();
                    seatStatusReminder.setSeatStatusId(status);
                    seatStatusReminder.setStatusName(ccStatusDic.get(status));
                    ccBaseDataDao.saveStatus(seatStatusReminder);
                }
            }
        }
    }

    @Override
    public void getQueueData() {
        //接口查询数策接口数据
        RetResult retResult = userDataApi.geShuCetUser();
        if (200 != retResult.getCode()) {
            return;
        }
        JSONObject ccJson = (JSONObject) retResult.getData();
        JSONArray result = ccJson.getJSONArray("result");
        List<CCSeatInfo> ccQueueDepts = JSONObject.parseArray(result.toJSONString(), CCSeatInfo.class);
        if (ccQueueDepts.size() > 0) {
            // 更新回访/热线队列绑定
            for (CCSeatInfo ccSeatInfo : ccQueueDepts) {
                //判断基础表是否存在该队列
                if (ccBaseDataDao.isExitQueue(ccSeatInfo) == 0) {
                    JSONObject jsonObject = (JSONObject) JSONObject.toJSON(ccSeatInfo);
                    ccBaseConfigDao.insertQueuedept(jsonObject);
                }
                ccBaseConfigDao.replaceIntoSeat(ccSeatInfo);
            }
        }
    }

    /**
     * 查询所有状态
     *
     * @return SeatStatusReminder
     */
    @Override
    public List<SeatStatusReminder> getSeatStatusReminder() {
        return ccBaseDataDao.selectSeatStatusReminder();

    }


}

