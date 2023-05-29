package utry.data.modular.baseConfig.dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import utry.data.modular.baseConfig.model.*;

import java.util.HashMap;
import java.util.List;

/**
 * 
 * @author ldk
 */
@Mapper
//@SuppressWarnings("all")
public interface CcBaseConfigDao {

    List<CcCoreTarget> selectCoreTarget(CcCoreTarget ccCoreTarget);

    Integer addCoreTarget(CcCoreTarget ccCoreTarget);

    Integer updateCoreTarget(CcCoreTarget ccCoreTarget);

    List<CcCoreTarget> selectBindingCoreTarget(CcCoreTarget ccCoreTarget);

    Integer delCoreTarget(CcCoreTarget ccCoreTarget);

    List<HumanResCoef> selectHumanResCoefByPage();

    Integer batchInsertHumanResCoef(@Param("list") List<HumanResCoef> ls);

    Integer editHumanResCoef(HumanResCoef humanResCoef);

    Integer delHumanResCoef(HumanResCoef humanResCoef);

    Integer editSeatTimeOut(SeatStatusReminder seatStatusReminder);

    List<SeatStatusReminder> selectAllSeatTimeOut();

//    Integer saveQueueBusiness(@Param("list") JSONArray jsonArray);

    Integer selectQueueBusinessIsBinding(JSONObject item);

    Integer updateQueueBusiness(JSONObject item);

    Integer insertQueueBusiness(JSONObject item);

    List<JSONObject> selectQueueBusinessByPage(JSONObject jsonObject);

//    Integer saveQueueTarget(QueueTarget queueTarget);

    /**
     * 根据年月获取目标10s率
     * @param yearMonth
     * @return
     */
    Double selectConnRateByTargetMonth(String yearMonth);

    /**
     * 根据队列id查询队列信息
     * @param queueIdList 队列id
     * @return 队列信息
     */
    List<CcQueueDept> selectQueueDeptInfoByQueueId(@Param("queueIdList") List<String> queueIdList);

    void insertQueuedept(JSONObject jsonObject);

    Integer findBindByJobNo(String jobNo);

    List<CcCoreTarget> selectCoreTargetNotExitSelf(CcCoreTarget ccCoreTarget);

    void replaceIntoSeat(CCSeatInfo ccSeatInfo);

    List<HashMap> getOverTime();

    /**
     * 根据队列id获取同级的所有队列id
     *
     * @param queueId 队列id
     * @return CcQueueDept
     */
    List<CcQueueDept> selectSubQueueIdByQueueId(@Param("queueId") List<String> queueId);

    /**
     * 获取所有坐席数据
     * @return
     */
    List<CCSeatInfo> getAllState();

    /**
     * 根据年月查询目标
     * @param yearMonthList 年月数组
     * @return 目标
     */
    List<CcCoreTarget> selectBatchConnRateByTargetMonth(@Param("yearMonthList") List<String> yearMonthList);
}
