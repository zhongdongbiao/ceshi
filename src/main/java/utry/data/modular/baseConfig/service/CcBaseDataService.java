package utry.data.modular.baseConfig.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import utry.data.modular.baseConfig.model.CcCoreTarget;
import utry.data.modular.baseConfig.model.HumanResCoef;
import utry.data.modular.baseConfig.model.SeatStatusReminder;
import utry.data.util.RetResult;

import java.util.List;

/**
 * @author ldk
 * @date 2022/4/11 9:47
 */
public interface CcBaseDataService {

    void getCcStatus();

    void getQueueData();

    /**
     * 查询所有状态
     *
     * @return SeatStatusReminder
     */
    List<SeatStatusReminder> getSeatStatusReminder();
}
