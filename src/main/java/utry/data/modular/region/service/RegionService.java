package utry.data.modular.region.service;

import com.alibaba.fastjson.JSONObject;

import utry.data.modular.common.dto.TemplateQueryDataDto;
import utry.data.modular.complaints.dto.ComplaintDto;
import utry.data.modular.region.controller.dto.RegionComplaintDto;
import utry.data.modular.region.controller.dto.RegionVisitMonitoringRequest;
import utry.data.modular.technicalQuality.dto.EngineerExportDTO;
import utry.data.util.RetResult;

import java.util.List;
import java.util.Map;

/**
 * 大区服务业务接口
 *
 * @author wanlei
 */
public interface RegionService {

    /**
     *服务单详情推送（SPI）
     * @param map
     * @return
     */
    RetResult serviceDetail(Map map);

    /**
     *挂单解挂（SPI）
     * @param map
     * @return
     */
    RetResult pendingOrder(Map map);

    /**
     *二次上门认定推送（SPI）
     * @param map
     * @return
     */
    RetResult secondDoor(Map map);

    /**
     *全量获取服务门店信息（API）
     * @param map
     * @return
     */
    RetResult storeApi(Map map);

    /**
     *工程师管理信息获取（API）
     * @param map
     * @return
     */
    RetResult engineerManagementApi(Map map);

    /**
     *派工单详情获取（API）
     * @param map
     * @return
     */
    RetResult dispatchingDetailApi(Map map);

    /**
     *投诉处理单拉取（API）
     * @param map
     * @return
     */
    RetResult complaintHandlingApi(Map map);

    /**
     * 30分钟及时预约率
     * @param map
     * @return
     */
    RetResult timely(Map map);
    /**
     * 30分钟及时预约率
     * @param map
     * @return
     */
    RetResult timely1(Map map);
    /**
     *预约准时上门率
     * @param map
     * @return
     */
    RetResult punctuality(Map map);
    /**
     *预约准时上门率图表
     * @param map
     * @return
     */
    RetResult punctualityMap(Map map);
    /**
     *预约准时上门率
     * @param map
     * @return
     */
    RetResult punctuality1(Map map);

    /**
     *TAT平均服务完成时长
     * @param map
     * @return
     */
    RetResult average(Map map);
    /**
     *TAT平均服务完成时长饼图
     * @param map
     * @return
     */
    RetResult averagePie(Map map);
    /**
     *TAT平均服务完成时长图表
     * @param map
     * @return
     */
    RetResult averageMap(Map map);
    /**
     *TAT平均服务完成时长
     * @param map
     * @return
     */
    RetResult average1(Map map);

    /**
     *投诉7天解决率
     * @param map
     * @return
     */
    RetResult solve(Map map);
    /**
     *投诉7天解决率图表
     * @param map
     * @return
     */
    RetResult solveMap(Map map);
    /**
     *投诉7天解决率
     * @param map
     * @return
     */
    RetResult solve1(Map map);

    /**
     *一次修复率
     * @param map
     * @return
     */
    RetResult repair(Map map);
    /**
     *品类一次修复率柱图
     * @param map
     * @return
     */
    RetResult repairBar(Map map);
    /**
     *一次修复率图表
     * @param map
     * @return
     */
    RetResult repairMap(Map map);

    /**
     *2天维修达成率
     * @param map
     * @return
     */
    RetResult maintain(Map map);
    /**
     *2天维修达成率图表
     * @param map
     * @return
     */
    RetResult maintainMap(Map map);

    /**
     *N+1投诉解决方案提交率
     * @param map
     * @return
     */
    RetResult scheme(Map map);
    /**
     *N+1投诉解决方案提交率图表
     * @param map
     * @return
     */
    RetResult schemeMap(Map map);

    /**
     * 排名
     * @param map
     * @return
     */
    RetResult ranking(Map map);

    /**
     * 地图数据
     * @param map
     * @return
     */
    RetResult mapData(Map map);

    /**
     * 大区管理列表
     * @param map
     * @return
     */
    RetResult regionManage(Map map);

    /**
     * 工程师管理列表
     * @param map
     * @return
     */
    RetResult engineerManage(Map map);

    /**
     * 上门服务异常监控
     * @param request
     * @return
     */
    RetResult<Object> visitMonitoring(RegionVisitMonitoringRequest request);

    /**
     * 上门服务异常监控
     * @param map
     * @return
     */
    RetResult<Object> visitMonitoring1(Map map);

    /**
     * 送修服务异常监控
     * @param map
     * @return
     */
    RetResult<Object> giveMonitoring(RegionVisitMonitoringRequest request);
    /**
     * 送修服务异常监控
     * @param map
     * @return
     */
    RetResult<Object> giveMonitoring1(Map map);

    /**
     * 寄修服务异常监控
     * @param map
     * @return
     */
    RetResult<Object> sendMonitoring(RegionVisitMonitoringRequest request);
    /**
     * 寄修服务异常监控
     * @param map
     * @return
     */
    RetResult<Object> sendMonitoring1(Map map);

    /**
     * TAB页值
     * @param map
     * @return
     */
    RetResult tabValue(Map map);

    /**
     * 派工单详情
     * @param map
     * @return
     */
    RetResult dispatchingDetail(Map map);

    /**
     * 流转历史
     * @param map
     * @return
     */
    RetResult transferInformation(Map map);

    /**
     * 服务门店管理列表
     * @param map
     * @return
     */
    RetResult storeManage(Map map);

    /**
     * 改约率
     * @param map
     * @return
     */
    RetResult reschedule(Map map);

    /**
     * 更新全表率
     * @param map
     * @return
     */
    RetResult updateAll(Map map);
    /**
     * 更新全表TAT平均服务时长
     * @param map
     * @return
     */
    RetResult updateTAT(Map map);

    /**
     * 详情列表
     * @param map
     * @return
     */
    RetResult allList(Map map);

    /**
     * 30分钟预约及时率图表
     * @param map
     * @return
     */
    RetResult timelyMap(Map map);

    /**
     * 30分钟预约及时率饼图
     * @param map
     * @return
     */
    RetResult timelyPie(Map map);

    /**
     * 定时任务补打二次上门标签
     * @param map
     * @return
     */
    RetResult updateTwoUp(Map map);

    /**
     * 内部投诉7天解决率
     * @param complaintDto
     * @return
     */
    Map<String, Object> innerSevenDaySolveRate(RegionComplaintDto complaintDto);

    /**
     * N+1解决方案及时提交率
     * @param complaintDto
     * @return
     */
    Map<String, Object> complaintSolveSubmissionRate(RegionComplaintDto complaintDto);

    /**
     * 查询投诉类型
     * @param
     * @return
     */
    List<String> selectComplaintType(RegionComplaintDto complaintDto);

    /**
     * 全国各省TAT4天达成率
     * @param map
     * @return
     */
    RetResult provinceTAT4AchievementRate(Map map);

    RetResult thirtyMinuteAppointmentsRate(Map map);

    RetResult firstOnDoorAppointmentsRate(Map map);

    RetResult tatNServiceCompletionRate(Map map);

    RetResult nDaysComplaintHandleData(Map map);

    RetResult directManagementAreaScore(Map map);

    /**
     * 导出---大区管理
     * @param map
     * @return
     */
    List<Map> exportEngineerList(Map map);

    RetResult regionAll();

    RetResult error()throws Exception;

    RetResult totalRanking(Map map);

    RetResult target(Map map);

    RetResult lineChart(Map map);
}
