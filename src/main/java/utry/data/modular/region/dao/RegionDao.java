package utry.data.modular.region.dao;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import utry.data.modular.complaints.dto.ComplaintDto;
import utry.data.modular.region.controller.dto.RegionComplaintDto;
import utry.data.modular.region.controller.dto.RegionVisitMonitoringRequest;

import java.util.List;
import java.util.Map;

@Mapper
public interface RegionDao {

    //派工单详情表新增
    Integer serviceDetailAdd(Map map);

    //派工单详情表修改
    Integer serviceDetailEdit(Map map);

    //挂单信息表修改
    Integer pendingOrderEdit(Map map);

    //挂单信息列表查询
    List<Map> pendingOrderList(Map map);

    //挂单信息表新增
    Integer pendingOrderAdd(Map map);

    //通过派工单号查询单个派工单信息
    /*@DS("git_adb")*/
    Map serviceDetailById(String dispatchingOrder);

    //派工单流转记录插入
    Integer transferSave(Map map);

    //通过派工单号查询单个二次上门认定信息
    /*@DS("git_adb")*/
    Map secondDoorById(Map map);

    //二次上门认定信息表新增
    Integer secondDoorAdd(Map map);

    //二次上门认定信息表修改
    Integer secondDoorEdit(Map map);

    //批量逻辑删除服务店信息
    Integer storeDels(List<Map> list);

    //批量新增服务店信息
    Integer storeAdds(List<Map> list);

    //批量修改服务店信息
    Integer storeUpdates(List<Map> upList);

    //查询全部服务店信息
    @DS("git_adb")
    List<Map> storeList();

    //单个30分钟预约及时率
    @DS("git_adb")
    Map timely(Map map);

    //按日聚合30分钟预约及时率
    @DS("git_adb")
    List<Map> dateTimely(Map map);

    //单个预约准时上门率
    @DS("git_adb")
    Map punctuality(Map map);

    //按日聚合预约准时上门率
    @DS("git_adb")
    List<Map> datePunctuality(Map map);

    //单个首次预约准时上门率
    @DS("git_adb")
    Map fristPunctuality(Map map);

    //按日聚合首次预约准时上门率
    @DS("git_adb")
    List<Map> fristDatePunctuality(Map map);

    //单个TAT平均服务完成时长
    @DS("git_adb")
    Map average(Map map);

    //按日聚合TAT平均服务完成时长
    @DS("git_adb")
    List<Map> dateAverage(Map map);

    //单个投诉7天解决率
    @DS("git_adb")
    Map solve(Map map);

    //按日聚合投诉7天解决率
    @DS("git_adb")
    List<Map> dateSolve(Map map);

    //单个一次修复率
    @DS("git_adb")
    Map repair(Map map);

    //按日聚合一次修复率
    @DS("git_adb")
    List<Map> dateRepair(Map map);

    //单个2天维修达成率
    @DS("git_adb")
    Map maintain(Map map);

    //按日聚合2天维修达成率
    @DS("git_adb")
    List<Map> dateMaintain(Map map);

    //单个N+1投诉解决方案提交率
    @DS("git_adb")
    Map scheme(Map map);

    //按日聚合N+1投诉解决方案提交率
    @DS("git_adb")
    List<Map> dateScheme(Map map);

    //列表查询五大指标
    @DS("git_adb")
    List<Map> indexList(Map map);
    @DS("git_adb")
    List<Map> indexListT(Map map);
    @DS("git_adb")
    List<Map> indexListS(Map map);

    //批量插入单个派工单的调换部件信息
    Integer replaceAdds(@Param("replacePart") List<Map> replacePart,@Param("dispatchingOrder")String dispatchingOrder );

    //批量插入单个派工单的检修部件信息
    Integer repairAdds(@Param("repairPart") List<Map> repairPart,@Param("dispatchingOrder") String dispatchingOrder );

    //TAB页值
    @DS("git_adb")
    Map tabValue(Map map);

    @DS("git_adb")
    Map tabValue1(Map map);

    @DS("git_adb")
    List<Map<String,Object>> tabValue2(Map map);

    @DS("git_adb")
    List<Map<String,Object>> tabValue3(Map map);

    //派工单信息
    @DS("git_adb")
    Map dispatchingDetail(Map map);

    //根据派工单查询调换部件信息
    @DS("git_adb")
    List<Map> replaceList(String dispatchingOrder);

    //根据派工单查询检修部件信息
    @DS("git_adb")
    List<Map> repairList(String dispatchingOrder);

    //流转历史
    @DS("git_adb")
    List<Map> transferInformation(Map map);
    
    /**
     * 上门服务异常监控 - 无需计算时间的，仅统计
     * @param request
     * @return
     */
    @DS("git_adb")
    Long visitMonitoringOnlyCount(Map<String, Object> map);
    
    /**
     * 上门服务异常监控 - 接单
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> visitMonitoring4JieDan(Map<String, Object> map);
    
    /**
     * 上门服务异常监控 - 预约
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> visitMonitoring4YuYue(Map<String, Object> map);
    /**
     * 上门服务异常监控 - 上门
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> visitMonitoring4ShangMen(Map<String, Object> map);
    
    /**
     * 上门服务异常监控 - 零件供应
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> visitMonitoring4LingJian(Map<String, Object> map);
    
    /**
     * 上门服务异常监控 - 服务完成
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> visitMonitoring4FuWuWanCheng(Map<String, Object> map);
    
    /**
     * 上门服务异常监控 - 服务提交
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> visitMonitoring4FuWuTiJiao(Map<String, Object> map);
    
    /**
     * 送修服务异常监控 - 接单
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> giveMonitoring4JieDan(Map<String, Object> map);
    
    /**
     * 送修服务异常监控 - 作业订单
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> giveMonitoring4ZuoYe(Map<String, Object> map);
    
    /**
     * 送修服务异常监控 - 零件供应
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> giveMonitoring4LingJian(Map<String, Object> map);
    
    /**
     * 送修服务异常监控 - 还件
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> giveMonitoring4HuanJian(Map<String, Object> map);
    
    /**
     * 送修服务异常监控 - 服务完成
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> giveMonitoring4FuWuWanCheng(Map<String, Object> map);
    
    /**
     * 送修服务异常监控 - 服务提交
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> giveMonitoring4FuWuTiJiao(Map<String, Object> map);
    
    /**
     * 寄修服务异常监控 - 寄修到件
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> sendMonitoring4JiXiu(Map<String, Object> map);
    
    /**
     * 寄修服务异常监控 - 接单
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> sendMonitoring4JieDan(Map<String, Object> map);
    
    /**
     * 寄修服务异常监控 - 作业订单
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> sendMonitoring4ZuoYe(Map<String, Object> map);
    
    /**
     * 寄修服务异常监控 - 零件供应
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> sendMonitoring4LingJian(Map<String, Object> map);
    
    /**
     * 寄修服务异常监控 - 还件
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> sendMonitoring4HuanJian(Map<String, Object> map);
    
    /**
     * 寄修服务异常监控 - 还件揽收
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> sendMonitoring4HuanJianLanShou(Map<String, Object> map);
    
    /**
     * 寄修服务异常监控 - 服务完成
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> sendMonitoring4FuWuWanCheng(Map<String, Object> map);
    
    /**
     * 寄修服务异常监控 - 服务提交
     * @param request
     * @return
     */
    @DS("git_adb")
    Map<String, Object> sendMonitoring4FuWuTiJiao(Map<String, Object> map);

    //单个改约率
    @DS("git_adb")
    Map reschedule(Map map);

    //按日聚合改约率
    @DS("git_adb")
    List<Map> dateReschedule(Map map);

    //通用查询
    @DS("git_adb")
    List<Map> lists(Map map);

    //挂单隔天的数量
    @DS("git_adb")
    List<Map> twoUp(Map map);

    //二次上门认定表
    @DS("git_adb")
    List<Map> twoUp2(Map map);

    //计算挂单时间
    @DS("git_adb")
    List<Map> gdLongTime(Map map);

    //关联服务单投诉
    @DS("git_adb")
    List<Map> complaintLists(Map map);

    //更新30分钟预约及时
    Integer timelyEligible(Map map);

    //更新
    Integer firstPunctualityEligible(Map map);

    Integer punctualityEligible(Map map);

    Integer averageEligible(Map map);

    Integer solveEligible(Map map);

    Integer repairEligible(Map map);

    Integer maintainEligible(Map map);

    Integer schemeEligible(Map map);

    Integer averageTime(Map map);

    Integer averageTime1(Map map);
    @DS("git_adb")
    List<Map> allList(Map map);
    @DS("git_adb")
    List<Long> timelyPie(Map map);
    @DS("git_adb")
    List<Map> punctualityPie(Map map);
    @DS("git_adb")
    List<Map> causePie(Map map);
    @DS("git_adb")
    List<Map> noPendingPie(Map map);
    @DS("git_adb")
    List<Map> repairBar(Map map);
    @DS("git_adb")
    //根据派工单号获取作业订单和到货签收信息
    List<Map> getPartsByOrder(String dispatchingOrder);

    Integer updateTwoUp1(Map map);

    Integer updateTwoUp2(Map map);

    //获取区域编码对应的名字
    @DS("git_adb")
    List<Map>  getMapDate();

    //投诉单服务单号
    @DS("git_adb")
    List<String> isComplaint(Map map);

    //处于作业订单状态的订单
    @DS("git_adb")
    List<String> isOrder(Map map);
    @DS("git_adb")
    List<String> isReceipt(Map map);
    @DS("git_adb")
    List<String> OrderReceipt(Map map);
    @DS("git_adb")
    List<Map> informationList(Map map);
    @DS("git_adb")
    List<Map> orderList(Map map);
    @DS("git_adb")
    List<String> getUserId(Map map);

    Integer TATFinishTime(Map map);
    @DS("git_adb")
    List<Map> tatNServiceCompletionRate(Map map);
    @DS("git_adb")
    List<Map> nDaysComplaintHandleData1(Map map);
    @DS("git_adb")
    List<Map> nDaysComplaintHandleData7(Map map);
    @DS("git_adb")
    List<Map> visitMonitoring(Map map);
    @DS("git_adb")
    List<Map> giveMonitoring(Map map);
    @DS("git_adb")
    List<Map> sendMonitoring(Map map);
    @DS("git_adb")
    Integer allListCount(Map map);

    @DS("git_adb")
    Integer getOutTimeCount(Map map);
    /**
     * 查询投诉7天解决率
     * @param complaintDto
     * @return
     */
    Map<String, Object> sevenDaySolveRate(@Param("dto") RegionComplaintDto complaintDto);
    /**
     * 分组查询每月或每天的解决率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> averageSolveRate(@Param("dto") RegionComplaintDto complaintDto);
    /**
     * 按月或按日查询N+1解决方案及时提交率
     * @param complaintDto
     * @return
     */
    List<Map<String, Object>> daysSubmissionRate(@Param("dto") RegionComplaintDto complaintDto);
    /**
     * 查询N+1解决方案及时提交率
     * @param complaintDto
     * @return
     */
    Map<String, Object> complaintSolveSubmissionRate(@Param("dto") RegionComplaintDto complaintDto);
    /**
     * 查询投诉类型
     * @param
     * @return
     */
    List<String> selectComplaintType(@Param("dto") RegionComplaintDto complaintDto);

    Integer dispatchingRegionAll();

    Integer complaintRegionAll();

    Integer errorSave(String dispatchingOrder);

    List<String> errorList();

    Integer dispatchingDel(String dispatchingOrder);

    Integer errorDel(String dispatchingOrder);
}
