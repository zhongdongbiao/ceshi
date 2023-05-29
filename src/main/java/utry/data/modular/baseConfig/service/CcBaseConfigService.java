package utry.data.modular.baseConfig.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import utry.data.modular.baseConfig.model.CcCoreTarget;
import utry.data.modular.baseConfig.model.HumanResCoef;
import utry.data.modular.baseConfig.model.QueueTarget;
import utry.data.modular.baseConfig.model.SeatStatusReminder;
import utry.data.util.RetResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ldk
 * @date 2022/4/11 9:47
 */
public interface CcBaseConfigService {

    List<CcCoreTarget> selectCoreTarget(CcCoreTarget ccCoreTarget);

    RetResult saveCoreTarget(CcCoreTarget ccCoreTarget);

    RetResult delCoreTarget(CcCoreTarget ccCoreTarget);

    List<HumanResCoef> selectHumanResCoefByPage();

    RetResult importHumanResCoef(List<HumanResCoef> ls);

    RetResult editHumanResCoef(HumanResCoef humanResCoef);

    RetResult delHumanResCoef(HumanResCoef humanResCoef);

    RetResult editSeatTimeOut(SeatStatusReminder seatStatusReminder);

    RetResult selectAllSeatTimeOut();

    RetResult saveQueueBusiness(JSONArray jsonObject);

    List<JSONObject> selectQueueBusinessByPage(JSONObject jsonObject);

    List<HashMap> getOverTime();


//    RetResult saveQueueTarget(QueueTarget queueTarget);
}
